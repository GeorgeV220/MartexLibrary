package com.georgev22.library.scheduler;

import com.georgev22.library.scheduler.interfaces.Task;
import com.georgev22.library.scheduler.interfaces.Worker;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.IntUnaryOperator;

/**
 * The fundamental concepts for this implementation:
 * <li>Main thread owns {@link #head} and {@link #currentTick}, but it may be read from any thread</li>
 * <li>Main thread exclusively controls {@link #temp} and {@link #pending}.
 * They are never to be accessed outside of the main thread; alternatives exist to prevent locking.</li>
 * <li>{@link #head} to {@link #tail} act as a linked list/queue, with 1 consumer and infinite producers.
 * Adding to the tail is atomic and very efficient; utility method is {@link #handle(com.georgev22.library.scheduler.Task, long)} or {@link #addTask(com.georgev22.library.scheduler.Task)}. </li>
 * <li>Changing the period on a task is delicate.
 * Any future task needs to notify waiting threads.
 * Async tasks must be synchronized to make sure that any thread that's finishing will remove itself from {@link #runners}.
 * Another utility method is provided for this, {@link #cancelTask(int)}</li>
 * <li>{@link #runners} provides a moderately up-to-date view of active tasks.
 * If the linked head to tail set is read, all remaining tasks that were active at the time execution started will be located in runners.</li>
 * <li>Async tasks are responsible for removing themselves from runners</li>
 * <li>Sync tasks are only to be removed from runners on the main thread when coupled with a removal from pending and temp.</li>
 * <li>Most of the design in this scheduler relies on queuing special tasks to perform any data changes on the main thread.
 * When executed from inside a synchronous method, the scheduler will be updated before next execution by virtue of the frequent {@link #parsePending()} calls.</li>
 */
public class Scheduler implements com.georgev22.library.scheduler.interfaces.Scheduler {

    /**
     * The start ID for the counter.
     */
    private static final int START_ID = 1;
    /**
     * Increment the {@link #ids} field and reset it to the {@link #START_ID} if it reaches {@link Integer#MAX_VALUE}
     */
    private static final IntUnaryOperator INCREMENT_IDS = previous -> {
        // We reached the end, go back to the start!
        if (previous == Integer.MAX_VALUE) {
            return START_ID;
        }
        return previous + 1;
    };
    /**
     * Counter for IDs. Order doesn't matter, only uniqueness.
     */
    private final AtomicInteger ids = new AtomicInteger(START_ID);
    /**
     * Current head of linked-list. This reference is always stale, {@link com.georgev22.library.scheduler.Task#next} is the live reference.
     */
    private volatile com.georgev22.library.scheduler.Task head = new com.georgev22.library.scheduler.Task();
    /**
     * Tail of a linked-list. AtomicReference only matters when adding to queue
     */
    private final AtomicReference<com.georgev22.library.scheduler.Task> tail = new AtomicReference<>(head);
    // If the tasks should run on the same tick they should be run FIFO
    /**
     * Main thread logic only
     */
    private final PriorityQueue<com.georgev22.library.scheduler.Task> pending = new PriorityQueue<>(10,
            Comparator.comparingLong(com.georgev22.library.scheduler.Task::getNextRun).thenComparingLong(com.georgev22.library.scheduler.Task::getCreatedAt));
    /**
     * Main thread logic only
     */
    private final List<com.georgev22.library.scheduler.Task> temp = new ArrayList<>();
    /**
     * These are tasks that are currently active. It's provided for 'viewing' the current state.
     */
    private final ConcurrentHashMap<Integer, com.georgev22.library.scheduler.Task> runners = new ConcurrentHashMap<>();
    /**
     * The sync task that is currently running on the main thread.
     */
    private volatile com.georgev22.library.scheduler.Task currentTask = null;
    private volatile int currentTick = -1;
    private final Executor executor = Executors.newCachedThreadPool(new ThreadFactoryBuilder().setNameFormat("Scheduler Thread - %d").build());
    private AsyncDebugger debugHead = new AsyncDebugger(-1, null, null) {
        @Override
        StringBuilder debugTo(StringBuilder string) {
            return string;
        }
    };
    private AsyncDebugger debugTail = debugHead;
    private static final int RECENT_TICKS;

    static {
        RECENT_TICKS = 30;
    }

    @Override
    public int scheduleSyncDelayedTask(final @NotNull Class<?> clazz, final @NotNull Runnable task) {
        return this.scheduleSyncDelayedTask(clazz, task, 0L);
    }

    @Override
    public @NotNull Task runTask(@NotNull Class<?> clazz, @NotNull Runnable runnable) {
        return runTaskLater(clazz, runnable, 0L);
    }

    @Override
    public void runTask(@NotNull Class<?> clazz, @NotNull Consumer<Task> task) throws IllegalArgumentException {
        runTaskLater(clazz, task, 0L);
    }

    @Deprecated
    @Override
    public int scheduleAsyncDelayedTask(final @NotNull Class<?> clazz, final @NotNull Runnable task) {
        return this.scheduleAsyncDelayedTask(clazz, task, 0L);
    }

    @Override
    public @NotNull Task runTaskAsynchronously(@NotNull Class<?> clazz, @NotNull Runnable runnable) {
        return runTaskLaterAsynchronously(clazz, runnable, 0L);
    }

    @Override
    public void runTaskAsynchronously(@NotNull Class<?> clazz, @NotNull Consumer<Task> task) throws IllegalArgumentException {
        runTaskLaterAsynchronously(clazz, task, 0L);
    }

    @Override
    public int scheduleSyncDelayedTask(final @NotNull Class<?> clazz, final @NotNull Runnable task, final long delay) {
        return this.scheduleSyncRepeatingTask(clazz, task, delay, com.georgev22.library.scheduler.Task.NO_REPEATING);
    }

    @Override
    public @NotNull Task runTaskLater(@NotNull Class<?> clazz, @NotNull Runnable runnable, long delay) {
        return runTaskTimer(clazz, runnable, delay, com.georgev22.library.scheduler.Task.NO_REPEATING);
    }

    @Override
    public void runTaskLater(@NotNull Class<?> clazz, @NotNull Consumer<Task> task, long delay) throws IllegalArgumentException {
        runTaskTimer(clazz, task, delay, com.georgev22.library.scheduler.Task.NO_REPEATING);
    }

    @Deprecated
    @Override
    public int scheduleAsyncDelayedTask(final @NotNull Class<?> clazz, final @NotNull Runnable task, final long delay) {
        return this.scheduleAsyncRepeatingTask(clazz, task, delay, com.georgev22.library.scheduler.Task.NO_REPEATING);
    }

    @Override
    public @NotNull Task runTaskLaterAsynchronously(@NotNull Class<?> clazz, @NotNull Runnable runnable, long delay) {
        return runTaskTimerAsynchronously(clazz, runnable, delay, com.georgev22.library.scheduler.Task.NO_REPEATING);
    }

    @Override
    public void runTaskLaterAsynchronously(@NotNull Class<?> clazz, @NotNull Consumer<Task> task, long delay) throws IllegalArgumentException {
        runTaskTimerAsynchronously(clazz, task, delay, com.georgev22.library.scheduler.Task.NO_REPEATING);
    }

    @Override
    public void runTaskTimerAsynchronously(@NotNull Class<?> clazz, @NotNull Consumer<Task> task, long delay, long period) throws IllegalArgumentException {
        runTaskTimerAsynchronously(clazz, (Object) task, delay, com.georgev22.library.scheduler.Task.NO_REPEATING);
    }

    @Override
    public int scheduleSyncRepeatingTask(final @NotNull Class<?> clazz, final @NotNull Runnable runnable, long delay, long period) {
        return runTaskTimer(clazz, runnable, delay, period).getTaskId();
    }

    @Override
    public @NotNull Task runTaskTimer(@NotNull Class<?> clazz, @NotNull Runnable runnable, long delay, long period) {
        return runTaskTimer(clazz, (Object) runnable, delay, period);
    }

    @Override
    public void runTaskTimer(@NotNull Class<?> clazz, @NotNull Consumer<Task> task, long delay, long period) throws IllegalArgumentException {
        runTaskTimer(clazz, (Object) task, delay, period);
    }

    public Task runTaskTimer(Class<?> clazz, Object runnable, long delay, long period) {
        validate(clazz, runnable);
        if (delay < 0L) {
            delay = 0;
        }
        if (period == com.georgev22.library.scheduler.Task.ERROR) {
            period = 1L;
        } else if (period < com.georgev22.library.scheduler.Task.NO_REPEATING) {
            period = com.georgev22.library.scheduler.Task.NO_REPEATING;
        }
        return handle(new com.georgev22.library.scheduler.Task(clazz, runnable, nextId(), period), delay);
    }

    @Deprecated
    @Override
    public int scheduleAsyncRepeatingTask(final @NotNull Class<?> clazz, final @NotNull Runnable runnable, long delay, long period) {
        return runTaskTimerAsynchronously(clazz, runnable, delay, period).getTaskId();
    }

    @Override
    public @NotNull Task runTaskTimerAsynchronously(@NotNull Class<?> clazz, @NotNull Runnable runnable, long delay, long period) {
        return runTaskTimerAsynchronously(clazz, (Object) runnable, delay, period);
    }

    public Task runTaskTimerAsynchronously(Class<?> clazz, Object runnable, long delay, long period) {
        validate(clazz, runnable);
        if (delay < 0L) {
            delay = 0;
        }
        if (period == com.georgev22.library.scheduler.Task.ERROR) {
            period = 1L;
        } else if (period < com.georgev22.library.scheduler.Task.NO_REPEATING) {
            period = com.georgev22.library.scheduler.Task.NO_REPEATING;
        }
        return handle(new AsyncTask(runners, clazz, runnable, nextId(), period), delay);
    }

    @Override
    public <T> java.util.concurrent.@NotNull Future<T> callSyncMethod(final @NotNull Class<?> clazz, final @NotNull Callable<T> task) {
        validate(clazz, task);
        final Future<T> future = new Future<>(task, clazz, nextId());
        handle(future, 0L);
        return future;
    }

    @Override
    public void cancelTask(final int taskId) {
        if (taskId <= 0) {
            return;
        }
        com.georgev22.library.scheduler.Task task = runners.get(taskId);
        if (task != null) {
            task.cancel0();
        }
        task = new com.georgev22.library.scheduler.Task(
                new Runnable() {
                    @Override
                    public void run() {
                        if (!check(Scheduler.this.temp)) {
                            check(Scheduler.this.pending);
                        }
                    }

                    private boolean check(final Iterable<com.georgev22.library.scheduler.Task> collection) {
                        final Iterator<com.georgev22.library.scheduler.Task> tasks = collection.iterator();
                        while (tasks.hasNext()) {
                            final com.georgev22.library.scheduler.Task task = tasks.next();
                            if (task.getTaskId() == taskId) {
                                task.cancel0();
                                tasks.remove();
                                if (task.isSync()) {
                                    runners.remove(taskId);
                                }
                                return true;
                            }
                        }
                        return false;
                    }
                });
        handle(task, 0L);
        for (com.georgev22.library.scheduler.Task taskPending = head.getNext(); taskPending != null; taskPending = taskPending.getNext()) {
            if (taskPending == task) {
                return;
            }
            if (taskPending.getTaskId() == taskId) {
                taskPending.cancel0();
            }
        }
    }

    @Override
    public void cancelTasks(final @NotNull Class<?> clazz) {
        Validate.notNull(clazz, "Cannot cancel tasks of null class");
        final com.georgev22.library.scheduler.Task task = new com.georgev22.library.scheduler.Task(
                new Runnable() {
                    @Override
                    public void run() {
                        check(Scheduler.this.pending);
                        check(Scheduler.this.temp);
                    }

                    void check(final Iterable<com.georgev22.library.scheduler.Task> collection) {
                        final Iterator<com.georgev22.library.scheduler.Task> tasks = collection.iterator();
                        while (tasks.hasNext()) {
                            final com.georgev22.library.scheduler.Task task = tasks.next();
                            if (task.getOwner().equals(clazz)) {
                                task.cancel0();
                                tasks.remove();
                                if (task.isSync()) {
                                    runners.remove(task.getTaskId());
                                }
                            }
                        }
                    }
                });
        handle(task, 0L);
        for (com.georgev22.library.scheduler.Task taskPending = head.getNext(); taskPending != null; taskPending = taskPending.getNext()) {
            if (taskPending == task) {
                break;
            }
            if (taskPending.getTaskId() != -1 && taskPending.getOwner().equals(clazz)) {
                taskPending.cancel0();
            }
        }
        for (com.georgev22.library.scheduler.Task runner : runners.values()) {
            if (runner.getOwner().equals(clazz)) {
                runner.cancel0();
            }
        }
    }

    @Override
    public boolean isCurrentlyRunning(final int taskId) {
        final com.georgev22.library.scheduler.Task task = runners.get(taskId);
        if (task == null) {
            return false;
        }
        if (task.isSync()) {
            return (task == currentTask);
        }
        final AsyncTask asyncTask = (AsyncTask) task;
        synchronized (asyncTask.getWorkers()) {
            return !asyncTask.getWorkers().isEmpty();
        }
    }

    @Override
    public boolean isQueued(final int taskId) {
        if (taskId <= 0) {
            return false;
        }
        for (com.georgev22.library.scheduler.Task task = head.getNext(); task != null; task = task.getNext()) {
            if (task.getTaskId() == taskId) {
                return task.getPeriod() >= com.georgev22.library.scheduler.Task.NO_REPEATING; // The task will run
            }
        }
        com.georgev22.library.scheduler.Task task = runners.get(taskId);
        return task != null && task.getPeriod() >= com.georgev22.library.scheduler.Task.NO_REPEATING;
    }

    @Override
    public @NotNull List<Worker> getActiveWorkers() {
        final ArrayList<Worker> workers = new ArrayList<>();
        for (final com.georgev22.library.scheduler.Task taskObj : runners.values()) {
            // Iterator will be a best-effort (may fail to grab very new values) if called from an async thread
            if (taskObj.isSync()) {
                continue;
            }
            final AsyncTask task = (AsyncTask) taskObj;
            synchronized (task.getWorkers()) {
                // This will never have an issue with stale threads; it's state-safe
                workers.addAll(task.getWorkers());
            }
        }
        return workers;
    }

    @Override
    public @NotNull List<Task> getPendingTasks() {
        final ArrayList<com.georgev22.library.scheduler.Task> truePending = new ArrayList<>();
        for (com.georgev22.library.scheduler.Task task = head.getNext(); task != null; task = task.getNext()) {
            if (task.getTaskId() != -1) {
                // -1 is special code
                truePending.add(task);
            }
        }

        final ArrayList<Task> pending = new ArrayList<>();
        for (com.georgev22.library.scheduler.Task task : runners.values()) {
            if (task.getPeriod() >= com.georgev22.library.scheduler.Task.NO_REPEATING) {
                pending.add(task);
            }
        }

        for (final com.georgev22.library.scheduler.Task task : truePending) {
            if (task.getPeriod() >= com.georgev22.library.scheduler.Task.NO_REPEATING && !pending.contains(task)) {
                pending.add(task);
            }
        }
        return pending;
    }

    /**
     * This method is designed to never block or wait for locks; an immediate execution of all current tasks.
     */
    public void mainThreadHeartbeat(final int currentTick) {
        this.currentTick = currentTick;
        final List<com.georgev22.library.scheduler.Task> temp = this.temp;
        parsePending();
        while (isReady(currentTick)) {
            final com.georgev22.library.scheduler.Task task = pending.remove();
            if (task.getPeriod() < com.georgev22.library.scheduler.Task.NO_REPEATING) {
                if (task.isSync()) {
                    runners.remove(task.getTaskId(), task);
                }
                parsePending();
                continue;
            }
            if (task.isSync()) {
                currentTask = task;
                try {
                    task.run();
                } catch (final Throwable throwable) {
                    throw new RuntimeException(
                            String.format(
                                    "Task #%s for %s generated an exception",
                                    task.getTaskId(),
                                    task.getOwner().getSimpleName()),
                            throwable);
                } finally {
                    currentTask = null;
                }
                parsePending();
            } else {
                debugTail = debugTail.setNext(new AsyncDebugger(currentTick + RECENT_TICKS, task.getOwner(), task.getTaskClass()));
                executor.execute(task);
                // We don't need to parse pending
                // (async tasks must live with race-conditions if they attempt to cancel between these few lines of code)
            }
            final long period = task.getPeriod(); // State consistency
            if (period > 0) {
                task.setNextRun(currentTick + period);
                temp.add(task);
            } else if (task.isSync()) {
                runners.remove(task.getTaskId());
            }
        }
        pending.addAll(temp);
        temp.clear();
        debugHead = debugHead.getNextHead(currentTick);
    }

    private void addTask(final com.georgev22.library.scheduler.Task task) {
        final AtomicReference<com.georgev22.library.scheduler.Task> tail = this.tail;
        com.georgev22.library.scheduler.Task tailTask = tail.get();
        while (!tail.compareAndSet(tailTask, task)) {
            tailTask = tail.get();
        }
        tailTask.setNext(task);
    }

    private com.georgev22.library.scheduler.Task handle(final com.georgev22.library.scheduler.Task task, final long delay) {
        task.setNextRun(currentTick + delay);
        addTask(task);
        return task;
    }

    private static void validate(final Class<?> clazz, final Object task) {
        Validate.notNull(clazz, "Class cannot be null");
        Validate.notNull(task, "Task cannot be null");
        Validate.isTrue(task instanceof Runnable || task instanceof Consumer || task instanceof Callable, "Task must be Runnable, Consumer, or Callable");
    }

    private int nextId() {
        Validate.isTrue(runners.size() < Integer.MAX_VALUE, "There are already " + Integer.MAX_VALUE + " tasks scheduled! Cannot schedule more.");
        int id;
        do {
            id = ids.updateAndGet(INCREMENT_IDS);
        } while (runners.containsKey(id)); // Avoid generating duplicate IDs
        return id;
    }

    private void parsePending() {
        com.georgev22.library.scheduler.Task head = this.head;
        com.georgev22.library.scheduler.Task task = head.getNext();
        com.georgev22.library.scheduler.Task lastTask = head;
        for (; task != null; task = (lastTask = task).getNext()) {
            if (task.getTaskId() == -1) {
                task.run();
            } else if (task.getPeriod() >= com.georgev22.library.scheduler.Task.NO_REPEATING) {
                pending.add(task);
                runners.put(task.getTaskId(), task);
            }
        }
        // We split this because of the way things are ordered for all the async calls in Scheduler
        // (it prevents race-conditions)
        for (task = head; task != lastTask; task = head) {
            head = task.getNext();
            task.setNext(null);
        }
        this.head = lastTask;
    }

    private boolean isReady(final int currentTick) {
        return !pending.isEmpty() && pending.peek().getNextRun() <= currentTick;
    }

    @Override
    public String toString() {
        int debugTick = currentTick;
        StringBuilder string = new StringBuilder("Recent tasks from ").append(debugTick - RECENT_TICKS).append('-').append(debugTick).append('{');
        debugHead.debugTo(string);
        return string.append('}').toString();
    }


    @Deprecated
    @Override
    public int scheduleSyncDelayedTask(@NotNull Class<?> clazz, @NotNull SchedulerRunnable task, long delay) {
        throw new UnsupportedOperationException("Use SchedulerRunnable#runTaskLater(Class, long)");
    }

    @Deprecated
    @Override
    public int scheduleSyncDelayedTask(@NotNull Class<?> clazz, @NotNull SchedulerRunnable task) {
        throw new UnsupportedOperationException("Use SchedulerRunnable#runTask(Class)");
    }

    @Deprecated
    @Override
    public int scheduleSyncRepeatingTask(@NotNull Class<?> clazz, @NotNull SchedulerRunnable task, long delay, long period) {
        throw new UnsupportedOperationException("Use SchedulerRunnable#runTaskTimer(Class, long, long)");
    }

    @Deprecated
    @Override
    public @NotNull com.georgev22.library.scheduler.Task runTask(@NotNull Class<?> clazz, @NotNull SchedulerRunnable task) throws IllegalArgumentException {
        throw new UnsupportedOperationException("Use SchedulerRunnable#runTask(Class)");
    }

    @Deprecated
    @Override
    public @NotNull com.georgev22.library.scheduler.Task runTaskAsynchronously(@NotNull Class<?> clazz, @NotNull SchedulerRunnable task) throws IllegalArgumentException {
        throw new UnsupportedOperationException("Use SchedulerRunnable#runTaskAsynchronously(Class)");
    }

    @Deprecated
    @Override
    public @NotNull com.georgev22.library.scheduler.Task runTaskLater(@NotNull Class<?> clazz, @NotNull SchedulerRunnable task, long delay) throws IllegalArgumentException {
        throw new UnsupportedOperationException("Use SchedulerRunnable#runTaskLater(Class, long)");
    }

    @Deprecated
    @Override
    public @NotNull com.georgev22.library.scheduler.Task runTaskLaterAsynchronously(@NotNull Class<?> clazz, @NotNull SchedulerRunnable task, long delay) throws IllegalArgumentException {
        throw new UnsupportedOperationException("Use SchedulerRunnable#runTaskLaterAsynchronously(Class, long)");
    }

    @Deprecated
    @Override
    public @NotNull com.georgev22.library.scheduler.Task runTaskTimer(@NotNull Class<?> clazz, @NotNull SchedulerRunnable task, long delay, long period) throws IllegalArgumentException {
        throw new UnsupportedOperationException("Use SchedulerRunnable#runTaskTimer(Class, long, long)");
    }

    @Deprecated
    @Override
    public @NotNull com.georgev22.library.scheduler.Task runTaskTimerAsynchronously(@NotNull Class<?> clazz, @NotNull SchedulerRunnable task, long delay, long period) throws IllegalArgumentException {
        throw new UnsupportedOperationException("Use SchedulerRunnable#runTaskTimerAsynchronously(Class, long, long)");
    }
}

package com.georgev22.library.extensions.scheduler;

import com.georgev22.library.exceptions.IllegalExtensionAccessException;
import com.georgev22.library.extensions.Extension;
import com.georgev22.library.extensions.scheduler.interfaces.ExtensionScheduler;
import com.georgev22.library.extensions.scheduler.interfaces.ExtensionTask;
import com.georgev22.library.extensions.scheduler.interfaces.ExtensionWorker;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.IntUnaryOperator;
import java.util.logging.Level;

import org.jetbrains.annotations.NotNull;

/**
 * The fundamental concepts for this implementation:
 * <li>Main thread owns {@link #head} and {@link #currentTick}, but it may be read from any thread</li>
 * <li>Main thread exclusively controls {@link #temp} and {@link #pending}.
 * They are never to be accessed outside of the main thread; alternatives exist to prevent locking.</li>
 * <li>{@link #head} to {@link #tail} act as a linked list/queue, with 1 consumer and infinite producers.
 * Adding to the tail is atomic and very efficient; utility method is {@link #handle(Task, long)} or {@link #addTask(Task)}. </li>
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
public class Scheduler implements ExtensionScheduler {

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
     * Current head of linked-list. This reference is always stale, {@link Task#next} is the live reference.
     */
    private volatile Task head = new Task();
    /**
     * Tail of a linked-list. AtomicReference only matters when adding to queue
     */
    private final AtomicReference<Task> tail = new AtomicReference<Task>(head);
    /**
     * Main thread logic only
     */
    private final PriorityQueue<Task> pending = new PriorityQueue<Task>(10,
            new Comparator<Task>() {
                @Override
                public int compare(final Task o1, final Task o2) {
                    int value = Long.compare(o1.getNextRun(), o2.getNextRun());

                    // If the tasks should run on the same tick they should be run FIFO
                    return value != 0 ? value : Long.compare(o1.getCreatedAt(), o2.getCreatedAt());
                }
            });
    /**
     * Main thread logic only
     */
    private final List<Task> temp = new ArrayList<Task>();
    /**
     * These are tasks that are currently active. It's provided for 'viewing' the current state.
     */
    private final ConcurrentHashMap<Integer, Task> runners = new ConcurrentHashMap<Integer, Task>();
    /**
     * The sync task that is currently running on the main thread.
     */
    private volatile Task currentTask = null;
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
    public int scheduleSyncDelayedTask(final @NotNull Extension extension, final @NotNull Runnable task) {
        return this.scheduleSyncDelayedTask(extension, task, 0L);
    }

    @Override
    public @NotNull ExtensionTask runTask(@NotNull Extension extension, @NotNull Runnable runnable) {
        return runTaskLater(extension, runnable, 0L);
    }

    @Override
    public void runTask(@NotNull Extension extension, @NotNull Consumer<ExtensionTask> task) throws IllegalArgumentException {
        runTaskLater(extension, task, 0L);
    }

    @Deprecated
    @Override
    public int scheduleAsyncDelayedTask(final @NotNull Extension extension, final @NotNull Runnable task) {
        return this.scheduleAsyncDelayedTask(extension, task, 0L);
    }

    @Override
    public @NotNull ExtensionTask runTaskAsynchronously(@NotNull Extension extension, @NotNull Runnable runnable) {
        return runTaskLaterAsynchronously(extension, runnable, 0L);
    }

    @Override
    public void runTaskAsynchronously(@NotNull Extension extension, @NotNull Consumer<ExtensionTask> task) throws IllegalArgumentException {
        runTaskLaterAsynchronously(extension, task, 0L);
    }

    @Override
    public int scheduleSyncDelayedTask(final @NotNull Extension extension, final @NotNull Runnable task, final long delay) {
        return this.scheduleSyncRepeatingTask(extension, task, delay, Task.NO_REPEATING);
    }

    @Override
    public @NotNull ExtensionTask runTaskLater(@NotNull Extension extension, @NotNull Runnable runnable, long delay) {
        return runTaskTimer(extension, runnable, delay, Task.NO_REPEATING);
    }

    @Override
    public void runTaskLater(@NotNull Extension extension, @NotNull Consumer<ExtensionTask> task, long delay) throws IllegalArgumentException {
        runTaskTimer(extension, task, delay, Task.NO_REPEATING);
    }

    @Deprecated
    @Override
    public int scheduleAsyncDelayedTask(final @NotNull Extension extension, final @NotNull Runnable task, final long delay) {
        return this.scheduleAsyncRepeatingTask(extension, task, delay, Task.NO_REPEATING);
    }

    @Override
    public @NotNull ExtensionTask runTaskLaterAsynchronously(@NotNull Extension extension, @NotNull Runnable runnable, long delay) {
        return runTaskTimerAsynchronously(extension, runnable, delay, Task.NO_REPEATING);
    }

    @Override
    public void runTaskLaterAsynchronously(@NotNull Extension extension, @NotNull Consumer<ExtensionTask> task, long delay) throws IllegalArgumentException {
        runTaskTimerAsynchronously(extension, task, delay, Task.NO_REPEATING);
    }

    @Override
    public void runTaskTimerAsynchronously(@NotNull Extension extension, @NotNull Consumer<ExtensionTask> task, long delay, long period) throws IllegalArgumentException {
        runTaskTimerAsynchronously(extension, (Object) task, delay, Task.NO_REPEATING);
    }

    @Override
    public int scheduleSyncRepeatingTask(final @NotNull Extension extension, final @NotNull Runnable runnable, long delay, long period) {
        return runTaskTimer(extension, runnable, delay, period).getTaskId();
    }

    @Override
    public @NotNull ExtensionTask runTaskTimer(@NotNull Extension extension, @NotNull Runnable runnable, long delay, long period) {
        return runTaskTimer(extension, (Object) runnable, delay, period);
    }

    @Override
    public void runTaskTimer(@NotNull Extension extension, @NotNull Consumer<ExtensionTask> task, long delay, long period) throws IllegalArgumentException {
        runTaskTimer(extension, (Object) task, delay, period);
    }

    public ExtensionTask runTaskTimer(Extension extension, Object runnable, long delay, long period) {
        validate(extension, runnable);
        if (delay < 0L) {
            delay = 0;
        }
        if (period == Task.ERROR) {
            period = 1L;
        } else if (period < Task.NO_REPEATING) {
            period = Task.NO_REPEATING;
        }
        return handle(new Task(extension, runnable, nextId(), period), delay);
    }

    @Deprecated
    @Override
    public int scheduleAsyncRepeatingTask(final @NotNull Extension extension, final @NotNull Runnable runnable, long delay, long period) {
        return runTaskTimerAsynchronously(extension, runnable, delay, period).getTaskId();
    }

    @Override
    public @NotNull ExtensionTask runTaskTimerAsynchronously(@NotNull Extension extension, @NotNull Runnable runnable, long delay, long period) {
        return runTaskTimerAsynchronously(extension, (Object) runnable, delay, period);
    }

    public ExtensionTask runTaskTimerAsynchronously(Extension extension, Object runnable, long delay, long period) {
        validate(extension, runnable);
        if (delay < 0L) {
            delay = 0;
        }
        if (period == Task.ERROR) {
            period = 1L;
        } else if (period < Task.NO_REPEATING) {
            period = Task.NO_REPEATING;
        }
        return handle(new AsyncTask(runners, extension, runnable, nextId(), period), delay);
    }

    @Override
    public <T> java.util.concurrent.@NotNull Future<T> callSyncMethod(final @NotNull Extension extension, final @NotNull Callable<T> task) {
        validate(extension, task);
        final Future<T> future = new Future<T>(task, extension, nextId());
        handle(future, 0L);
        return future;
    }

    @Override
    public void cancelTask(final int taskId) {
        if (taskId <= 0) {
            return;
        }
        Task task = runners.get(taskId);
        if (task != null) {
            task.cancel0();
        }
        task = new Task(
                new Runnable() {
                    @Override
                    public void run() {
                        if (!check(Scheduler.this.temp)) {
                            check(Scheduler.this.pending);
                        }
                    }

                    private boolean check(final Iterable<Task> collection) {
                        final Iterator<Task> tasks = collection.iterator();
                        while (tasks.hasNext()) {
                            final Task task = tasks.next();
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
        for (Task taskPending = head.getNext(); taskPending != null; taskPending = taskPending.getNext()) {
            if (taskPending == task) {
                return;
            }
            if (taskPending.getTaskId() == taskId) {
                taskPending.cancel0();
            }
        }
    }

    @Override
    public void cancelTasks(final @NotNull Extension extension) {
        //noinspection ConstantValue
        if (extension == null) {
            throw new IllegalArgumentException("Cannot cancel tasks of null extension");
        }
        final Task task = new Task(
                new Runnable() {
                    @Override
                    public void run() {
                        check(Scheduler.this.pending);
                        check(Scheduler.this.temp);
                    }

                    void check(final Iterable<Task> collection) {
                        final Iterator<Task> tasks = collection.iterator();
                        while (tasks.hasNext()) {
                            final Task task = tasks.next();
                            if (task.getOwner().equals(extension)) {
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
        for (Task taskPending = head.getNext(); taskPending != null; taskPending = taskPending.getNext()) {
            if (taskPending == task) {
                break;
            }
            if (taskPending.getTaskId() != -1 && taskPending.getOwner().equals(extension)) {
                taskPending.cancel0();
            }
        }
        for (Task runner : runners.values()) {
            if (runner.getOwner().equals(extension)) {
                runner.cancel0();
            }
        }
    }

    @Override
    public boolean isCurrentlyRunning(final int taskId) {
        final Task task = runners.get(taskId);
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
        for (Task task = head.getNext(); task != null; task = task.getNext()) {
            if (task.getTaskId() == taskId) {
                return task.getPeriod() >= Task.NO_REPEATING; // The task will run
            }
        }
        Task task = runners.get(taskId);
        return task != null && task.getPeriod() >= Task.NO_REPEATING;
    }

    @Override
    public @NotNull List<ExtensionWorker> getActiveWorkers() {
        final ArrayList<ExtensionWorker> workers = new ArrayList<>();
        for (final Task taskObj : runners.values()) {
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
    public @NotNull List<ExtensionTask> getPendingTasks() {
        final ArrayList<Task> truePending = new ArrayList<>();
        for (Task task = head.getNext(); task != null; task = task.getNext()) {
            if (task.getTaskId() != -1) {
                // -1 is special code
                truePending.add(task);
            }
        }

        final ArrayList<ExtensionTask> pending = new ArrayList<>();
        for (Task task : runners.values()) {
            if (task.getPeriod() >= Task.NO_REPEATING) {
                pending.add(task);
            }
        }

        for (final Task task : truePending) {
            if (task.getPeriod() >= Task.NO_REPEATING && !pending.contains(task)) {
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
        final List<Task> temp = this.temp;
        parsePending();
        while (isReady(currentTick)) {
            final Task task = pending.remove();
            if (task.getPeriod() < Task.NO_REPEATING) {
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
                    task.getOwner().getLogger().log(
                            Level.WARNING,
                            String.format(
                                    "Task #%s for %s generated an exception",
                                    task.getTaskId(),
                                    task.getOwner().getDescription().getFullName()),
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

    private void addTask(final Task task) {
        final AtomicReference<Task> tail = this.tail;
        Task tailTask = tail.get();
        while (!tail.compareAndSet(tailTask, task)) {
            tailTask = tail.get();
        }
        tailTask.setNext(task);
    }

    private Task handle(final Task task, final long delay) {
        task.setNextRun(currentTick + delay);
        addTask(task);
        return task;
    }

    private static void validate(final Extension extension, final Object task) {
        if (extension == null) {
            throw new IllegalExtensionAccessException("Extension cannot be null");
        }
        if (task == null) {
            throw new IllegalExtensionAccessException("Task cannot be null");
        }
        if (!(task instanceof Runnable) && !(task instanceof Consumer) && !(task instanceof Callable)) {
            throw new IllegalExtensionAccessException("Task must be Runnable, Consumer, or Callable");
        }
        if (!extension.isEnabled()) {
            throw new IllegalExtensionAccessException("Extension attempted to register task while disabled");
        }
    }

    private int nextId() {
        if (runners.size() == Integer.MAX_VALUE) {
            throw new IllegalStateException("There are already " + Integer.MAX_VALUE + " tasks scheduled! Cannot schedule more.");
        }
        int id;
        do {
            id = ids.updateAndGet(INCREMENT_IDS);
        } while (runners.containsKey(id)); // Avoid generating duplicate IDs
        return id;
    }

    private void parsePending() {
        Task head = this.head;
        Task task = head.getNext();
        Task lastTask = head;
        for (; task != null; task = (lastTask = task).getNext()) {
            if (task.getTaskId() == -1) {
                task.run();
            } else if (task.getPeriod() >= Task.NO_REPEATING) {
                pending.add(task);
                runners.put(task.getTaskId(), task);
            }
        }
        // We split this because of the way things are ordered for all of the async calls in Scheduler
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
    public int scheduleSyncDelayedTask(@NotNull Extension extension, @NotNull ExtensionRunnable task, long delay) {
        throw new UnsupportedOperationException("Use ExtensionRunnable#runTaskLater(Extension, long)");
    }

    @Deprecated
    @Override
    public int scheduleSyncDelayedTask(@NotNull Extension extension, @NotNull ExtensionRunnable task) {
        throw new UnsupportedOperationException("Use ExtensionRunnable#runTask(Extension)");
    }

    @Deprecated
    @Override
    public int scheduleSyncRepeatingTask(@NotNull Extension extension, @NotNull ExtensionRunnable task, long delay, long period) {
        throw new UnsupportedOperationException("Use ExtensionRunnable#runTaskTimer(Extension, long, long)");
    }

    @Deprecated
    @Override
    public @NotNull Task runTask(@NotNull Extension extension, @NotNull ExtensionRunnable task) throws IllegalArgumentException {
        throw new UnsupportedOperationException("Use ExtensionRunnable#runTask(Extension)");
    }

    @Deprecated
    @Override
    public @NotNull Task runTaskAsynchronously(@NotNull Extension extension, @NotNull ExtensionRunnable task) throws IllegalArgumentException {
        throw new UnsupportedOperationException("Use ExtensionRunnable#runTaskAsynchronously(Extension)");
    }

    @Deprecated
    @Override
    public @NotNull Task runTaskLater(@NotNull Extension extension, @NotNull ExtensionRunnable task, long delay) throws IllegalArgumentException {
        throw new UnsupportedOperationException("Use ExtensionRunnable#runTaskLater(Extension, long)");
    }

    @Deprecated
    @Override
    public @NotNull Task runTaskLaterAsynchronously(@NotNull Extension extension, @NotNull ExtensionRunnable task, long delay) throws IllegalArgumentException {
        throw new UnsupportedOperationException("Use ExtensionRunnable#runTaskLaterAsynchronously(Extension, long)");
    }

    @Deprecated
    @Override
    public @NotNull Task runTaskTimer(@NotNull Extension extension, @NotNull ExtensionRunnable task, long delay, long period) throws IllegalArgumentException {
        throw new UnsupportedOperationException("Use ExtensionRunnable#runTaskTimer(Extension, long, long)");
    }

    @Deprecated
    @Override
    public @NotNull Task runTaskTimerAsynchronously(@NotNull Extension extension, @NotNull ExtensionRunnable task, long delay, long period) throws IllegalArgumentException {
        throw new UnsupportedOperationException("Use ExtensionRunnable#runTaskTimerAsynchronously(Extension, long, long)");
    }
}

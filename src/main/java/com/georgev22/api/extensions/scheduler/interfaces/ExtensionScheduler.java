package com.georgev22.api.extensions.scheduler.interfaces;


import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.function.Consumer;

import com.georgev22.api.extensions.Extension;
import com.georgev22.api.extensions.scheduler.ExtensionRunnable;
import org.jetbrains.annotations.NotNull;

public interface ExtensionScheduler {

    /**
     * Schedules a once off task to occur after a delay.
     * <p>
     * This task will be executed by the main server thread.
     *
     * @param extension Extension that owns the task
     * @param task      Task to be executed
     * @param delay     Delay in server ticks before executing task
     * @return Task id number (-1 if scheduling failed)
     */
    int scheduleSyncDelayedTask(@NotNull Extension extension, @NotNull Runnable task, long delay);

    /**
     * @param extension Extension that owns the task
     * @param task      Task to be executed
     * @param delay     Delay in server ticks before executing task
     * @return Task id number (-1 if scheduling failed)
     * @deprecated Use {@link ExtensionRunnable#runTaskLater(Extension, long)}
     */
    @Deprecated
    int scheduleSyncDelayedTask(@NotNull Extension extension, @NotNull ExtensionRunnable task, long delay);

    /**
     * Schedules a once off task to occur as soon as possible.
     * <p>
     * This task will be executed by the main server thread.
     *
     * @param extension Extension that owns the task
     * @param task      Task to be executed
     * @return Task id number (-1 if scheduling failed)
     */
    int scheduleSyncDelayedTask(@NotNull Extension extension, @NotNull Runnable task);

    /**
     * @param extension Extension that owns the task
     * @param task      Task to be executed
     * @return Task id number (-1 if scheduling failed)
     * @deprecated Use {@link ExtensionRunnable#runTask(Extension)}
     */
    @Deprecated
    int scheduleSyncDelayedTask(@NotNull Extension extension, @NotNull ExtensionRunnable task);

    /**
     * Schedules a repeating task.
     * <p>
     * This task will be executed by the main server thread.
     *
     * @param extension Extension that owns the task
     * @param task      Task to be executed
     * @param delay     Delay in server ticks before executing first repeat
     * @param period    Period in server ticks of the task
     * @return Task id number (-1 if scheduling failed)
     */
    int scheduleSyncRepeatingTask(@NotNull Extension extension, @NotNull Runnable task, long delay, long period);

    /**
     * @param extension Extension that owns the task
     * @param task      Task to be executed
     * @param delay     Delay in server ticks before executing first repeat
     * @param period    Period in server ticks of the task
     * @return Task id number (-1 if scheduling failed)
     * @deprecated Use {@link ExtensionRunnable#runTaskTimer(Extension, long, long)}
     */
    @Deprecated
    int scheduleSyncRepeatingTask(@NotNull Extension extension, @NotNull ExtensionRunnable task, long delay, long period);

    /**
     * <b>Asynchronous tasks should never access any API in Bukkit. Great care
     * should be taken to assure the thread-safety of asynchronous tasks.</b>
     * <p>
     * Schedules a once off task to occur after a delay. This task will be
     * executed by a thread managed by the scheduler.
     *
     * @param extension Extension that owns the task
     * @param task      Task to be executed
     * @param delay     Delay in server ticks before executing task
     * @return Task id number (-1 if scheduling failed)
     * @deprecated This name is misleading, as it does not schedule "a sync"
     * task, but rather, "an async" task
     */
    @Deprecated
    int scheduleAsyncDelayedTask(@NotNull Extension extension, @NotNull Runnable task, long delay);

    /**
     * <b>Asynchronous tasks should never access any API in Bukkit. Great care
     * should be taken to assure the thread-safety of asynchronous tasks.</b>
     * <p>
     * Schedules a once off task to occur as soon as possible. This task will
     * be executed by a thread managed by the scheduler.
     *
     * @param extension Extension that owns the task
     * @param task      Task to be executed
     * @return Task id number (-1 if scheduling failed)
     * @deprecated This name is misleading, as it does not schedule "a sync"
     * task, but rather, "an async" task
     */
    @Deprecated
    int scheduleAsyncDelayedTask(@NotNull Extension extension, @NotNull Runnable task);

    /**
     * <b>Asynchronous tasks should never access any API in Bukkit. Great care
     * should be taken to assure the thread-safety of asynchronous tasks.</b>
     * <p>
     * Schedules a repeating task. This task will be executed by a thread
     * managed by the scheduler.
     *
     * @param extension Extension that owns the task
     * @param task      Task to be executed
     * @param delay     Delay in server ticks before executing first repeat
     * @param period    Period in server ticks of the task
     * @return Task id number (-1 if scheduling failed)
     * @deprecated This name is misleading, as it does not schedule "a sync"
     * task, but rather, "an async" task
     */
    @Deprecated
    int scheduleAsyncRepeatingTask(@NotNull Extension extension, @NotNull Runnable task, long delay, long period);

    /**
     * Calls a method on the main thread and returns a Future object. This
     * task will be executed by the main server thread.
     * <ul>
     * <li>Note: The Future.get() methods must NOT be called from the main
     *     thread.
     * <li>Note2: There is at least an average of 10ms latency until the
     *     isDone() method returns true.
     * </ul>
     *
     * @param <T>       The callable's return type
     * @param extension Extension that owns the task
     * @param task      Task to be executed
     * @return Future Future object related to the task
     */
    @NotNull <T> Future<T> callSyncMethod(@NotNull Extension extension, @NotNull Callable<T> task);

    /**
     * Removes task from scheduler.
     *
     * @param taskId Id number of task to be removed
     */
    void cancelTask(int taskId);

    /**
     * Removes all tasks associated with a particular extension from the
     * scheduler.
     *
     * @param extension Owner of tasks to be removed
     */
    void cancelTasks(@NotNull Extension extension);

    /**
     * Check if the task currently running.
     * <p>
     * A repeating task might not be running currently, but will be running in
     * the future. A task that has finished, and does not repeat, will not be
     * running ever again.
     * <p>
     * Explicitly, a task is running if there exists a thread for it, and that
     * thread is alive.
     *
     * @param taskId The task to check.
     *               <p>
     * @return If the task is currently running.
     */
    boolean isCurrentlyRunning(int taskId);

    /**
     * Check if the task queued to be run later.
     * <p>
     * If a repeating task is currently running, it might not be queued now
     * but could be in the future. A task that is not queued, and not running,
     * will not be queued again.
     *
     * @param taskId The task to check.
     *               <p>
     * @return If the task is queued to be run.
     */
    boolean isQueued(int taskId);

    /**
     * Returns a list of all active workers.
     * <p>
     * This list contains asynch tasks that are being executed by separate
     * threads.
     *
     * @return Active workers
     */
    @NotNull List<ExtensionWorker> getActiveWorkers();

    /**
     * Returns a list of all pending tasks. The ordering of the tasks is not
     * related to their order of execution.
     *
     * @return Active workers
     */
    @NotNull List<ExtensionTask> getPendingTasks();

    /**
     * Returns a task that will run on the next server tick.
     *
     * @param extension the reference to the extension scheduling task
     * @param task      the task to be run
     * @return a ExtensionTask that contains the id number
     * @throws IllegalArgumentException if extension is null
     * @throws IllegalArgumentException if task is null
     */
    @NotNull ExtensionTask runTask(@NotNull Extension extension, @NotNull Runnable task) throws IllegalArgumentException;

    /**
     * Returns a task that will run on the next server tick.
     *
     * @param extension the reference to the extension scheduling task
     * @param task      the task to be run
     * @throws IllegalArgumentException if extension is null
     * @throws IllegalArgumentException if task is null
     */
    void runTask(@NotNull Extension extension, @NotNull Consumer<ExtensionTask> task) throws IllegalArgumentException;

    /**
     * @param extension the reference to the extension scheduling task
     * @param task      the task to be run
     * @return a ExtensionTask that contains the id number
     * @throws IllegalArgumentException if extension is null
     * @throws IllegalArgumentException if task is null
     * @deprecated Use {@link ExtensionRunnable#runTask(Extension)}
     */
    @Deprecated
    @NotNull ExtensionTask runTask(@NotNull Extension extension, @NotNull ExtensionRunnable task) throws IllegalArgumentException;

    /**
     * <b>Asynchronous tasks should never access any API in Bukkit. Great care
     * should be taken to assure the thread-safety of asynchronous tasks.</b>
     * <p>
     * Returns a task that will run asynchronously.
     *
     * @param extension the reference to the extension scheduling task
     * @param task      the task to be run
     * @return a ExtensionTask that contains the id number
     * @throws IllegalArgumentException if extension is null
     * @throws IllegalArgumentException if task is null
     */
    @NotNull ExtensionTask runTaskAsynchronously(@NotNull Extension extension, @NotNull Runnable task) throws IllegalArgumentException;

    /**
     * <b>Asynchronous tasks should never access any API in Bukkit. Great care
     * should be taken to assure the thread-safety of asynchronous tasks.</b>
     * <p>
     * Returns a task that will run asynchronously.
     *
     * @param extension the reference to the extension scheduling task
     * @param task      the task to be run
     * @throws IllegalArgumentException if extension is null
     * @throws IllegalArgumentException if task is null
     */
    void runTaskAsynchronously(@NotNull Extension extension, @NotNull Consumer<ExtensionTask> task) throws IllegalArgumentException;

    /**
     * @param extension the reference to the extension scheduling task
     * @param task      the task to be run
     * @return a ExtensionTask that contains the id number
     * @throws IllegalArgumentException if extension is null
     * @throws IllegalArgumentException if task is null
     * @deprecated Use {@link ExtensionRunnable#runTaskAsynchronously(Extension)}
     */
    @Deprecated
    @NotNull ExtensionTask runTaskAsynchronously(@NotNull Extension extension, @NotNull ExtensionRunnable task) throws IllegalArgumentException;

    /**
     * Returns a task that will run after the specified number of server
     * ticks.
     *
     * @param extension the reference to the extension scheduling task
     * @param task      the task to be run
     * @param delay     the ticks to wait before running the task
     * @return a ExtensionTask that contains the id number
     * @throws IllegalArgumentException if extension is null
     * @throws IllegalArgumentException if task is null
     */
    @NotNull ExtensionTask runTaskLater(@NotNull Extension extension, @NotNull Runnable task, long delay) throws IllegalArgumentException;

    /**
     * Returns a task that will run after the specified number of server
     * ticks.
     *
     * @param extension the reference to the extension scheduling task
     * @param task      the task to be run
     * @param delay     the ticks to wait before running the task
     * @throws IllegalArgumentException if extension is null
     * @throws IllegalArgumentException if task is null
     */
    void runTaskLater(@NotNull Extension extension, @NotNull Consumer<ExtensionTask> task, long delay) throws IllegalArgumentException;

    /**
     * @param extension the reference to the extension scheduling task
     * @param task      the task to be run
     * @param delay     the ticks to wait before running the task
     * @return a ExtensionTask that contains the id number
     * @throws IllegalArgumentException if extension is null
     * @throws IllegalArgumentException if task is null
     * @deprecated Use {@link ExtensionRunnable#runTaskLater(Extension, long)}
     */
    @Deprecated
    @NotNull ExtensionTask runTaskLater(@NotNull Extension extension, @NotNull ExtensionRunnable task, long delay) throws IllegalArgumentException;

    /**
     * <b>Asynchronous tasks should never access any API in Bukkit. Great care
     * should be taken to assure the thread-safety of asynchronous tasks.</b>
     * <p>
     * Returns a task that will run asynchronously after the specified number
     * of server ticks.
     *
     * @param extension the reference to the extension scheduling task
     * @param task      the task to be run
     * @param delay     the ticks to wait before running the task
     * @return a ExtensionTask that contains the id number
     * @throws IllegalArgumentException if extension is null
     * @throws IllegalArgumentException if task is null
     */
    @NotNull ExtensionTask runTaskLaterAsynchronously(@NotNull Extension extension, @NotNull Runnable task, long delay) throws IllegalArgumentException;

    /**
     * <b>Asynchronous tasks should never access any API in Bukkit. Great care
     * should be taken to assure the thread-safety of asynchronous tasks.</b>
     * <p>
     * Returns a task that will run asynchronously after the specified number
     * of server ticks.
     *
     * @param extension the reference to the extension scheduling task
     * @param task      the task to be run
     * @param delay     the ticks to wait before running the task
     * @throws IllegalArgumentException if extension is null
     * @throws IllegalArgumentException if task is null
     */
    void runTaskLaterAsynchronously(@NotNull Extension extension, @NotNull Consumer<ExtensionTask> task, long delay) throws IllegalArgumentException;

    /**
     * @param extension the reference to the extension scheduling task
     * @param task      the task to be run
     * @param delay     the ticks to wait before running the task
     * @return a ExtensionTask that contains the id number
     * @throws IllegalArgumentException if extension is null
     * @throws IllegalArgumentException if task is null
     * @deprecated Use {@link ExtensionRunnable#runTaskLaterAsynchronously(Extension, long)}
     */
    @Deprecated
    @NotNull ExtensionTask runTaskLaterAsynchronously(@NotNull Extension extension, @NotNull ExtensionRunnable task, long delay) throws IllegalArgumentException;

    /**
     * Returns a task that will repeatedly run until cancelled, starting after
     * the specified number of server ticks.
     *
     * @param extension the reference to the extension scheduling task
     * @param task      the task to be run
     * @param delay     the ticks to wait before running the task
     * @param period    the ticks to wait between runs
     * @return a ExtensionTask that contains the id number
     * @throws IllegalArgumentException if extension is null
     * @throws IllegalArgumentException if task is null
     */
    @NotNull ExtensionTask runTaskTimer(@NotNull Extension extension, @NotNull Runnable task, long delay, long period) throws IllegalArgumentException;

    /**
     * Returns a task that will repeatedly run until cancelled, starting after
     * the specified number of server ticks.
     *
     * @param extension the reference to the extension scheduling task
     * @param task      the task to be run
     * @param delay     the ticks to wait before running the task
     * @param period    the ticks to wait between runs
     * @throws IllegalArgumentException if extension is null
     * @throws IllegalArgumentException if task is null
     */
    void runTaskTimer(@NotNull Extension extension, @NotNull Consumer<ExtensionTask> task, long delay, long period) throws IllegalArgumentException;

    /**
     * @param extension the reference to the extension scheduling task
     * @param task      the task to be run
     * @param delay     the ticks to wait before running the task
     * @param period    the ticks to wait between runs
     * @return a ExtensionTask that contains the id number
     * @throws IllegalArgumentException if extension is null
     * @throws IllegalArgumentException if task is null
     * @deprecated Use {@link ExtensionRunnable#runTaskTimer(Extension, long, long)}
     */
    @Deprecated
    @NotNull ExtensionTask runTaskTimer(@NotNull Extension extension, @NotNull ExtensionRunnable task, long delay, long period) throws IllegalArgumentException;

    /**
     * <b>Asynchronous tasks should never access any API in Bukkit. Great care
     * should be taken to assure the thread-safety of asynchronous tasks.</b>
     * <p>
     * Returns a task that will repeatedly run asynchronously until cancelled,
     * starting after the specified number of server ticks.
     *
     * @param extension the reference to the extension scheduling task
     * @param task      the task to be run
     * @param delay     the ticks to wait before running the task for the first
     *                  time
     * @param period    the ticks to wait between runs
     * @return a ExtensionTask that contains the id number
     * @throws IllegalArgumentException if extension is null
     * @throws IllegalArgumentException if task is null
     */
    @NotNull ExtensionTask runTaskTimerAsynchronously(@NotNull Extension extension, @NotNull Runnable task, long delay, long period) throws IllegalArgumentException;

    /**
     * <b>Asynchronous tasks should never access any API in Bukkit. Great care
     * should be taken to assure the thread-safety of asynchronous tasks.</b>
     * <p>
     * Returns a task that will repeatedly run asynchronously until cancelled,
     * starting after the specified number of server ticks.
     *
     * @param extension the reference to the extension scheduling task
     * @param task      the task to be run
     * @param delay     the ticks to wait before running the task for the first
     *                  time
     * @param period    the ticks to wait between runs
     * @throws IllegalArgumentException if extension is null
     * @throws IllegalArgumentException if task is null
     */
    void runTaskTimerAsynchronously(@NotNull Extension extension, @NotNull Consumer<ExtensionTask> task, long delay, long period) throws IllegalArgumentException;

    /**
     * @param extension the reference to the extension scheduling task
     * @param task      the task to be run
     * @param delay     the ticks to wait before running the task for the first
     *                  time
     * @param period    the ticks to wait between runs
     * @return a ExtensionTask that contains the id number
     * @throws IllegalArgumentException if extension is null
     * @throws IllegalArgumentException if task is null
     * @deprecated Use {@link ExtensionRunnable#runTaskTimerAsynchronously(Extension, long, long)}
     */
    @Deprecated
    @NotNull ExtensionTask runTaskTimerAsynchronously(@NotNull Extension extension, @NotNull ExtensionRunnable task, long delay, long period) throws IllegalArgumentException;
}

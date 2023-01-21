package com.georgev22.library.scheduler;

import com.georgev22.library.scheduler.interfaces.Scheduler;
import com.georgev22.library.scheduler.interfaces.Task;
import org.jetbrains.annotations.NotNull;

public abstract class SchedulerRunnable implements Runnable {

    private Task task;

    /**
     * Returns true if this task has been cancelled.
     *
     * @return true if the task has been cancelled
     * @throws IllegalStateException if task was not scheduled yet
     */
    public synchronized boolean isCancelled() throws IllegalStateException {
        checkScheduled();
        return task.isCancelled();
    }

    /**
     * Attempts to cancel this task.
     *
     * @throws IllegalStateException if task was not scheduled yet
     */
    public synchronized void cancel() throws IllegalStateException {
        SchedulerManager.getScheduler().cancelTask(getTaskId());
    }

    /**
     * Schedules this in the Bukkit scheduler to run on next tick.
     *
     * @param clazz the reference to the clazz, scheduling task
     * @return a Task that contains the id number
     * @throws IllegalArgumentException if clazz, is null
     * @throws IllegalStateException    if this was already scheduled
     * @see com.georgev22.library.scheduler.interfaces.Scheduler#runTask(Class, Runnable)
     */
    @NotNull
    public synchronized Task runTask(@NotNull Class<?> clazz) throws IllegalArgumentException, IllegalStateException {
        checkNotYetScheduled();
        return setupTask(SchedulerManager.getScheduler().runTask(clazz, (Runnable) this));
    }

    /**
     * <b>Asynchronous tasks should never access any API in Bukkit. Great care
     * should be taken to assure the thread-safety of asynchronous tasks.</b>
     * <p>
     * Schedules this in the Bukkit scheduler to run asynchronously.
     *
     * @param clazz the reference to the clazz, scheduling task
     * @return a Task that contains the id number
     * @throws IllegalArgumentException if clazz, is null
     * @throws IllegalStateException    if this was already scheduled
     * @see com.georgev22.library.scheduler.interfaces.Scheduler#runTaskAsynchronously(Class, Runnable)
     */
    @NotNull
    public synchronized Task runTaskAsynchronously(@NotNull Class<?> clazz) throws IllegalArgumentException, IllegalStateException {
        checkNotYetScheduled();
        return setupTask(SchedulerManager.getScheduler().runTaskAsynchronously(clazz, (Runnable) this));
    }

    /**
     * Schedules this to run after the specified number of server ticks.
     *
     * @param clazz the reference to the clazz, scheduling task
     * @param delay the ticks to wait before running the task
     * @return a Task that contains the id number
     * @throws IllegalArgumentException if clazz, is null
     * @throws IllegalStateException    if this was already scheduled
     * @see com.georgev22.library.scheduler.interfaces.Scheduler#runTaskLater(Class, Runnable, long)
     */
    @NotNull
    public synchronized Task runTaskLater(@NotNull Class<?> clazz, long delay) throws IllegalArgumentException, IllegalStateException {
        checkNotYetScheduled();
        return setupTask(SchedulerManager.getScheduler().runTaskLater(clazz, (Runnable) this, delay));
    }

    /**
     * <b>Asynchronous tasks should never access any API in Bukkit. Great care
     * should be taken to assure the thread-safety of asynchronous tasks.</b>
     * <p>
     * Schedules this to run asynchronously after the specified number of
     * server ticks.
     *
     * @param clazz the reference to the clazz, scheduling task
     * @param delay the ticks to wait before running the task
     * @return a Task that contains the id number
     * @throws IllegalArgumentException if clazz, is null
     * @throws IllegalStateException    if this was already scheduled
     * @see com.georgev22.library.scheduler.interfaces.Scheduler#runTaskLaterAsynchronously(Class, Runnable, long)
     */
    @NotNull
    public synchronized Task runTaskLaterAsynchronously(@NotNull Class<?> clazz, long delay) throws IllegalArgumentException, IllegalStateException {
        checkNotYetScheduled();
        return setupTask(SchedulerManager.getScheduler().runTaskLaterAsynchronously(clazz, (Runnable) this, delay));
    }

    /**
     * Schedules this to repeatedly run until cancelled, starting after the
     * specified number of server ticks.
     *
     * @param clazz  the reference to the clazz, scheduling task
     * @param delay  the ticks to wait before running the task
     * @param period the ticks to wait between runs
     * @return a Task that contains the id number
     * @throws IllegalArgumentException if clazz, is null
     * @throws IllegalStateException    if this was already scheduled
     * @see com.georgev22.library.scheduler.interfaces.Scheduler#runTaskTimer(Class, Runnable, long, long)
     */
    @NotNull
    public synchronized Task runTaskTimer(@NotNull Class<?> clazz, long delay, long period) throws IllegalArgumentException, IllegalStateException {
        checkNotYetScheduled();
        return setupTask(SchedulerManager.getScheduler().runTaskTimer(clazz, (Runnable) this, delay, period));
    }

    /**
     * <b>Asynchronous tasks should never access any API in Bukkit. Great care
     * should be taken to assure the thread-safety of asynchronous tasks.</b>
     * <p>
     * Schedules this to repeatedly run asynchronously until cancelled,
     * starting after the specified number of server ticks.
     *
     * @param clazz  the reference to the clazz, scheduling task
     * @param delay  the ticks to wait before running the task for the first
     *               time
     * @param period the ticks to wait between runs
     * @return a Task that contains the id number
     * @throws IllegalArgumentException if clazz, is null
     * @throws IllegalStateException    if this was already scheduled
     * @see Scheduler#runTaskTimerAsynchronously(Class, Runnable, long,
     * long)
     */
    @NotNull
    public synchronized Task runTaskTimerAsynchronously(@NotNull Class<?> clazz, long delay, long period) throws IllegalArgumentException, IllegalStateException {
        checkNotYetScheduled();
        return setupTask(SchedulerManager.getScheduler().runTaskTimerAsynchronously(clazz, (Runnable) this, delay, period));
    }

    /**
     * Gets the task id for this runnable.
     *
     * @return the task id that this runnable was scheduled as
     * @throws IllegalStateException if task was not scheduled yet
     */
    public synchronized int getTaskId() throws IllegalStateException {
        checkScheduled();
        return task.getTaskId();
    }

    private void checkScheduled() {
        if (task == null) {
            throw new IllegalStateException("Not scheduled yet");
        }
    }

    private void checkNotYetScheduled() {
        if (task != null) {
            throw new IllegalStateException("Already scheduled as " + task.getTaskId());
        }
    }

    @NotNull
    private Task setupTask(@NotNull final Task task) {
        this.task = task;
        return task;
    }

}

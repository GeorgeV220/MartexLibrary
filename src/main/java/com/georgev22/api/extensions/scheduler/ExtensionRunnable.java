package com.georgev22.api.extensions.scheduler;

import com.georgev22.api.extensions.Extension;
import com.georgev22.api.extensions.ExtensionManager;
import com.georgev22.api.extensions.scheduler.interfaces.ExtensionScheduler;
import com.georgev22.api.extensions.scheduler.interfaces.ExtensionTask;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

public abstract class ExtensionRunnable implements Runnable {

    private ExtensionTask task;

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
        Bukkit.getScheduler().cancelTask(getTaskId());
    }

    /**
     * Schedules this in the Bukkit scheduler to run on next tick.
     *
     * @param extension the reference to the extension scheduling task
     * @return a ExtensionTask that contains the id number
     * @throws IllegalArgumentException if extension is null
     * @throws IllegalStateException    if this was already scheduled
     * @see ExtensionScheduler#runTask(Extension, Runnable)
     */
    @NotNull
    public synchronized ExtensionTask runTask(@NotNull Extension extension) throws IllegalArgumentException, IllegalStateException {
        checkNotYetScheduled();
        return setupTask(ExtensionManager.getScheduler().runTask(extension, (Runnable) this));
    }

    /**
     * <b>Asynchronous tasks should never access any API in Bukkit. Great care
     * should be taken to assure the thread-safety of asynchronous tasks.</b>
     * <p>
     * Schedules this in the Bukkit scheduler to run asynchronously.
     *
     * @param extension the reference to the extension scheduling task
     * @return a ExtensionTask that contains the id number
     * @throws IllegalArgumentException if extension is null
     * @throws IllegalStateException    if this was already scheduled
     * @see ExtensionScheduler#runTaskAsynchronously(Extension, Runnable)
     */
    @NotNull
    public synchronized ExtensionTask runTaskAsynchronously(@NotNull Extension extension) throws IllegalArgumentException, IllegalStateException {
        checkNotYetScheduled();
        return setupTask(ExtensionManager.getScheduler().runTaskAsynchronously(extension, (Runnable) this));
    }

    /**
     * Schedules this to run after the specified number of server ticks.
     *
     * @param extension the reference to the extension scheduling task
     * @param delay     the ticks to wait before running the task
     * @return a ExtensionTask that contains the id number
     * @throws IllegalArgumentException if extension is null
     * @throws IllegalStateException    if this was already scheduled
     * @see ExtensionScheduler#runTaskLater(Extension, Runnable, long)
     */
    @NotNull
    public synchronized ExtensionTask runTaskLater(@NotNull Extension extension, long delay) throws IllegalArgumentException, IllegalStateException {
        checkNotYetScheduled();
        return setupTask(ExtensionManager.getScheduler().runTaskLater(extension, (Runnable) this, delay));
    }

    /**
     * <b>Asynchronous tasks should never access any API in Bukkit. Great care
     * should be taken to assure the thread-safety of asynchronous tasks.</b>
     * <p>
     * Schedules this to run asynchronously after the specified number of
     * server ticks.
     *
     * @param extension the reference to the extension scheduling task
     * @param delay     the ticks to wait before running the task
     * @return a ExtensionTask that contains the id number
     * @throws IllegalArgumentException if extension is null
     * @throws IllegalStateException    if this was already scheduled
     * @see ExtensionScheduler#runTaskLaterAsynchronously(Extension, Runnable, long)
     */
    @NotNull
    public synchronized ExtensionTask runTaskLaterAsynchronously(@NotNull Extension extension, long delay) throws IllegalArgumentException, IllegalStateException {
        checkNotYetScheduled();
        return setupTask(ExtensionManager.getScheduler().runTaskLaterAsynchronously(extension, (Runnable) this, delay));
    }

    /**
     * Schedules this to repeatedly run until cancelled, starting after the
     * specified number of server ticks.
     *
     * @param extension the reference to the extension scheduling task
     * @param delay     the ticks to wait before running the task
     * @param period    the ticks to wait between runs
     * @return a ExtensionTask that contains the id number
     * @throws IllegalArgumentException if extension is null
     * @throws IllegalStateException    if this was already scheduled
     * @see ExtensionScheduler#runTaskTimer(Extension, Runnable, long, long)
     */
    @NotNull
    public synchronized ExtensionTask runTaskTimer(@NotNull Extension extension, long delay, long period) throws IllegalArgumentException, IllegalStateException {
        checkNotYetScheduled();
        return setupTask(ExtensionManager.getScheduler().runTaskTimer(extension, (Runnable) this, delay, period));
    }

    /**
     * <b>Asynchronous tasks should never access any API in Bukkit. Great care
     * should be taken to assure the thread-safety of asynchronous tasks.</b>
     * <p>
     * Schedules this to repeatedly run asynchronously until cancelled,
     * starting after the specified number of server ticks.
     *
     * @param extension the reference to the extension scheduling task
     * @param delay     the ticks to wait before running the task for the first
     *                  time
     * @param period    the ticks to wait between runs
     * @return a ExtensionTask that contains the id number
     * @throws IllegalArgumentException if extension is null
     * @throws IllegalStateException    if this was already scheduled
     * @see ExtensionScheduler#runTaskTimerAsynchronously(Extension, Runnable, long,
     * long)
     */
    @NotNull
    public synchronized ExtensionTask runTaskTimerAsynchronously(@NotNull Extension extension, long delay, long period) throws IllegalArgumentException, IllegalStateException {
        checkNotYetScheduled();
        return setupTask(ExtensionManager.getScheduler().runTaskTimerAsynchronously(extension, (Runnable) this, delay, period));
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
    private ExtensionTask setupTask(@NotNull final ExtensionTask task) {
        this.task = task;
        return task;
    }

}

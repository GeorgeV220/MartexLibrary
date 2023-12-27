package com.georgev22.library.minecraft.scheduler;

import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public abstract class SchedulerRunnable implements Runnable {

    private SchedulerTask task;

    private final MinecraftScheduler minecraftScheduler;

    public SchedulerRunnable(MinecraftScheduler minecraftScheduler) {
        this.minecraftScheduler = minecraftScheduler;
    }


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
        task.cancel();
    }

    /**
     * Schedules this in the Bukkit scheduler to run on next tick.
     *
     * @param javaPlugin The JavaPlugin associated with this task.
     * @return a Task that contains the id number
     * @throws IllegalArgumentException if clazz, is null
     * @throws IllegalStateException    if this was already scheduled
     */
    @NotNull
    public synchronized SchedulerTask runTask(JavaPlugin javaPlugin) throws IllegalArgumentException, IllegalStateException {
        checkNotYetScheduled();
        return setupTask(this.minecraftScheduler.runTask(javaPlugin, this));
    }

    /**
     * <b>Asynchronous tasks should never access any API in Bukkit. Great care
     * should be taken to assure the thread-safety of asynchronous tasks.</b>
     * <p>
     * Schedules this in the Bukkit scheduler to run asynchronously.
     *
     * @param javaPlugin The JavaPlugin associated with this task.
     * @return a Task that contains the id number
     * @throws IllegalArgumentException if clazz, is null
     * @throws IllegalStateException    if this was already scheduled
     */
    @NotNull
    public synchronized SchedulerTask runTaskAsynchronously(@NotNull JavaPlugin javaPlugin) throws IllegalArgumentException, IllegalStateException {
        checkNotYetScheduled();
        return setupTask(this.minecraftScheduler.runAsyncTask(javaPlugin, this));
    }

    /**
     * Schedules this to run after the specified number of server ticks.
     *
     * @param javaPlugin The JavaPlugin associated with this task.
     * @param delay      the ticks to wait before running the task
     * @return a Task that contains the id number
     * @throws IllegalArgumentException if clazz, is null
     * @throws IllegalStateException    if this was already scheduled
     */
    @NotNull
    public synchronized SchedulerTask runTaskLater(@NotNull JavaPlugin javaPlugin, long delay) throws IllegalArgumentException, IllegalStateException {
        checkNotYetScheduled();
        return setupTask(this.minecraftScheduler.createDelayedTask(javaPlugin, this, delay));
    }

    /**
     * <b>Asynchronous tasks should never access any API in Bukkit. Great care
     * should be taken to assure the thread-safety of asynchronous tasks.</b>
     * <p>
     * Schedules this to run asynchronously after the specified number of
     * server ticks.
     *
     * @param javaPlugin The JavaPlugin associated with this task.
     * @param delay      the ticks to wait before running the task
     * @return a Task that contains the id number
     * @throws IllegalArgumentException if clazz, is null
     * @throws IllegalStateException    if this was already scheduled
     */
    @NotNull
    public synchronized SchedulerTask runTaskLaterAsynchronously(@NotNull JavaPlugin javaPlugin, long delay) throws IllegalArgumentException, IllegalStateException {
        checkNotYetScheduled();
        return setupTask(this.minecraftScheduler.createAsyncDelayedTask(javaPlugin, this, delay));
    }

    /**
     * Schedules this to repeatedly run until cancelled, starting after the
     * specified number of server ticks.
     *
     * @param javaPlugin The JavaPlugin associated with this task.
     * @param delay      the ticks to wait before running the task
     * @param period     the ticks to wait between runs
     * @return a Task that contains the id number
     * @throws IllegalArgumentException if clazz, is null
     * @throws IllegalStateException    if this was already scheduled
     */
    @NotNull
    public synchronized SchedulerTask runTaskTimer(@NotNull JavaPlugin javaPlugin, long delay, long period) throws IllegalArgumentException, IllegalStateException {
        checkNotYetScheduled();
        return setupTask(this.minecraftScheduler.createRepeatingTask(javaPlugin, this, delay, period));
    }

    /**
     * <b>Asynchronous tasks should never access any API in Bukkit. Great care
     * should be taken to assure the thread-safety of asynchronous tasks.</b>
     * <p>
     * Schedules this to repeatedly run asynchronously until cancelled,
     * starting after the specified number of server ticks.
     *
     * @param javaPlugin The JavaPlugin associated with this task.
     * @param delay      the ticks to wait before running the task for the first
     *                   time
     * @param period     the ticks to wait between runs
     * @return a Task that contains the id number
     * @throws IllegalArgumentException if clazz, is null
     * @throws IllegalStateException    if this was already scheduled
     *                                  long)
     */
    @NotNull
    public synchronized SchedulerTask runTaskTimerAsynchronously(@NotNull JavaPlugin javaPlugin, long delay, long period) throws IllegalArgumentException, IllegalStateException {
        checkNotYetScheduled();
        return setupTask(this.minecraftScheduler.createAsyncRepeatingTask(javaPlugin, this, delay, period));
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
    private SchedulerTask setupTask(@NotNull final SchedulerTask task) {
        this.task = task;
        return task;
    }

}

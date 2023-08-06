package com.georgev22.library.minecraft.scheduler;

import org.bukkit.plugin.Plugin;

/**
 * A  non-extendable interface representing a scheduler for task scheduling and cancellation.
 */
public interface MinecraftScheduler {

    /**
     * Schedules a task to be executed synchronously on the server's main thread.
     *
     * @param plugin The plugin associated with this task.
     * @param task   The task to be executed.
     */
    void runTask(Plugin plugin, Runnable task);

    /**
     * Schedules a task to be executed asynchronously on a separate thread.
     * This method is suitable for handling time-consuming tasks to avoid blocking the main thread.
     *
     * @param plugin The plugin associated with this task.
     * @param task   The task to be executed.
     */
    void runAsyncTask(Plugin plugin, Runnable task);

    /**
     * Creates a delayed task that will run the specified `task` after the given `delay`.
     *
     * @param plugin The plugin associated with this task.
     * @param task   The task to be executed after the delay.
     * @param delay  The delay before the task is executed.
     */
    void createDelayedTask(Plugin plugin, Runnable task, long delay);

    /**
     * Creates a repeating task that will run the specified `task` after an initial `delay`,
     * and then repeatedly execute with the given `period` between executions.
     *
     * @param plugin The plugin associated with this task.
     * @param task   The task to be executed repeatedly.
     * @param delay  The delay before the first execution.
     * @param period The time between successive executions.
     */
    void createRepeatingTask(Plugin plugin, Runnable task, long delay, long period);

    /**
     * Schedules an asynchronous delayed task that will run the specified `task` after the given `delay`.
     * The task will be executed on a separate thread, making it suitable for non-blocking operations.
     *
     * @param plugin The plugin associated with this task.
     * @param task   The task to be executed after the delay.
     * @param delay  The delay before the task is executed.
     */
    void createAsyncDelayedTask(Plugin plugin, Runnable task, long delay);

    /**
     * Schedules an asynchronous repeating task that will run the specified `task` after an initial `delay`,
     * and then repeatedly execute with the given `period` between executions.
     * The task will be executed on a separate thread, making it suitable for non-blocking operations.
     *
     * @param plugin The plugin associated with this task.
     * @param task   The task to be executed repeatedly.
     * @param delay  The delay before the first execution.
     * @param period The time between successive executions.
     */
    void createAsyncRepeatingTask(Plugin plugin, Runnable task, long delay, long period);

    /**
     * Cancels all tasks associated with the given `plugin`.
     *
     * @param plugin The plugin whose tasks should be canceled.
     */
    void cancelTasks(Plugin plugin);

    /**
     * Gets the scheduler
     *
     * @return The scheduler
     */
    MinecraftScheduler getScheduler();

}

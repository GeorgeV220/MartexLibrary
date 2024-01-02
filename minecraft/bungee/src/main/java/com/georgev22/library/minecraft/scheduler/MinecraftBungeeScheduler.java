package com.georgev22.library.minecraft.scheduler;

import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public class MinecraftBungeeScheduler<T, Location, World, Chunk> implements MinecraftScheduler<Plugin, Location, World, Chunk> {

    /**
     * Schedules a task to be executed synchronously on the server's main thread.
     *
     * @param plugin The plugin associated with this task.
     * @param task   The task to be executed.
     */
    @Override
    public SchedulerTask runTask(@NotNull Plugin plugin, Runnable task) {
        return new BungeeSchedulerTask(plugin.getProxy().getScheduler().runAsync(plugin, task));
    }

    /**
     * Schedules a task to be executed asynchronously on a separate thread.
     * This method is suitable for handling time-consuming tasks to avoid blocking the main thread.
     *
     * @param plugin The plugin associated with this task.
     * @param task   The task to be executed.
     */
    @Override
    public SchedulerTask runAsyncTask(@NotNull Plugin plugin, Runnable task) {
        return new BungeeSchedulerTask(plugin.getProxy().getScheduler().runAsync(plugin, task));
    }

    /**
     * Creates a delayed task that will run the specified `task` after the given `delay`.
     *
     * @param plugin The plugin associated with this task.
     * @param task   The task to be executed after the delay.
     * @param delay  The delay before the task is executed.
     */
    @Override
    public SchedulerTask createDelayedTask(@NotNull Plugin plugin, Runnable task, long delay) {
        return new BungeeSchedulerTask(plugin.getProxy().getScheduler().schedule(plugin, task, (delay / 20), TimeUnit.SECONDS));
    }

    /**
     * Creates a repeating task that will run the specified `task` after an initial `delay`,
     * and then repeatedly execute with the given `period` between executions.
     *
     * @param plugin The plugin associated with this task.
     * @param task   The task to be executed repeatedly.
     * @param delay  The delay before the first execution.
     * @param period The time between successive executions.
     */
    @Override
    public SchedulerTask createRepeatingTask(@NotNull Plugin plugin, Runnable task, long delay, long period) {
        return new BungeeSchedulerTask(plugin.getProxy().getScheduler().schedule(plugin, task, (delay / 20), (period / 20), TimeUnit.SECONDS));
    }

    /**
     * Schedules an asynchronous delayed task that will run the specified `task` after the given `delay`.
     * The task will be executed on a separate thread, making it suitable for non-blocking operations.
     *
     * @param plugin The plugin associated with this task.
     * @param task   The task to be executed after the delay.
     * @param delay  The delay before the task is executed.
     */
    @Override
    public SchedulerTask createAsyncDelayedTask(@NotNull Plugin plugin, Runnable task, long delay) {
        return new BungeeSchedulerTask(plugin.getProxy().getScheduler().schedule(plugin, task, (delay / 20), TimeUnit.SECONDS));
    }

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
    @Override
    public SchedulerTask createAsyncRepeatingTask(@NotNull Plugin plugin, Runnable task, long delay, long period) {
        return new BungeeSchedulerTask(plugin.getProxy().getScheduler().schedule(plugin, task, (delay / 20), (period / 20), TimeUnit.SECONDS));
    }

    /**
     * Creates a delayed task for a specific world and chunk.
     *
     * @param plugin The plugin that owns this task.
     * @param task   The runnable task to execute.
     * @param world  The world in which the chunk is located.
     * @param chunk  The chunk in which the task will be executed.
     * @param delay  The delay in ticks before the task is executed.
     * @return A SchedulerTask representing the created task.
     */
    @Override
    public SchedulerTask createDelayedTaskForWorld(@NotNull Plugin plugin, Runnable task, World world, @NotNull Chunk chunk, long delay) {
        return new BungeeSchedulerTask(plugin.getProxy().getScheduler().schedule(plugin, task, (delay / 20), TimeUnit.SECONDS));
    }

    /**
     * Creates a delayed task for a specific location.
     *
     * @param plugin   The plugin that owns this task.
     * @param task     The runnable task to execute.
     * @param location The location at which the task will be executed.
     * @param delay    The delay in ticks before the task is executed.
     * @return A SchedulerTask representing the created task.
     */
    @Override
    public SchedulerTask createDelayedForLocation(@NotNull Plugin plugin, Runnable task, Location location, long delay) {
        return new BungeeSchedulerTask(plugin.getProxy().getScheduler().schedule(plugin, task, (delay / 20), TimeUnit.SECONDS));
    }

    /**
     * Creates a task for a specific world and chunk.
     *
     * @param plugin The plugin that owns this task.
     * @param task   The runnable task to execute.
     * @param world  The world in which the chunk is located.
     * @param chunk  The chunk in which the task will be executed.
     * @return A SchedulerTask representing the created task.
     */
    @Override
    public SchedulerTask createTaskForWorld(@NotNull Plugin plugin, Runnable task, World world, @NotNull Chunk chunk) {
        return new BungeeSchedulerTask(plugin.getProxy().getScheduler().runAsync(plugin, task));
    }

    /**
     * Creates a task for a specific location.
     *
     * @param plugin   The plugin that owns this task.
     * @param task     The runnable task to execute.
     * @param location The location at which the task will be executed.
     * @return A SchedulerTask representing the created task.
     */
    @Override
    public SchedulerTask createTaskForLocation(@NotNull Plugin plugin, Runnable task, Location location) {
        return new BungeeSchedulerTask(plugin.getProxy().getScheduler().runAsync(plugin, task));
    }

    /**
     * Creates a repeating task for a specific world and chunk.
     *
     * @param plugin The plugin that owns this task.
     * @param task   The runnable task to execute.
     * @param world  The world in which the chunk is located.
     * @param chunk  The chunk in which the task will be executed.
     * @param delay  The initial delay in ticks before the first execution.
     * @param period The period in ticks between consecutive executions.
     * @return A SchedulerTask representing the created task.
     */
    @Override
    public SchedulerTask createRepeatingTaskForWorld(@NotNull Plugin plugin, Runnable task, World world, @NotNull Chunk chunk, long delay, long period) {
        return new BungeeSchedulerTask(plugin.getProxy().getScheduler().schedule(plugin, task, (delay / 20), (period / 20), TimeUnit.SECONDS));
    }

    /**
     * Creates a repeating task for a specific location.
     *
     * @param plugin   The plugin that owns this task.
     * @param task     The runnable task to execute.
     * @param location The location at which the task will be executed.
     * @param delay    The initial delay in ticks before the first execution.
     * @param period   The period in ticks between consecutive executions.
     * @return A SchedulerTask representing the created task.
     */
    @Override
    public SchedulerTask createRepeatingTaskForLocation(@NotNull Plugin plugin, Runnable task, Location location, long delay, long period) {
        return new BungeeSchedulerTask(plugin.getProxy().getScheduler().schedule(plugin, task, (delay / 20), (period / 20), TimeUnit.SECONDS));
    }

    /**
     * Cancels all tasks associated with the given `plugin`.
     *
     * @param plugin The plugin whose tasks should be canceled.
     */
    @Override
    public void cancelTasks(@NotNull Plugin plugin) {
        plugin.getProxy().getScheduler().cancel(plugin);
    }

    /**
     * Gets the scheduler
     *
     * @return The scheduler
     */
    @Override
    public MinecraftScheduler<Plugin, Location, World, Chunk> getScheduler() {
        return null;
    }

    private static class BungeeSchedulerTask implements SchedulerTask {

        private final ScheduledTask bungeeTask;

        public BungeeSchedulerTask(ScheduledTask scheduledTask) {
            this.bungeeTask = scheduledTask;
        }


        @Override
        public void cancel() {
            this.bungeeTask.cancel();
        }

        @Override
        public boolean isCancelled() {
            return false;
        }

        @Override
        public int getTaskId() {
            return this.bungeeTask.getId();
        }
    }
}

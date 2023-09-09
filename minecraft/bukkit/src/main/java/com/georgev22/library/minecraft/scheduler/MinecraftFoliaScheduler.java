package com.georgev22.library.minecraft.scheduler;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.ApiStatus;

import java.util.concurrent.TimeUnit;

@ApiStatus.NonExtendable
public class MinecraftFoliaScheduler implements MinecraftScheduler {

    /**
     * Schedules a task to be executed synchronously on the server's main thread.
     *
     * @param plugin The plugin associated with this task.
     * @param task   The task to be executed.
     */
    @Override
    public SchedulerTask runTask(Plugin plugin, Runnable task) {
        return new FoliaSchedulerTask(Bukkit.getGlobalRegionScheduler().run(plugin, (scheduledTask) -> task.run()));
    }

    /**
     * Schedules a task to be executed asynchronously on a separate thread.
     * This method is suitable for handling time-consuming tasks to avoid blocking the main thread.
     *
     * @param plugin The plugin associated with this task.
     * @param task   The task to be executed.
     */
    @Override
    public SchedulerTask runAsyncTask(Plugin plugin, Runnable task) {
        return new FoliaSchedulerTask(Bukkit.getAsyncScheduler().runNow(plugin, (scheduledTask) -> task.run()));
    }

    /**
     * Creates a delayed task that will run the specified `task` after the given `delay`.
     *
     * @param plugin The plugin associated with this task.
     * @param task   The task to be executed after the delay.
     * @param delay  The delay before the task is executed.
     */
    @Override
    public SchedulerTask createDelayedTask(Plugin plugin, Runnable task, long delay) {
        return new FoliaSchedulerTask(Bukkit.getGlobalRegionScheduler().runDelayed(plugin, (scheduledTask) -> task.run(), delay));
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
    public SchedulerTask createRepeatingTask(Plugin plugin, Runnable task, long delay, long period) {
        return new FoliaSchedulerTask(Bukkit.getGlobalRegionScheduler().runAtFixedRate(plugin, (scheduledTask) -> task.run(), delay, period));
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
    public SchedulerTask createAsyncDelayedTask(Plugin plugin, Runnable task, long delay) {
        return new FoliaSchedulerTask(Bukkit.getAsyncScheduler().runDelayed(plugin, (scheduledTask) -> task.run(), (delay / 20), TimeUnit.SECONDS));
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
    public SchedulerTask createAsyncRepeatingTask(Plugin plugin, Runnable task, long delay, long period) {
        return new FoliaSchedulerTask(Bukkit.getAsyncScheduler().runAtFixedRate(plugin, (scheduledTask) -> task.run(), (delay / 20), (period / 20), TimeUnit.SECONDS));
    }

    /**
     * Cancels all tasks associated with the given `plugin`.
     *
     * @param plugin The plugin whose tasks should be canceled.
     */
    @Override
    public void cancelTasks(Plugin plugin) {
        Bukkit.getGlobalRegionScheduler().cancelTasks(plugin);
    }

    /**
     * Gets the scheduler instance for this class.
     * Since this class is already a subclass of {@link MinecraftScheduler},
     * it returns the current instance as the scheduler.
     *
     * @return The scheduler instance for this class (i.e., this).
     */
    @Override
    public MinecraftScheduler getScheduler() {
        return this;
    }

    public static class FoliaSchedulerTask implements SchedulerTask {

        private final ScheduledTask scheduledTask;

        public FoliaSchedulerTask(ScheduledTask scheduledTask) {
            this.scheduledTask = scheduledTask;
        }

        @Override
        public void cancel() {
            scheduledTask.cancel();
        }
    }
}

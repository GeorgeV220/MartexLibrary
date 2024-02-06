package com.georgev22.library.minecraft.scheduler;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

@ApiStatus.NonExtendable
public class MinecraftFoliaScheduler implements MinecraftScheduler<Plugin, Location, World, Chunk, Entity> {

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
     * Creates a delayed task for a specific world and chunk.
     *
     * @param plugin The plugin that owns this task.
     * @param task   The runnable task to execute.
     * @param world  The world in which the chunk is located.
     * @param chunk  The chunk in which the task will be executed.
     * @param delay  The delay in ticks before the task is executed.
     * @return A SchedulerTask representing the created task.
     */
    public SchedulerTask createDelayedTaskForWorld(Plugin plugin, Runnable task, World world, @NotNull Chunk chunk, long delay) {
        return new FoliaSchedulerTask(Bukkit.getRegionScheduler().runDelayed(plugin, world, chunk.getX(), chunk.getZ(), (scheduledTask) -> task.run(), delay));
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
    public SchedulerTask createDelayedForLocation(Plugin plugin, Runnable task, Location location, long delay) {
        return new FoliaSchedulerTask(Bukkit.getRegionScheduler().runDelayed(plugin, location, (scheduledTask) -> task.run(), delay));
    }

    /**
     * Creates a delayed task for a specific location.
     *
     * @param plugin  The plugin that owns this task.
     * @param task    The runnable task to execute.
     * @param retired – Retire callback to run if the entity is retired before the run callback can be invoked, may be null.
     * @param entity  The entity in which the task will be executed.
     * @param delay   The delay in ticks before the task is executed.
     * @return A SchedulerTask representing the created task.
     */
    @Override
    public SchedulerTask createDelayedForEntity(Plugin plugin, Runnable task, Runnable retired, @NotNull Entity entity, long delay) {
        return new FoliaSchedulerTask(entity.getScheduler().runDelayed(plugin, (scheduledTask) -> task.run(), retired, delay));
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
    public SchedulerTask createTaskForWorld(Plugin plugin, Runnable task, World world, @NotNull Chunk chunk) {
        return new FoliaSchedulerTask(Bukkit.getRegionScheduler().run(plugin, world, chunk.getX(), chunk.getZ(), (scheduledTask) -> task.run()));
    }

    /**
     * Creates a task for a specific location.
     *
     * @param plugin   The plugin that owns this task.
     * @param task     The runnable task to execute.
     * @param location The location at which the task will be executed.
     * @return A SchedulerTask representing the created task.
     */
    public SchedulerTask createTaskForLocation(Plugin plugin, Runnable task, Location location) {
        return new FoliaSchedulerTask(Bukkit.getRegionScheduler().run(plugin, location, (scheduledTask) -> task.run()));
    }

    /**
     * Creates a task for a specific location.
     *
     * @param plugin  The plugin that owns this task.
     * @param task    The runnable task to execute.
     * @param retired Retire callback to run if the entity is retired before the run callback can be invoked, may be null.
     * @param entity  The entity in which the task will be executed.
     * @return A SchedulerTask representing the created task.
     */
    @Override
    public SchedulerTask createTaskForEntity(Plugin plugin, Runnable task, Runnable retired, @NotNull Entity entity) {
        return new FoliaSchedulerTask(entity.getScheduler().run(plugin, (scheduledTask) -> task.run(), retired));
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
    public SchedulerTask createRepeatingTaskForWorld(Plugin plugin, Runnable task, World world, @NotNull Chunk chunk, long delay, long period) {
        return new FoliaSchedulerTask(Bukkit.getRegionScheduler().runAtFixedRate(plugin, world, chunk.getX(), chunk.getZ(), (scheduledTask) -> task.run(), delay, period));
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
    public SchedulerTask createRepeatingTaskForLocation(Plugin plugin, Runnable task, Location location, long delay, long period) {
        return new FoliaSchedulerTask(Bukkit.getRegionScheduler().runAtFixedRate(plugin, location, (scheduledTask) -> task.run(), delay, period));
    }

    /**
     * Creates a repeating task for a specific location.
     *
     * @param plugin  The plugin that owns this task.
     * @param task    The runnable task to execute.
     * @param retired Retire callback to run if the entity is retired before the run callback can be invoked, may be null.
     * @param entity  The entity in which the task will be executed.
     * @param delay   The initial delay in ticks before the first execution.
     * @param period  The period in ticks between consecutive executions.
     * @return A SchedulerTask representing the created task.
     */
    @Override
    public SchedulerTask createRepeatingTaskForEntity(Plugin plugin, Runnable task, Runnable retired, @NotNull Entity entity, long delay, long period) {
        return new FoliaSchedulerTask(entity.getScheduler().runAtFixedRate(plugin, (scheduledTask) -> task.run(), retired, delay, period));
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
    public MinecraftScheduler<Plugin, Location, World, Chunk, Entity> getScheduler() {
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

        @Override
        public boolean isCancelled() {
            return scheduledTask.isCancelled();
        }

        @Override
        public int getTaskId() {
            return 0;
        }
    }
}

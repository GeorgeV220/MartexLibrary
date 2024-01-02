package com.georgev22.library.minecraft.scheduler;

import com.georgev22.library.minecraft.VelocityMinecraftUtils;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.scheduler.ScheduledTask;
import com.velocitypowered.api.scheduler.TaskStatus;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class VelocityMinecraftScheduler<T, Location, World, Chunk> implements MinecraftScheduler<Object, Location, World, Chunk> {

    private final ProxyServer proxyServer = VelocityMinecraftUtils.getServer();

    private static final List<SchedulerTask> tasks = new ArrayList<>();

    /**
     * Schedules a task to be executed synchronously on the server's main thread.
     *
     * @param o    The plugin associated with this task.
     * @param task The task to be executed.
     */
    @Override
    public SchedulerTask runTask(Object o, Runnable task) {
        return new VelocitySchedulerTask(proxyServer.getScheduler().buildTask(o, task).schedule());
    }

    /**
     * Schedules a task to be executed asynchronously on a separate thread.
     * This method is suitable for handling time-consuming tasks to avoid blocking the main thread.
     *
     * @param o    The plugin associated with this task.
     * @param task The task to be executed.
     */
    @Override
    public SchedulerTask runAsyncTask(Object o, Runnable task) {
        return new VelocitySchedulerTask(proxyServer.getScheduler().buildTask(o, task).schedule());
    }

    /**
     * Creates a delayed task that will run the specified `task` after the given `delay`.
     *
     * @param o     The plugin associated with this task.
     * @param task  The task to be executed after the delay.
     * @param delay The delay before the task is executed.
     */
    @Override
    public SchedulerTask createDelayedTask(Object o, Runnable task, long delay) {
        return new VelocitySchedulerTask(proxyServer.getScheduler().buildTask(o, task).delay((delay / 20), TimeUnit.SECONDS).schedule());
    }

    /**
     * Creates a repeating task that will run the specified `task` after an initial `delay`,
     * and then repeatedly execute with the given `period` between executions.
     *
     * @param o      The plugin associated with this task.
     * @param task   The task to be executed repeatedly.
     * @param delay  The delay before the first execution.
     * @param period The time between successive executions.
     */
    @Override
    public SchedulerTask createRepeatingTask(Object o, Runnable task, long delay, long period) {
        return new VelocitySchedulerTask(proxyServer.getScheduler().buildTask(o, task).delay((delay / 20), TimeUnit.SECONDS).repeat((period / 20), TimeUnit.SECONDS).schedule());
    }

    /**
     * Schedules an asynchronous delayed task that will run the specified `task` after the given `delay`.
     * The task will be executed on a separate thread, making it suitable for non-blocking operations.
     *
     * @param o     The plugin associated with this task.
     * @param task  The task to be executed after the delay.
     * @param delay The delay before the task is executed.
     */
    @Override
    public SchedulerTask createAsyncDelayedTask(Object o, Runnable task, long delay) {
        return new VelocitySchedulerTask(proxyServer.getScheduler().buildTask(o, task).delay((delay / 20), TimeUnit.SECONDS).schedule());
    }

    /**
     * Schedules an asynchronous repeating task that will run the specified `task` after an initial `delay`,
     * and then repeatedly execute with the given `period` between executions.
     * The task will be executed on a separate thread, making it suitable for non-blocking operations.
     *
     * @param o      The plugin associated with this task.
     * @param task   The task to be executed repeatedly.
     * @param delay  The delay before the first execution.
     * @param period The time between successive executions.
     */
    @Override
    public SchedulerTask createAsyncRepeatingTask(Object o, Runnable task, long delay, long period) {
        return new VelocitySchedulerTask(proxyServer.getScheduler().buildTask(o, task).delay((delay / 20), TimeUnit.SECONDS).repeat((period / 20), TimeUnit.SECONDS).schedule());
    }

    /**
     * Creates a delayed task for a specific world and chunk.
     *
     * @param o     The plugin that owns this task.
     * @param task  The runnable task to execute.
     * @param world The world in which the chunk is located.
     * @param chunk The chunk in which the task will be executed.
     * @param delay The delay in ticks before the task is executed.
     * @return A SchedulerTask representing the created task.
     */
    @Override
    public SchedulerTask createDelayedTaskForWorld(Object o, Runnable task, World world, @NotNull Chunk chunk, long delay) {
        return new VelocitySchedulerTask(proxyServer.getScheduler().buildTask(o, task).delay((delay / 20), TimeUnit.SECONDS).schedule());
    }

    /**
     * Creates a delayed task for a specific location.
     *
     * @param o        The plugin that owns this task.
     * @param task     The runnable task to execute.
     * @param location The location at which the task will be executed.
     * @param delay    The delay in ticks before the task is executed.
     * @return A SchedulerTask representing the created task.
     */
    @Override
    public SchedulerTask createDelayedForLocation(Object o, Runnable task, Location location, long delay) {
        return new VelocitySchedulerTask(proxyServer.getScheduler().buildTask(o, task).delay((delay / 20), TimeUnit.SECONDS).schedule());
    }

    /**
     * Creates a task for a specific world and chunk.
     *
     * @param o     The plugin that owns this task.
     * @param task  The runnable task to execute.
     * @param world The world in which the chunk is located.
     * @param chunk The chunk in which the task will be executed.
     * @return A SchedulerTask representing the created task.
     */
    @Override
    public SchedulerTask createTaskForWorld(Object o, Runnable task, World world, @NotNull Chunk chunk) {
        return new VelocitySchedulerTask(proxyServer.getScheduler().buildTask(o, task).schedule());
    }

    /**
     * Creates a task for a specific location.
     *
     * @param o        The plugin that owns this task.
     * @param task     The runnable task to execute.
     * @param location The location at which the task will be executed.
     * @return A SchedulerTask representing the created task.
     */
    @Override
    public SchedulerTask createTaskForLocation(Object o, Runnable task, Location location) {
        return new VelocitySchedulerTask(proxyServer.getScheduler().buildTask(o, task).schedule());
    }

    /**
     * Creates a repeating task for a specific world and chunk.
     *
     * @param o      The plugin that owns this task.
     * @param task   The runnable task to execute.
     * @param world  The world in which the chunk is located.
     * @param chunk  The chunk in which the task will be executed.
     * @param delay  The initial delay in ticks before the first execution.
     * @param period The period in ticks between consecutive executions.
     * @return A SchedulerTask representing the created task.
     */
    @Override
    public SchedulerTask createRepeatingTaskForWorld(Object o, Runnable task, World world, @NotNull Chunk chunk, long delay, long period) {
        return new VelocitySchedulerTask(proxyServer.getScheduler().buildTask(o, task).delay((delay / 20), TimeUnit.SECONDS).repeat((period / 20), TimeUnit.SECONDS).schedule());
    }

    /**
     * Creates a repeating task for a specific location.
     *
     * @param o        The plugin that owns this task.
     * @param task     The runnable task to execute.
     * @param location The location at which the task will be executed.
     * @param delay    The initial delay in ticks before the first execution.
     * @param period   The period in ticks between consecutive executions.
     * @return A SchedulerTask representing the created task.
     */
    @Override
    public SchedulerTask createRepeatingTaskForLocation(Object o, Runnable task, Location location, long delay, long period) {
        return new VelocitySchedulerTask(proxyServer.getScheduler().buildTask(o, task).delay((delay / 20), TimeUnit.SECONDS).repeat((period / 20), TimeUnit.SECONDS).schedule());
    }

    /**
     * Cancels all tasks associated with the given `plugin`.
     *
     * @param o The plugin whose tasks should be canceled.
     */
    @Override
    public void cancelTasks(Object o) {
        new ArrayList<>(tasks).forEach(schedulerTask -> {
            tasks.remove(schedulerTask);
            if (schedulerTask.isCancelled()) {
                return;
            }
            schedulerTask.cancel();
        });
    }

    /**
     * Gets the scheduler
     *
     * @return The scheduler
     */
    @Override
    public MinecraftScheduler<Object, Location, World, Chunk> getScheduler() {
        return this;
    }

    @SuppressWarnings("ClassCanBeRecord")
    private static class VelocitySchedulerTask implements SchedulerTask {

        private final ScheduledTask task;

        public VelocitySchedulerTask(ScheduledTask task) {
            this.task = task;
            tasks.add(this);
        }


        @Override
        public void cancel() {
            this.task.cancel();
        }

        @Override
        public boolean isCancelled() {
            return this.task.status().equals(TaskStatus.CANCELLED) || this.task.status().equals(TaskStatus.FINISHED);
        }

        @Override
        public int getTaskId() {
            return 0;
        }
    }
}

package com.georgev22.library.scheduler.interfaces;

import org.jetbrains.annotations.NotNull;

/**
 * Represents a worker thread for the scheduler. This gives information about
 * the Thread object for the task, owner of the task and the taskId.
 * <p>
 * Workers are used to execute async tasks.
 */
public interface Worker {

    /**
     * Returns the taskId for the task being executed by this worker.
     *
     * @return Task id number
     */
    int getTaskId();

    /**
     * Returns the Class that owns this task.
     *
     * @return The Class that owns the task
     */
    @NotNull Class<?> getOwner();

    /**
     * Returns the thread for the worker.
     *
     * @return The Thread object for the worker
     */
    @NotNull Thread getThread();

}

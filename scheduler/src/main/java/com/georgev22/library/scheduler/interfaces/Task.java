package com.georgev22.library.scheduler.interfaces;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

/**
 * Represents a task being executed by the scheduler
 */
public interface Task extends Serializable {

    /**
     * Returns the taskId for the task.
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
     * Returns true if the Task is a sync task.
     *
     * @return true if the task is run by main thread
     */
    boolean isSync();

    /**
     * Returns true if this task has been cancelled.
     *
     * @return true if the task has been cancelled
     */
    boolean isCancelled();

    /**
     * Will attempt to cancel this task.
     */
    void cancel();
}

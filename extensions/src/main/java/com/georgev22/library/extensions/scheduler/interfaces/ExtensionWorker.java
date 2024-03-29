package com.georgev22.library.extensions.scheduler.interfaces;

import com.georgev22.library.extensions.Extension;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a worker thread for the scheduler. This gives information about
 * the Thread object for the task, owner of the task and the taskId.
 * <p>
 * Workers are used to execute async tasks.
 */
public interface ExtensionWorker {

    /**
     * Returns the taskId for the task being executed by this worker.
     *
     * @return Task id number
     */
    int getTaskId();

    /**
     * Returns the Extension that owns this task.
     *
     * @return The Extension that owns the task
     */
    @NotNull Extension getOwner();

    /**
     * Returns the thread for the worker.
     *
     * @return The Thread object for the worker
     */
    @NotNull Thread getThread();

}

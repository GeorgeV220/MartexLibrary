package com.georgev22.library.extensions.scheduler;

import com.georgev22.library.extensions.Extension;
import com.georgev22.library.extensions.Extensions;
import com.georgev22.library.extensions.scheduler.interfaces.ExtensionTask;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

class Task implements ExtensionTask, Runnable {

    private volatile Task next = null;
    public static final int ERROR = 0;
    public static final int NO_REPEATING = -1;
    public static final int CANCEL = -2;
    public static final int PROCESS_FOR_FUTURE = -3;
    public static final int DONE_FOR_FUTURE = -4;
    /**
     * -1 means no repeating <br>
     * -2 means cancel <br>
     * -3 means processing for Future <br>
     * -4 means done for Future <br>
     * Never 0 <br>
     * >0 means number of ticks to wait between each execution
     */
    private volatile long period;
    private long nextRun;
    private final Runnable rTask;
    private final Consumer<ExtensionTask> cTask;
    private final Extension extension;
    private final int id;
    private final long createdAt = System.nanoTime();

    Task() {
        this(null, null, Task.NO_REPEATING, Task.NO_REPEATING);
    }

    Task(final Object task) {
        this(null, task, Task.NO_REPEATING, Task.NO_REPEATING);
    }

    Task(final Extension extension, final Object task, final int id, final long period) {
        this.extension = extension;
        if (task instanceof Runnable) {
            this.rTask = (Runnable) task;
            this.cTask = null;
        } else if (task instanceof Consumer) {
            this.cTask = (Consumer<ExtensionTask>) task;
            this.rTask = null;
        } else if (task == null) {
            // Head or Future task
            this.rTask = null;
            this.cTask = null;
        } else {
            throw new AssertionError("Illegal task class " + task);
        }
        this.id = id;
        this.period = period;
    }

    @Override
    public final int getTaskId() {
        return id;
    }

    @Override
    public final @NotNull Extension getOwner() {
        return extension;
    }

    @Override
    public boolean isSync() {
        return true;
    }

    @Override
    public void run() {
        if (rTask != null) {
            rTask.run();
        } else {
            cTask.accept(this);
        }
    }

    long getCreatedAt() {
        return createdAt;
    }

    long getPeriod() {
        return period;
    }

    void setPeriod(long period) {
        this.period = period;
    }

    long getNextRun() {
        return nextRun;
    }

    void setNextRun(long nextRun) {
        this.nextRun = nextRun;
    }

    Task getNext() {
        return next;
    }

    void setNext(Task next) {
        this.next = next;
    }

    Class<?> getTaskClass() {
        return (rTask != null) ? rTask.getClass() : ((cTask != null) ? cTask.getClass() : null);
    }

    @Override
    public boolean isCancelled() {
        return (period == Task.CANCEL);
    }

    @Override
    public void cancel() {
        Extensions.getScheduler().cancelTask(id);
    }

    /**
     * This method properly sets the status to cancelled, synchronizing when required.
     *
     * @return false if it is a future task that has already begun execution, true otherwise
     */
    boolean cancel0() {
        setPeriod(Task.CANCEL);
        return true;
    }
}

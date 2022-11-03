package com.georgev22.api.scheduler;

import com.georgev22.api.scheduler.interfaces.Worker;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

class AsyncTask extends Task {

    private final LinkedList<Worker> workers = new LinkedList<>();
    private final Map<Integer, Task> runners;

    AsyncTask(final Map<Integer, Task> runners, final Class<?> clazz, final Object task, final int id, final long delay) {
        super(clazz, task, id, delay);
        this.runners = runners;
    }

    @Override
    public boolean isSync() {
        return false;
    }

    @Override
    public void run() {
        final Thread thread = Thread.currentThread();
        synchronized (workers) {
            if (getPeriod() == Task.CANCEL) {
                // Never continue running after cancelled.
                // Checking this with the lock is important!
                return;
            }
            workers.add(
                    new Worker() {
                        @Override
                        public @NotNull Thread getThread() {
                            return thread;
                        }

                        @Override
                        public int getTaskId() {
                            return AsyncTask.this.getTaskId();
                        }

                        @Override
                        public @NotNull Class<?> getOwner() {
                            return AsyncTask.this.getOwner();
                        }
                    });
        }
        Throwable thrown = null;
        try {
            super.run();
        } catch (final Throwable t) {
            thrown = t;
            throw new RuntimeException(
                    String.format(
                            "Class %s generated an exception while executing task %s",
                            getOwner().getSimpleName(),
                            getTaskId()),
                    t
            );
        } finally {
            // Cleanup is important for any async task, otherwise ghost tasks are everywhere
            synchronized (workers) {
                try {
                    final Iterator<Worker> workers = this.workers.iterator();
                    boolean removed = false;
                    while (workers.hasNext()) {
                        if (workers.next().getThread() == thread) {
                            workers.remove();
                            removed = true; // Don't throw exception
                            break;
                        }
                    }
                    if (!removed) {
                        throw new IllegalStateException(
                                String.format(
                                        "Unable to remove worker %s on task %s for %s",
                                        thread.getName(),
                                        getTaskId(),
                                        getOwner().getSimpleName()),
                                thrown); // We don't want to lose the original exception, if any
                    }
                } finally {
                    if (getPeriod() < 0 && workers.isEmpty()) {
                        // At this spot, we know we are the final async task being executed!
                        // Because we have the lock, nothing else is running or will run because delay < 0
                        runners.remove(getTaskId());
                    }
                }
            }
        }
    }

    LinkedList<Worker> getWorkers() {
        return workers;
    }

    @Override
    boolean cancel0() {
        synchronized (workers) {
            // Synchronizing here prevents race condition for a completing task
            setPeriod(Task.CANCEL);
            if (workers.isEmpty()) {
                runners.remove(getTaskId());
            }
        }
        return true;
    }
}

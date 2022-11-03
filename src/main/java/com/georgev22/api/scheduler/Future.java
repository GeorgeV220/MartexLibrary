package com.georgev22.api.scheduler;


import java.util.concurrent.*;

class Future<T> extends Task implements java.util.concurrent.Future<T> {

    private final Callable<T> callable;
    private T value;
    private Exception exception = null;

    Future(final Callable<T> callable, final Class<?> clazz, final int id) {
        super(clazz, null, id, Task.NO_REPEATING);
        this.callable = callable;
    }

    @Override
    public synchronized boolean cancel(final boolean mayInterruptIfRunning) {
        if (getPeriod() != Task.NO_REPEATING) {
            return false;
        }
        setPeriod(Task.CANCEL);
        return true;
    }

    @Override
    public boolean isDone() {
        final long period = this.getPeriod();
        return period != Task.NO_REPEATING && period != Task.PROCESS_FOR_FUTURE;
    }

    @Override
    public T get() throws CancellationException, InterruptedException, ExecutionException {
        try {
            return get(0, TimeUnit.MILLISECONDS);
        } catch (final TimeoutException e) {
            throw new Error(e);
        }
    }

    @Override
    public synchronized T get(long timeout, final TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        timeout = unit.toMillis(timeout);
        long period = this.getPeriod();
        long timestamp = timeout > 0 ? System.currentTimeMillis() : 0L;
        while (true) {
            if (period == Task.NO_REPEATING || period == Task.PROCESS_FOR_FUTURE) {
                this.wait(timeout);
                period = this.getPeriod();
                if (period == Task.NO_REPEATING || period == Task.PROCESS_FOR_FUTURE) {
                    if (timeout == 0L) {
                        continue;
                    }
                    timeout += timestamp - (timestamp = System.currentTimeMillis());
                    if (timeout > 0) {
                        continue;
                    }
                    throw new TimeoutException();
                }
            }
            if (period == Task.CANCEL) {
                throw new CancellationException();
            }
            if (period == Task.DONE_FOR_FUTURE) {
                if (exception == null) {
                    return value;
                }
                throw new ExecutionException(exception);
            }
            throw new IllegalStateException("Expected " + Task.NO_REPEATING + " to " + Task.DONE_FOR_FUTURE + ", got " + period);
        }
    }

    @Override
    public void run() {
        synchronized (this) {
            if (getPeriod() == Task.CANCEL) {
                return;
            }
            setPeriod(Task.PROCESS_FOR_FUTURE);
        }
        try {
            value = callable.call();
        } catch (final Exception e) {
            exception = e;
        } finally {
            synchronized (this) {
                setPeriod(Task.DONE_FOR_FUTURE);
                this.notifyAll();
            }
        }
    }

    @Override
    synchronized boolean cancel0() {
        if (getPeriod() != Task.NO_REPEATING) {
            return false;
        }
        setPeriod(Task.CANCEL);
        notifyAll();
        return true;
    }
}

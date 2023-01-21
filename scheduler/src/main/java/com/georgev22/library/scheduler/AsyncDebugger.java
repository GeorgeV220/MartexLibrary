package com.georgev22.library.scheduler;


class AsyncDebugger {
    private AsyncDebugger next = null;
    private final int expiry;
    private final Class<?> clazzT;
    private final Class<?> clazz;

    AsyncDebugger(final int expiry, final Class<?> clazzT, final Class<?> clazz) {
        this.expiry = expiry;
        this.clazzT = clazzT;
        this.clazz = clazz;

    }

    final AsyncDebugger getNextHead(final int time) {
        AsyncDebugger next, current = this;
        while (time > current.expiry && (next = current.next) != null) {
            current = next;
        }
        return current;
    }

    final AsyncDebugger setNext(final AsyncDebugger next) {
        return this.next = next;
    }

    StringBuilder debugTo(final StringBuilder string) {
        for (AsyncDebugger next = this; next != null; next = next.next) {
            string.append(next.clazzT.getSimpleName()).append(':').append(next.clazz.getName()).append('@').append(next.expiry).append(',');
        }
        return string;
    }
}

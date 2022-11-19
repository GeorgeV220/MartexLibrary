package com.georgev22.library.extensions.scheduler;

import com.georgev22.library.extensions.Extension;

class AsyncDebugger {
    private AsyncDebugger next = null;
    private final int expiry;
    private final Extension extension;
    private final Class<?> clazz;

    AsyncDebugger(final int expiry, final Extension extension, final Class<?> clazz) {
        this.expiry = expiry;
        this.extension = extension;
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
            string.append(next.extension.getDescription().getName()).append(':').append(next.clazz.getName()).append('@').append(next.expiry).append(',');
        }
        return string;
    }
}

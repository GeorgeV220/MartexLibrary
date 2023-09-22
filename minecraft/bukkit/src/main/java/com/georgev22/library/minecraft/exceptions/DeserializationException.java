package com.georgev22.library.minecraft.exceptions;

import java.io.Serial;

public final class DeserializationException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public DeserializationException() {
    }

    public DeserializationException(String message, Throwable cause) {
        super(message, cause);
    }

    public DeserializationException(String message) {
        super(message);
    }

    public DeserializationException(Throwable cause) {
        super(cause);
    }

}

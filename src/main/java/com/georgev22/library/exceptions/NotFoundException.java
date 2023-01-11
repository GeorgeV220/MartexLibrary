package com.georgev22.library.exceptions;

import org.jetbrains.annotations.NotNull;

public class NotFoundException extends Exception {

    public NotFoundException(String msg) {
        super(msg);
    }

    public NotFoundException(String msg, @NotNull Exception e) {
        super(msg + " because of " + e);
    }
}
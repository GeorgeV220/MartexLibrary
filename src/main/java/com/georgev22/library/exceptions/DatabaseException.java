package com.georgev22.library.exceptions;

public class DatabaseException extends RuntimeException {

    public DatabaseException(String message) {
        super(message);
    }

    public DatabaseException(String message, Throwable throwable) {
        super(message, throwable);
    }

}

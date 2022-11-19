package com.georgev22.library.exceptions;

public class DatabaseConnectionException extends RuntimeException {

    public DatabaseConnectionException(String message) {
        super(message);
    }

    public DatabaseConnectionException(String message, Throwable throwable) {
        super(message, throwable);
    }

}

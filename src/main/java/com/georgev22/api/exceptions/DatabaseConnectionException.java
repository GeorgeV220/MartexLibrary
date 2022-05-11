package com.georgev22.api.exceptions;

public class DatabaseConnectionException extends RuntimeException {

    public DatabaseConnectionException(String message) {
        super(message);
    }

    public DatabaseConnectionException(String message, Throwable throwable) {
        super(message, throwable);
    }

}

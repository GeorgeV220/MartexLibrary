package com.georgev22.library.exceptions;

public class DatabaseConnectionException extends RuntimeException {

    /**
     * Constructs a new DatabaseConnectionException based on the given
     * Exception
     *
     * @param throwable Exception that triggered this Exception
     */
    public DatabaseConnectionException(final Throwable throwable) {
        super(throwable);
    }

    /**
     * Constructs a new DatabaseConnectionException with the given message
     *
     * @param message Brief message explaining the cause of the exception
     */
    public DatabaseConnectionException(String message) {
        super(message);
    }

    /**
     * Constructs a new DatabaseConnectionException based on the given
     * Exception
     *
     * @param message   Brief message explaining the cause of the exception
     * @param throwable Exception that triggered this Exception
     */
    public DatabaseConnectionException(String message, Throwable throwable) {
        super(message, throwable);
    }

}

package com.georgev22.library.exceptions;

public class DatabaseException extends RuntimeException {

    /**
     * Constructs a new DatabaseException based on the given
     * Exception
     *
     * @param throwable Exception that triggered this Exception
     */
    public DatabaseException(final Throwable throwable) {
        super(throwable);
    }

    /**
     * Constructs a new DatabaseException with the given message
     *
     * @param message Brief message explaining the cause of the exception
     */
    public DatabaseException(String message) {
        super(message);
    }

    /**
     * Constructs a new DatabaseException based on the given
     * Exception
     *
     * @param message   Brief message explaining the cause of the exception
     * @param throwable Exception that triggered this Exception
     */
    public DatabaseException(String message, Throwable throwable) {
        super(message, throwable);
    }

}

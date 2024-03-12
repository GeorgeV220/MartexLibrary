package com.georgev22.library.utilities.exceptions;

/**
 * Thrown when a particular constructor cannot be found.
 */
public class NoSuchConstructorException extends ReflectiveOperationException {

    /**
     * Constructs a {@code NoSuchConstructorException} without a detail message.
     */
    public NoSuchConstructorException() {
        super();
    }

    /**
     * Constructs a {@code NoSuchConstructorException} with a detail message.
     *
     * @param s the detail message.
     */
    public NoSuchConstructorException(String s) {
        super(s);
    }
}

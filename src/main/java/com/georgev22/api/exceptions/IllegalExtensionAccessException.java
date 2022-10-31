package com.georgev22.api.exceptions;

/**
 * Thrown when an extension attempts to interact with the server when it is not
 * enabled
 */
public class IllegalExtensionAccessException extends RuntimeException {

    /**
     * Creates a new instance of <code>IllegalExtensionAccessException</code>
     * without detail message.
     */
    public IllegalExtensionAccessException() {
    }

    /**
     * Constructs an instance of <code>IllegalExtensionAccessException</code>
     * with the specified detail message.
     *
     * @param msg the detail message.
     */
    public IllegalExtensionAccessException(String msg) {
        super(msg);
    }
}

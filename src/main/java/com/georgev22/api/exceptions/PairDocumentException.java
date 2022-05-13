package com.georgev22.api.exceptions;

public class PairDocumentException extends RuntimeException {

    public PairDocumentException(String message) {
        super(message);
    }

    public PairDocumentException(String message, Throwable throwable) {
        super(message, throwable);
    }


}

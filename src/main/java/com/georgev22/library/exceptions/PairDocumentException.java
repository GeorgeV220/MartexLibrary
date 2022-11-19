package com.georgev22.library.exceptions;

public class PairDocumentException extends RuntimeException {

    public PairDocumentException(String message) {
        super(message);
    }

    public PairDocumentException(String message, Throwable throwable) {
        super(message, throwable);
    }


}

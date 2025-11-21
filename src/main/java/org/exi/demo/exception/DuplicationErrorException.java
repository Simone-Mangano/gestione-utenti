package org.exi.demo.exception;

public class DuplicationErrorException extends RuntimeException {

    public DuplicationErrorException(String message) {
        super(message);
    }

    public DuplicationErrorException(String message, Throwable cause) {
        super(message, cause);
    }
}
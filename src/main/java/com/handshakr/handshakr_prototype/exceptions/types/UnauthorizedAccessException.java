package com.handshakr.handshakr_prototype.exceptions.types;

public class UnauthorizedAccessException extends RuntimeException {
    public UnauthorizedAccessException(String message) {
        super(message);
    }
}

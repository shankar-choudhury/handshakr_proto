package com.handshakr.handshakr_prototype.exceptions.types;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}

package com.handshakr.handshakr_prototype.exceptions.general;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}

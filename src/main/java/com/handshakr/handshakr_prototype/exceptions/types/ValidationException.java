package com.handshakr.handshakr_prototype.exceptions.types;

public class ValidationException extends BadRequestException {
    public ValidationException(String message) {
        super(message);
    }
}

package com.handshakr.handshakr_prototype.exceptions.general;

public class ValidationException extends BadRequestException {
    public ValidationException(String message) {
        super(message);
    }
}

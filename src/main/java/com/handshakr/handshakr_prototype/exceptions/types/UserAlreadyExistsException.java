package com.handshakr.handshakr_prototype.exceptions.types;

public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
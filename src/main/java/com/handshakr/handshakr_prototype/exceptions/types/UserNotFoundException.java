package com.handshakr.handshakr_prototype.exceptions.types;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
}

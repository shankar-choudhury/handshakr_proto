package com.handshakr.handshakr_prototype.exceptions.user;

import com.handshakr.handshakr_prototype.exceptions.general.ConflictException;

public class UserAlreadyExistsException extends ConflictException {
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
package com.handshakr.handshakr_prototype.exceptions.user;

import com.handshakr.handshakr_prototype.exceptions.general.ServiceUnavailableException;

public class UserServiceUnavailableException extends ServiceUnavailableException {
    public UserServiceUnavailableException(String message) {
        super(message);
    }
}

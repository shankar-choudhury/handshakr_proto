package com.handshakr.handshakr_prototype.exceptions.user;

import com.handshakr.handshakr_prototype.exceptions.security.InvalidCredentialsException;

public class AccountLockedException extends InvalidCredentialsException {
    public AccountLockedException(String message) {
        super(message);
    }
}

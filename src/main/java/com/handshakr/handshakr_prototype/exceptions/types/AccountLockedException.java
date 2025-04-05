package com.handshakr.handshakr_prototype.exceptions.types;

public class AccountLockedException extends InvalidCredentialsException {
    public AccountLockedException(String message) {
        super(message);
    }
}

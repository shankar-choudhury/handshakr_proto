package com.handshakr.handshakr_prototype.exceptions.user;

import com.handshakr.handshakr_prototype.exceptions.security.InvalidCredentialsException;
import org.springframework.security.authentication.LockedException;

public class AccountLockedException extends LockedException {
    public AccountLockedException(String message) {
        super(message);
    }
}

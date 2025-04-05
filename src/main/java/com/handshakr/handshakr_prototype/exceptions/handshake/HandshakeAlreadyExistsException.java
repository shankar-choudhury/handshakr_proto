package com.handshakr.handshakr_prototype.exceptions.handshake;

import com.handshakr.handshakr_prototype.exceptions.general.ConflictException;

public class HandshakeAlreadyExistsException extends ConflictException {
    public HandshakeAlreadyExistsException(String message) {
        super(message);
    }
}

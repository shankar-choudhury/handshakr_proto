package com.handshakr.handshakr_prototype.exceptions.handshake;

import com.handshakr.handshakr_prototype.exceptions.general.ServiceUnavailableException;

public class HandshakeServiceUnavailableException extends ServiceUnavailableException {
    public HandshakeServiceUnavailableException(String message) {
        super(message);
    }
}

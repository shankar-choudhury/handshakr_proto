package com.handshakr.handshakr_prototype.exceptions.general;

public class ServiceUnavailableException extends RuntimeException {
    public ServiceUnavailableException(String message) {
        super(message);
    }
}

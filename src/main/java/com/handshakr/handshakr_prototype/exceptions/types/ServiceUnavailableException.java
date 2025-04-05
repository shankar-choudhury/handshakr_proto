package com.handshakr.handshakr_prototype.exceptions.types;

public class ServiceUnavailableException extends RuntimeException {
    public ServiceUnavailableException(String message) {
        super(message);
    }
}

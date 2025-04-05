package com.handshakr.handshakr_prototype.exceptions.handshake;

import com.handshakr.handshakr_prototype.exceptions.general.BadRequestException;

public enum HandshakeExceptionType {
    HANDSHAKE_ALREADY_EXISTS {
        @Override
        public RuntimeException create(String... params) {
            return new HandshakeAlreadyExistsException(
                    String.format("Handshake with name '%s' already exists", params[0]));
        }
    },
    HANDSHAKE_NOT_FOUND {
        @Override
        public RuntimeException create(String... params) {
            return new HandshakeNotFoundException(
                    String.format("Handshake with name '%s' not found", params[0]));
        }
    },
    HANDSHAKE_SERVICE_UNAVAILABLE {
        @Override
        public RuntimeException create(String... params) {
            return new HandshakeServiceUnavailableException(
                    params.length > 0 ? params[0] : "Handshake service unavailable");
        }
    },
    RECEIVER_NOT_FOUND {
        @Override
        public RuntimeException create(String... params) {
            return new HandshakeReceiverNotFoundException(
                    String.format("No handshake found for receiver '%s'", params[0]));
        }
    },
    INITIATOR_NOT_FOUND {
        @Override
        public RuntimeException create(String... params) {
            return new HandshakeInitiatorNotFoundException(
                    String.format("No handshake found for initiator '%s'", params[0]));
        }
    },
    BAD_REQUEST {
        @Override
        public RuntimeException create(String... params) {
            return new BadRequestException(params[0]);
        }
    };

    public abstract RuntimeException create(String... params);
}

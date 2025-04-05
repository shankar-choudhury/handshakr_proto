package com.handshakr.handshakr_prototype.exceptions;

import com.handshakr.handshakr_prototype.exceptions.general.BadRequestException;
import com.handshakr.handshakr_prototype.exceptions.general.DatabaseException;
import com.handshakr.handshakr_prototype.exceptions.handshake.*;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Function;

public class HandshakeExceptionFactory implements BaseExceptionFactory {

    private final Map<HandshakeExceptionType, Function<String[], RuntimeException>> exceptionMap;

    public HandshakeExceptionFactory() {
        this.exceptionMap = new EnumMap<>(HandshakeExceptionType.class);
        initializeMap();
    }

    private void initializeMap() {
        for (HandshakeExceptionType type : HandshakeExceptionType.values()) {
            exceptionMap.put(type, type::create);
        }
    }
    @Override
    public RuntimeException create(Enum<?> type, String... params) {
        if (!(type instanceof HandshakeExceptionType handshakeType)) {
            throw new IllegalArgumentException("Invalid HandshakeExceptionType");
        }
        return exceptionMap.get(handshakeType).apply(params);
    }

    @Override
    public BadRequestException badRequest(String message) {
        return (BadRequestException) create(HandshakeExceptionType.BAD_REQUEST, message);
    }

    public HandshakeAlreadyExistsException handshakeAlreadyExists(String handshakeName) {
        return (HandshakeAlreadyExistsException) create(
                HandshakeExceptionType.HANDSHAKE_ALREADY_EXISTS, handshakeName);
    }

    public HandshakeNotFoundException handshakeNotFound(String identifier) {
        return (HandshakeNotFoundException) create(
                HandshakeExceptionType.HANDSHAKE_NOT_FOUND, identifier);
    }

    public HandshakeReceiverNotFoundException receiverNotFound(String username) {
        return (HandshakeReceiverNotFoundException) create(
                HandshakeExceptionType.RECEIVER_NOT_FOUND, username);
    }

    public HandshakeInitiatorNotFoundException initiatorNotFound(String username) {
        return (HandshakeInitiatorNotFoundException) create(
                HandshakeExceptionType.INITIATOR_NOT_FOUND, username);
    }

    public DatabaseException databaseError(String message) {
        return (DatabaseException) create(HandshakeExceptionType.DATABASE_ERROR, message);
    }

    public HandshakeServiceUnavailableException serviceUnavailable(String message) {
        return (HandshakeServiceUnavailableException) create(
                HandshakeExceptionType.HANDSHAKE_SERVICE_UNAVAILABLE, message);
    }
}

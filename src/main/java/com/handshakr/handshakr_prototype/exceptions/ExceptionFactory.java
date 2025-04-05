package com.handshakr.handshakr_prototype.exceptions;

import com.handshakr.handshakr_prototype.exceptions.types.*;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class ExceptionFactory {
    private final Map<ExceptionType, Function<String[], RuntimeException>> exceptionMap;

    public ExceptionFactory() {
        this.exceptionMap = new EnumMap<>(ExceptionType.class);
        initializeMap();
    }

    private void initializeMap() {
        for (ExceptionType type : ExceptionType.values()) {
            exceptionMap.put(type, type::create);
        }
    }

    public RuntimeException create(ExceptionType type, String... params) {
        if (!exceptionMap.containsKey(type)) {
            throw new IllegalArgumentException("Unknown exception type: " + type);
        }
        return exceptionMap.get(type).apply(params);
    }

    // Convenience methods
    public UserAlreadyExistsException userAlreadyExists(String username) {
        return (UserAlreadyExistsException) create(
                ExceptionType.USER_ALREADY_EXISTS, username);
    }

    public UserNotFoundException userNotFound(String identifier) {
        return (UserNotFoundException) create(
                ExceptionType.USER_NOT_FOUND, identifier);
    }

    public InvalidCredentialsException invalidCredentials() {
        return (InvalidCredentialsException) create(
                ExceptionType.INVALID_CREDENTIALS);
    }

    public InvalidCredentialsException accountDisabled() {
        return (InvalidCredentialsException) create(
                ExceptionType.ACCOUNT_DISABLED);
    }

    public HandshakeNotFoundException handshakeNotFound(String identifier) {
        return (HandshakeNotFoundException) create(
                ExceptionType.HANDSHAKE_NOT_FOUND, identifier);
    }

    public BadRequestException badRequest(String message) {
        return (BadRequestException) create(
                ExceptionType.BAD_REQUEST, message);
    }
}

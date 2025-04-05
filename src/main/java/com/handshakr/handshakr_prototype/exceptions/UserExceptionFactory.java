package com.handshakr.handshakr_prototype.exceptions;

import com.handshakr.handshakr_prototype.exceptions.security.InvalidCredentialsException;
import com.handshakr.handshakr_prototype.exceptions.general.*;
import com.handshakr.handshakr_prototype.exceptions.user.AccountLockedException;
import com.handshakr.handshakr_prototype.exceptions.user.UserAlreadyExistsException;
import com.handshakr.handshakr_prototype.exceptions.user.UserExceptionType;
import com.handshakr.handshakr_prototype.exceptions.user.UserNotFoundException;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class UserExceptionFactory implements BaseExceptionFactory {
    private final Map<UserExceptionType, Function<String[], RuntimeException>> exceptionMap;

    public UserExceptionFactory() {
        this.exceptionMap = new EnumMap<>(UserExceptionType.class);
        initializeMap();
    }

    private void initializeMap() {
        for (UserExceptionType type : UserExceptionType.values()) {
            exceptionMap.put(type, type::create);
        }
    }

    @Override
    public RuntimeException create(Enum<?> type, String... params) {
        if (!(type instanceof UserExceptionType userType)) {
            throw new IllegalArgumentException("Invalid UserExceptionType");
        }
        return exceptionMap.get(userType).apply(params);
    }

    // Convenience methods
    public UserAlreadyExistsException userAlreadyExists(String username) {
        return (UserAlreadyExistsException) create(
                UserExceptionType.USER_ALREADY_EXISTS, username);
    }

    public UserNotFoundException userNotFound(String identifier) {
        return (UserNotFoundException) create(
                UserExceptionType.USER_NOT_FOUND, identifier);
    }

    public InvalidCredentialsException invalidCredentials() {
        return (InvalidCredentialsException) create(
                UserExceptionType.INVALID_CREDENTIALS);
    }

    public InvalidCredentialsException accountDisabled() {
        return (InvalidCredentialsException) create(
                UserExceptionType.ACCOUNT_DISABLED);
    }

    public BadRequestException badRequest(String message) {
        return (BadRequestException) create(
                UserExceptionType.BAD_REQUEST, message);
    }

    public ServiceUnavailableException serviceUnavailable(String message) {
        return (ServiceUnavailableException) create(
                UserExceptionType.SERVICE_UNAVAILABLE, message);
    }

    public DatabaseException databaseError(String message) {
        return (DatabaseException) create(
                UserExceptionType.DATABASE_ERROR, message);
    }

    public AccountLockedException accountLocked(String message) {
        return (AccountLockedException) create(
                UserExceptionType.ACCOUNT_LOCKED, message);
    }
}

package com.handshakr.handshakr_prototype.exceptions.user;

import com.handshakr.handshakr_prototype.exceptions.handshake.HandshakeNotFoundException;
import com.handshakr.handshakr_prototype.exceptions.security.InvalidCredentialsException;
import com.handshakr.handshakr_prototype.exceptions.general.*;

public enum UserExceptionType {
    USER_ALREADY_EXISTS {
        @Override
        public RuntimeException create(String... params) {
            return new UserAlreadyExistsException(
                    String.format("Username '%s' is already taken", params[0]));
        }
    },
    USER_NOT_FOUND {
        @Override
        public RuntimeException create(String... params) {
            return new UserNotFoundException(
                    String.format("User not found with identifier: %s", params[0]));
        }
    },
    INVALID_CREDENTIALS {
        @Override
        public RuntimeException create(String... params) {
            return new InvalidCredentialsException("Invalid username or password");
        }
    },
    BAD_REQUEST {
        @Override
        public RuntimeException create(String... params) {
            return new BadRequestException(params[0]);
        }
    },
    ACCOUNT_DISABLED {
        @Override
        public RuntimeException create(String... params) {
            return new InvalidCredentialsException("Account is disabled");
        }
    },
    SERVICE_UNAVAILABLE {
        @Override
        public RuntimeException create(String... params) {
            return new ServiceUnavailableException(
                    params.length > 0 ? params[0] : "Service unavailable");
        }
    },
    DATABASE_ERROR {
        @Override
        public RuntimeException create(String... params) {
            return new DatabaseException(
                    params.length > 0 ? params[0] : "Database operation failed");
        }
    },
    ACCOUNT_LOCKED {
        @Override
        public RuntimeException create(String... params) {
            return new AccountLockedException(
                    params.length > 0 ? params[0] : "Account is locked");
        }
    };

    public abstract RuntimeException create(String... params);
}

package com.handshakr.handshakr_prototype.exceptions.types;

public enum ExceptionType {
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
    HANDSHAKE_NOT_FOUND {
        @Override
        public RuntimeException create(String... params) {
            return new HandshakeNotFoundException(
                    String.format("Handshake not found with identifier: %s", params[0]));
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
    };

    public abstract RuntimeException create(String... params);
}

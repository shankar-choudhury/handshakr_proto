package com.handshakr.handshakr_prototype.exceptions;

import com.handshakr.handshakr_prototype.exceptions.general.BadRequestException;

public interface BaseExceptionFactory {
    RuntimeException create(Enum<?> type, String... params);
    BadRequestException badRequest(String message);
}

package com.handshakr.handshakr_prototype.handshake;

/**
 * Enum representing the various possible statuses of a handshake.
 * This helps track the lifecycle of the handshake.
 */
public enum HandshakeStatus {
    CREATED, PENDING, ACCEPTED, CANCELLED, COMPLETED, FAILED;
}

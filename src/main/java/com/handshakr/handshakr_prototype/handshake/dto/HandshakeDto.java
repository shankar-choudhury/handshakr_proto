package com.handshakr.handshakr_prototype.handshake.dto;

import com.handshakr.handshakr_prototype.handshake.Handshake;
import com.handshakr.handshakr_prototype.handshake.HandshakeStatus;

import java.time.Instant;
import java.util.Date;

/**
 * A Data Transfer Object (DTO) that represents a handshake.
 * Used to transfer the necessary data of a handshake between layers of the application.
 */
public record HandshakeDto(
        String handshakeName,
        String encryptedDetails,
        Instant signedDate,
        Instant completedDate,
        HandshakeStatus handshakeStatus,
        String initiatorUsername,
        String acceptorUsername) {
    /**
     * Converts a Handshake entity to a HandshakeDto.
     *
     * @param handshake The Handshake entity to convert.
     * @return The corresponding HandshakeDto.
     */
    public static HandshakeDto from(Handshake handshake) {
        return new HandshakeDto(
                handshake.getHandshakeName(),
                handshake.getEncryptedDetails(),
                handshake.getCreatedDate(),
                handshake.getMostRecentUpdateDate(),
                handshake.getHandshakeStatus(),
                handshake.getInitiatorUsername(),
                handshake.getReceiverUsername()
        );
    }
}

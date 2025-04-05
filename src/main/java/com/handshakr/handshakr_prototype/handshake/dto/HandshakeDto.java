package com.handshakr.handshakr_prototype.handshake.dto;

import com.handshakr.handshakr_prototype.handshake.Handshake;
import com.handshakr.handshakr_prototype.handshake.HandshakeStatus;

import java.time.Instant;
import java.util.Date;

public record HandshakeDto(
        String handshakeName,
        String encryptedDetails,
        Instant signedDate,
        Instant completedDate,
        HandshakeStatus handshakeStatus,
        String initiatorUsername,
        String acceptorUsername) {
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

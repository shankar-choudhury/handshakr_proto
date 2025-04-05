package com.handshakr.handshakr_prototype.handshake.dto;

public record CreateHandshakeRequest(
        String handshakeName,
        String encryptedDetails,
        String initiatorUsername,
        String receiverUsername) { }

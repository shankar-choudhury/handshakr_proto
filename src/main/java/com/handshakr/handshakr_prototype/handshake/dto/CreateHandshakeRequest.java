package com.handshakr.handshakr_prototype.handshake.dto;

import com.handshakr.handshakr_prototype.handshake.HandshakeStatus;

import java.util.Date;

public record CreateHandshakeRequest(
        String handshakeName,
        String encryptedDetails,
        Date createdDate,
        String initiatorUsername,
        String acceptorUsername) { }

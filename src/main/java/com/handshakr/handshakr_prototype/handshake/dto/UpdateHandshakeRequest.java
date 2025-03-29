package com.handshakr.handshakr_prototype.handshake.dto;

import com.handshakr.handshakr_prototype.handshake.HandshakeStatus;

import java.util.Date;

public record UpdateHandshakeRequest(
        String handshakeName,
        Date updatedDate,
        HandshakeStatus status) { }

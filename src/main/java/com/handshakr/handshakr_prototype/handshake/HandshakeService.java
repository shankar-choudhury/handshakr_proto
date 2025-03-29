package com.handshakr.handshakr_prototype.handshake;

import com.handshakr.handshakr_prototype.handshake.dto.CreateHandshakeRequest;
import com.handshakr.handshakr_prototype.handshake.dto.HandshakeDto;
import com.handshakr.handshakr_prototype.handshake.dto.UpdateHandshakeRequest;

public interface HandshakeService {
    void createHandshake(CreateHandshakeRequest request);
    void updateHandshake(UpdateHandshakeRequest request);
    HandshakeDto getHandshakeByAcceptor(String username);
    HandshakeDto getHandshakeByInitiator(String username);
    HandshakeDto getHandshakeByName(String handshakeName);
}

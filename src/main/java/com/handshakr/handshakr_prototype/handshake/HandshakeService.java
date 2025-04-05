package com.handshakr.handshakr_prototype.handshake;

import com.handshakr.handshakr_prototype.handshake.dto.CreateHandshakeRequest;
import com.handshakr.handshakr_prototype.handshake.dto.HandshakeDto;

import java.util.List;

public interface HandshakeService {
    void createHandshake(CreateHandshakeRequest request, String initiatorUsername);
    void updateHandshake(String handshakeName, HandshakeStatus status);
    HandshakeDto getHandshakeByAcceptor(String username);
    HandshakeDto getHandshakeByInitiator(String username);
    HandshakeDto getHandshakeByName(String handshakeName);
    List<HandshakeDto> getHandshakesByInitiator(String username);
    List<HandshakeDto> getHandshakesByAcceptor(String username);
}

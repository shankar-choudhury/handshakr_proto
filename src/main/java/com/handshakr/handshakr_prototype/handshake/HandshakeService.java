package com.handshakr.handshakr_prototype.handshake;

import com.handshakr.handshakr_prototype.handshake.dto.CreateHandshakeRequest;
import com.handshakr.handshakr_prototype.handshake.dto.HandshakeDto;

import java.util.List;

/**
 * Service interface for managing handshakes.
 * Defines methods for creating, updating, and retrieving handshakes.
 */
public interface HandshakeService {

    /**
     * Creates a new handshake based on the provided request and initiator's username.
     *
     * @param request The details for creating a handshake.
     * @param initiatorUsername The username of the user initiating the handshake.
     */
    void createHandshake(CreateHandshakeRequest request, String initiatorUsername);

    /**
     * Updates the status of an existing handshake.
     *
     * @param handshakeName The unique name of the handshake to update.
     * @param status The new status for the handshake.
     */
    void updateHandshake(String handshakeName, HandshakeStatus status);

    /**
     * Retrieves the handshake associated with the given acceptor username.
     *
     * @param username The username of the acceptor.
     * @return A DTO representing the handshake associated with the acceptor.
     */
    HandshakeDto getHandshakeByAcceptor(String username);

    /**
     * Retrieves the handshake associated with the given initiator username.
     *
     * @param username The username of the initiator.
     * @return A DTO representing the handshake associated with the initiator.
     */
    HandshakeDto getHandshakeByInitiator(String username);

    /**
     * Retrieves the handshake with the given unique handshake name.
     *
     * @param handshakeName The name of the handshake.
     * @return A DTO representing the handshake associated with the provided name.
     */
    HandshakeDto getHandshakeByName(String handshakeName);

    /**
     * Retrieves all handshakes initiated by a specific user.
     *
     * @param username The username of the initiator.
     * @return A list of DTOs representing all handshakes initiated by the user.
     */
    List<HandshakeDto> getHandshakesByInitiator(String username);

    /**
     * Retrieves all handshakes received by a specific user.
     *
     * @param username The username of the acceptor.
     * @return A list of DTOs representing all handshakes received by the user.
     */
    List<HandshakeDto> getHandshakesByAcceptor(String username);
}

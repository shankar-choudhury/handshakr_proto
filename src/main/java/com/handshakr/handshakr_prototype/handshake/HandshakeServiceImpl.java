package com.handshakr.handshakr_prototype.handshake;

import com.handshakr.handshakr_prototype.exceptions.HandshakeExceptionFactory;
import com.handshakr.handshakr_prototype.exceptions.user.UserNotFoundException;
import com.handshakr.handshakr_prototype.handshake.dto.CreateHandshakeRequest;
import com.handshakr.handshakr_prototype.handshake.dto.HandshakeDto;
import com.handshakr.handshakr_prototype.user.User;
import com.handshakr.handshakr_prototype.user.UserService;
import jakarta.transaction.Transactional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of the HandshakeService interface.
 * Provides methods to create, update, and retrieve handshakes, and handles the business logic of handshakes.
 */
@Service
public class HandshakeServiceImpl implements HandshakeService{
    private final UserService userService;
    private final HandshakeRepository repository;
    private final HandshakeExceptionFactory exceptionFactory;

    public HandshakeServiceImpl(UserService userService, HandshakeRepository repository, HandshakeExceptionFactory exceptionFactory) {
        this.userService = userService;
        this.repository = repository;
        this.exceptionFactory = exceptionFactory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void createHandshake(CreateHandshakeRequest request, String initiatorUsername) {
        // Validate request
        if (request == null) {
            throw exceptionFactory.badRequest("Handshake request cannot be null");
        }

        try {
            User initiator = userService.findByUsername(initiatorUsername);
            User acceptor = userService.findByUsername(request.receiverUsername());

            // Check if handshake name already exists
            if (repository.existsByHandshakeName(request.handshakeName())) {
                throw exceptionFactory.handshakeAlreadyExists(request.handshakeName());
            }

            Handshake newHandshake = new Handshake(
                    request.handshakeName(),
                    request.encryptedDetails(),
                    initiatorUsername,
                    request.receiverUsername(),
                    initiator,
                    acceptor);

            repository.save(newHandshake);

        } catch (UserNotFoundException e) {
            throw exceptionFactory.badRequest(
                    String.format("User not found: %s",
                            e.getMessage().contains(request.receiverUsername()) ?
                                    "receiver" : "initiator"));

        } catch (DataIntegrityViolationException e) {
            throw exceptionFactory.databaseError(
                    "Failed to create handshake: " + e.getMostSpecificCause().getMessage());

        } catch (Exception e) {
            throw exceptionFactory.serviceUnavailable(
                    "Failed to create handshake: " + e.getMessage());
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void updateHandshake(String handshakeName, HandshakeStatus status) {
        if (handshakeName == null || handshakeName.isBlank()) {
            throw exceptionFactory.badRequest("Handshake name cannot be empty");
        }

        try {
            Handshake toUpdate = repository.findByHandshakeName(handshakeName)
                    .orElseThrow(() -> exceptionFactory.handshakeNotFound(handshakeName));

            toUpdate.setMostRecentUpdateDate(Instant.now());
            toUpdate.setHandshakeStatus(status);

            repository.save(toUpdate);
        } catch (Exception e) {
            throw exceptionFactory.databaseError(
                    "Failed to update handshake: " + e.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HandshakeDto getHandshakeByAcceptor(String username) {
        validateUsername(username);
        try {
            return HandshakeDto.from(
                    repository.findByReceiverUsername(username)
                            .orElseThrow(() -> exceptionFactory.receiverNotFound(username)));
        } catch (Exception e) {
            throw exceptionFactory.serviceUnavailable(
                    "Failed to retrieve handshake by acceptor: " + e.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HandshakeDto getHandshakeByInitiator(String username) {
        validateUsername(username);
        try {
            return HandshakeDto.from(
                    repository.findByInitiatorUsername(username)
                            .orElseThrow(() -> exceptionFactory.initiatorNotFound(username)));
        } catch (Exception e) {
            throw exceptionFactory.serviceUnavailable(
                    "Failed to retrieve handshake by initiator: " + e.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HandshakeDto getHandshakeByName(String handshakeName) {
        if (handshakeName == null || handshakeName.isBlank()) {
            throw exceptionFactory.badRequest("Handshake name cannot be empty");
        }
        try {
            return HandshakeDto.from(
                    repository.findByHandshakeName(handshakeName)
                            .orElseThrow(() -> exceptionFactory.handshakeNotFound(handshakeName)));
        } catch (Exception e) {
            throw exceptionFactory.serviceUnavailable(
                    "Failed to retrieve handshake by name: " + e.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<HandshakeDto> getHandshakesByInitiator(String username) {
        validateUsername(username);
        try {
            return repository.findAllByInitiatorUsername(username)
                    .stream()
                    .map(HandshakeDto::from)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw exceptionFactory.serviceUnavailable(
                    "Failed to retrieve handshakes by initiator: " + e.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<HandshakeDto> getHandshakesByAcceptor(String username) {
        validateUsername(username);
        try {
            return repository.findAllByReceiverUsername(username)
                    .stream()
                    .map(HandshakeDto::from)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw exceptionFactory.serviceUnavailable(
                    "Failed to retrieve handshakes by acceptor: " + e.getMessage());
        }
    }

    private void validateUsername(String username) {
        if (username == null || username.isBlank()) {
            throw exceptionFactory.badRequest("Username cannot be empty");
        }
    }
}

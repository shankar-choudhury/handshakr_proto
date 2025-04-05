package com.handshakr.handshakr_prototype.handshake;

import com.handshakr.handshakr_prototype.exceptions.HandshakeExceptionFactory;
import com.handshakr.handshakr_prototype.exceptions.UserExceptionFactory;
import com.handshakr.handshakr_prototype.exceptions.user.UserNotFoundException;
import com.handshakr.handshakr_prototype.handshake.dto.CreateHandshakeRequest;
import com.handshakr.handshakr_prototype.handshake.dto.HandshakeDto;
import com.handshakr.handshakr_prototype.handshake.dto.UpdateHandshakeRequest;
import com.handshakr.handshakr_prototype.user.User;
import com.handshakr.handshakr_prototype.user.UserService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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

    @Override
    public void createHandshake(CreateHandshakeRequest request) {
        // Validate request
        if (request == null) {
            throw exceptionFactory.badRequest("Handshake request cannot be null");
        }

        try {
            User initiator = userService.findByUsername(request.initiatorUsername());
            User acceptor = userService.findByUsername(request.acceptorUsername());

            // Check if handshake name already exists
            if (repository.existsByHandshakeName(request.handshakeName())) {
                throw exceptionFactory.create(
                        ExceptionType.CONFLICT,
                        "Handshake with name '" + request.handshakeName() + "' already exists");
            }

            Handshake newHandshake = new Handshake(
                    request.handshakeName(),
                    request.encryptedDetails(),
                    request.createdDate(),
                    request.initiatorUsername(),
                    request.acceptorUsername(),
                    initiator,
                    acceptor);

            repository.save(newHandshake);

        } catch (UserNotFoundException e) {
            throw exceptionFactory.userNotFound(
                    e.getMessage().contains("username") ? request.acceptorUsername() : request.initiatorUsername());
        } catch (DataIntegrityViolationException e) {
            throw exceptionFactory.databaseError(
                    "Failed to create handshake: " + e.getMostSpecificCause().getMessage());
        } catch (Exception e) {
            throw exceptionFactory.create(
                    ExceptionType.SERVICE_UNAVAILABLE,
                    "Failed to create handshake: " + e.getMessage());
        }
    }

    @Override
    public void updateHandshake(UpdateHandshakeRequest request) {
        if (request == null || request.handshakeName() == null) {
            throw exceptionFactory.badRequest("Update request and handshake name cannot be null");
        }

        try {
            Handshake toUpdate = repository.findByHandshakeName(request.handshakeName())
                    .orElseThrow(() -> exceptionFactory.create(
                            ExceptionType.HANDSHAKE_NOT_FOUND,
                            request.handshakeName()));

            toUpdate.setMostRecentUpdateDate(request.updatedDate());
            toUpdate.setHandshakeStatus(request.status());

            repository.save(toUpdate);
        } catch (Exception e) {
            throw exceptionFactory.databaseError(
                    "Failed to update handshake: " + e.getMessage());
        }
    }

    @Override
    public HandshakeDto getHandshakeByAcceptor(String username) {
        validateUsername(username);
        try {
            return HandshakeDto.from(
                    repository.findByReceiverUsername(username)
                            .orElseThrow(() -> exceptionFactory.create(
                                    ExceptionType.HANDSHAKE_NOT_FOUND,
                                    username, "acceptor username")));
        } catch (Exception e) {
            throw exceptionFactory.create(
                    ExceptionType.SERVICE_UNAVAILABLE,
                    "Failed to retrieve handshake by acceptor: " + e.getMessage());
        }
    }

    @Override
    public HandshakeDto getHandshakeByInitiator(String username) {
        validateUsername(username);
        try {
            return HandshakeDto.from(
                    repository.findByInitiatorUsername(username)
                            .orElseThrow(() -> exceptionFactory.create(
                                    ExceptionType.HANDSHAKE_NOT_FOUND,
                                    username, "initiator username")));
        } catch (Exception e) {
            throw exceptionFactory.create(
                    ExceptionType.SERVICE_UNAVAILABLE,
                    "Failed to retrieve handshake by initiator: " + e.getMessage());
        }
    }

    @Override
    public HandshakeDto getHandshakeByName(String handshakeName) {
        if (handshakeName == null || handshakeName.isBlank()) {
            throw exceptionFactory.badRequest("Handshake name cannot be empty");
        }
        try {
            return HandshakeDto.from(
                    repository.findByHandshakeName(handshakeName)
                            .orElseThrow(() -> exceptionFactory.create(
                                    ExceptionType.HANDSHAKE_NOT_FOUND,
                                    handshakeName)));
        } catch (Exception e) {
            throw exceptionFactory.create(
                    ExceptionType.SERVICE_UNAVAILABLE,
                    "Failed to retrieve handshake by name: " + e.getMessage());
        }
    }

    public List<HandshakeDto> getHandshakesByInitiator(String username) {
        validateUsername(username);
        try {
            return repository.findAllByInitiatorUsername(username)
                    .stream()
                    .map(HandshakeDto::from)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw exceptionFactory.create(
                    ExceptionType.SERVICE_UNAVAILABLE,
                    "Failed to retrieve handshakes by initiator: " + e.getMessage());
        }
    }

    @Override
    public List<HandshakeDto> getHandshakesByAcceptor(String username) {
        validateUsername(username);
        try {
            return repository.findAllByReceiverUsername(username)
                    .stream()
                    .map(HandshakeDto::from)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw exceptionFactory.create(
                    ExceptionType.SERVICE_UNAVAILABLE,
                    "Failed to retrieve handshakes by acceptor: " + e.getMessage());
        }
    }

    private void validateUsername(String username) {
        if (username == null || username.isBlank()) {
            throw exceptionFactory.badRequest("Username cannot be empty");
        }
    }
}

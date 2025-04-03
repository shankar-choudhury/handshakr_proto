package com.handshakr.handshakr_prototype.handshake;

import com.handshakr.handshakr_prototype.handshake.dto.CreateHandshakeRequest;
import com.handshakr.handshakr_prototype.handshake.dto.HandshakeDto;
import com.handshakr.handshakr_prototype.handshake.dto.UpdateHandshakeRequest;
import com.handshakr.handshakr_prototype.user.User;
import com.handshakr.handshakr_prototype.user.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class HandshakeServiceImpl implements HandshakeService{
    private final UserService userService;
    private final HandshakeRepository repository;

    public HandshakeServiceImpl(UserService userService, HandshakeRepository repository) {
        this.userService = userService;
        this.repository = repository;
    }

    @Override
    public void createHandshake(CreateHandshakeRequest request) {
        User initiator = userService.findByUsername(request.initiatorUsername());
        User acceptor = userService.findByUsername(request.acceptorUsername());

        System.out.println(initiator.getUsername());
        System.out.println(acceptor.getUsername());

        Handshake newHandshake = new Handshake(
                request.handshakeName(),
                request.encryptedDetails(),
                request.createdDate(),
                request.initiatorUsername(),
                request.acceptorUsername(),
                initiator,
                acceptor);

        Optional.of(repository.save(newHandshake))
                .orElseThrow(() -> new RuntimeException("Handshake could not be saved"));
    }

    @Override
    public void updateHandshake(UpdateHandshakeRequest request) {
        Handshake toUpdate = repository.findByHandshakeName(Objects.requireNonNull(request).handshakeName())
                .orElseThrow(() -> new RuntimeException("Handshake could not be found"));

        toUpdate.setMostRecentUpdateDate(request.updatedDate());
        toUpdate.setHandshakeStatus(request.status());

        repository.save(toUpdate);
    }

    @Override
    public HandshakeDto getHandshakeByAcceptor(String username) {
        return HandshakeDto.from(
                repository.findByReceiverUsername(Objects.requireNonNull(username))
                        .orElseThrow(() -> new RuntimeException("Handshake with that username could not be found")));
    }

    @Override
    public HandshakeDto getHandshakeByInitiator(String username) {
        return HandshakeDto.from(repository.findByReceiverUsername(Objects.requireNonNull(username))
                .orElseThrow(() -> new RuntimeException("Handshake with that username could not be found")));
    }

    @Override
    public HandshakeDto getHandshakeByName(String handshakeName) {
        return HandshakeDto.from(repository.findByHandshakeName(Objects.requireNonNull(handshakeName))
                .orElseThrow(() -> new RuntimeException("Handshake with that username could not be found")));
    }

    @Override
    public List<HandshakeDto> getHandshakesByInitiator(String username) {
        return repository.findAllByInitiatorUsername(Objects.requireNonNull(username))
                .stream()
                .map(HandshakeDto::from)
                .collect(Collectors.toList());
    }

    @Override
    public List<HandshakeDto> getHandshakesByAcceptor(String username) {
        return repository.findAllByReceiverUsername(Objects.requireNonNull(username))
                .stream()
                .map(HandshakeDto::from)
                .collect(Collectors.toList());
    }
}

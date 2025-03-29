package com.handshakr.handshakr_prototype.handshake;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface HandshakeRepository extends CrudRepository<Handshake, Long> {
    Optional<Handshake> findByAcceptorUsername(String username);
    Optional<Handshake> findByInitiatorUsername(String username);
    Optional<Handshake> findByHandshakeName(String handshakeName);
}

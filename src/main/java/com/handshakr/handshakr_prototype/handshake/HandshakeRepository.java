package com.handshakr.handshakr_prototype.handshake;

import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface HandshakeRepository extends CrudRepository<Handshake, Long> {
    Optional<Handshake> findByReceiverUsername(String username);
    Optional<Handshake> findByInitiatorUsername(String username);
    Optional<Handshake> findByHandshakeName(String handshakeName);
    List<Handshake> findAllByInitiatorUsername(String username);
    List<Handshake> findAllByReceiverUsername(String username);

    boolean existsByHandshakeName(String handshakeName);
}

package com.handshakr.handshakr_prototype.handshake;

import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for accessing and managing Handshake entities in the database.
 * Extends the CrudRepository for basic CRUD operations.
 */
public interface HandshakeRepository extends CrudRepository<Handshake, Long> {

    /**
     * Finds a handshake by the receiver's username.
     *
     * @param username The username of the receiver.
     * @return An Optional containing the handshake if found.
     */
    Optional<Handshake> findByReceiverUsername(String username);

    /**
     * Finds a handshake by the initiator's username.
     *
     * @param username The username of the initiator.
     * @return An Optional containing the handshake if found.
     */
    Optional<Handshake> findByInitiatorUsername(String username);

    /**
     * Finds a handshake by its unique name.
     *
     * @param handshakeName The unique name of the handshake.
     * @return An Optional containing the handshake if found.
     */
    Optional<Handshake> findByHandshakeName(String handshakeName);

    /**
     * Retrieves all handshakes initiated by a specific user.
     *
     * @param username The username of the initiator.
     * @return A list of handshakes initiated by the user.
     */
    List<Handshake> findAllByInitiatorUsername(String username);

    /**
     * Retrieves all handshakes received by a specific user.
     *
     * @param username The username of the receiver.
     * @return A list of handshakes received by the user.
     */
    List<Handshake> findAllByReceiverUsername(String username);

    /**
     * Checks if a handshake with a given name already exists.
     *
     * @param handshakeName The name of the handshake.
     * @return True if the handshake name already exists, false otherwise.
     */
    boolean existsByHandshakeName(String handshakeName);
}

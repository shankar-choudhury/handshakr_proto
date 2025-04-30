package com.handshakr.handshakr_prototype.repository;

import com.handshakr.handshakr_prototype.handshake.Handshake;
import com.handshakr.handshakr_prototype.handshake.HandshakeRepository;
import com.handshakr.handshakr_prototype.user.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class HandshakeRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private HandshakeRepository handshakeRepository;

    private final User user1 = new User("user1", "user1@gmail.com", "password");
    private final User user2 = new User("user2", "user2@gmail.com", "password");

    @Test
    void findByHandshakeName_WhenExists_ReturnsHandshake() {
        // Given

        Handshake savedHandshake = entityManager.persistAndFlush(
                new Handshake(
                        "test-handshake",
                        "encrypted details",
                        "user1",
                        "user2",
                        user1, user2
                ));

        // When
        Optional<Handshake> found = handshakeRepository.findByHandshakeName("test-handshake");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getHandshakeName()).isEqualTo("test-handshake");
    }

    @Test
    void findAllByInitiatorUsername_ReturnsCorrectHandshakes() {
        // Given
        entityManager.persistAndFlush(
                new Handshake(
                        "handshake1",
                        "encrypted details",
                        "user1",
                        "user2",
                        user1, user2
                )
        );
        entityManager.persistAndFlush(
                new Handshake(
                        "handshake2",
                        "encrypted details",
                        "user1",
                        "user2",
                        user1, user2
                )
        );
        entityManager.persistAndFlush(
                new Handshake(
                        "handshake3",
                        "encrypted details",
                        "user1",
                        "user2",
                        user1, user2
                )
        );

        // When
        List<Handshake> user1Handshakes = handshakeRepository.findAllByInitiatorUsername("user1");

        // Then
        assertThat(user1Handshakes).hasSize(2);
        assertThat(user1Handshakes)
                .extracting(Handshake::getHandshakeName)
                .containsExactlyInAnyOrder("handshake1", "handshake2");
    }

    @Test
    void existsByHandshakeName_WhenExists_ReturnsTrue() {
        // Given
        entityManager.persistAndFlush(
                new Handshake(
                        "existing-handshake",
                        "encrypted details",
                        "user1",
                        "user2",
                        user1, user2
                )
        );

        // When/Then
        assertThat(handshakeRepository.existsByHandshakeName("existing-handshake")).isTrue();
        assertThat(handshakeRepository.existsByHandshakeName("non-existent")).isFalse();
    }

    @Test
    void findByReceiverUsername_WhenMultipleExist_ReturnsCorrectOne() {
        // Given
        entityManager.persistAndFlush(
                new Handshake(
                        "hs1",
                        "encrypted details",
                        "user1",
                        "user2",
                        user1, user2
                )
        );
        entityManager.persistAndFlush(
                new Handshake(
                        "hs2",
                        "encrypted details",
                        "user1",
                        "user2",
                        user1, user2
                )
        );

        // When
        Optional<Handshake> found = handshakeRepository.findByReceiverUsername("bob");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getReceiverUsername()).isEqualTo("bob");
    }
}

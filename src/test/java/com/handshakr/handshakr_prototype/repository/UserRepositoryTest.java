package com.handshakr.handshakr_prototype.repository;


import com.handshakr.handshakr_prototype.user.User;
import com.handshakr.handshakr_prototype.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByUsername_WhenExists_ReturnsUser() {
        // Given
        User savedUser = entityManager.persistAndFlush(
                new User("testuser", "test@email.com", "encodedPassword")
        );

        // When
        Optional<User> found = userRepository.findByUsername("testuser");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("test@email.com");
    }

    @Test
    void existsByEmail_WhenExists_ReturnsTrue() {
        // Given
        entityManager.persistAndFlush(
                new User("user1", "exists@test.com", "pass")
        );

        // When/Then
        assertThat(userRepository.existsByEmail("exists@test.com")).isTrue();
        assertThat(userRepository.existsByEmail("nonexistent@test.com")).isFalse();
    }

    @Test
    void findAll_ReturnsAllUsers() {
        // Given
        entityManager.persist(new User("user1", "u1@test.com", "pass1"));
        entityManager.persist(new User("user2", "u2@test.com", "pass2"));
        entityManager.flush();

        // When
        List<User> users = userRepository.findAll();

        // Then
        assertThat(users).hasSize(2);
        assertThat(users)
                .extracting(User::getUsername)
                .containsExactlyInAnyOrder("user1", "user2");
    }

    @Test
    void findByEmail_WhenNotExists_ReturnsEmpty() {
        // When
        Optional<User> found = userRepository.findByEmail("unknown@test.com");

        // Then
        assertThat(found).isEmpty();
    }
}

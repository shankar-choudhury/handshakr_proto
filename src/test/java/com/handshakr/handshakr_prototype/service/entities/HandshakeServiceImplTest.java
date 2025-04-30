package com.handshakr.handshakr_prototype.service.entities;

import com.handshakr.handshakr_prototype.exceptions.HandshakeExceptionFactory;
import com.handshakr.handshakr_prototype.exceptions.handshake.HandshakeNotFoundException;
import com.handshakr.handshakr_prototype.exceptions.user.UserNotFoundException;
import com.handshakr.handshakr_prototype.handshake.Handshake;
import com.handshakr.handshakr_prototype.handshake.HandshakeRepository;
import com.handshakr.handshakr_prototype.handshake.HandshakeServiceImpl;
import com.handshakr.handshakr_prototype.handshake.HandshakeStatus;
import com.handshakr.handshakr_prototype.handshake.dto.CreateHandshakeRequest;
import com.handshakr.handshakr_prototype.handshake.dto.HandshakeDto;
import com.handshakr.handshakr_prototype.user.User;
import com.handshakr.handshakr_prototype.user.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HandshakeServiceImplTest {

    @Mock
    private UserService userService;

    @Mock
    private HandshakeRepository repository;

    @Mock
    private HandshakeExceptionFactory exceptionFactory;

    @InjectMocks
    private HandshakeServiceImpl handshakeService;

    private final User user1 = new User("user1", "user1@gmail.com", "password");
    private final User user2 = new User("user2", "user2@gmail.com", "password");

    // ===== CREATE HANDSHAKE TESTS =====
    @Test
    void createHandshake_ValidRequest_CreatesHandshake() {
        CreateHandshakeRequest request = new CreateHandshakeRequest(
                "test-handshake", "receiver", "encrypted-data");
        User initiator = new User("initiator", "init@test.com", "pass");
        User receiver = new User("receiver", "rec@test.com", "pass");

        when(userService.findByUsername("initiator")).thenReturn(initiator);
        when(userService.findByUsername("receiver")).thenReturn(receiver);
        when(repository.existsByHandshakeName("test-handshake")).thenReturn(false);

        handshakeService.createHandshake(request, "initiator");

        verify(repository).save(any(Handshake.class));
    }

    @Test
    void createHandshake_ExistingName_ThrowsException() {
        CreateHandshakeRequest request = new CreateHandshakeRequest(
                "existing", "receiver", "data");
        when(repository.existsByHandshakeName("existing")).thenReturn(true);

        assertThatThrownBy(() -> handshakeService.createHandshake(request, "initiator"))
                .isInstanceOf(RuntimeException.class);
    }

    // ===== UPDATE HANDSHAKE TESTS =====
    @Test
    void updateHandshake_ValidRequest_UpdatesStatus() {
        Handshake existing = new Handshake(
                "test",
                "encrypted details",
                "user1",
                "user2",
                user1,
                user2
        );
        when(repository.findByHandshakeName("test")).thenReturn(Optional.of(existing));

        handshakeService.updateHandshake("test", HandshakeStatus.ACCEPTED);

        assertThat(existing.getHandshakeStatus()).isEqualTo(HandshakeStatus.ACCEPTED);
        verify(repository).save(existing);
    }

    @Test
    void updateHandshake_NotFound_ThrowsException() {
        when(repository.findByHandshakeName("missing")).thenReturn(Optional.empty());
        when(exceptionFactory.handshakeNotFound("missing"))
                .thenReturn((HandshakeNotFoundException) new RuntimeException("Not found"));

        assertThatThrownBy(() -> handshakeService.updateHandshake("missing", HandshakeStatus.ACCEPTED))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Not found");
    }

    // ===== GET HANDSHAKE TESTS =====
    @Test
    void getHandshakeByName_Exists_ReturnsDto() {
        Handshake handshake = new Handshake(
                "test",
                "encrypted details",
                "user1",
                "user2",
                user1, user2
        );
        when(repository.findByHandshakeName("test")).thenReturn(Optional.of(handshake));

        HandshakeDto dto = handshakeService.getHandshakeByName("test");

        assertThat(dto.handshakeName()).isEqualTo("test");
    }

    @Test
    void getHandshakesByInitiator_ReturnsList() {
        Handshake handshake1 = new Handshake(
                "hs1",
                "encrypted details",
                "user1",
                "user2",
                user1, user2
        );
        Handshake handshake2 = new Handshake(
                "hs2",
                "encrypted details",
                "user1",
                "user2",
                user1, user2
        );
        when(repository.findAllByInitiatorUsername("user1")).thenReturn(List.of(handshake1, handshake2));

        List<HandshakeDto> results = handshakeService.getHandshakesByInitiator("user1");

        assertThat(results).hasSize(2);
        assertThat(results.get(0).handshakeName()).isEqualTo("hs1");
    }
}

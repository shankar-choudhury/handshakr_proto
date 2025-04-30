package com.handshakr.handshakr_prototype.controller;

import com.handshakr.handshakr_prototype.handshake.HandshakeService;
import com.handshakr.handshakr_prototype.handshake.HandshakeStatus;
import com.handshakr.handshakr_prototype.handshake.dto.CreateHandshakeRequest;
import com.handshakr.handshakr_prototype.handshake.dto.HandshakeDto;
import com.handshakr.handshakr_prototype.security.SecurityConfiguration;
import com.handshakr.handshakr_prototype.user.User;
import com.handshakr.handshakr_prototype.user.UserService;
import com.handshakr.handshakr_prototype.user.dto.SetPublicKeyRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static com.handshakr.handshakr_prototype.controller.AuthControllerTest.asJsonString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import(SecurityConfiguration.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private HandshakeService handshakeService;

    // ===== GET USER TESTS =====
    @Test
    @WithMockUser
    void getAuthenticatedUser_ValidUser_ReturnsUserDetails() throws Exception {
        User mockUser = new User("testUser", "email@test.com", "password");
        when(userService.findByUsername("testUser")).thenReturn(mockUser);

        mockMvc.perform(get("/users/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.username").value("testUser"));
    }

    // ===== PUBLIC KEY TESTS =====
    @Test
    @WithMockUser(username = "keyUser")
    void setPublicKey_ValidRequest_UpdatesKey() throws Exception {
        User user = new User("keyUser", "email@test.com", "password");
        when(userService.findByUsername("keyUser")).thenReturn(user);

        SetPublicKeyRequest request = new SetPublicKeyRequest("new-public-key");

        mockMvc.perform(post("/users/me/setPublicKey")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isOk());

        assertThat(user.getPublicKey()).isEqualTo("new-public-key");
        verify(userService).saveUser(user);
    }

    // ===== HANDSHAKE CREATION TESTS =====
    @Test
    @WithMockUser(username = "initiator")
    void createHandshake_ValidRequest_CreatesHandshake() throws Exception {
        CreateHandshakeRequest request = new CreateHandshakeRequest(
                "test-handshake", "receiver", "Test handshake");

        User initiator = new User("initiator", "init@test.com", "pass");
        User receiver = new User("receiver", "rec@test.com", "pass");

        when(userService.findByUsername("initiator")).thenReturn(initiator);
        when(userService.findByUsername("receiver")).thenReturn(receiver);

        HandshakeDto mockHandshake = new HandshakeDto(
                "test-handshake",
                "encrypted details",
                Instant.now(),
                Instant.now().plus(1, ChronoUnit.HOURS),
                HandshakeStatus.PENDING,
                "initiatorUser",
                "acceptorUser");
        when(handshakeService.getHandshakeByName("test-handshake")).thenReturn(mockHandshake);

        mockMvc.perform(post("/users/create-handshake")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.initiatorUsername").value("initiator"));
    }
}

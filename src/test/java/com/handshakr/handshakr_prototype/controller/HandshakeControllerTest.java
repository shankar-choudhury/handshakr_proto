package com.handshakr.handshakr_prototype.controller;

import com.handshakr.handshakr_prototype.exceptions.handshake.HandshakeAlreadyExistsException;
import com.handshakr.handshakr_prototype.exceptions.handshake.HandshakeNotFoundException;
import com.handshakr.handshakr_prototype.exceptions.handshake.HandshakeServiceUnavailableException;
import com.handshakr.handshakr_prototype.handshake.HandshakeService;
import com.handshakr.handshakr_prototype.handshake.HandshakeStatus;
import com.handshakr.handshakr_prototype.handshake.dto.HandshakeDto;
import com.handshakr.handshakr_prototype.security.auth.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HandshakeController.class)
@ExtendWith(MockitoExtension.class)
class HandshakeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private HandshakeService handshakeService;

    @MockitoBean
    private JwtService jwtService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // ========== GET BY NAME TESTS ==========
    @Test
    void getHandshakeByName_Exists_ReturnsHandshake() throws Exception {
        HandshakeDto mockHandshake = createTestHandshake("test-handshake");
        when(handshakeService.getHandshakeByName("test-handshake")).thenReturn(mockHandshake);

        mockMvc.perform(get("/handshake/get-handshake-by-name/test-handshake"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.handshakeName").value("test-handshake"))
                .andExpect(jsonPath("$.data.status").value("PENDING"));

        verify(handshakeService).getHandshakeByName("test-handshake");
    }

    @Test
    void getHandshakeByName_NotFound_Returns404() throws Exception {
        when(handshakeService.getHandshakeByName("nonexistent"))
                .thenThrow(new HandshakeNotFoundException("Not found"));

        mockMvc.perform(get("/handshake/get-handshake-by-name/nonexistent"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));

        verify(handshakeService).getHandshakeByName("nonexistent");
    }

    // ========== GET BY PARTICIPANT TESTS ==========
    @Test
    void getHandshakeByInitiator_Exists_ReturnsHandshake() throws Exception {
        HandshakeDto mockHandshake = createTestHandshake("initiator-handshake");
        when(handshakeService.getHandshakeByInitiator("testUser")).thenReturn(mockHandshake);

        mockMvc.perform(get("/handshake/get-handshake-by-initiator/testUser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.initiatorUsername").value("testUser"));
    }

    @Test
    void getHandshakesByAcceptor_MultipleExist_ReturnsList() throws Exception {
        List<HandshakeDto> handshakes = Arrays.asList(
                createTestHandshake("handshake1"),
                createTestHandshake("handshake2")
        );
        when(handshakeService.getHandshakesByAcceptor("acceptorUser")).thenReturn(handshakes);

        mockMvc.perform(get("/handshake/get-handshakes-by-acceptor/acceptorUser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(2));
    }

    // ========== STATUS UPDATE TESTS ==========
    @Test
    void acceptHandshake_ValidRequest_UpdatesStatus() throws Exception {
        mockMvc.perform(put("/handshake/accept-handshake")
                        .param("handshakeName", "test-handshake"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Handshake accepted"));

        verify(handshakeService).updateHandshake("test-handshake", HandshakeStatus.ACCEPTED);
    }

    @Test
    void rejectHandshake_ValidRequest_UpdatesStatus() throws Exception {
        mockMvc.perform(put("/handshake/reject-handshake")
                        .param("handshakeName", "test-handshake"))
                .andExpect(status().isOk());

        verify(handshakeService).updateHandshake("test-handshake", HandshakeStatus.CANCELLED);
    }

    @Test
    void completeHandshake_AlreadyCompleted_ReturnsConflict() throws Exception {
        doThrow(new HandshakeAlreadyExistsException("Already completed"))
                .when(handshakeService).updateHandshake("completed-handshake", HandshakeStatus.COMPLETED);

        mockMvc.perform(put("/handshake/complete-handshake")
                        .param("handshakeName", "completed-handshake"))
                .andExpect(status().isConflict());

        verify(handshakeService).updateHandshake("completed-handshake", HandshakeStatus.COMPLETED);
    }

    @Test
    void cancelHandshake_NotExists_ReturnsNotFound() throws Exception {
        doThrow(new HandshakeNotFoundException("Not found"))
                .when(handshakeService).updateHandshake("nonexistent", HandshakeStatus.CANCELLED);

        mockMvc.perform(put("/handshake/cancel-handshake")
                        .param("handshakeName", "nonexistent"))
                .andExpect(status().isNotFound());
    }

    // ========== EDGE CASES ==========
    @Test
    void getHandshakeByAcceptor_ServiceUnavailable_Returns503() throws Exception {
        when(handshakeService.getHandshakeByAcceptor(anyString()))
                .thenThrow(new HandshakeServiceUnavailableException("Service down"));

        mockMvc.perform(get("/handshake/get-handshake-by-acceptor/testUser"))
                .andExpect(status().isServiceUnavailable());
    }

    @Test
    void updateHandshake_InvalidStatus_Returns400() throws Exception {
        mockMvc.perform(put("/handshake/accept-handshake") // Missing required param
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    // ========== HELPER METHODS ==========
    private HandshakeDto createTestHandshake(String name) {
        return new HandshakeDto(
                name,
                "encrypted details",
                Instant.now(),
                Instant.now().plus(1, ChronoUnit.HOURS),
                HandshakeStatus.PENDING,
                "initiatorUser",
                "acceptorUser"
        );
    }

    private String asJsonString(final Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

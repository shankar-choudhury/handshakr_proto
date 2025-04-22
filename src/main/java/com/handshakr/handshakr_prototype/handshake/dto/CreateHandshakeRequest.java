package com.handshakr.handshakr_prototype.handshake.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.lang.NonNull;

/**
 * A Data Transfer Object (DTO) for creating a new handshake request.
 * Used to transfer the necessary data when a user initiates a handshake.
 */
public record CreateHandshakeRequest(
        @NotBlank @NotEmpty @NonNull String handshakeName,
        @NotBlank @NotEmpty @NonNull String encryptedDetails,
        @NotBlank @NotEmpty @NonNull String receiverUsername) { }

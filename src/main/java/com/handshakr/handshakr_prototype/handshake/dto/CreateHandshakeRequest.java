package com.handshakr.handshakr_prototype.handshake.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.lang.NonNull;

public record CreateHandshakeRequest(
        @NotBlank @NotEmpty @NonNull String handshakeName,
        @NotBlank @NotEmpty @NonNull String encryptedDetails,
        @NotBlank @NotEmpty @NonNull String initiatorUsername,
        @NotBlank @NotEmpty @NonNull String receiverUsername) { }

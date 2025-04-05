package com.handshakr.handshakr_prototype.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.lang.NonNull;

public record SetPublicKeyRequest(@NotBlank @NotEmpty @NonNull String publicKey) {
}

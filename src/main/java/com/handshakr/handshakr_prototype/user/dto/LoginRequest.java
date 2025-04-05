package com.handshakr.handshakr_prototype.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.lang.NonNull;

public record LoginRequest(@NotBlank @NotEmpty @NonNull String username, @NotBlank @NotEmpty @NonNull String password) {
}

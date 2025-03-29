package com.handshakr.handshakr_prototype.user;

public record UserDto(
        Long id,
        String username,
        String email
        // Exclude collections and sensitive data
) {
    public static UserDto from(User user) {
        return new UserDto(
                user.getId(),
                user.getUsername(),
                user.getEmail()
        );
    }
}

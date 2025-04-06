package com.handshakr.handshakr_prototype.response;

public record ApiResponse<T>(String message, int httpStatus, T data) {
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(message, 200, data);
    }

    public static ApiResponse<Void> success(String message) {
        return new ApiResponse<>(message, 200, null);
    }

    public static ApiResponse<String> error(String message, int status) {
        return new ApiResponse<>(message, status, null);
    }
}

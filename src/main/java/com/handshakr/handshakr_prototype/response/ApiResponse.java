package com.handshakr.handshakr_prototype.response;

/**
 * A generic API response wrapper used to standardize REST API responses.
 *
 * @param message    a human-readable message describing the result
 * @param httpStatus the HTTP status code to be returned
 * @param data       the payload/data of the response
 * @param <T>        the type of the data payload
 */
public record ApiResponse<T>(String message, int httpStatus, T data) {

    /**
     * Creates a successful {@link ApiResponse} with a message and payload.
     *
     * @param message the success message
     * @param data    the response payload
     * @param <T>     the type of the payload
     * @return a populated {@link ApiResponse} with HTTP 200 status
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(message, 200, data);
    }

    /**
     * Creates a successful {@link ApiResponse} without any payload.
     *
     * @param message the success message
     * @return a response with null payload and HTTP 200 status
     */
    public static ApiResponse<Void> success(String message) {
        return new ApiResponse<>(message, 200, null);
    }

    /**
     * Creates an error {@link ApiResponse} with a message and status code.
     *
     * @param message the error message
     * @param status  the HTTP status code representing the error
     * @return an error response with null data
     */
    public static ApiResponse<String> error(String message, int status) {
        return new ApiResponse<>(message, status, null);
    }
}

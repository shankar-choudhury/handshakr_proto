package com.handshakr.handshakr_prototype.exceptions;

import com.handshakr.handshakr_prototype.exceptions.handshake.HandshakeAlreadyExistsException;
import com.handshakr.handshakr_prototype.exceptions.handshake.HandshakeInitiatorNotFoundException;
import com.handshakr.handshakr_prototype.exceptions.handshake.HandshakeNotFoundException;
import com.handshakr.handshakr_prototype.exceptions.handshake.HandshakeReceiverNotFoundException;
import com.handshakr.handshakr_prototype.exceptions.security.InvalidCredentialsException;
import com.handshakr.handshakr_prototype.exceptions.security.UnauthorizedAccessException;
import com.handshakr.handshakr_prototype.exceptions.general.*;
import com.handshakr.handshakr_prototype.exceptions.user.AccountLockedException;
import com.handshakr.handshakr_prototype.exceptions.user.UserAlreadyExistsException;
import com.handshakr.handshakr_prototype.exceptions.user.UserNotFoundException;
import com.handshakr.handshakr_prototype.response.ApiResponse;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // ====== Validation Exceptions ======
    @ExceptionHandler({
            ConstraintViolationException.class,
            MethodArgumentNotValidException.class,
            BadRequestException.class
    })
    public ResponseEntity<ApiResponse<Void>> handleValidationExceptions(Exception ex) {
        Map<String, String> errors = new HashMap<>();

        if (ex instanceof ConstraintViolationException cve) {
            cve.getConstraintViolations().forEach(violation ->
                    errors.put(violation.getPropertyPath().toString(), violation.getMessage()));
        }
        else if (ex instanceof MethodArgumentNotValidException manve) {
            manve.getBindingResult().getFieldErrors().forEach(error ->
                    errors.put(error.getField(), error.getDefaultMessage()));
        }

        String message = ex instanceof BadRequestException ?
                ex.getMessage() : "Validation failed: " + errors;

        logger.warn("Validation error: {}", message);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(message, HttpStatus.BAD_REQUEST.value()));
    }

    // ====== Authentication/Authorization Exceptions ======
    @ExceptionHandler({
            BadCredentialsException.class,
            UnauthorizedAccessException.class
    })
    public ResponseEntity<ApiResponse<Void>> handleAuthExceptions(RuntimeException ex) {
        HttpStatus status = ex instanceof BadCredentialsException ?
                HttpStatus.UNAUTHORIZED : HttpStatus.FORBIDDEN;

        logger.warn("Authentication error: {}", ex.getMessage());
        return ResponseEntity
                .status(status)
                .body(ApiResponse.error(ex.getMessage(), status.value()));
    }

    // ====== Not Found Exceptions ======
    @ExceptionHandler({
            UserNotFoundException.class,
            HandshakeNotFoundException.class,
            HandshakeReceiverNotFoundException.class,
            HandshakeInitiatorNotFoundException.class
    })
    public ResponseEntity<ApiResponse<Void>> handleNotFoundExceptions(RuntimeException ex) {
        logger.warn("Resource not found: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(ex.getMessage(), HttpStatus.NOT_FOUND.value()));
    }

    // ====== Conflict Exceptions ======
    @ExceptionHandler({
            UserAlreadyExistsException.class,
            HandshakeAlreadyExistsException.class,
            ConflictException.class,
            DataIntegrityViolationException.class
    })
    public ResponseEntity<ApiResponse<Void>> handleConflictExceptions(RuntimeException ex) {
        String message = ex instanceof DataIntegrityViolationException ?
                "Database error: " + (ex.getCause() != null ? ex.getCause().getMessage() : "Constraint violation") :
                ex.getMessage();

        logger.warn("Conflict detected: {}", message);
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ApiResponse.error(message, HttpStatus.CONFLICT.value()));
    }

    // ====== Forbidden Exceptions ======
    @ExceptionHandler({
            InvalidCredentialsException.class,
            AccountLockedException.class
    })
    public ResponseEntity<ApiResponse<Void>> handleForbiddenExceptions(RuntimeException ex) {
        logger.warn("Access denied: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error(ex.getMessage(), HttpStatus.FORBIDDEN.value()));
    }

    // ====== Service Availability Exceptions ======
    @ExceptionHandler(ServiceUnavailableException.class)
    public ResponseEntity<ApiResponse<Void>> handleServiceUnavailable(ServiceUnavailableException ex) {
        logger.error("Service unavailable: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ApiResponse.error(ex.getMessage(), HttpStatus.SERVICE_UNAVAILABLE.value()));
    }

    // ====== Database Exceptions ======
    @ExceptionHandler(DatabaseException.class)
    public ResponseEntity<ApiResponse<Void>> handleDatabaseExceptions(DatabaseException ex) {
        logger.error("Database error: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value()));
    }

    // ====== Global Fallback ======
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGlobalException(Exception ex, WebRequest request) {
        logger.error("Unhandled exception for request {}: {}",
                request.getDescription(false), ex.getMessage(), ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("An unexpected error occurred",
                        HttpStatus.INTERNAL_SERVER_ERROR.value()));
    }
}

package com.handshakr.handshakr_prototype.exceptions;

import com.handshakr.handshakr_prototype.exceptions.handshake.*;
import com.handshakr.handshakr_prototype.exceptions.security.*;
import com.handshakr.handshakr_prototype.exceptions.general.*;
import com.handshakr.handshakr_prototype.exceptions.user.*;
import com.handshakr.handshakr_prototype.response.ApiResponse;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
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
        String message;

        if (ex instanceof ConstraintViolationException cve) {
            cve.getConstraintViolations().forEach(violation ->
                    errors.put(
                            violation.getPropertyPath().toString(),
                            violation.getMessage()
                    ));
            message = "Validation failed: " + errors;
        }
        else if (ex instanceof MethodArgumentNotValidException manve) {
            manve.getBindingResult().getFieldErrors().forEach(error ->
                    errors.put(
                            error.getField(),
                            error.getDefaultMessage()
                    ));
            message = "Invalid request: " + errors;
        }
        else {
            message = ex.getMessage();
        }

        logger.warn("Validation error: {}", message);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(message, HttpStatus.BAD_REQUEST.value()));
    }

    // ====== Authentication Failures (401) ======
    @ExceptionHandler({
            BadCredentialsException.class,
            UsernameNotFoundException.class,
            InvalidCredentialsException.class
    })
    public ResponseEntity<ApiResponse<Void>> handleAuthenticationFailures(RuntimeException ex) {
        logger.warn("Authentication failure: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error("Invalid username or password", HttpStatus.UNAUTHORIZED.value()));
    }

    // ====== Account Status Issues (403) ======
    @ExceptionHandler({
            DisabledException.class,
            LockedException.class,
            AccountLockedException.class
    })
    public ResponseEntity<ApiResponse<Void>> handleAccountStatusExceptions(RuntimeException ex) {
        String message = ex instanceof DisabledException ?
                "Account is disabled" : "Account is locked";

        logger.warn("Account status error: {}", message);
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error(message, HttpStatus.FORBIDDEN.value()));
    }

    // ====== Not Found Exceptions (404) ======
    @ExceptionHandler({
            InternalAuthenticationServiceException.class,
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

    // ====== Conflict Exceptions (409) ======
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

    // ====== Service Availability (503) ======
    @ExceptionHandler(ServiceUnavailableException.class)
    public ResponseEntity<ApiResponse<Void>> handleServiceUnavailable(ServiceUnavailableException ex) {
        logger.error("Service unavailable: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ApiResponse.error(ex.getMessage(), HttpStatus.SERVICE_UNAVAILABLE.value()));
    }

    // ====== Database Errors (500) ======
    @ExceptionHandler(DatabaseException.class)
    public ResponseEntity<ApiResponse<Void>> handleDatabaseExceptions(DatabaseException ex) {
        logger.error("Database error: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value()));
    }

    // ====== Authorization Failures (403) ======
    @ExceptionHandler(UnauthorizedAccessException.class)
    public ResponseEntity<ApiResponse<Void>> handleAuthorizationFailures(UnauthorizedAccessException ex) {
        logger.warn("Authorization failure: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error(ex.getMessage(), HttpStatus.FORBIDDEN.value()));
    }

    // ====== Global Fallback (500) ======
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
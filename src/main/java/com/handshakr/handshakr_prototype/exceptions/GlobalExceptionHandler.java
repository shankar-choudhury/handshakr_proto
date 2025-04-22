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

/**
 * Global exception handler for all controllers. Intercepts and processes exceptions
 * thrown by the application, mapping them to appropriate HTTP responses with standardized
 * {@link ApiResponse} objects.
 *
 * <p>This class is annotated with {@link ControllerAdvice} to allow centralized exception
 * handling across all {@code @RestController} components.</p>
 *
 * <p>It prioritizes itself as the highest precedence handler using {@link Order}.</p>
 */
@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handles validation exceptions including:
     * <ul>
     *   <li>{@link ConstraintViolationException} - from Hibernate Validator for @Valid annotated params</li>
     *   <li>{@link MethodArgumentNotValidException} - from invalid request bodies</li>
     *   <li>{@link BadRequestException} - custom thrown bad requests</li>
     * </ul>
     *
     * @param ex the thrown exception
     * @return a {@link ResponseEntity} with 400 Bad Request status
     */
    // ====== Validation Exceptions ======
    @ExceptionHandler({
            ConstraintViolationException.class,
            MethodArgumentNotValidException.class,
            BadRequestException.class
    })
    public ResponseEntity<ApiResponse<String>> handleValidationExceptions(Exception ex) {
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

    /**
     * Handles authentication failures such as invalid credentials or user not found.
     *
     * @param ex the thrown authentication exception
     * @return a {@link ResponseEntity} with 401 Unauthorized status
     */
    // ====== Authentication Failures (401) ======
    @ExceptionHandler({
            BadCredentialsException.class,
            UsernameNotFoundException.class,
            InvalidCredentialsException.class
    })
    public ResponseEntity<ApiResponse<String>> handleAuthenticationFailures(RuntimeException ex) {
        logger.warn("Authentication failure: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error("Invalid username or password", HttpStatus.UNAUTHORIZED.value()));
    }

    /**
     * Handles account status issues like disabled or locked accounts.
     *
     * @param ex the exception representing a forbidden account status
     * @return a {@link ResponseEntity} with 403 Forbidden status
     */
    // ====== Account Status Issues (403) ======
    @ExceptionHandler({
            DisabledException.class,
            LockedException.class,
            AccountLockedException.class
    })
    public ResponseEntity<ApiResponse<String>> handleAccountStatusExceptions(RuntimeException ex) {
        String message = ex instanceof DisabledException ?
                "Account is disabled" : "Account is locked";

        logger.warn("Account status error: {}", message);
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error(message, HttpStatus.FORBIDDEN.value()));
    }

    /**
     * Handles not found errors such as missing users or handshakes.
     *
     * @param ex the exception indicating a missing resource
     * @return a {@link ResponseEntity} with 404 Not Found status
     */
    // ====== Not Found Exceptions (404) ======
    @ExceptionHandler({
            InternalAuthenticationServiceException.class,
            UserNotFoundException.class,
            HandshakeNotFoundException.class,
            HandshakeReceiverNotFoundException.class,
            HandshakeInitiatorNotFoundException.class
    })
    public ResponseEntity<ApiResponse<String>> handleNotFoundExceptions(RuntimeException ex) {
        logger.warn("Resource not found: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(ex.getMessage(), HttpStatus.NOT_FOUND.value()));
    }

    /**
     * Handles conflicts and constraint violations such as duplicate usernames or handshake collisions.
     *
     * @param ex the thrown conflict exception
     * @return a {@link ResponseEntity} with 409 Conflict status
     */
    // ====== Conflict Exceptions (409) ======
    @ExceptionHandler({
            UserAlreadyExistsException.class,
            HandshakeAlreadyExistsException.class,
            ConflictException.class,
            DataIntegrityViolationException.class
    })
    public ResponseEntity<ApiResponse<String>> handleConflictExceptions(RuntimeException ex) {
        String message = ex instanceof DataIntegrityViolationException ?
                "Database error: " + (ex.getCause() != null ? ex.getCause().getMessage() : "Constraint violation") :
                ex.getMessage();

        logger.warn("Conflict detected: {}", message);
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ApiResponse.error(message, HttpStatus.CONFLICT.value()));
    }

    /**
     * Handles service unavailable errors, typically for external system failures or resource exhaustion.
     *
     * @param ex the exception indicating service unavailability
     * @return a {@link ResponseEntity} with 503 Service Unavailable status
     */
    // ====== Service Availability (503) ======
    @ExceptionHandler(ServiceUnavailableException.class)
    public ResponseEntity<ApiResponse<String>> handleServiceUnavailable(ServiceUnavailableException ex) {
        logger.error("Service unavailable: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ApiResponse.error(ex.getMessage(), HttpStatus.SERVICE_UNAVAILABLE.value()));
    }

    /**
     * Handles internal server errors caused by database issues.
     *
     * @param ex the {@link DatabaseException}
     * @return a {@link ResponseEntity} with 500 Internal Server Error status
     */
    // ====== Database Errors (500) ======
    @ExceptionHandler(DatabaseException.class)
    public ResponseEntity<ApiResponse<String>> handleDatabaseExceptions(DatabaseException ex) {
        logger.error("Database error: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value()));
    }

    /**
     * Handles authorization failures when access is denied due to missing permissions.
     *
     * @param ex the {@link UnauthorizedAccessException}
     * @return a {@link ResponseEntity} with 403 Forbidden status
     */
    // ====== Authorization Failures (403) ======
    @ExceptionHandler(UnauthorizedAccessException.class)
    public ResponseEntity<ApiResponse<String>> handleAuthorizationFailures(UnauthorizedAccessException ex) {
        logger.warn("Authorization failure: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error(ex.getMessage(), HttpStatus.FORBIDDEN.value()));
    }

    /**
     * Catch-all handler for any unexpected exceptions not explicitly handled elsewhere.
     *
     * @param ex      the unhandled exception
     * @param request the originating request context
     * @return a {@link ResponseEntity} with 500 Internal Server Error status
     */
    // ====== Global Fallback (500) ======
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<String>> handleGlobalException(Exception ex, WebRequest request) {
        logger.error("Unhandled exception for request {}: {}",
                request.getDescription(false), ex.getMessage(), ex);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("An unexpected error occurred",
                        HttpStatus.INTERNAL_SERVER_ERROR.value()));
    }
}
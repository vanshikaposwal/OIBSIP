package com.library.management.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Global Exception Handler for the entire application.
 * 
 * In Spring Boot, @RestControllerAdvice intercepts exceptions thrown by any @RestController.
 * It formats the error into a clean, predictable JSON response object instead of leaking
 * ugly Java stack traces to the user/client.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * DTO representing the error payload returned to the API client.
     */
    public record ErrorBody(
            LocalDateTime timestamp,
            int status,
            String error,
            String message,
            String path,
            List<Map<String, String>> fieldErrors
    ) {}

    /**
     * Catches ResourceNotFoundException when an entity (User, Book, Issue, etc.) is not found.
     * Returns HTTP 404 NOT FOUND.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorBody> handleNotFound(ResourceNotFoundException ex, HttpServletRequest req) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), req, null);
    }

    /**
     * Catches BusinessException when business rules are violated (e.g. duplicate ISBN, unpaid fines).
     * Returns HTTP 409 CONFLICT or HTTP 422 UNPROCESSABLE_ENTITY.
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorBody> handleBusiness(BusinessException ex, HttpServletRequest req) {
        HttpStatus status = HttpStatus.resolve(ex.getStatusCode());
        if (status == null) {
            status = HttpStatus.CONFLICT;
        }
        return build(status, ex.getMessage(), req, null);
    }

    /**
     * Catches Bean Validation errors triggered by @Valid annotations (e.g., blank title, invalid email).
     * Returns HTTP 400 BAD REQUEST along with a list of field error messages.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorBody> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        List<Map<String, String>> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> Map.of("field", fe.getField(), "message", defaultMessage(fe)))
                .toList();
        return build(HttpStatus.BAD_REQUEST, "Validation failed", req, fieldErrors);
    }

    /**
     * Catches AccessDeniedException when a user attempts an action outside their role permissions.
     * Returns HTTP 403 FORBIDDEN.
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorBody> handleAccessDenied(AccessDeniedException ex, HttpServletRequest req) {
        return build(HttpStatus.FORBIDDEN, "Access denied", req, null);
    }

    /**
     * Catches AuthenticationException when a user provides bad credentials.
     * Returns HTTP 401 UNAUTHORIZED.
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorBody> handleAuth(AuthenticationException ex, HttpServletRequest req) {
        return build(HttpStatus.UNAUTHORIZED, "Unauthorized", req, null);
    }

    /**
     * Fallback handler for any uncaught system exceptions.
     * Returns HTTP 500 INTERNAL SERVER ERROR with a generic message so internal details aren't leaked.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorBody> handleGeneric(Exception ex, HttpServletRequest req) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred", req, null);
    }

    private ResponseEntity<ErrorBody> build(HttpStatus status, String message,
                                             HttpServletRequest req,
                                             List<Map<String, String>> fieldErrors) {
        return ResponseEntity.status(status).body(new ErrorBody(
                LocalDateTime.now(), status.value(), status.getReasonPhrase(),
                message, req.getRequestURI(), fieldErrors
        ));
    }

    private String defaultMessage(FieldError fe) {
        return fe.getDefaultMessage() != null ? fe.getDefaultMessage() : "Invalid value";
    }
}

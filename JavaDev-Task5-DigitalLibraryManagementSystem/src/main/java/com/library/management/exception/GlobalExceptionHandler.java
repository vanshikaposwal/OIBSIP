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
 * Global error handler — returns consistent JSON error body, never exposes stack traces.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    record ErrorBody(LocalDateTime timestamp, int status, String error, String message, String path,
                     List<Map<String, String>> fieldErrors) {}

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorBody> handleNotFound(ResourceNotFoundException ex, HttpServletRequest req) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), req, null);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorBody> handleBusiness(BusinessException ex, HttpServletRequest req) {
        HttpStatus status = HttpStatus.resolve(ex.getStatusCode());
        if (status == null) status = HttpStatus.CONFLICT;
        return build(status, ex.getMessage(), req, null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorBody> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        List<Map<String, String>> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> Map.of("field", fe.getField(), "message", defaultMessage(fe)))
                .toList();
        return build(HttpStatus.BAD_REQUEST, "Validation failed", req, fieldErrors);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorBody> handleAccessDenied(AccessDeniedException ex, HttpServletRequest req) {
        return build(HttpStatus.FORBIDDEN, "Access denied", req, null);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorBody> handleAuth(AuthenticationException ex, HttpServletRequest req) {
        return build(HttpStatus.UNAUTHORIZED, "Unauthorized", req, null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorBody> handleGeneric(Exception ex, HttpServletRequest req) {
        // Log but never expose stack trace to client
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

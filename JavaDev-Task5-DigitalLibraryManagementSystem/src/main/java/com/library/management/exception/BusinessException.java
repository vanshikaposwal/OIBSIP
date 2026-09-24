package com.library.management.exception;

/**
 * Thrown for business rule violations (→ 409 or 422).
 */
public class BusinessException extends RuntimeException {
    private final int statusCode;

    public BusinessException(String message) {
        super(message);
        this.statusCode = 409;
    }

    public BusinessException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}

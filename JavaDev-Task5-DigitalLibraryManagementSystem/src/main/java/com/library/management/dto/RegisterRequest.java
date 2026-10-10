package com.library.management.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object (DTO) for User Registration.
 * Validates name, valid email format, and password length (minimum 6 characters).
 */
public record RegisterRequest(
        @NotBlank @Size(min = 2, max = 120) String name,
        @NotBlank @Email String email,
        @NotBlank @Size(min = 6, max = 100) String password,
        String phone,
        String address
) {}

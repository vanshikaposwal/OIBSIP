package com.library.management.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Data Transfer Object (DTO) for User Login credentials.
 */
public record LoginRequest(
        @NotBlank @Email String email,
        @NotBlank String password
) {}

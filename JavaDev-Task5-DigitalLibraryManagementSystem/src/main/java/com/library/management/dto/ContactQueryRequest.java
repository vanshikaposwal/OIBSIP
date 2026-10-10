package com.library.management.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object (DTO) for submitting a support message or inquiry.
 */
public record ContactQueryRequest(
        @NotBlank @Size(max = 120) String name,
        @NotBlank @Email @Size(max = 180) String email,
        @NotBlank @Size(max = 300) String subject,
        @NotBlank @Size(max = 5000) String message
) {}

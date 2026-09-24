package com.library.management.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

/**
 * Used by admin to update a user account (partial update).
 */
public record UpdateUserRequest(
        @Size(min = 2, max = 120) String name,
        @Email @Size(max = 180) String email,
        @Size(min = 6, max = 100) String password,
        String phone,
        String address,
        Boolean enabled,
        String role   // "ROLE_USER" or "ROLE_ADMIN"
) {}

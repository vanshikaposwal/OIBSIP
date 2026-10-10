package com.library.management.dto;

import com.library.management.entity.User;

import java.time.LocalDateTime;

/**
 * Data Transfer Object (DTO) for returning User profile data.
 * Crucially omits the passwordHash field to protect security credentials.
 */
public record UserResponse(
        Long id,
        String name,
        String email,
        String role,
        String phone,
        String address,
        boolean enabled,
        LocalDateTime createdAt
) {
    /**
     * Factory method: Maps User entity to UserResponse DTO.
     */
    public static UserResponse from(User u) {
        return new UserResponse(
                u.getId(), u.getName(), u.getEmail(),
                u.getRole().name(), u.getPhone(), u.getAddress(),
                u.isEnabled(), u.getCreatedAt()
        );
    }
}

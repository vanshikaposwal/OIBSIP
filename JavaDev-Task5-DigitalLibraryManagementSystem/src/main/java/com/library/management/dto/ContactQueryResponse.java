package com.library.management.dto;

import com.library.management.entity.ContactQuery;

import java.time.LocalDateTime;

public record ContactQueryResponse(
        Long id,
        Long userId,
        String name,
        String email,
        String subject,
        String message,
        String status,
        LocalDateTime createdAt
) {
    public static ContactQueryResponse from(ContactQuery cq) {
        return new ContactQueryResponse(
                cq.getId(),
                cq.getUser() != null ? cq.getUser().getId() : null,
                cq.getName(), cq.getEmail(),
                cq.getSubject(), cq.getMessage(),
                cq.getStatus().name(),
                cq.getCreatedAt()
        );
    }
}

package com.library.management.dto;

import com.library.management.entity.Fine;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record FineResponse(
        Long id,
        Long issueId,
        Long userId,
        String userName,
        String bookTitle,
        BigDecimal amount,
        boolean paid,
        LocalDateTime paidAt,
        LocalDateTime createdAt
) {
    public static FineResponse from(Fine f) {
        return new FineResponse(
                f.getId(),
                f.getIssue().getId(),
                f.getUser().getId(),
                f.getUser().getName(),
                f.getIssue().getBook().getTitle(),
                f.getAmount(),
                f.isPaid(),
                f.getPaidAt(),
                f.getCreatedAt()
        );
    }
}

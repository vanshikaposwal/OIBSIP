package com.library.management.dto;

import com.library.management.entity.Reservation;

import java.time.LocalDateTime;

public record ReservationResponse(
        Long id,
        Long userId,
        String userName,
        Long bookId,
        String bookTitle,
        String status,
        LocalDateTime reservationDate,
        LocalDateTime fulfilledAt,
        LocalDateTime createdAt
) {
    public static ReservationResponse from(Reservation r) {
        return new ReservationResponse(
                r.getId(),
                r.getUser().getId(),
                r.getUser().getName(),
                r.getBook().getId(),
                r.getBook().getTitle(),
                r.getStatus().name(),
                r.getReservationDate(),
                r.getFulfilledAt(),
                r.getCreatedAt()
        );
    }
}

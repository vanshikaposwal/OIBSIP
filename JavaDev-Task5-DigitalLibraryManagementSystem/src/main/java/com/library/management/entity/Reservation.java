package com.library.management.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "reservations")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 12)
    private ReservationStatus status = ReservationStatus.ACTIVE;

    @Column(name = "reservation_date", nullable = false)
    private LocalDateTime reservationDate;

    @Column(name = "fulfilled_at")
    private LocalDateTime fulfilledAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = updatedAt = LocalDateTime.now();
        if (reservationDate == null) reservationDate = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum ReservationStatus {
        ACTIVE, FULFILLED, CANCELLED
    }
}

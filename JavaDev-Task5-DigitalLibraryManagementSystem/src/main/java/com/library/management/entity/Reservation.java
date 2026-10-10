package com.library.management.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * JPA Entity representing a Book Reservation (Waitlist entry).
 * Maps to the 'reservations' table in MySQL.
 */
@Entity
@Table(name = "reservations")
public class Reservation {

    // Primary Key (Auto-Increment)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Member who placed the reservation
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Book that is reserved
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    // Status: ACTIVE, FULFILLED, or CANCELLED
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 12)
    private ReservationStatus status = ReservationStatus.ACTIVE;

    // Timestamp when the reservation was placed
    @Column(name = "reservation_date", nullable = false)
    private LocalDateTime reservationDate;

    // Timestamp when a returned copy fulfilled this reservation
    @Column(name = "fulfilled_at")
    private LocalDateTime fulfilledAt;

    // Timestamps
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Default no-argument constructor required by JPA
    public Reservation() {}

    public Reservation(Long id, User user, Book book, ReservationStatus status, LocalDateTime reservationDate, LocalDateTime fulfilledAt, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.user = user;
        this.book = book;
        this.status = status != null ? status : ReservationStatus.ACTIVE;
        this.reservationDate = reservationDate != null ? reservationDate : LocalDateTime.now();
        this.fulfilledAt = fulfilledAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

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

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Book getBook() { return book; }
    public void setBook(Book book) { this.book = book; }

    public ReservationStatus getStatus() { return status; }
    public void setStatus(ReservationStatus status) { this.status = status; }

    public LocalDateTime getReservationDate() { return reservationDate; }
    public void setReservationDate(LocalDateTime reservationDate) { this.reservationDate = reservationDate; }

    public LocalDateTime getFulfilledAt() { return fulfilledAt; }
    public void setFulfilledAt(LocalDateTime fulfilledAt) { this.fulfilledAt = fulfilledAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private User user;
        private Book book;
        private ReservationStatus status = ReservationStatus.ACTIVE;
        private LocalDateTime reservationDate;
        private LocalDateTime fulfilledAt;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder user(User user) { this.user = user; return this; }
        public Builder book(Book book) { this.book = book; return this; }
        public Builder status(ReservationStatus status) { this.status = status; return this; }
        public Builder reservationDate(LocalDateTime reservationDate) { this.reservationDate = reservationDate; return this; }
        public Builder fulfilledAt(LocalDateTime fulfilledAt) { this.fulfilledAt = fulfilledAt; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public Reservation build() {
            return new Reservation(id, user, book, status, reservationDate, fulfilledAt, createdAt, updatedAt);
        }
    }
}

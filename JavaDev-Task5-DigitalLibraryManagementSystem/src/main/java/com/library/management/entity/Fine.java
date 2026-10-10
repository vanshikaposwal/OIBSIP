package com.library.management.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * JPA Entity representing a Fine levied for an overdue book.
 * Maps to the 'fines' table in MySQL.
 */
@Entity
@Table(name = "fines")
public class Fine {

    // Primary Key (Auto-Increment)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 1:1 relationship with the BookIssue that caused this fine
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "issue_id", nullable = false, unique = true)
    private BookIssue issue;

    // User who owes this fine
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Total monetary amount (e.g. ₹20.00)
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    // Payment status flag (false = unpaid, true = paid)
    @Column(nullable = false)
    private boolean paid = false;

    // Timestamp when the fine was settled
    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    // Timestamps
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Default no-argument constructor required by JPA
    public Fine() {}

    public Fine(Long id, BookIssue issue, User user, BigDecimal amount, boolean paid, LocalDateTime paidAt, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.issue = issue;
        this.user = user;
        this.amount = amount;
        this.paid = paid;
        this.paidAt = paidAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public BookIssue getIssue() { return issue; }
    public void setIssue(BookIssue issue) { this.issue = issue; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public boolean isPaid() { return paid; }
    public void setPaid(boolean paid) { this.paid = paid; }

    public LocalDateTime getPaidAt() { return paidAt; }
    public void setPaidAt(LocalDateTime paidAt) { this.paidAt = paidAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private BookIssue issue;
        private User user;
        private BigDecimal amount;
        private boolean paid = false;
        private LocalDateTime paidAt;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder issue(BookIssue issue) { this.issue = issue; return this; }
        public Builder user(User user) { this.user = user; return this; }
        public Builder amount(BigDecimal amount) { this.amount = amount; return this; }
        public Builder paid(boolean paid) { this.paid = paid; return this; }
        public Builder paidAt(LocalDateTime paidAt) { this.paidAt = paidAt; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public Fine build() {
            return new Fine(id, issue, user, amount, paid, paidAt, createdAt, updatedAt);
        }
    }
}

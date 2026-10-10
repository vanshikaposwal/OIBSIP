package com.library.management.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * JPA Entity representing a Book Issue (Loan / Circulation record).
 * Maps to the 'book_issues' table in MySQL.
 */
@Entity
@Table(name = "book_issues")
public class BookIssue {

    // Primary Key (Auto-Increment)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Many book issues belong to one User (Borrower)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Many book issues belong to one Book
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    // Date the book was borrowed
    @Column(name = "issue_date", nullable = false)
    private LocalDate issueDate;

    // Date the book is expected to be returned (default: issueDate + 14 days)
    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    // Actual date the book was returned (null if currently borrowed)
    @Column(name = "return_date")
    private LocalDate returnDate;

    // Circulation status: ISSUED or RETURNED
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private IssueStatus status = IssueStatus.ISSUED;

    // Timestamps
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Default no-argument constructor required by JPA
    public BookIssue() {}

    public BookIssue(Long id, User user, Book book, LocalDate issueDate, LocalDate dueDate, LocalDate returnDate, IssueStatus status, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.user = user;
        this.book = book;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
        this.status = status != null ? status : IssueStatus.ISSUED;
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

    public enum IssueStatus {
        ISSUED, RETURNED
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Book getBook() { return book; }
    public void setBook(Book book) { this.book = book; }

    public LocalDate getIssueDate() { return issueDate; }
    public void setIssueDate(LocalDate issueDate) { this.issueDate = issueDate; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public LocalDate getReturnDate() { return returnDate; }
    public void setReturnDate(LocalDate returnDate) { this.returnDate = returnDate; }

    public IssueStatus getStatus() { return status; }
    public void setStatus(IssueStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private User user;
        private Book book;
        private LocalDate issueDate;
        private LocalDate dueDate;
        private LocalDate returnDate;
        private IssueStatus status = IssueStatus.ISSUED;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder user(User user) { this.user = user; return this; }
        public Builder book(Book book) { this.book = book; return this; }
        public Builder issueDate(LocalDate issueDate) { this.issueDate = issueDate; return this; }
        public Builder dueDate(LocalDate dueDate) { this.dueDate = dueDate; return this; }
        public Builder returnDate(LocalDate returnDate) { this.returnDate = returnDate; return this; }
        public Builder status(IssueStatus status) { this.status = status; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public BookIssue build() {
            return new BookIssue(id, user, book, issueDate, dueDate, returnDate, status, createdAt, updatedAt);
        }
    }
}

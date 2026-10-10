package com.library.management.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * JPA Entity representing a Book in the library catalog.
 * Maps to the 'books' table in MySQL.
 */
@Entity
@Table(name = "books")
public class Book {

    // Primary Key (Auto-Increment)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Title of the book (Max 300 characters, mandatory)
    @Column(nullable = false, length = 300)
    private String title;

    // Author name (Max 200 characters, mandatory)
    @Column(nullable = false, length = 200)
    private String author;

    // International Standard Book Number (Unique identifier, mandatory)
    @Column(nullable = false, unique = true, length = 20)
    private String isbn;

    // Category / Subject genre (e.g. Java, Database, Science, etc.)
    @Column(nullable = false, length = 80)
    private String category;

    // Publishing company name
    @Column(length = 200)
    private String publisher;

    // Year published (e.g. 2021)
    @Column(name = "publish_year")
    private Short publishYear;

    // Summary / Synopsis
    @Column(columnDefinition = "TEXT")
    private String description;

    // Total copies owned by the library
    @Column(name = "total_quantity", nullable = false)
    private int totalQuantity = 1;

    // Currently available copies on the shelf (not issued)
    @Column(name = "available_quantity", nullable = false)
    private int availableQuantity = 1;

    // Timestamps
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Default no-argument constructor required by JPA
    public Book() {}

    public Book(Long id, String title, String author, String isbn, String category, String publisher, Short publishYear, String description, int totalQuantity, int availableQuantity, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.category = category;
        this.publisher = publisher;
        this.publishYear = publishYear;
        this.description = description;
        this.totalQuantity = totalQuantity;
        this.availableQuantity = availableQuantity;
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

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getPublisher() { return publisher; }
    public void setPublisher(String publisher) { this.publisher = publisher; }

    public Short getPublishYear() { return publishYear; }
    public void setPublishYear(Short publishYear) { this.publishYear = publishYear; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getTotalQuantity() { return totalQuantity; }
    public void setTotalQuantity(int totalQuantity) { this.totalQuantity = totalQuantity; }

    public int getAvailableQuantity() { return availableQuantity; }
    public void setAvailableQuantity(int availableQuantity) { this.availableQuantity = availableQuantity; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private String title;
        private String author;
        private String isbn;
        private String category;
        private String publisher;
        private Short publishYear;
        private String description;
        private int totalQuantity = 1;
        private int availableQuantity = 1;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder title(String title) { this.title = title; return this; }
        public Builder author(String author) { this.author = author; return this; }
        public Builder isbn(String isbn) { this.isbn = isbn; return this; }
        public Builder category(String category) { this.category = category; return this; }
        public Builder publisher(String publisher) { this.publisher = publisher; return this; }
        public Builder publishYear(Short publishYear) { this.publishYear = publishYear; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder totalQuantity(int totalQuantity) { this.totalQuantity = totalQuantity; return this; }
        public Builder availableQuantity(int availableQuantity) { this.availableQuantity = availableQuantity; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public Book build() {
            return new Book(id, title, author, isbn, category, publisher, publishYear, description, totalQuantity, availableQuantity, createdAt, updatedAt);
        }
    }
}

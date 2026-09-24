package com.library.management.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "books")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 300)
    private String title;

    @Column(nullable = false, length = 200)
    private String author;

    @Column(nullable = false, unique = true, length = 20)
    private String isbn;

    @Column(nullable = false, length = 80)
    private String category;

    @Column(length = 200)
    private String publisher;

    @Column(name = "publish_year")
    private Short publishYear;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "total_quantity", nullable = false)
    private int totalQuantity = 1;

    @Column(name = "available_quantity", nullable = false)
    private int availableQuantity = 1;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

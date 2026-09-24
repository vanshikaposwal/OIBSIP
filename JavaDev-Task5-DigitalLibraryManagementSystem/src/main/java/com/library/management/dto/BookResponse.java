package com.library.management.dto;

import com.library.management.entity.Book;

import java.time.LocalDateTime;

public record BookResponse(
        Long id,
        String title,
        String author,
        String isbn,
        String category,
        String publisher,
        Short publishYear,
        String description,
        int totalQuantity,
        int availableQuantity,
        LocalDateTime createdAt
) {
    public static BookResponse from(Book b) {
        return new BookResponse(
                b.getId(), b.getTitle(), b.getAuthor(), b.getIsbn(),
                b.getCategory(), b.getPublisher(), b.getPublishYear(),
                b.getDescription(), b.getTotalQuantity(), b.getAvailableQuantity(),
                b.getCreatedAt()
        );
    }
}

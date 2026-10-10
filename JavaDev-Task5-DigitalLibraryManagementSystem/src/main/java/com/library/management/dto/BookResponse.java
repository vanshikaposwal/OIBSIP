package com.library.management.dto;

import com.library.management.entity.Book;

import java.time.LocalDateTime;

/**
 * Data Transfer Object (DTO) for returning Book details to the client.
 * 
 * Provides an immutable snapshot of a book without exposing internal entity mechanics.
 */
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
    /**
     * Factory method: Converts a Book entity into a BookResponse DTO.
     */
    public static BookResponse from(Book b) {
        return new BookResponse(
                b.getId(), b.getTitle(), b.getAuthor(), b.getIsbn(),
                b.getCategory(), b.getPublisher(), b.getPublishYear(),
                b.getDescription(), b.getTotalQuantity(), b.getAvailableQuantity(),
                b.getCreatedAt()
        );
    }
}

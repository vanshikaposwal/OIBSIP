package com.library.management.service;

import com.library.management.dto.*;
import com.library.management.entity.Book;
import com.library.management.exception.BusinessException;
import com.library.management.exception.ResourceNotFoundException;
import com.library.management.repository.BookRepository;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for managing book catalog operations.
 * Handles searching, viewing, creating, updating, and deleting books.
 */
@Service
public class BookService {

    private final BookRepository bookRepository;

    // Constructor injection: Spring automatically injects the BookRepository bean here
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    /**
     * Search books with optional keyword query and category filter.
     * Supports pagination and sorting.
     */
    public Page<BookResponse> search(String query, String category, Pageable pageable) {
        return bookRepository.searchBooks(query, category, pageable).map(BookResponse::from);
    }

    /**
     * Finds a single book by its primary key ID.
     */
    public BookResponse findById(Long id) {
        Book book = getOrThrow(id);
        return BookResponse.from(book);
    }

    /**
     * Creates a new book in the library catalog.
     * Validates that ISBN is unique before saving.
     */
    @Transactional
    public BookResponse create(BookRequest req) {
        // Step 1: Check if ISBN is already used by another book
        if (bookRepository.existsByIsbn(req.isbn())) {
            throw new BusinessException("ISBN already exists: " + req.isbn());
        }

        // Step 2: Convert BookRequest DTO into a Book entity
        Book book = Book.builder()
                .title(req.title())
                .author(req.author())
                .isbn(req.isbn())
                .category(req.category())
                .publisher(req.publisher())
                .publishYear(req.publishYear() != null ? req.publishYear().shortValue() : null)
                .description(req.description())
                .totalQuantity(req.totalQuantity())
                .availableQuantity(req.totalQuantity()) // Initially, all copies are available
                .build();

        // Step 3: Save to database and return response DTO
        Book savedBook = bookRepository.save(book);
        return BookResponse.from(savedBook);
    }

    /**
     * Updates an existing book's details and total quantity.
     * Safely adjusts available copies without causing negative stock.
     */
    @Transactional
    public BookResponse update(Long id, BookRequest req) {
        // Step 1: Find existing book or throw 404 error
        Book book = getOrThrow(id);

        // Step 2: If the ISBN has changed, ensure the new ISBN is not already used
        if (!book.getIsbn().equals(req.isbn()) && bookRepository.existsByIsbn(req.isbn())) {
            throw new BusinessException("ISBN already in use: " + req.isbn());
        }

        // Step 3: Calculate the change in total quantity
        // Example: If total was 5 and is updated to 7, diff = +2, so available increases by 2.
        int diff = req.totalQuantity() - book.getTotalQuantity();
        int newAvailable = book.getAvailableQuantity() + diff;

        // Step 4: Ensure available quantity does not fall below zero (can't have less copies than currently issued)
        if (newAvailable < 0) {
            throw new BusinessException("Cannot reduce total quantity below currently issued copies");
        }

        // Step 5: Update the entity fields with new values
        book.setTitle(req.title());
        book.setAuthor(req.author());
        book.setIsbn(req.isbn());
        book.setCategory(req.category());
        book.setPublisher(req.publisher());
        book.setPublishYear(req.publishYear() != null ? req.publishYear().shortValue() : null);
        book.setDescription(req.description());
        book.setTotalQuantity(req.totalQuantity());
        book.setAvailableQuantity(newAvailable);

        // Step 6: Save changes and return response
        Book updatedBook = bookRepository.save(book);
        return BookResponse.from(updatedBook);
    }

    /**
     * Deletes a book from the catalog by ID.
     */
    @Transactional
    public void delete(Long id) {
        Book book = getOrThrow(id);
        bookRepository.delete(book);
    }

    /**
     * Helper method: Finds a book by ID or throws ResourceNotFoundException.
     */
    public Book getOrThrow(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found: " + id));
    }
}

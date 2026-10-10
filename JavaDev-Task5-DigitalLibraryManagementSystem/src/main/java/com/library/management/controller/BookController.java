package com.library.management.controller;

import com.library.management.dto.*;
import com.library.management.service.BookService;
import jakarta.validation.Valid;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for Book Catalog operations.
 * Endpoints:
 * - GET    /api/books       : Public search and list books with pagination & filters
 * - GET    /api/books/{id}  : Public get book details
 * - POST   /api/books       : Admin only: add new book
 * - PUT    /api/books/{id}  : Admin only: edit existing book
 * - DELETE /api/books/{id}  : Admin only: delete book
 */
@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    // Constructor injection for BookService
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    /**
     * Public endpoint: Search & list books with filters, pagination, and sorting.
     */
    @GetMapping
    public ResponseEntity<Page<BookResponse>> search(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "title") String sortBy,
            @RequestParam(defaultValue = "asc") String dir) {

        Sort sort = dir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(bookService.search(query, category, pageable));
    }

    /**
     * Public endpoint: Get details of a single book by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<BookResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.findById(id));
    }

    /**
     * Admin only: Create a new book.
     * @PreAuthorize ensures that only users with ROLE_ADMIN can call this.
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BookResponse> create(@Valid @RequestBody BookRequest req) {
        BookResponse response = bookService.create(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Admin only: Update existing book details and stock.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BookResponse> update(@PathVariable Long id,
                                                @Valid @RequestBody BookRequest req) {
        return ResponseEntity.ok(bookService.update(id, req));
    }

    /**
     * Admin only: Delete book from library catalog.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        bookService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

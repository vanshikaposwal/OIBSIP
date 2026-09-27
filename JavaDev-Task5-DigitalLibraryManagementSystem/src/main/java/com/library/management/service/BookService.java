package com.library.management.service;

import com.library.management.dto.*;
import com.library.management.entity.Book;
import com.library.management.exception.BusinessException;
import com.library.management.exception.ResourceNotFoundException;
import com.library.management.repository.BookRepository;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public Page<BookResponse> search(String query, String category, Pageable pageable) {
        return bookRepository.searchBooks(query, category, pageable).map(BookResponse::from);
    }

    public BookResponse findById(Long id) {
        return BookResponse.from(getOrThrow(id));
    }

    @Transactional
    public BookResponse create(BookRequest req) {
        if (bookRepository.existsByIsbn(req.isbn()))
            throw new BusinessException("ISBN already exists: " + req.isbn());
        Book book = Book.builder()
                .title(req.title())
                .author(req.author())
                .isbn(req.isbn())
                .category(req.category())
                .publisher(req.publisher())
                .publishYear(req.publishYear() != null ? req.publishYear().shortValue() : null)
                .description(req.description())
                .totalQuantity(req.totalQuantity())
                .availableQuantity(req.totalQuantity())
                .build();
        return BookResponse.from(bookRepository.save(book));
    }

    @Transactional
    public BookResponse update(Long id, BookRequest req) {
        Book book = getOrThrow(id);
        if (!book.getIsbn().equals(req.isbn()) && bookRepository.existsByIsbn(req.isbn()))
            throw new BusinessException("ISBN already in use: " + req.isbn());

        int diff = req.totalQuantity() - book.getTotalQuantity();
        int newAvailable = book.getAvailableQuantity() + diff;
        if (newAvailable < 0)
            throw new BusinessException("Cannot reduce total quantity below currently issued copies");

        book.setTitle(req.title());
        book.setAuthor(req.author());
        book.setIsbn(req.isbn());
        book.setCategory(req.category());
        book.setPublisher(req.publisher());
        book.setPublishYear(req.publishYear() != null ? req.publishYear().shortValue() : null);
        book.setDescription(req.description());
        book.setTotalQuantity(req.totalQuantity());
        book.setAvailableQuantity(newAvailable);
        return BookResponse.from(bookRepository.save(book));
    }

    @Transactional
    public void delete(Long id) {
        bookRepository.delete(getOrThrow(id));
    }

    /** Internal: load entity with lock for issue/return operations. */
    public Book getOrThrow(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found: " + id));
    }
}

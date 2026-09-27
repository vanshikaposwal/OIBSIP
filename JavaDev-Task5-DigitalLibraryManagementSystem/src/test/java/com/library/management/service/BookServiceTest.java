package com.library.management.service;

import com.library.management.dto.BookRequest;
import com.library.management.dto.BookResponse;
import com.library.management.entity.Book;
import com.library.management.exception.BusinessException;
import com.library.management.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    private Book sampleBook;

    @BeforeEach
    void setUp() {
        sampleBook = Book.builder()
                .id(1L)
                .title("Effective Java")
                .author("Joshua Bloch")
                .isbn("978-0134685991")
                .category("Java")
                .publisher("Addison-Wesley")
                .publishYear((short) 2018)
                .totalQuantity(3)
                .availableQuantity(3)
                .build();
    }

    @Test
    @DisplayName("Create book successfully")
    void create_Success() {
        BookRequest req = new BookRequest("Effective Java", "Joshua Bloch", "978-0134685991", "Java", "Addison-Wesley", 2018, null, 3);

        when(bookRepository.existsByIsbn(req.isbn())).thenReturn(false);
        when(bookRepository.save(any(Book.class))).thenReturn(sampleBook);

        BookResponse res = bookService.create(req);

        assertNotNull(res);
        assertEquals("Effective Java", res.title());
        assertEquals(3, res.availableQuantity());
        verify(bookRepository).save(any(Book.class));
    }

    @Test
    @DisplayName("Create book with duplicate ISBN throws BusinessException")
    void create_DuplicateIsbn_ThrowsException() {
        BookRequest req = new BookRequest("Effective Java", "Joshua Bloch", "978-0134685991", "Java", null, null, null, 2);

        when(bookRepository.existsByIsbn(req.isbn())).thenReturn(true);

        assertThrows(BusinessException.class, () -> bookService.create(req));
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    @DisplayName("Update book quantity safely preserves available copies")
    void update_QuantityAdjustment_Success() {
        Book existing = Book.builder()
                .id(1L)
                .title("Effective Java")
                .author("Joshua Bloch")
                .isbn("978-0134685991")
                .category("Java")
                .totalQuantity(5)
                .availableQuantity(2) // 3 issued
                .build();

        when(bookRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Increase total by 2 (5 -> 7), so available should become 2 + 2 = 4
        BookRequest req = new BookRequest("Effective Java", "Joshua Bloch", "978-0134685991", "Java", null, null, null, 7);
        BookResponse res = bookService.update(1L, req);

        assertEquals(7, res.totalQuantity());
        assertEquals(4, res.availableQuantity());
    }

    @Test
    @DisplayName("Update book total quantity below issued count throws BusinessException")
    void update_ReduceBelowIssued_ThrowsException() {
        Book existing = Book.builder()
                .id(1L)
                .title("Effective Java")
                .author("Joshua Bloch")
                .isbn("978-0134685991")
                .category("Java")
                .totalQuantity(5)
                .availableQuantity(1) // 4 currently issued
                .build();

        when(bookRepository.findById(1L)).thenReturn(Optional.of(existing));

        // Trying to reduce total from 5 to 2 (diff = -3, newAvailable = 1 - 3 = -2 < 0)
        BookRequest req = new BookRequest("Effective Java", "Joshua Bloch", "978-0134685991", "Java", null, null, null, 2);

        assertThrows(BusinessException.class, () -> bookService.update(1L, req));
    }
}

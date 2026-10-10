package com.library.management.repository;

import com.library.management.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA Repository for Books.
 * Provides out-of-the-box CRUD methods plus custom query methods.
 */
@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    /**
     * Checks if a book with this ISBN already exists.
     * Spring Data JPA automatically derives the SQL: SELECT count(*) > 0 FROM books WHERE isbn = ?
     */
    boolean existsByIsbn(String isbn);

    /**
     * Custom JPQL query for multi-criteria search.
     * Searches title, author, or ISBN case-insensitively, and filters by category if provided.
     */
    @Query("""
        SELECT b FROM Book b
        WHERE (:query IS NULL OR LOWER(b.title) LIKE LOWER(CONCAT('%', :query, '%'))
                              OR LOWER(b.author) LIKE LOWER(CONCAT('%', :query, '%'))
                              OR LOWER(b.isbn) LIKE LOWER(CONCAT('%', :query, '%')))
          AND (:category IS NULL OR LOWER(b.category) = LOWER(:category))
        """)
    Page<Book> searchBooks(@Param("query") String query,
                           @Param("category") String category,
                           Pageable pageable);
}

package com.library.management.repository;

import com.library.management.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    boolean existsByIsbn(String isbn);

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

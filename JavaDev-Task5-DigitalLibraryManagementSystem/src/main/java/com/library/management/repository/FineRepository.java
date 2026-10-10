package com.library.management.repository;

import com.library.management.entity.Fine;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA Repository for Fines.
 */
@Repository
public interface FineRepository extends JpaRepository<Fine, Long> {

    // Find fine associated with an issue record
    Optional<Fine> findByIssueId(Long issueId);

    // List fines for a user with pagination
    Page<Fine> findByUserId(Long userId, Pageable pageable);

    // Checks if the user has any unpaid fines (paid = false)
    boolean existsByUserIdAndPaidFalse(Long userId);

    // Member query loading fines with issue, book, and user details (avoids N+1)
    @Query("SELECT f FROM Fine f JOIN FETCH f.issue i JOIN FETCH i.book JOIN FETCH f.user WHERE f.user.id = :userId")
    Page<Fine> findByUserIdFetchAll(@Param("userId") Long userId, Pageable pageable);

    // Master ledger query loading all fines with issue, book, and user details
    @Query("SELECT f FROM Fine f JOIN FETCH f.issue i JOIN FETCH i.book JOIN FETCH f.user")
    Page<Fine> findAllFetchAll(Pageable pageable);
}

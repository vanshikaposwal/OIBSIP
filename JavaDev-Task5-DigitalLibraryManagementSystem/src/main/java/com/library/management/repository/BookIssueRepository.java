package com.library.management.repository;

import com.library.management.entity.BookIssue;
import com.library.management.entity.BookIssue.IssueStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Spring Data JPA Repository for Book Issues (Loans).
 */
@Repository
public interface BookIssueRepository extends JpaRepository<BookIssue, Long> {

    // Simple pagination query for issues by user
    Page<BookIssue> findByUserId(Long userId, Pageable pageable);

    // Pagination query for all issues
    Page<BookIssue> findAll(Pageable pageable);

    // Counts active issues for a user to enforce the maximum borrowing limit (5)
    long countByUserIdAndStatus(Long userId, IssueStatus status);

    // Checks if the user has already borrowed this specific book
    boolean existsByUserIdAndBookIdAndStatus(Long userId, Long bookId, IssueStatus status);

    // Finds all issues that have passed their due date
    @Query("SELECT bi FROM BookIssue bi WHERE bi.status = 'ISSUED' AND bi.dueDate < :today")
    List<BookIssue> findAllOverdue(@Param("today") LocalDate today);

    // JOIN FETCH avoids N+1 query problem by loading User and Book entities together
    @Query("SELECT bi FROM BookIssue bi JOIN FETCH bi.user JOIN FETCH bi.book WHERE bi.user.id = :userId")
    Page<BookIssue> findByUserIdFetchAll(@Param("userId") Long userId, Pageable pageable);

    // Master ledger query loading all issues with user and book details
    @Query("SELECT bi FROM BookIssue bi JOIN FETCH bi.user JOIN FETCH bi.book")
    Page<BookIssue> findAllFetchAll(Pageable pageable);
}

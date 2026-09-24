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

@Repository
public interface BookIssueRepository extends JpaRepository<BookIssue, Long> {

    Page<BookIssue> findByUserId(Long userId, Pageable pageable);

    Page<BookIssue> findAll(Pageable pageable);

    long countByUserIdAndStatus(Long userId, IssueStatus status);

    boolean existsByUserIdAndBookIdAndStatus(Long userId, Long bookId, IssueStatus status);

    @Query("SELECT bi FROM BookIssue bi WHERE bi.status = 'ISSUED' AND bi.dueDate < :today")
    List<BookIssue> findAllOverdue(@Param("today") LocalDate today);

    @Query("SELECT bi FROM BookIssue bi JOIN FETCH bi.user JOIN FETCH bi.book WHERE bi.user.id = :userId")
    Page<BookIssue> findByUserIdFetchAll(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT bi FROM BookIssue bi JOIN FETCH bi.user JOIN FETCH bi.book")
    Page<BookIssue> findAllFetchAll(Pageable pageable);
}

package com.library.management.repository;

import com.library.management.entity.Fine;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FineRepository extends JpaRepository<Fine, Long> {

    Optional<Fine> findByIssueId(Long issueId);

    Page<Fine> findByUserId(Long userId, Pageable pageable);

    boolean existsByUserIdAndPaidFalse(Long userId);

    @Query("SELECT f FROM Fine f JOIN FETCH f.issue i JOIN FETCH i.book JOIN FETCH f.user WHERE f.user.id = :userId")
    Page<Fine> findByUserIdFetchAll(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT f FROM Fine f JOIN FETCH f.issue i JOIN FETCH i.book JOIN FETCH f.user")
    Page<Fine> findAllFetchAll(Pageable pageable);
}

package com.library.management.repository;

import com.library.management.entity.ContactQuery;
import com.library.management.entity.ContactQuery.QueryStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA Repository for Support / Contact Inquiries.
 */
@Repository
public interface ContactQueryRepository extends JpaRepository<ContactQuery, Long> {

    // Filter queries by status (OPEN or RESOLVED)
    Page<ContactQuery> findByStatus(QueryStatus status, Pageable pageable);

    // List queries submitted by a specific user
    Page<ContactQuery> findByUserId(Long userId, Pageable pageable);
}

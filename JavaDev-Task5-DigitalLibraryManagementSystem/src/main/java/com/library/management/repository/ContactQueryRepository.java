package com.library.management.repository;

import com.library.management.entity.ContactQuery;
import com.library.management.entity.ContactQuery.QueryStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactQueryRepository extends JpaRepository<ContactQuery, Long> {

    Page<ContactQuery> findByStatus(QueryStatus status, Pageable pageable);

    Page<ContactQuery> findByUserId(Long userId, Pageable pageable);
}

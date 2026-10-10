package com.library.management.service;

import com.library.management.dto.*;
import com.library.management.entity.ContactQuery;
import com.library.management.entity.ContactQuery.QueryStatus;
import com.library.management.entity.User;
import com.library.management.exception.ResourceNotFoundException;
import com.library.management.repository.ContactQueryRepository;
import com.library.management.repository.UserRepository;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for Contact & Support Queries.
 * Handles customer inquiries, member messages, and admin resolution.
 */
@Service
public class ContactQueryService {

    private final ContactQueryRepository queryRepository;
    private final UserRepository userRepository;

    // Constructor injection for repositories
    public ContactQueryService(ContactQueryRepository queryRepository,
                                UserRepository userRepository) {
        this.queryRepository = queryRepository;
        this.userRepository  = userRepository;
    }

    /**
     * Submits a new support/inquiry message.
     * Can be submitted by a registered user or an anonymous visitor.
     */
    @Transactional
    public ContactQueryResponse submit(ContactQueryRequest req, Long userId) {
        // Step 1: If user is logged in, link their user profile
        User user = null;
        if (userId != null) {
            user = userRepository.findById(userId).orElse(null);
        }

        // Step 2: Create query entity with OPEN status
        ContactQuery cq = ContactQuery.builder()
                .user(user)
                .name(req.name())
                .email(req.email())
                .subject(req.subject())
                .message(req.message())
                .status(QueryStatus.OPEN)
                .build();

        // Step 3: Save to database and return response DTO
        ContactQuery saved = queryRepository.save(cq);
        return ContactQueryResponse.from(saved);
    }

    /**
     * Admin view: lists all contact queries.
     */
    public Page<ContactQueryResponse> findAll(Pageable pageable) {
        return queryRepository.findAll(pageable).map(ContactQueryResponse::from);
    }

    /**
     * User view: lists queries submitted by this user.
     */
    public Page<ContactQueryResponse> findByUser(Long userId, Pageable pageable) {
        return queryRepository.findByUserId(userId, pageable).map(ContactQueryResponse::from);
    }

    /**
     * Admin action: marks a support inquiry ticket as RESOLVED.
     */
    @Transactional
    public ContactQueryResponse resolve(Long id) {
        // Step 1: Find query or throw 404
        ContactQuery cq = queryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Query not found: " + id));

        // Step 2: Mark status as RESOLVED
        cq.setStatus(QueryStatus.RESOLVED);

        // Step 3: Save and return response DTO
        ContactQuery updated = queryRepository.save(cq);
        return ContactQueryResponse.from(updated);
    }
}

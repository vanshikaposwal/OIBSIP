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

@Service
public class ContactQueryService {

    private final ContactQueryRepository queryRepository;
    private final UserRepository userRepository;

    public ContactQueryService(ContactQueryRepository queryRepository,
                                UserRepository userRepository) {
        this.queryRepository = queryRepository;
        this.userRepository  = userRepository;
    }

    @Transactional
    public ContactQueryResponse submit(ContactQueryRequest req, Long userId) {
        User user = userId != null
                ? userRepository.findById(userId).orElse(null)
                : null;

        ContactQuery cq = ContactQuery.builder()
                .user(user)
                .name(req.name())
                .email(req.email())
                .subject(req.subject())
                .message(req.message())
                .status(QueryStatus.OPEN)
                .build();
        return ContactQueryResponse.from(queryRepository.save(cq));
    }

    public Page<ContactQueryResponse> findAll(Pageable pageable) {
        return queryRepository.findAll(pageable).map(ContactQueryResponse::from);
    }

    public Page<ContactQueryResponse> findByUser(Long userId, Pageable pageable) {
        return queryRepository.findByUserId(userId, pageable).map(ContactQueryResponse::from);
    }

    @Transactional
    public ContactQueryResponse resolve(Long id) {
        ContactQuery cq = queryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Query not found: " + id));
        cq.setStatus(QueryStatus.RESOLVED);
        return ContactQueryResponse.from(queryRepository.save(cq));
    }
}

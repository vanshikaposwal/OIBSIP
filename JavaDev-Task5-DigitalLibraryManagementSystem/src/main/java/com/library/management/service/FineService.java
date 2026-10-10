package com.library.management.service;

import com.library.management.dto.FineResponse;
import com.library.management.entity.Fine;
import com.library.management.exception.BusinessException;
import com.library.management.exception.ResourceNotFoundException;
import com.library.management.repository.FineRepository;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Service class for Library Fines management.
 * Tracks overdue book fines and processes member fine payments.
 */
@Service
public class FineService {

    private final FineRepository fineRepository;

    // Constructor injection: Spring injects FineRepository
    public FineService(FineRepository fineRepository) {
        this.fineRepository = fineRepository;
    }

    /**
     * Lists all fines for a specific member.
     */
    public Page<FineResponse> findByUser(Long userId, Pageable pageable) {
        return fineRepository.findByUserIdFetchAll(userId, pageable).map(FineResponse::from);
    }

    /**
     * Lists all fines in the entire library (Admin master ledger).
     */
    public Page<FineResponse> findAll(Pageable pageable) {
        return fineRepository.findAllFetchAll(pageable).map(FineResponse::from);
    }

    /**
     * Finds a single fine record by ID.
     */
    public FineResponse findById(Long id) {
        Fine fine = getOrThrow(id);
        return FineResponse.from(fine);
    }

    /**
     * Pays / clears an overdue fine.
     * Members can pay their own fines, or an admin can record payment.
     */
    @Transactional
    public FineResponse pay(Long fineId, Long requestingUserId, boolean isAdmin) {
        // Step 1: Find the fine record or throw 404
        Fine fine = getOrThrow(fineId);

        // Step 2: Security check: User must be either the fine owner or an Admin
        if (!isAdmin && !fine.getUser().getId().equals(requestingUserId)) {
            throw new BusinessException("Access denied", 403);
        }

        // Step 3: Check if fine is already paid
        if (fine.isPaid()) {
            throw new BusinessException("Fine already paid");
        }

        // Step 4: Mark fine as paid and record payment timestamp
        fine.setPaid(true);
        fine.setPaidAt(LocalDateTime.now());

        // Step 5: Save and return response
        Fine savedFine = fineRepository.save(fine);
        return FineResponse.from(savedFine);
    }

    /**
     * Helper method to find fine by ID or throw ResourceNotFoundException.
     */
    private Fine getOrThrow(Long id) {
        return fineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fine not found: " + id));
    }
}

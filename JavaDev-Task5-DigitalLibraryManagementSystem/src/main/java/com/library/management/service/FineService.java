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

@Service
public class FineService {

    private final FineRepository fineRepository;

    public FineService(FineRepository fineRepository) {
        this.fineRepository = fineRepository;
    }

    public Page<FineResponse> findByUser(Long userId, Pageable pageable) {
        return fineRepository.findByUserIdFetchAll(userId, pageable).map(FineResponse::from);
    }

    public Page<FineResponse> findAll(Pageable pageable) {
        return fineRepository.findAllFetchAll(pageable).map(FineResponse::from);
    }

    public FineResponse findById(Long id) {
        return FineResponse.from(getOrThrow(id));
    }

    @Transactional
    public FineResponse pay(Long fineId, Long requestingUserId, boolean isAdmin) {
        Fine fine = getOrThrow(fineId);
        if (!isAdmin && !fine.getUser().getId().equals(requestingUserId))
            throw new BusinessException("Access denied", 403);
        if (fine.isPaid())
            throw new BusinessException("Fine already paid");
        fine.setPaid(true);
        fine.setPaidAt(LocalDateTime.now());
        return FineResponse.from(fineRepository.save(fine));
    }

    private Fine getOrThrow(Long id) {
        return fineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fine not found: " + id));
    }
}

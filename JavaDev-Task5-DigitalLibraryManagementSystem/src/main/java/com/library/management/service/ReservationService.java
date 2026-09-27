package com.library.management.service;

import com.library.management.dto.ReservationResponse;
import com.library.management.entity.*;
import com.library.management.entity.Reservation.ReservationStatus;
import com.library.management.exception.BusinessException;
import com.library.management.exception.ResourceNotFoundException;
import com.library.management.repository.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public ReservationService(ReservationRepository reservationRepository,
                               BookRepository bookRepository,
                               UserRepository userRepository) {
        this.reservationRepository = reservationRepository;
        this.bookRepository        = bookRepository;
        this.userRepository        = userRepository;
    }

    @Transactional
    public ReservationResponse reserve(Long userId, Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found: " + bookId));

        if (book.getAvailableQuantity() > 0)
            throw new BusinessException("Book is currently available — please issue it directly", 422);

        if (reservationRepository.existsByUserIdAndBookIdAndStatus(userId, bookId, ReservationStatus.ACTIVE))
            throw new BusinessException("You already have an active reservation for this book");

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        Reservation res = Reservation.builder()
                .user(user)
                .book(book)
                .status(ReservationStatus.ACTIVE)
                .reservationDate(java.time.LocalDateTime.now())
                .build();
        return ReservationResponse.from(reservationRepository.save(res));
    }

    @Transactional
    public ReservationResponse cancel(Long reservationId, Long requestingUserId, boolean isAdmin) {
        Reservation res = getOrThrow(reservationId);
        if (!isAdmin && !res.getUser().getId().equals(requestingUserId))
            throw new BusinessException("Access denied", 403);
        if (res.getStatus() != ReservationStatus.ACTIVE)
            throw new BusinessException("Reservation is not active");
        res.setStatus(ReservationStatus.CANCELLED);
        return ReservationResponse.from(reservationRepository.save(res));
    }

    public Page<ReservationResponse> findByUser(Long userId, Pageable pageable) {
        return reservationRepository.findByUserIdFetchAll(userId, pageable).map(ReservationResponse::from);
    }

    public Page<ReservationResponse> findAll(Pageable pageable) {
        return reservationRepository.findAllFetchAll(pageable).map(ReservationResponse::from);
    }

    private Reservation getOrThrow(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found: " + id));
    }
}

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

/**
 * Service class for Book Reservations (Waitlist queue).
 * Allows users to reserve books when stock reaches 0.
 */
@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    // Constructor injection for repositories
    public ReservationService(ReservationRepository reservationRepository,
                               BookRepository bookRepository,
                               UserRepository userRepository) {
        this.reservationRepository = reservationRepository;
        this.bookRepository        = bookRepository;
        this.userRepository        = userRepository;
    }

    /**
     * Places a reservation for a book when no copies are available.
     * - Checks book exists
     * - Ensures stock is 0 (if copies exist, user should borrow instead)
     * - Prevents duplicate active reservations for the same book by the same user
     */
    @Transactional
    public ReservationResponse reserve(Long userId, Long bookId) {
        // Step 1: Check if the book exists
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found: " + bookId));

        // Step 2: Ensure available quantity is 0 (reservations are only for out-of-stock books)
        if (book.getAvailableQuantity() > 0) {
            throw new BusinessException("Book is currently available — please issue it directly", 422);
        }

        // Step 3: Check if this user already has an active reservation for this book
        if (reservationRepository.existsByUserIdAndBookIdAndStatus(userId, bookId, ReservationStatus.ACTIVE)) {
            throw new BusinessException("You already have an active reservation for this book");
        }

        // Step 4: Verify the user exists
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        // Step 5: Create and save the new active reservation
        Reservation res = Reservation.builder()
                .user(user)
                .book(book)
                .status(ReservationStatus.ACTIVE)
                .reservationDate(java.time.LocalDateTime.now())
                .build();

        Reservation savedReservation = reservationRepository.save(res);
        return ReservationResponse.from(savedReservation);
    }

    /**
     * Cancels an active reservation.
     * Only the reservation owner or an administrator can cancel.
     */
    @Transactional
    public ReservationResponse cancel(Long reservationId, Long requestingUserId, boolean isAdmin) {
        // Step 1: Find the reservation
        Reservation res = getOrThrow(reservationId);

        // Step 2: Check authorization (must be either the book owner or admin)
        if (!isAdmin && !res.getUser().getId().equals(requestingUserId)) {
            throw new BusinessException("Access denied", 403);
        }

        // Step 3: Check if reservation is still active
        if (res.getStatus() != ReservationStatus.ACTIVE) {
            throw new BusinessException("Reservation is not active");
        }

        // Step 4: Update status to CANCELLED and save
        res.setStatus(ReservationStatus.CANCELLED);
        Reservation updated = reservationRepository.save(res);
        return ReservationResponse.from(updated);
    }

    /**
     * Lists all reservations for a specific user.
     */
    public Page<ReservationResponse> findByUser(Long userId, Pageable pageable) {
        return reservationRepository.findByUserIdFetchAll(userId, pageable).map(ReservationResponse::from);
    }

    /**
     * Lists all reservations in the system (Admin view).
     */
    public Page<ReservationResponse> findAll(Pageable pageable) {
        return reservationRepository.findAllFetchAll(pageable).map(ReservationResponse::from);
    }

    /**
     * Helper method to find a reservation or throw a 404 error.
     */
    private Reservation getOrThrow(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found: " + id));
    }
}

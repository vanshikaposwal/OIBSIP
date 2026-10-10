package com.library.management.repository;

import com.library.management.entity.Reservation;
import com.library.management.entity.Reservation.ReservationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA Repository for Reservations (Waitlists).
 */
@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    // Checks if a member already has an active reservation for this book
    boolean existsByUserIdAndBookIdAndStatus(Long userId, Long bookId, ReservationStatus status);

    // Finds the oldest active reservation in the queue for a given book (FIFO queue)
    @Query("""
        SELECT r FROM Reservation r
        WHERE r.book.id = :bookId AND r.status = 'ACTIVE'
        ORDER BY r.reservationDate ASC
        """)
    Optional<Reservation> findOldestActiveForBook(@Param("bookId") Long bookId);

    // Finds reservations by user ID
    Page<Reservation> findByUserId(Long userId, Pageable pageable);

    // Master query loading all reservations with user and book entities (avoids N+1)
    @Query("SELECT r FROM Reservation r JOIN FETCH r.user JOIN FETCH r.book")
    Page<Reservation> findAllFetchAll(Pageable pageable);

    // User query loading reservations with book entities (avoids N+1)
    @Query("SELECT r FROM Reservation r JOIN FETCH r.user JOIN FETCH r.book WHERE r.user.id = :userId")
    Page<Reservation> findByUserIdFetchAll(@Param("userId") Long userId, Pageable pageable);
}

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

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    boolean existsByUserIdAndBookIdAndStatus(Long userId, Long bookId, ReservationStatus status);

    @Query("""
        SELECT r FROM Reservation r
        WHERE r.book.id = :bookId AND r.status = 'ACTIVE'
        ORDER BY r.reservationDate ASC
        """)
    Optional<Reservation> findOldestActiveForBook(@Param("bookId") Long bookId);

    Page<Reservation> findByUserId(Long userId, Pageable pageable);

    @Query("SELECT r FROM Reservation r JOIN FETCH r.user JOIN FETCH r.book")
    Page<Reservation> findAllFetchAll(Pageable pageable);

    @Query("SELECT r FROM Reservation r JOIN FETCH r.user JOIN FETCH r.book WHERE r.user.id = :userId")
    Page<Reservation> findByUserIdFetchAll(@Param("userId") Long userId, Pageable pageable);
}

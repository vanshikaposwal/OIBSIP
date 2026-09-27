package com.library.management.service;

import com.library.management.dto.ReservationResponse;
import com.library.management.entity.Book;
import com.library.management.entity.Reservation;
import com.library.management.entity.Reservation.ReservationStatus;
import com.library.management.entity.User;
import com.library.management.exception.BusinessException;
import com.library.management.repository.BookRepository;
import com.library.management.repository.ReservationRepository;
import com.library.management.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ReservationService reservationService;

    private User sampleUser;
    private Book sampleBook;

    @BeforeEach
    void setUp() {
        sampleUser = User.builder().id(1L).name("Alice").build();
        sampleBook = Book.builder().id(5L).title("Algorithms").availableQuantity(0).build();
    }

    @Test
    @DisplayName("Reserve book when available quantity is 0 succeeds")
    void reserve_ZeroStock_Success() {
        when(bookRepository.findById(5L)).thenReturn(Optional.of(sampleBook));
        when(reservationRepository.existsByUserIdAndBookIdAndStatus(1L, 5L, ReservationStatus.ACTIVE)).thenReturn(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));

        when(reservationRepository.save(any(Reservation.class))).thenAnswer(i -> {
            Reservation r = i.getArgument(0);
            r.setId(99L);
            return r;
        });

        ReservationResponse res = reservationService.reserve(1L, 5L);

        assertNotNull(res);
        assertEquals("ACTIVE", res.status());
        verify(reservationRepository).save(any(Reservation.class));
    }

    @Test
    @DisplayName("Reserve book when copies are available throws BusinessException")
    void reserve_CopiesAvailable_ThrowsException() {
        sampleBook.setAvailableQuantity(2);
        when(bookRepository.findById(5L)).thenReturn(Optional.of(sampleBook));

        BusinessException ex = assertThrows(BusinessException.class, () -> reservationService.reserve(1L, 5L));
        assertEquals(422, ex.getStatusCode());
        verify(reservationRepository, never()).save(any(Reservation.class));
    }

    @Test
    @DisplayName("Cancel reservation succeeds for owner")
    void cancel_Owner_Success() {
        Reservation r = Reservation.builder()
                .id(99L)
                .user(sampleUser)
                .book(sampleBook)
                .status(ReservationStatus.ACTIVE)
                .build();

        when(reservationRepository.findById(99L)).thenReturn(Optional.of(r));
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(i -> i.getArgument(0));

        ReservationResponse res = reservationService.cancel(99L, 1L, false);

        assertEquals("CANCELLED", res.status());
        verify(reservationRepository).save(r);
    }
}

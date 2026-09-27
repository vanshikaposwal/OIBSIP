package com.library.management.service;

import com.library.management.config.LibraryProperties;
import com.library.management.dto.BookIssueResponse;
import com.library.management.entity.*;
import com.library.management.entity.BookIssue.IssueStatus;
import com.library.management.exception.BusinessException;
import com.library.management.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookIssueServiceTest {

    @Mock
    private BookIssueRepository issueRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private FineRepository fineRepository;

    @Mock
    private ReservationRepository reservationRepository;

    private LibraryProperties props;
    private BookIssueService issueService;

    private User sampleUser;
    private Book sampleBook;

    @BeforeEach
    void setUp() {
        props = new LibraryProperties();
        props.setLoanDays(14);
        props.setFinePerDay(5);
        props.setMaxActiveIssues(5);
        props.setBlockOnUnpaidFines(true);

        issueService = new BookIssueService(issueRepository, bookRepository, userRepository, fineRepository, reservationRepository, props);

        sampleUser = User.builder().id(10L).name("Bob Kumar").email("bob@example.com").build();
        sampleBook = Book.builder()
                .id(20L)
                .title("Clean Code")
                .author("Robert C. Martin")
                .totalQuantity(3)
                .availableQuantity(2)
                .build();
    }

    @Test
    @DisplayName("Issue book succeeds: decrements stock and sets 14 days due date")
    void issue_Success() {
        when(userRepository.findById(10L)).thenReturn(Optional.of(sampleUser));
        when(fineRepository.existsByUserIdAndPaidFalse(10L)).thenReturn(false);
        when(issueRepository.countByUserIdAndStatus(10L, IssueStatus.ISSUED)).thenReturn(1L);
        when(issueRepository.existsByUserIdAndBookIdAndStatus(10L, 20L, IssueStatus.ISSUED)).thenReturn(false);
        when(bookRepository.findById(20L)).thenReturn(Optional.of(sampleBook));

        when(issueRepository.save(any(BookIssue.class))).thenAnswer(i -> {
            BookIssue bi = i.getArgument(0);
            bi.setId(100L);
            return bi;
        });

        BookIssueResponse res = issueService.issue(10L, 20L);

        assertNotNull(res);
        assertEquals(1, sampleBook.getAvailableQuantity()); // decremented from 2 to 1
        assertEquals(LocalDate.now().plusDays(14), res.dueDate());
        assertEquals("ISSUED", res.status());
        verify(bookRepository).save(sampleBook);
        verify(issueRepository).save(any(BookIssue.class));
    }

    @Test
    @DisplayName("Issue book fails when user has unpaid fines")
    void issue_BlockedByUnpaidFines_ThrowsException() {
        when(userRepository.findById(10L)).thenReturn(Optional.of(sampleUser));
        when(fineRepository.existsByUserIdAndPaidFalse(10L)).thenReturn(true);

        BusinessException ex = assertThrows(BusinessException.class, () -> issueService.issue(10L, 20L));
        assertEquals(422, ex.getStatusCode());
        verify(issueRepository, never()).save(any(BookIssue.class));
    }

    @Test
    @DisplayName("Issue book fails when available quantity is 0")
    void issue_OutOfStock_ThrowsException() {
        sampleBook.setAvailableQuantity(0);

        when(userRepository.findById(10L)).thenReturn(Optional.of(sampleUser));
        when(fineRepository.existsByUserIdAndPaidFalse(10L)).thenReturn(false);
        when(issueRepository.countByUserIdAndStatus(10L, IssueStatus.ISSUED)).thenReturn(0L);
        when(issueRepository.existsByUserIdAndBookIdAndStatus(10L, 20L, IssueStatus.ISSUED)).thenReturn(false);
        when(bookRepository.findById(20L)).thenReturn(Optional.of(sampleBook));

        assertThrows(BusinessException.class, () -> issueService.issue(10L, 20L));
    }

    @Test
    @DisplayName("Return book on time: increments stock, no fine created")
    void returnBook_OnTime_Success() {
        BookIssue issue = BookIssue.builder()
                .id(100L)
                .user(sampleUser)
                .book(sampleBook)
                .issueDate(LocalDate.now().minusDays(5))
                .dueDate(LocalDate.now().plusDays(9))
                .status(IssueStatus.ISSUED)
                .build();

        when(issueRepository.findById(100L)).thenReturn(Optional.of(issue));
        when(reservationRepository.findOldestActiveForBook(20L)).thenReturn(Optional.empty());

        BookIssueResponse res = issueService.returnBook(100L);

        assertEquals("RETURNED", res.status());
        assertEquals(3, sampleBook.getAvailableQuantity()); // incremented from 2 to 3
        verify(fineRepository, never()).save(any(Fine.class));
        verify(bookRepository).save(sampleBook);
    }

    @Test
    @DisplayName("Return overdue book: creates fine = overdue days * fine rate, fulfills oldest reservation")
    void returnBook_Overdue_CreatesFineAndFulfillsReservation() {
        // Due 4 days ago
        BookIssue issue = BookIssue.builder()
                .id(100L)
                .user(sampleUser)
                .book(sampleBook)
                .issueDate(LocalDate.now().minusDays(18))
                .dueDate(LocalDate.now().minusDays(4))
                .status(IssueStatus.ISSUED)
                .build();

        Reservation oldestRes = Reservation.builder()
                .id(50L)
                .book(sampleBook)
                .status(Reservation.ReservationStatus.ACTIVE)
                .build();

        when(issueRepository.findById(100L)).thenReturn(Optional.of(issue));
        when(reservationRepository.findOldestActiveForBook(20L)).thenReturn(Optional.of(oldestRes));

        BookIssueResponse res = issueService.returnBook(100L);

        assertEquals("RETURNED", res.status());
        // Verify fine created: 4 days * 5 = 20
        verify(fineRepository).save(argThat(fine ->
                fine.getAmount().compareTo(BigDecimal.valueOf(20)) == 0 && !fine.isPaid()
        ));
        // Verify reservation fulfilled
        assertEquals(Reservation.ReservationStatus.FULFILLED, oldestRes.getStatus());
        assertNotNull(oldestRes.getFulfilledAt());
        verify(reservationRepository).save(oldestRes);
    }
}

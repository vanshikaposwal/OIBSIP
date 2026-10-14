package com.library.management.service;

import com.library.management.config.LibraryProperties;
import com.library.management.dto.BookIssueResponse;
import com.library.management.entity.*;
import com.library.management.entity.BookIssue.IssueStatus;
import com.library.management.exception.BusinessException;
import com.library.management.exception.ResourceNotFoundException;
import com.library.management.repository.*;
import org.springframework.data.domain.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Service class for Book Issue (Circulation) operations.
 * Handles issuing books to users, returning books, calculating overdue fines,
 * and automatically fulfilling active reservations when copies become available.
 */
@Service
public class BookIssueService {

    private final BookIssueRepository issueRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final FineRepository fineRepository;
    private final ReservationRepository reservationRepository;
    private final LibraryProperties props;

    // Constructor injection for all required repositories and library properties
    public BookIssueService(BookIssueRepository issueRepository,
                            BookRepository bookRepository,
                            UserRepository userRepository,
                            FineRepository fineRepository,
                            ReservationRepository reservationRepository,
                            LibraryProperties props) {
        this.issueRepository       = issueRepository;
        this.bookRepository        = bookRepository;
        this.userRepository        = userRepository;
        this.fineRepository        = fineRepository;
        this.reservationRepository = reservationRepository;
        this.props                 = props;
    }

    // ──────────────────────────────────────────────────────────────────────────
    // 1. Issue a Book
    // ──────────────────────────────────────────────────────────────────────────

    /**
     * Issues a book to a user.
     * Enforces all circulation rules:
     * - User must exist
     * - User must have no unpaid fines
     * - User must not exceed the max active issue limit (5 books)
     * - User cannot borrow two copies of the same book at once
     * - Book must have available stock (> 0)
     */
    @Transactional
    public BookIssueResponse issue(Long userId, Long bookId) {
        // Step 1: Verify the user exists in the database
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        // Step 2: Check if user is blocked due to unpaid fines
        if (props.isBlockOnUnpaidFines() && fineRepository.existsByUserIdAndPaidFalse(userId)) {
            throw new BusinessException("You have unpaid fines. Please pay them before issuing new books.", 422);
        }

        // Step 3: Check if the user has reached the maximum allowed active loans (default: 5)
        long activeIssuesCount = issueRepository.countByUserIdAndStatus(userId, IssueStatus.ISSUED);
        if (activeIssuesCount >= props.getMaxActiveIssues()) {
            throw new BusinessException("Active issue limit reached (" + props.getMaxActiveIssues() + ")", 422);
        }

        // Step 4: Check if the user already has an active issue for this exact book
        if (issueRepository.existsByUserIdAndBookIdAndStatus(userId, bookId, IssueStatus.ISSUED)) {
            throw new BusinessException("You already have an active issue for this book");
        }

        // Step 5: Check if the book exists
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found: " + bookId));

        // Step 6: Check if copies are available in stock
        if (book.getAvailableQuantity() <= 0) {
            throw new BusinessException("No copies available for this book");
        }

        // Step 7: Decrement available stock by 1
        book.setAvailableQuantity(book.getAvailableQuantity() - 1);
        bookRepository.save(book);

        // Step 8: Calculate due date (e.g. today + 14 days loan period)
        LocalDate today = LocalDate.now();
        LocalDate dueDate = today.plusDays(props.getLoanDays());

        // Step 9: Create and save the BookIssue record
        BookIssue bi = BookIssue.builder()
                .user(user)
                .book(book)
                .issueDate(today)
                .dueDate(dueDate)
                .status(IssueStatus.ISSUED)
                .build();

        BookIssue savedIssue = issueRepository.save(bi);
        return BookIssueResponse.from(savedIssue);
    }

    // ──────────────────────────────────────────────────────────────────────────
    // 2. Return a Book
    // ──────────────────────────────────────────────────────────────────────────

    /**
     * Processes the return of an issued book.
     * - Restores 1 available quantity to the book
     * - If overdue, calculates fine (days overdue * rate per day)
     * - Checks if another user had reserved this book and fulfills the oldest reservation
     */
    @Transactional
    public BookIssueResponse returnBook(Long issueId) {
        // Step 1: Find the issue record or throw 404
        BookIssue bi = getOrThrow(issueId);

        // Step 2: Ensure the book wasn't already returned earlier
        if (bi.getStatus() == IssueStatus.RETURNED) {
            throw new BusinessException("Book already returned");
        }

        // Step 3: Mark status as RETURNED and record return date as today
        bi.setStatus(IssueStatus.RETURNED);
        bi.setReturnDate(LocalDate.now());
        issueRepository.save(bi);

        // Step 4: Increase the available book stock by 1
        Book book = bi.getBook();
        book.setAvailableQuantity(book.getAvailableQuantity() + 1);
        bookRepository.save(book);

        // Step 5: Check if return is overdue and compute fine
        LocalDate today = LocalDate.now();
        if (today.isAfter(bi.getDueDate())) {
            // Calculate total overdue days using ChronoUnit
            long overdueDays = java.time.temporal.ChronoUnit.DAYS.between(bi.getDueDate(), today);
            long fineRate = props.getFinePerDay();
            java.math.BigDecimal amount = java.math.BigDecimal.valueOf(overdueDays * fineRate);

            Fine fine = Fine.builder()
                    .issue(bi)
                    .user(bi.getUser())
                    .amount(amount)
                    .paid(false)
                    .build();
            fineRepository.save(fine);
        }

        // Step 6: If anyone has reserved this book, fulfill the oldest active reservation
        java.util.Optional<Reservation> oldestReservation = reservationRepository.findOldestActiveForBook(book.getId());
        if (oldestReservation.isPresent()) {
            Reservation res = oldestReservation.get();
            res.setStatus(Reservation.ReservationStatus.FULFILLED);
            res.setFulfilledAt(java.time.LocalDateTime.now());
            reservationRepository.save(res);
        }

        return BookIssueResponse.from(bi);
    }

    // ──────────────────────────────────────────────────────────────────────────
    // 3. Queries
    // ──────────────────────────────────────────────────────────────────────────

    /**
     * Fetches paginated borrowing history for a specific user.
     */
    public Page<BookIssueResponse> findByUser(Long userId, Pageable pageable) {
        return issueRepository.findByUserIdFetchAll(userId, pageable).map(BookIssueResponse::from);
    }

    /**
     * Fetches all borrowing records across the entire library (Admin).
     */
    public Page<BookIssueResponse> findAll(Pageable pageable) {
        return issueRepository.findAllFetchAll(pageable).map(BookIssueResponse::from);
    }

    /**
     * Finds a single issue record by ID.
     */
    public BookIssueResponse findById(Long id) {
        return BookIssueResponse.from(getOrThrow(id));
    }

    // ──────────────────────────────────────────────────────────────────────────
    // 4. Background Scheduled Tasks
    // ──────────────────────────────────────────────────────────────────────────

    /**
     * Daily background job (runs at midnight 00:00:00 every day).
     * Scans for overdue books and logs count for administrators.
     */
    @Scheduled(cron = "0 0 0 * * *")
    public void syncOverdueStatus() {
        LocalDate today = LocalDate.now();
        List<BookIssue> overdue = issueRepository.findAllOverdue(today);
        org.slf4j.LoggerFactory.getLogger(BookIssueService.class)
                .info("Daily Overdue Job: Found {} overdue books on {}", overdue.size(), today);
    }

    // ──────────────────────────────────────────────────────────────────────────
    // 5. Helper Methods
    // ──────────────────────────────────────────────────────────────────────────

    private BookIssue getOrThrow(Long id) {
        return issueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Issue not found: " + id));
        
    }
}

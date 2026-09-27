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

@Service
public class BookIssueService {

    private final BookIssueRepository issueRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final FineRepository fineRepository;
    private final ReservationRepository reservationRepository;
    private final LibraryProperties props;

    public BookIssueService(BookIssueRepository issueRepository,
                            BookRepository bookRepository,
                            UserRepository userRepository,
                            FineRepository fineRepository,
                            ReservationRepository reservationRepository,
                            LibraryProperties props) {
        this.issueRepository      = issueRepository;
        this.bookRepository       = bookRepository;
        this.userRepository       = userRepository;
        this.fineRepository       = fineRepository;
        this.reservationRepository = reservationRepository;
        this.props                = props;
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Issue
    // ──────────────────────────────────────────────────────────────────────────

    @Transactional
    public BookIssueResponse issue(Long userId, Long bookId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        // Block on unpaid fines
        if (props.isBlockOnUnpaidFines() && fineRepository.existsByUserIdAndPaidFalse(userId))
            throw new BusinessException("You have unpaid fines. Please pay them before issuing new books.", 422);

        // Max active issues
        long active = issueRepository.countByUserIdAndStatus(userId, IssueStatus.ISSUED);
        if (active >= props.getMaxActiveIssues())
            throw new BusinessException("Active issue limit reached (" + props.getMaxActiveIssues() + ")", 422);

        // Duplicate active issue
        if (issueRepository.existsByUserIdAndBookIdAndStatus(userId, bookId, IssueStatus.ISSUED))
            throw new BusinessException("You already have an active issue for this book");

        // Pessimistic lock on book row
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found: " + bookId));

        if (book.getAvailableQuantity() <= 0)
            throw new BusinessException("No copies available for this book");

        book.setAvailableQuantity(book.getAvailableQuantity() - 1);
        bookRepository.save(book);

        LocalDate today = LocalDate.now();
        BookIssue bi = BookIssue.builder()
                .user(user)
                .book(book)
                .issueDate(today)
                .dueDate(today.plusDays(props.getLoanDays()))
                .status(IssueStatus.ISSUED)
                .build();
        return BookIssueResponse.from(issueRepository.save(bi));
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Return
    // ──────────────────────────────────────────────────────────────────────────

    @Transactional
    public BookIssueResponse returnBook(Long issueId) {
        BookIssue bi = getOrThrow(issueId);

        if (bi.getStatus() == IssueStatus.RETURNED)
            throw new BusinessException("Book already returned");

        bi.setStatus(IssueStatus.RETURNED);
        bi.setReturnDate(LocalDate.now());
        issueRepository.save(bi);

        // Restore quantity (pessimistic: already in @Transactional scope)
        Book book = bi.getBook();
        book.setAvailableQuantity(book.getAvailableQuantity() + 1);
        bookRepository.save(book);

        // Create fine if overdue
        LocalDate today = LocalDate.now();
        if (today.isAfter(bi.getDueDate())) {
            long overdueDays = bi.getDueDate().until(today).getDays();
            java.math.BigDecimal amount = java.math.BigDecimal.valueOf(overdueDays * props.getFinePerDay());
            Fine fine = Fine.builder()
                    .issue(bi)
                    .user(bi.getUser())
                    .amount(amount)
                    .paid(false)
                    .build();
            fineRepository.save(fine);
        }

        // Fulfill oldest ACTIVE reservation, if any
        reservationRepository.findOldestActiveForBook(book.getId()).ifPresent(res -> {
            res.setStatus(Reservation.ReservationStatus.FULFILLED);
            res.setFulfilledAt(java.time.LocalDateTime.now());
            reservationRepository.save(res);
        });

        return BookIssueResponse.from(bi);
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Queries
    // ──────────────────────────────────────────────────────────────────────────

    public Page<BookIssueResponse> findByUser(Long userId, Pageable pageable) {
        return issueRepository.findByUserIdFetchAll(userId, pageable).map(BookIssueResponse::from);
    }

    public Page<BookIssueResponse> findAll(Pageable pageable) {
        return issueRepository.findAllFetchAll(pageable).map(BookIssueResponse::from);
    }

    public BookIssueResponse findById(Long id) {
        return BookIssueResponse.from(getOrThrow(id));
    }

    // ──────────────────────────────────────────────────────────────────────────
    // @Scheduled overdue sync (daily at midnight)
    // ──────────────────────────────────────────────────────────────────────────

    @Scheduled(cron = "0 0 0 * * *")
    public void syncOverdueStatus() {
        // Overdue is derived on read; nothing to persist.
        // This hook exists for future notifications or status caching.
        List<BookIssue> overdue = issueRepository.findAllOverdue(LocalDate.now());
        // Log count — real notifications would go here
        org.slf4j.LoggerFactory.getLogger(BookIssueService.class)
                .info("Overdue sync: {} overdue issues found", overdue.size());
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Helpers
    // ──────────────────────────────────────────────────────────────────────────

    private BookIssue getOrThrow(Long id) {
        return issueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Issue not found: " + id));
    }
}

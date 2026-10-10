package com.library.management.dto;

import com.library.management.entity.BookIssue;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Data Transfer Object (DTO) for returning Book Issue (Circulation) records.
 * Calculates dynamic fields like OVERDUE status at runtime.
 */
public record BookIssueResponse(
        Long id,
        Long userId,
        String userName,
        Long bookId,
        String bookTitle,
        String bookAuthor,
        LocalDate issueDate,
        LocalDate dueDate,
        LocalDate returnDate,
        String status,   // ISSUED | RETURNED | OVERDUE (dynamically derived)
        LocalDateTime createdAt
) {
    /**
     * Factory method: Maps BookIssue entity to BookIssueResponse DTO.
     * If an issue is still ISSUED and today is after dueDate, marks status as OVERDUE.
     */
    public static BookIssueResponse from(BookIssue bi) {
        String derivedStatus = bi.getStatus().name();
        if (bi.getStatus() == BookIssue.IssueStatus.ISSUED
                && bi.getDueDate().isBefore(LocalDate.now())) {
            derivedStatus = "OVERDUE";
        }
        return new BookIssueResponse(
                bi.getId(),
                bi.getUser().getId(),
                bi.getUser().getName(),
                bi.getBook().getId(),
                bi.getBook().getTitle(),
                bi.getBook().getAuthor(),
                bi.getIssueDate(),
                bi.getDueDate(),
                bi.getReturnDate(),
                derivedStatus,
                bi.getCreatedAt()
        );
    }
}

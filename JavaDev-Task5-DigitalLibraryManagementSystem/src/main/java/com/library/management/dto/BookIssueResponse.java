package com.library.management.dto;

import com.library.management.entity.BookIssue;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
        String status,   // ISSUED | RETURNED | OVERDUE (derived)
        LocalDateTime createdAt
) {
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

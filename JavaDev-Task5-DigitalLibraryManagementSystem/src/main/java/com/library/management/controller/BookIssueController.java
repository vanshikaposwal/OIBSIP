package com.library.management.controller;

import com.library.management.dto.BookIssueResponse;
import com.library.management.entity.User;
import com.library.management.exception.BusinessException;
import com.library.management.repository.UserRepository;
import com.library.management.service.BookIssueService;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for Book Issues (Circulation).
 * Endpoints:
 * - POST /api/issues/book/{bookId}                   : Member self-issues a book
 * - POST /api/issues/admin/book/{bookId}/user/{userId}: Admin issues on behalf of member
 * - POST /api/issues/{issueId}/return                 : Returns a borrowed book
 * - GET  /api/issues/my                               : Member views own issue history
 * - GET  /api/issues                                  : Admin views all issues
 */
@RestController
@RequestMapping("/api/issues")
public class BookIssueController {

    private final BookIssueService issueService;
    private final UserRepository userRepository;

    // Constructor injection for services and repositories
    public BookIssueController(BookIssueService issueService, UserRepository userRepository) {
        this.issueService   = issueService;
        this.userRepository = userRepository;
    }

    /**
     * Member self-issue endpoint.
     * Takes the book ID from URL path and the logged-in user from the session.
     */
    @PostMapping("/book/{bookId}")
    public ResponseEntity<BookIssueResponse> issue(
            @PathVariable Long bookId,
            @AuthenticationPrincipal UserDetails principal) {
        Long userId = resolveUserId(principal);
        BookIssueResponse response = issueService.issue(userId, bookId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Admin issue-on-behalf endpoint.
     * Allows librarian to issue a book directly to any member ID.
     */
    @PostMapping("/admin/book/{bookId}/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BookIssueResponse> issueForUser(
            @PathVariable Long bookId,
            @PathVariable Long userId) {
        BookIssueResponse response = issueService.issue(userId, bookId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Return book endpoint.
     * Accessible by the member who borrowed it, or by an administrator.
     */
    @PostMapping("/{issueId}/return")
    public ResponseEntity<BookIssueResponse> returnBook(
            @PathVariable Long issueId,
            @AuthenticationPrincipal UserDetails principal) {
        BookIssueResponse bi = issueService.findById(issueId);
        Long requestingId = resolveUserId(principal);
        boolean isAdmin = principal.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        // Non-admins can only return books they borrowed themselves
        if (!isAdmin && !bi.userId().equals(requestingId)) {
            throw new BusinessException("Access denied", 403);
        }
        return ResponseEntity.ok(issueService.returnBook(issueId));
    }

    /**
     * Member view: List current user's borrowed books and history.
     */
    @GetMapping("/my")
    public ResponseEntity<Page<BookIssueResponse>> myIssues(
            @AuthenticationPrincipal UserDetails principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long userId = resolveUserId(principal);
        return ResponseEntity.ok(issueService.findByUser(userId, PageRequest.of(page, size,
                Sort.by("createdAt").descending())));
    }

    /**
     * Admin view: List all book issue records across the entire library.
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<BookIssueResponse>> all(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(issueService.findAll(PageRequest.of(page, size,
                Sort.by("createdAt").descending())));
    }

    /**
     * Helper method: Resolves internal numeric user ID from Spring Security's UserDetails.
     */
    private Long resolveUserId(UserDetails principal) {
        return userRepository.findByEmail(principal.getUsername())
                .map(User::getId).orElseThrow();
    }
}

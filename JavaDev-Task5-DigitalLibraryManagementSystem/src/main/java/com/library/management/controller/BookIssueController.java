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

@RestController
@RequestMapping("/api/issues")
public class BookIssueController {

    private final BookIssueService issueService;
    private final UserRepository userRepository;

    public BookIssueController(BookIssueService issueService, UserRepository userRepository) {
        this.issueService   = issueService;
        this.userRepository = userRepository;
    }

    /** User: issue a book to self */
    @PostMapping("/book/{bookId}")
    public ResponseEntity<BookIssueResponse> issue(
            @PathVariable Long bookId,
            @AuthenticationPrincipal UserDetails principal) {
        Long userId = resolveUserId(principal);
        return ResponseEntity.status(HttpStatus.CREATED).body(issueService.issue(userId, bookId));
    }

    /** Admin: issue on behalf of any user */
    @PostMapping("/admin/book/{bookId}/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BookIssueResponse> issueForUser(
            @PathVariable Long bookId,
            @PathVariable Long userId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(issueService.issue(userId, bookId));
    }

    /** User or admin: return */
    @PostMapping("/{issueId}/return")
    public ResponseEntity<BookIssueResponse> returnBook(
            @PathVariable Long issueId,
            @AuthenticationPrincipal UserDetails principal) {
        BookIssueResponse bi = issueService.findById(issueId);
        Long requestingId = resolveUserId(principal);
        boolean isAdmin = principal.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (!isAdmin && !bi.userId().equals(requestingId))
            throw new BusinessException("Access denied", 403);
        return ResponseEntity.ok(issueService.returnBook(issueId));
    }

    /** User: own issues */
    @GetMapping("/my")
    public ResponseEntity<Page<BookIssueResponse>> myIssues(
            @AuthenticationPrincipal UserDetails principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long userId = resolveUserId(principal);
        return ResponseEntity.ok(issueService.findByUser(userId, PageRequest.of(page, size,
                Sort.by("createdAt").descending())));
    }

    /** Admin: all issues */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<BookIssueResponse>> all(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(issueService.findAll(PageRequest.of(page, size,
                Sort.by("createdAt").descending())));
    }

    private Long resolveUserId(UserDetails principal) {
        return userRepository.findByEmail(principal.getUsername())
                .map(User::getId).orElseThrow();
    }
}

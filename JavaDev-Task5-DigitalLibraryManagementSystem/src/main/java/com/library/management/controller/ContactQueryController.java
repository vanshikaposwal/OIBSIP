package com.library.management.controller;

import com.library.management.dto.*;
import com.library.management.entity.User;
import com.library.management.repository.UserRepository;
import com.library.management.service.ContactQueryService;
import jakarta.validation.Valid;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for Support / Contact Inquiries.
 * Endpoints:
 * - POST /api/queries            : Submit a new inquiry (Public or Member)
 * - GET  /api/queries/my         : Member views own submitted inquiries
 * - GET  /api/queries            : Admin views all inquiries
 * - POST /api/queries/{id}/resolve: Admin marks inquiry as resolved
 */
@RestController
@RequestMapping("/api/queries")
public class ContactQueryController {

    private final ContactQueryService queryService;
    private final UserRepository userRepository;

    // Constructor injection for service and repository
    public ContactQueryController(ContactQueryService queryService, UserRepository userRepository) {
        this.queryService   = queryService;
        this.userRepository = userRepository;
    }

    /**
     * Submit inquiry endpoint.
     * Open to anyone (including anonymous visitors).
     */
    @PostMapping
    public ResponseEntity<ContactQueryResponse> submit(
            @Valid @RequestBody ContactQueryRequest req,
            @AuthenticationPrincipal UserDetails principal) {
        Long userId = principal != null
                ? userRepository.findByEmail(principal.getUsername()).map(User::getId).orElse(null)
                : null;
        ContactQueryResponse response = queryService.submit(req, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Member view: List own submitted support queries.
     */
    @GetMapping("/my")
    public ResponseEntity<Page<ContactQueryResponse>> mine(
            @AuthenticationPrincipal UserDetails principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long userId = userRepository.findByEmail(principal.getUsername()).map(User::getId).orElseThrow();
        return ResponseEntity.ok(queryService.findByUser(userId,
                PageRequest.of(page, size, Sort.by("createdAt").descending())));
    }

    /**
     * Admin view: List all submitted support tickets across the library.
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<ContactQueryResponse>> all(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(queryService.findAll(
                PageRequest.of(page, size, Sort.by("createdAt").descending())));
    }

    /**
     * Admin action: Mark an inquiry as resolved.
     */
    @PostMapping("/{id}/resolve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ContactQueryResponse> resolve(@PathVariable Long id) {
        return ResponseEntity.ok(queryService.resolve(id));
    }
}

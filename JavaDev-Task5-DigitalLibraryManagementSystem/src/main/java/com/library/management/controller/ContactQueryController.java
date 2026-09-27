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

@RestController
@RequestMapping("/api/queries")
public class ContactQueryController {

    private final ContactQueryService queryService;
    private final UserRepository userRepository;

    public ContactQueryController(ContactQueryService queryService, UserRepository userRepository) {
        this.queryService   = queryService;
        this.userRepository = userRepository;
    }

    /** Anyone (even anonymous) can submit a query */
    @PostMapping
    public ResponseEntity<ContactQueryResponse> submit(
            @Valid @RequestBody ContactQueryRequest req,
            @AuthenticationPrincipal UserDetails principal) {
        Long userId = principal != null
                ? userRepository.findByEmail(principal.getUsername()).map(User::getId).orElse(null)
                : null;
        return ResponseEntity.status(HttpStatus.CREATED).body(queryService.submit(req, userId));
    }

    /** Authenticated user: own queries */
    @GetMapping("/my")
    public ResponseEntity<Page<ContactQueryResponse>> mine(
            @AuthenticationPrincipal UserDetails principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long userId = userRepository.findByEmail(principal.getUsername()).map(User::getId).orElseThrow();
        return ResponseEntity.ok(queryService.findByUser(userId,
                PageRequest.of(page, size, Sort.by("createdAt").descending())));
    }

    /** Admin: all queries */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<ContactQueryResponse>> all(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(queryService.findAll(
                PageRequest.of(page, size, Sort.by("createdAt").descending())));
    }

    /** Admin: resolve a query */
    @PostMapping("/{id}/resolve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ContactQueryResponse> resolve(@PathVariable Long id) {
        return ResponseEntity.ok(queryService.resolve(id));
    }
}

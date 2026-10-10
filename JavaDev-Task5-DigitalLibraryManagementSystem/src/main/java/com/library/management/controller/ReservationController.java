package com.library.management.controller;

import com.library.management.dto.ReservationResponse;
import com.library.management.entity.User;
import com.library.management.repository.UserRepository;
import com.library.management.service.ReservationService;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for Book Reservations (Waitlist).
 * Endpoints:
 * - POST /api/reservations/book/{bookId} : Member joins waitlist for an out-of-stock book
 * - POST /api/reservations/{id}/cancel   : Member or admin cancels reservation
 * - GET  /api/reservations/my            : Member views their active and past reservations
 * - GET  /api/reservations               : Admin views all reservations
 */
@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;
    private final UserRepository userRepository;

    // Constructor injection for service and repository
    public ReservationController(ReservationService reservationService, UserRepository userRepository) {
        this.reservationService = reservationService;
        this.userRepository     = userRepository;
    }

    /**
     * Reserve an out-of-stock book.
     */
    @PostMapping("/book/{bookId}")
    public ResponseEntity<ReservationResponse> reserve(
            @PathVariable Long bookId,
            @AuthenticationPrincipal UserDetails principal) {
        Long userId = resolveId(principal);
        ReservationResponse response = reservationService.reserve(userId, bookId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Cancel an existing reservation.
     */
    @PostMapping("/{id}/cancel")
    public ResponseEntity<ReservationResponse> cancel(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails principal) {
        Long userId = resolveId(principal);
        boolean isAdmin = principal.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        return ResponseEntity.ok(reservationService.cancel(id, userId, isAdmin));
    }

    /**
     * Member view: List current user's reservations.
     */
    @GetMapping("/my")
    public ResponseEntity<Page<ReservationResponse>> mine(
            @AuthenticationPrincipal UserDetails principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(reservationService.findByUser(
                resolveId(principal), PageRequest.of(page, size, Sort.by("reservationDate").descending())));
    }

    /**
     * Admin view: List all library reservations.
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<ReservationResponse>> all(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(reservationService.findAll(
                PageRequest.of(page, size, Sort.by("reservationDate").descending())));
    }

    /**
     * Helper method: Resolves numeric user ID from UserDetails.
     */
    private Long resolveId(UserDetails principal) {
        return userRepository.findByEmail(principal.getUsername()).map(User::getId).orElseThrow();
    }
}

package com.library.management.controller;

import com.library.management.dto.FineResponse;
import com.library.management.entity.User;
import com.library.management.repository.UserRepository;
import com.library.management.service.FineService;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/fines")
public class FineController {

    private final FineService fineService;
    private final UserRepository userRepository;

    public FineController(FineService fineService, UserRepository userRepository) {
        this.fineService    = fineService;
        this.userRepository = userRepository;
    }

    @GetMapping("/my")
    public ResponseEntity<Page<FineResponse>> myFines(
            @AuthenticationPrincipal UserDetails principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long userId = resolveId(principal);
        return ResponseEntity.ok(fineService.findByUser(userId,
                PageRequest.of(page, size, Sort.by("createdAt").descending())));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<FineResponse>> all(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(fineService.findAll(
                PageRequest.of(page, size, Sort.by("createdAt").descending())));
    }

    @PostMapping("/{id}/pay")
    public ResponseEntity<FineResponse> pay(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails principal) {
        Long userId = resolveId(principal);
        boolean isAdmin = principal.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        return ResponseEntity.ok(fineService.pay(id, userId, isAdmin));
    }

    private Long resolveId(UserDetails principal) {
        return userRepository.findByEmail(principal.getUsername()).map(User::getId).orElseThrow();
    }
}

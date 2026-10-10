package com.library.management.controller;

import com.library.management.dto.*;
import com.library.management.entity.User;
import com.library.management.repository.UserRepository;
import com.library.management.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for Admin User Management.
 * All endpoints require ROLE_ADMIN.
 * 
 * Endpoints:
 * - GET    /api/admin/users     : List all members (paginated)
 * - GET    /api/admin/users/{id}: Get member details by ID
 * - PUT    /api/admin/users/{id}: Update member account
 * - DELETE /api/admin/users/{id}: Delete member account
 */
@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class UserController {

    private final UserService userService;

    // Constructor injection for UserService
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Admin view: List all library members with pagination.
     */
    @GetMapping
    public ResponseEntity<Page<UserResponse>> all(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(userService.findAll(
                PageRequest.of(page, size, Sort.by("createdAt").descending())));
    }

    /**
     * Admin view: Get a single user's profile by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    /**
     * Admin action: Update user profile, password, role, or enabled status.
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> update(@PathVariable Long id,
                                                @Valid @RequestBody UpdateUserRequest req) {
        return ResponseEntity.ok(userService.updateUser(id, req));
    }

    /**
     * Admin action: Delete a user account from the system.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}

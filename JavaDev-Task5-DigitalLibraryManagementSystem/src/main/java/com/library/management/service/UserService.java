package com.library.management.service;

import com.library.management.config.LibraryProperties;
import com.library.management.dto.*;
import com.library.management.entity.User;
import com.library.management.exception.BusinessException;
import com.library.management.exception.ResourceNotFoundException;
import com.library.management.repository.FineRepository;
import com.library.management.repository.UserRepository;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for User Account management.
 * Handles user registration, profile lookup, updates, and deletion.
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final FineRepository fineRepository;
    private final LibraryProperties props;

    // Constructor injection for repositories and password encoder
    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       FineRepository fineRepository,
                       LibraryProperties props) {
        this.userRepository  = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.fineRepository  = fineRepository;
        this.props           = props;
    }

    /**
     * Registers a new library member.
     * Hashes password using BCrypt and assigns default ROLE_USER.
     */
    @Transactional
    public UserResponse register(RegisterRequest req) {
        // Step 1: Check if an account already exists with this email address
        if (userRepository.existsByEmail(req.email())) {
            throw new BusinessException("Email already registered");
        }

        // Step 2: Hash the plain text password securely with BCrypt
        String hashedPassword = passwordEncoder.encode(req.password());

        // Step 3: Create the new User entity
        User user = User.builder()
                .name(req.name())
                .email(req.email())
                .passwordHash(hashedPassword)
                .role(User.Role.ROLE_USER)
                .phone(req.phone())
                .address(req.address())
                .enabled(true)
                .build();

        // Step 4: Save user in database and return response DTO
        User savedUser = userRepository.save(user);
        return UserResponse.from(savedUser);
    }

    /**
     * Finds a user by email address.
     */
    public UserResponse findByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        return UserResponse.from(user);
    }

    /**
     * Finds a user by ID.
     */
    public UserResponse findById(Long id) {
        User user = getOrThrow(id);
        return UserResponse.from(user);
    }

    /**
     * Retrieves all users (paginated) for admin member management.
     */
    public Page<UserResponse> findAll(Pageable pageable) {
        return userRepository.findAll(pageable).map(UserResponse::from);
    }

    /**
     * Updates an existing user's details (admin edit).
     */
    @Transactional
    public UserResponse updateUser(Long id, UpdateUserRequest req) {
        // Step 1: Find the existing user
        User user = getOrThrow(id);

        // Step 2: Update name if provided
        if (req.name() != null) {
            user.setName(req.name());
        }

        // Step 3: Update email if provided and ensure uniqueness
        if (req.email() != null && !req.email().equals(user.getEmail())) {
            if (userRepository.existsByEmail(req.email())) {
                throw new BusinessException("Email already in use");
            }
            user.setEmail(req.email());
        }

        // Step 4: Update password if provided
        if (req.password() != null && !req.password().isBlank()) {
            user.setPasswordHash(passwordEncoder.encode(req.password()));
        }

        // Step 5: Update contact details
        if (req.phone() != null) {
            user.setPhone(req.phone());
        }
        if (req.address() != null) {
            user.setAddress(req.address());
        }

        // Step 6: Update enabled status
        if (req.enabled() != null) {
            user.setEnabled(req.enabled());
        }

        // Step 7: Update user role
        if (req.role() != null) {
            try {
                user.setRole(User.Role.valueOf(req.role()));
            } catch (IllegalArgumentException e) {
                throw new BusinessException("Invalid role: " + req.role(), 400);
            }
        }

        User updatedUser = userRepository.save(user);
        return UserResponse.from(updatedUser);
    }

    /**
     * Deletes a user account by ID.
     */
    @Transactional
    public void deleteUser(Long id) {
        User user = getOrThrow(id);
        userRepository.delete(user);
    }

    /**
     * Checks whether a user has any unpaid fines (used during book issue validation).
     */
    public boolean hasUnpaidFines(Long userId) {
        return props.isBlockOnUnpaidFines() && fineRepository.existsByUserIdAndPaidFalse(userId);
    }

    /**
     * Helper method to find user or throw 404 error.
     */
    private User getOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
    }
}

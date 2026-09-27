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

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final FineRepository fineRepository;
    private final LibraryProperties props;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       FineRepository fineRepository,
                       LibraryProperties props) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.fineRepository = fineRepository;
        this.props = props;
    }

    @Transactional
    public UserResponse register(RegisterRequest req) {
        if (userRepository.existsByEmail(req.email())) {
            throw new BusinessException("Email already registered");
        }
        User user = User.builder()
                .name(req.name())
                .email(req.email())
                .passwordHash(passwordEncoder.encode(req.password()))
                .role(User.Role.ROLE_USER)
                .phone(req.phone())
                .address(req.address())
                .enabled(true)
                .build();
        return UserResponse.from(userRepository.save(user));
    }

    public UserResponse findByEmail(String email) {
        return UserResponse.from(userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found")));
    }

    public UserResponse findById(Long id) {
        return UserResponse.from(getOrThrow(id));
    }

    public Page<UserResponse> findAll(Pageable pageable) {
        return userRepository.findAll(pageable).map(UserResponse::from);
    }

    @Transactional
    public UserResponse updateUser(Long id, UpdateUserRequest req) {
        User user = getOrThrow(id);
        if (req.name() != null)    user.setName(req.name());
        if (req.email() != null && !req.email().equals(user.getEmail())) {
            if (userRepository.existsByEmail(req.email()))
                throw new BusinessException("Email already in use");
            user.setEmail(req.email());
        }
        if (req.password() != null) user.setPasswordHash(passwordEncoder.encode(req.password()));
        if (req.phone() != null)    user.setPhone(req.phone());
        if (req.address() != null)  user.setAddress(req.address());
        if (req.enabled() != null)  user.setEnabled(req.enabled());
        if (req.role() != null) {
            try { user.setRole(User.Role.valueOf(req.role())); }
            catch (IllegalArgumentException e) { throw new BusinessException("Invalid role: " + req.role(), 400); }
        }
        return UserResponse.from(userRepository.save(user));
    }

    @Transactional
    public void deleteUser(Long id) {
        userRepository.delete(getOrThrow(id));
    }

    /** Checks whether a user has any unpaid fines (used by BookIssueService). */
    public boolean hasUnpaidFines(Long userId) {
        return props.isBlockOnUnpaidFines() && fineRepository.existsByUserIdAndPaidFalse(userId);
    }

    private User getOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
    }
}

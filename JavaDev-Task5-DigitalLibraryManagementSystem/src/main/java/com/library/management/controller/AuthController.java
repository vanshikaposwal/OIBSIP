package com.library.management.controller;

import com.library.management.config.CustomUserDetailsService;
import com.library.management.dto.*;
import com.library.management.entity.User;
import com.library.management.repository.UserRepository;
import com.library.management.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;

/**
 * Controller handling user authentication and session management.
 * Endpoints:
 * - POST /api/auth/register  : Creates a new user account
 * - POST /api/auth/login     : Authenticates user and establishes HTTP session
 * - GET  /api/auth/me        : Returns profile of current authenticated user
 * - POST /api/auth/logout    : Terminates active user session
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;

    // Constructor injection for services
    public AuthController(UserService userService,
                          UserRepository userRepository,
                          AuthenticationManager authenticationManager) {
        this.userService           = userService;
        this.userRepository        = userRepository;
        this.authenticationManager = authenticationManager;
    }

    /**
     * Handles member registration.
     * Validates input with @Valid and returns 201 Created.
     */
    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest req) {
        UserResponse response = userService.register(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Handles login via JSON credentials.
     * Authenticates with Spring's AuthenticationManager and stores the
     * security context inside the user's HttpSession (JSESSIONID cookie).
     */
    @PostMapping("/login")
    public ResponseEntity<UserResponse> login(@Valid @RequestBody LoginRequest req,
                                               HttpServletRequest httpRequest) {
        // Step 1: Authenticate the username/email and password
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.email(), req.password()));

        // Step 2: Store authentication into SecurityContextHolder and HttpSession
        SecurityContext sc = SecurityContextHolder.getContext();
        sc.setAuthentication(auth);
        HttpSession session = httpRequest.getSession(true);
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, sc);

        // Step 3: Fetch user profile and return to client
        User user = userRepository.findByEmail(req.email()).orElseThrow();
        return ResponseEntity.ok(UserResponse.from(user));
    }

    /**
     * Returns the currently authenticated user's profile.
     * If unauthenticated, returns 401 Unauthorized.
     */
    @GetMapping("/me")
    public ResponseEntity<?> me(@AuthenticationPrincipal UserDetails principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not authenticated");
        }
        User user = userRepository.findByEmail(principal.getUsername()).orElseThrow();
        return ResponseEntity.ok(UserResponse.from(user));
    }

    /**
     * Logs out the user by invalidating their active HttpSession.
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok().build();
    }
}

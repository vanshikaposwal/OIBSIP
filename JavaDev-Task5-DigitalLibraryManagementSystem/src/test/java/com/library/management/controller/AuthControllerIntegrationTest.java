package com.library.management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.management.dto.LoginRequest;
import com.library.management.dto.RegisterRequest;
import com.library.management.entity.User;
import com.library.management.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        User user = User.builder()
                .name("Alice Test")
                .email("alice@test.com")
                .passwordHash(passwordEncoder.encode("Password@123"))
                .role(User.Role.ROLE_USER)
                .enabled(true)
                .build();
        userRepository.save(user);
    }

    @Test
    @DisplayName("POST /api/auth/register - creates new user and returns 201 Created")
    void register_Success() throws Exception {
        RegisterRequest req = new RegisterRequest("New User", "new@test.com", "Password@123", "9999999999", "Address");

        mockMvc.perform(post("/api/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("new@test.com"))
                .andExpect(jsonPath("$.role").value("ROLE_USER"))
                .andExpect(jsonPath("$.passwordHash").doesNotExist()); // Never leak password hash
    }

    @Test
    @DisplayName("POST /api/auth/register - duplicate email returns 409 Conflict via GlobalExceptionHandler")
    void register_DuplicateEmail_Returns409() throws Exception {
        RegisterRequest req = new RegisterRequest("Duplicate User", "alice@test.com", "Password@123", null, null);

        mockMvc.perform(post("/api/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message", containsString("Email already registered")));
    }

    @Test
    @DisplayName("POST /api/auth/login - valid credentials returns 200 OK")
    void login_ValidCredentials_ReturnsOk() throws Exception {
        LoginRequest req = new LoginRequest("alice@test.com", "Password@123");

        mockMvc.perform(post("/api/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("alice@test.com"))
                .andExpect(jsonPath("$.name").value("Alice Test"));
    }

    @Test
    @DisplayName("POST /api/auth/login - invalid password returns 401 Unauthorized via GlobalExceptionHandler")
    void login_InvalidPassword_Returns401() throws Exception {
        LoginRequest req = new LoginRequest("alice@test.com", "WrongPassword");

        mockMvc.perform(post("/api/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }
}

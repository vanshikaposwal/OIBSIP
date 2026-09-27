package com.library.management.service;

import com.library.management.config.LibraryProperties;
import com.library.management.dto.RegisterRequest;
import com.library.management.dto.UserResponse;
import com.library.management.entity.User;
import com.library.management.exception.BusinessException;
import com.library.management.repository.FineRepository;
import com.library.management.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private FineRepository fineRepository;

    private LibraryProperties libraryProperties;

    private UserService userService;

    private User sampleUser;

    @BeforeEach
    void setUp() {
        libraryProperties = new LibraryProperties();
        libraryProperties.setBlockOnUnpaidFines(true);
        userService = new UserService(userRepository, passwordEncoder, fineRepository, libraryProperties);
        sampleUser = User.builder()
                .id(1L)
                .name("Alice Sharma")
                .email("alice@example.com")
                .passwordHash("hashed-password")
                .role(User.Role.ROLE_USER)
                .enabled(true)
                .build();
    }

    @Test
    @DisplayName("Register user successfully")
    void register_Success() {
        RegisterRequest req = new RegisterRequest("Alice Sharma", "alice@example.com", "Password@123", "9876543210", "Delhi");

        when(userRepository.existsByEmail(req.email())).thenReturn(false);
        when(passwordEncoder.encode(req.password())).thenReturn("hashed-password");
        when(userRepository.save(any(User.class))).thenReturn(sampleUser);

        UserResponse res = userService.register(req);

        assertNotNull(res);
        assertEquals("alice@example.com", res.email());
        assertEquals("ROLE_USER", res.role());
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Register user with duplicate email throws BusinessException")
    void register_DuplicateEmail_ThrowsException() {
        RegisterRequest req = new RegisterRequest("Alice Sharma", "alice@example.com", "Password@123", null, null);

        when(userRepository.existsByEmail(req.email())).thenReturn(true);

        assertThrows(BusinessException.class, () -> userService.register(req));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Check user has unpaid fines when property enabled")
    void hasUnpaidFines_ReturnsTrue() {
        libraryProperties.setBlockOnUnpaidFines(true);
        when(fineRepository.existsByUserIdAndPaidFalse(1L)).thenReturn(true);

        assertTrue(userService.hasUnpaidFines(1L));
    }
}

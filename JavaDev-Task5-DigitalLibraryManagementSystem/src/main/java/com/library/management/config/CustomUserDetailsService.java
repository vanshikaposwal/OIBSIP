package com.library.management.config;

import com.library.management.entity.User;
import com.library.management.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Custom UserDetailsService implementation.
 * 
 * Spring Security calls loadUserByUsername() during authentication (login).
 * It fetches the user record from the database by email, loads their role and password hash,
 * and converts them into Spring Security's UserDetails object.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    // Constructor injection for UserRepository
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Step 1: Look up user in database by email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        // Step 2: Convert entity into Spring Security's UserDetails
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPasswordHash())
                .authorities(List.of(new SimpleGrantedAuthority(user.getRole().name())))
                .accountLocked(!user.isEnabled())
                .disabled(!user.isEnabled())
                .build();
    }
}

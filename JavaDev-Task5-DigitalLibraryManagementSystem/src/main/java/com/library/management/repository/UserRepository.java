package com.library.management.repository;

import com.library.management.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA Repository for Users.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Finds a user account by email address (used during login and authentication)
    Optional<User> findByEmail(String email);

    // Checks whether an email is already registered in the system
    boolean existsByEmail(String email);
}

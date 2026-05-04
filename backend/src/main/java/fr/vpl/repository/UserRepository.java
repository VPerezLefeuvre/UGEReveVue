package fr.vpl.repository;

import fr.vpl.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Repository interface for User entity.
 * Provides standard CRUD operations and custom existence checks.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Checks if a user exists with the given username.
     * Used for pre-registration validation.
     */
    boolean existsByUsername(String username);

    /**
     * Checks if a user exists with the given email.
     * Used for pre-registration validation.
     */
    boolean existsByEmail(String email);

    Optional<User> findByUsername(String username);
}
package fr.vpl.repository;

import fr.vpl.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Persistence layer tests for {@link UserRepository}.
 * H2 is used through the test profile to verify Spring Data query methods
 * without depending on the development PostgreSQL database.
 */
@DataJpaTest(showSql = false)
@ActiveProfiles("test")
@DisplayName("UserRepository persistence tests")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("existsByUsername returns true when username is present")
    void existsByUsername_shouldReturnTrue_whenUsernameExists() {
        persistUser("architect", "arch@vpl.fr");

        assertThat(userRepository.existsByUsername("architect")).isTrue();
    }

    @Test
    @DisplayName("existsByUsername returns false when username is absent")
    void existsByUsername_shouldReturnFalse_whenUsernameDoesNotExist() {
        persistUser("architect", "arch@vpl.fr");

        assertThat(userRepository.existsByUsername("missing")).isFalse();
    }

    @Test
    @DisplayName("existsByEmail returns true when email is present")
    void existsByEmail_shouldReturnTrue_whenEmailExists() {
        persistUser("architect", "arch@vpl.fr");

        assertThat(userRepository.existsByEmail("arch@vpl.fr")).isTrue();
    }

    @Test
    @DisplayName("findByUsername returns the matching user")
    void findByUsername_shouldReturnUser_whenUsernameExists() {
        persistUser("pro_dev", "dev@vpl.fr");

        Optional<User> found = userRepository.findByUsername("pro_dev");

        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo("pro_dev");
    }

    @Test
    @DisplayName("findByUsername returns empty when username is absent")
    void findByUsername_shouldReturnEmpty_whenUsernameDoesNotExist() {
        Optional<User> found = userRepository.findByUsername("missing");

        assertThat(found).isEmpty();
    }

    private void persistUser(String username, String email) {
        User user = User.builder()
                .username(username)
                .email(email)
                .password("encoded")
                .build();
        entityManager.persist(user);
        entityManager.flush();
    }
}

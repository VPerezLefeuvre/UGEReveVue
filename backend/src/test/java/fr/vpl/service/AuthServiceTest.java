package fr.vpl.service;

import fr.vpl.dto.RegisterRequest;
import fr.vpl.entity.User;
import fr.vpl.exception.UserAlreadyExistsException;
import fr.vpl.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link AuthService}.
 * Collaborators are mocked so the assertions focus on duplicate checks,
 * password hashing, and the entity sent to persistence.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService unit tests")
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @Test
    @DisplayName("register saves a user with encoded password and default USER role")
    void register_shouldSaveUserWithEncodedPasswordAndDefaultRole_whenRequestIsValid() {
        RegisterRequest request = new RegisterRequest("cleanCode", "bob@vpl.fr", "Secure123!");
        when(userRepository.existsByUsername("cleanCode")).thenReturn(false);
        when(userRepository.existsByEmail("bob@vpl.fr")).thenReturn(false);
        when(passwordEncoder.encode("Secure123!")).thenReturn("hashed_password");

        authService.register(request);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertThat(savedUser.getUsername()).isEqualTo("cleanCode");
        assertThat(savedUser.getEmail()).isEqualTo("bob@vpl.fr");
        assertThat(savedUser.getPassword()).isEqualTo("hashed_password");
        assertThat(savedUser.getRole()).isEqualTo(User.Role.USER);
    }

    @Test
    @DisplayName("register throws before email check when username already exists")
    void register_shouldThrowAndStop_whenUsernameAlreadyExists() {
        when(userRepository.existsByUsername("taken")).thenReturn(true);

        RegisterRequest request = new RegisterRequest("taken", "new@test.com", "Password123!");

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessage("validation.user.username.exists");

        verify(userRepository, never()).existsByEmail(anyString());
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("register throws before password encoding when email already exists")
    void register_shouldThrowAndStop_whenEmailAlreadyExists() {
        when(userRepository.existsByUsername("user")).thenReturn(false);
        when(userRepository.existsByEmail("dup@test.com")).thenReturn(true);

        RegisterRequest request = new RegisterRequest("user", "dup@test.com", "Password123!");

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessage("validation.user.email.exists");

        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }
}

package fr.vpl.service;

import fr.vpl.dto.LoginRequest;
import fr.vpl.dto.RegisterRequest;
import fr.vpl.entity.User;
import fr.vpl.exception.InvalidCredentialsException;
import fr.vpl.exception.UserAlreadyExistsException;
import fr.vpl.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Handles registration, login, and user identity rules.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            log.warn("Registration failed: Username {} already exists", request.username());
            throw new UserAlreadyExistsException("username");
        }

        if (userRepository.existsByEmail(request.email())) {
            log.warn("Registration failed: Email {} already exists", request.email());
            throw new UserAlreadyExistsException("email");
        }

        User user = User.builder()
                .username(request.username())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(User.Role.USER)
                .build();

        userRepository.save(user);
        log.info("User successfully registered: {}", user.getUsername());
        return user;

    }

    /**
     * Verifies login credentials without revealing which field failed.
     */
    @Transactional(readOnly = true)
    public User login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> {
                    log.warn("Login failed: Email {} was not found", request.email());
                    return new InvalidCredentialsException();
                });

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            log.warn("Login failed: Invalid password for email {}", request.email());
            throw new InvalidCredentialsException();
        }

        log.info("User successfully logged in: {}", user.getUsername());
        return user;
    }
}

package fr.vpl.service;

import fr.vpl.dto.RegisterRequest;
import fr.vpl.entity.User;
import fr.vpl.exception.UserAlreadyExistsException;
import fr.vpl.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service handling authentication and user identity management.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Registers a new user in the system.
     * Checks for duplicates before persistence to provide clean business errors.
     *
     * @param request Validated registration data
     * @throws UserAlreadyExistsException if username or email is already taken
     */
    @Transactional
    public void register(RegisterRequest request) {
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
    }
}
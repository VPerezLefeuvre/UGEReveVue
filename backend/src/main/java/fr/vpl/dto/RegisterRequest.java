package fr.vpl.dto;

import fr.vpl.validation.ValidPassword;
import jakarta.validation.constraints.*;

/**
 * Data Transfer Object for user registration.
 * Uses Jakarta Validation constraints with localized messages.
 *
 * @param username The desired username (3-20 chars)
 * @param email    The user's email (max 100 chars)
 * @param password The raw password (validated via custom logic)
 */
public record RegisterRequest(
        @NotBlank(message = "USERNAME_REQUIRED")
        @Size(min = 3, max = 20, message = "USERNAME_SIZE_INVALID")
        @Pattern(regexp = "^[a-zA-Z0-9_]*$", message = "USERNAME_PATTERN_INVALID")
        String username,

        @NotBlank(message = "EMAIL_REQUIRED")
        @Size(max = 100, message = "EMAIL_SIZE_INVALID")
        @Email(message = "EMAIL_FORMAT_INVALID")
        String email,

        @NotBlank(message = "PASSWORD_REQUIRED")
        @Size(min = 8, max = 128, message = "PASSWORD_SIZE_INVALID")
        @ValidPassword
        String password
) {}
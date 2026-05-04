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
        @NotBlank(message = "{validation.user.username.required}")
        @Size(min = 3, max = 20, message = "{validation.user.username.size}")
        @Pattern(regexp = "^[a-zA-Z0-9_]*$", message = "{validation.user.username.pattern}")
        String username,

        @NotBlank(message = "{validation.user.email.required}")
        @Size(max = 100, message = "{validation.user.email.size}")
        @Email(message = "{validation.user.email.invalid}")
        String email,

        @NotBlank(message = "{validation.user.password.required}")
        @Size(min = 8, max = 128, message = "{validation.user.password.size}")
        @ValidPassword
        String password
) {}
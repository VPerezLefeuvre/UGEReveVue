package fr.vpl.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Data Transfer Object for user login.
 * Keeps authentication input minimal and validates the email format before
 * credentials are checked by the service layer.
 *
 * @param email    The user's email
 * @param password The raw password to verify
 */
public record LoginRequest(
        @NotBlank(message = "EMAIL_REQUIRED")
        String email,

        @NotBlank(message = "PASSWORD_REQUIRED")
        String password
) {}

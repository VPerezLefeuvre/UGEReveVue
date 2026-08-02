package fr.vpl.dto;

/**
 * Response returned after a successful login.
 * Exposes only client-safe account information.
 *
 * @param id       The authenticated user's identifier
 * @param username The authenticated user's username
 * @param email    The authenticated user's email
 * @param role     The authenticated user's application role
 */
public record LoginResponse(
        Long id,
        String username,
        String email,
        String role
) {}

package fr.vpl.exception;

/**
 * Business exception thrown when login credentials cannot be verified.
 * The message stays generic so the API never reveals whether the email exists.
 */
public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException() {
        super("INVALID_CREDENTIALS");
    }
}

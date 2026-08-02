package fr.vpl.exception;

import lombok.Getter;

/**
 * Business exception thrown when a registration fails due to duplicate data.
 * Captured by GlobalExceptionHandler to return a 409 Conflict.
 */
@Getter
public class UserAlreadyExistsException extends RuntimeException {
    private final String field;

    public UserAlreadyExistsException(String field) {
        super(field.toUpperCase() + "_ALREADY_EXISTS");
        this.field = field;
    }
}
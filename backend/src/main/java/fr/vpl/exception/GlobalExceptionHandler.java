package fr.vpl.exception;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Intercepts all application exceptions to return standardized JSON responses.
 */
@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    /**
     * Handles DTO validation failures (@Valid).
     * Group errors by field: { "username": ["Required", "Too short"] }
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, List<String>>> handleValidation(MethodArgumentNotValidException ex, Locale locale) {
        Map<String, List<String>> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.groupingBy(
                        FieldError::getField,
                        Collectors.mapping(e -> messageSource.getMessage(e, locale), Collectors.toList())
                ));
        return ResponseEntity.badRequest().body(errors);
    }

    /**
     * Handles business logic conflicts (duplicate user).
     */
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<Map<String, String>> handleUserExists(UserAlreadyExistsException ex, Locale locale) {
        String message = messageSource.getMessage(ex.getMessage(), null, locale);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(ex.getField(), message));
    }

    /**
     * Safety net for database constraints (race conditions).
     * Logs the technical cause but hides it from the client for security.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleSql(DataIntegrityViolationException ex, Locale locale) {
        log.error("Database Integrity Violation: {}", ex.getMostSpecificCause().getMessage());
        String message = messageSource.getMessage("validation.database.conflict", null, locale);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", message));
    }
}
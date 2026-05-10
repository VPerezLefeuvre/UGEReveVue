package fr.vpl.exception;

import fr.vpl.dto.ErrorResponse;
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

    /**
     * Handles DTO validation errors (@Valid).
     * Retrieves error slugs (e.g., "EMAIL_FORMAT_INVALID") from the constraints.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, List<String>> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.groupingBy(
                        FieldError::getField,
                        Collectors.mapping(FieldError::getDefaultMessage, Collectors.toList())
                ));

        return buildResponse(HttpStatus.BAD_REQUEST, "VALIDATION_FAILED", errors);
    }

    /**
     * Handles business logic conflicts (e.g., User already exists).
     */
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUserExists(UserAlreadyExistsException ex) {
        Map<String, List<String>> errors = Map.of(ex.getField(), List.of(ex.getMessage()));

        return buildResponse(HttpStatus.CONFLICT, "USER_ALREADY_EXISTS", errors);
    }

    /**
     * Safety net for database integrity violations (e.g., unique constraints).
     * Logs the specific cause for debugging but returns a generic slug to the client.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleSql(DataIntegrityViolationException ex) {
        log.error("Database Integrity Violation: {}", ex.getMostSpecificCause().getMessage());

        Map<String, List<String>> errors = Map.of("database", List.of("DATABASE_CONFLICT"));
        return buildResponse(HttpStatus.CONFLICT, "DATA_INTEGRITY_ERROR", errors);
    }

    /**
     * Internal helper to build a standardized error response.
     */
    private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status, String code, Map<String, List<String>> details) {
        ErrorResponse response = new ErrorResponse(
                java.time.Instant.now().toString(),
                status.value(),
                code,
                details
        );
        return ResponseEntity.status(status).body(response);
    }
}
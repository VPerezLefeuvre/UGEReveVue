package fr.vpl.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link RegisterRequest} validation constraints.
 * These tests keep DTO validation independent from the web and persistence layers.
 */
@DisplayName("RegisterRequest validation tests")
class RegisterRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("validation accepts a fully valid registration request")
    void validate_shouldHaveNoViolation_whenRequestIsValid() {
        RegisterRequest request = new RegisterRequest("valid_user", "test@example.com", "SecureP@ss123");
        var violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("validation rejects an invalid email format")
    void validate_shouldRejectInvalidEmail() {
        RegisterRequest request = new RegisterRequest("user123", "wrong-email-format", "Password123!");
        var violations = validator.validate(request);

        assertThat(violations)
                .extracting(ConstraintViolation::getMessageTemplate)
                .contains("{validation.user.email.invalid}");
    }

    @Test
    @DisplayName("validation rejects usernames outside the allowed format")
    void validate_shouldRejectInvalidUsernamePattern() {
        RegisterRequest request = new RegisterRequest("bad-name", "test@example.com", "Password123!");
        var violations = validator.validate(request);

        assertThat(violations)
                .extracting(ConstraintViolation::getMessageTemplate)
                .contains("{validation.user.username.pattern}");
    }

    @Test
    @DisplayName("validation reports every missing required field")
    void validate_shouldRejectBlankRequiredFields() {
        RegisterRequest request = new RegisterRequest("", "", "");
        var violations = validator.validate(request);

        assertThat(violations)
                .extracting(violation -> violation.getPropertyPath().toString())
                .contains("username", "email", "password");
    }
}

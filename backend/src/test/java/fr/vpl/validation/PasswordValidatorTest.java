package fr.vpl.validation;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link PasswordValidator}.
 * They verify both the boolean result and the validation message templates
 * exposed to Jakarta Validation.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("PasswordValidator unit tests")
class PasswordValidatorTest {

    private final PasswordValidator validator = new PasswordValidator();

    @Mock
    private ConstraintValidatorContext context;

    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder builder;

    @BeforeEach
    void setUp() {
        lenient().when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
        lenient().when(builder.addConstraintViolation()).thenReturn(context);
    }

    @Test
    @DisplayName("isValid returns true for a password matching every complexity rule")
    void isValid_shouldReturnTrue_whenPasswordIsStrong() {
        boolean result = validator.isValid("StrongP@ss123", context);

        assertThat(result).isTrue();
        verifyNoInteractions(context, builder);
    }

    @Test
    @DisplayName("isValid returns false and reports every missing complexity rule")
    void isValid_shouldReturnFalseAndAddViolations_whenPasswordIsWeak() {
        boolean result = validator.isValid("abcde", context);

        assertThat(result).isFalse();
        verify(context).disableDefaultConstraintViolation();
        verify(context).buildConstraintViolationWithTemplate("{validation.user.password.uppercase}");
        verify(context).buildConstraintViolationWithTemplate("{validation.user.password.digit}");
        verify(context).buildConstraintViolationWithTemplate("{validation.user.password.special}");
        verify(builder, times(3)).addConstraintViolation();
    }

    @Test
    @DisplayName("isValid returns false for null because @NotBlank owns the user-facing message")
    void isValid_shouldReturnFalse_whenPasswordIsNull() {
        boolean result = validator.isValid(null, context);

        assertThat(result).isFalse();
        verifyNoInteractions(context, builder);
    }
}

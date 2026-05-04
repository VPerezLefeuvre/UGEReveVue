package fr.vpl.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.ArrayList;
import java.util.List;

/**
 * Validator checking password complexity (Uppercase, Digits, Special chars).
 * Length is handled by @Size in the DTO for better documentation.
 */
public class PasswordValidator implements ConstraintValidator<ValidPassword, String> {

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null) return false;

        List<String> errorKeys = new ArrayList<>();

        if (!password.matches(".*[A-Z].*")) errorKeys.add("{validation.user.password.uppercase}");
        if (!password.matches(".*[0-9].*")) errorKeys.add("{validation.user.password.digit}");
        if (!password.matches(".*[@#$%^&+=!].*")) errorKeys.add("{validation.user.password.special}");

        if (!errorKeys.isEmpty()) {
            // Replace default "Invalid password" with multiple specific errors
            context.disableDefaultConstraintViolation();
            for (String key : errorKeys) {
                context.buildConstraintViolationWithTemplate(key)
                        .addConstraintViolation();
            }
            return false;
        }
        return true;
    }
}
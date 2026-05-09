package fr.vpl.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.ArrayList;
import java.util.List;

/**
 * Validates password complexity. Length is handled by the DTO with {@code @Size}.
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

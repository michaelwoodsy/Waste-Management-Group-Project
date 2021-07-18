package org.seng302.project.serviceLayer.dto.validators.user;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Defines the validation method performed on
 * fields annotated with @ValidPassword in DTOs
 */
public class ValidPasswordValidator implements ConstraintValidator<ValidPassword, String> {

    @Override
    public boolean isValid(String newPassword, ConstraintValidatorContext context) {
        return newPassword.matches("(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{8,}");
    }
}

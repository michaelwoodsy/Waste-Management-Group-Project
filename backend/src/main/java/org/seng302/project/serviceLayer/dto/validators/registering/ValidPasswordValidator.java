package org.seng302.project.serviceLayer.dto.validators.registering;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Defines the validation method performed on
 * fields annotated with @ValidPassword in DTOs
 */
public class ValidPasswordValidator implements ConstraintValidator<ValidPassword, String> {

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        return password.matches("(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{8,}");
    }
}

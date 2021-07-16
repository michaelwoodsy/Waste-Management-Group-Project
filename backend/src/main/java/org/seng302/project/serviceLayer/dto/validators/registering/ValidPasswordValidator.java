package org.seng302.project.serviceLayer.dto.validators.registering;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Defines the validation method performed on
 * fields annotated with @ValidPassword in DTOs
 */
public class ValidPasswordValidator implements ConstraintValidator<ValidPassword, String> {

    @Override
    public boolean isValid(String newPassword, ConstraintValidatorContext context) {
        //newPassword can be null, meaning that it is not being changed.
        return newPassword == null ||
                newPassword.equals("") ||
                newPassword.matches("(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{8,}");
    }
}

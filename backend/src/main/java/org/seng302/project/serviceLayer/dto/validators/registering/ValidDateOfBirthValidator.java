package org.seng302.project.serviceLayer.dto.validators.registering;

import org.seng302.project.serviceLayer.util.DateArithmetic;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Defines the validation method performed on
 * fields annotated with @ValidDateOfBirth in DTOs
 */
public class ValidDateOfBirthValidator implements ConstraintValidator<ValidDateOfBirth, String> {

    @Override
    public boolean isValid(String dateOfBirth, ConstraintValidatorContext context) {
        Date dateOfBirthDate;
        try {
            dateOfBirthDate = new SimpleDateFormat("yyyy-MM-dd").parse(dateOfBirth);
        } catch (Exception exception) {
            return false;
        }
        var currentDate = new Date();

        return DateArithmetic.getDiffYears(dateOfBirthDate, currentDate) >= 13 &&
                DateArithmetic.getDiffYears(dateOfBirthDate, currentDate) <= 200;
    }
}

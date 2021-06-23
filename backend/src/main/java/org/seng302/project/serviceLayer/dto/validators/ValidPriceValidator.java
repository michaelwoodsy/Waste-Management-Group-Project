package org.seng302.project.serviceLayer.dto.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;


/**
 * Defines the validation method performed on
 * fields annotated with @ValidPrice in DTOs
 */
public class ValidPriceValidator implements ConstraintValidator<ValidPrice, Double> {
    @Override
    public boolean isValid(Double price, ConstraintValidatorContext context) {
        return price == null || price >= 0;
    }
}

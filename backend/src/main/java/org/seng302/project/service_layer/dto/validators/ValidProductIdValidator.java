package org.seng302.project.service_layer.dto.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Defines the validation method performed on
 * fields annotated with @ValidProductId in DTOs
 */
public class ValidProductIdValidator implements ConstraintValidator<ValidProductId, String> {

    @Override
    public boolean isValid(String productId, ConstraintValidatorContext context) {
        var productIdRegEx = "^[a-zA-Z0-9\\-^\\S]+$";
        return productId.matches(productIdRegEx);
    }
}

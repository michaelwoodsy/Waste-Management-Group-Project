package org.seng302.project.serviceLayer.dto.validators;


import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ValidBusinessTypeValidator implements ConstraintValidator<ValidBusinessType, String> {

    @Override
    public boolean isValid(String type, ConstraintValidatorContext context) {
        return type.equals("Accommodation and Food Services") ||
                type.equals("Retail Trade") ||
                type.equals("Charitable organisation") ||
                type.equals("Non-profit organisation") ||
                type.equals(""); // Type can be empty for searching,
                // use @NotEmpty annotation in DTO if required

    }
}

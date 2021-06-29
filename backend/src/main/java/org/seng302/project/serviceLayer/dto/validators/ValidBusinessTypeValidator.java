package org.seng302.project.serviceLayer.dto.validators;


import org.seng302.project.repositoryLayer.model.types.BusinessType;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ValidBusinessTypeValidator implements ConstraintValidator<ValidBusinessType, String> {

    @Override
    public boolean isValid(String type, ConstraintValidatorContext context) {
        return BusinessType.checkType(type);
    }
}

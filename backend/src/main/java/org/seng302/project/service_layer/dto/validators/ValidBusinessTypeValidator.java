package org.seng302.project.service_layer.dto.validators;


import org.seng302.project.repository_layer.model.enums.BusinessType;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ValidBusinessTypeValidator implements ConstraintValidator<ValidBusinessType, String> {

    @Override
    public boolean isValid(String type, ConstraintValidatorContext context) {
        return BusinessType.checkType(type);
    }
}

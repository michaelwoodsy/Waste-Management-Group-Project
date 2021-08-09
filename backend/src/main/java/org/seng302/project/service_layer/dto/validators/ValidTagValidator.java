package org.seng302.project.service_layer.dto.validators;

import org.seng302.project.repository_layer.model.enums.Tag;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ValidTagValidator implements ConstraintValidator<ValidTag, String> {
    @Override
    public boolean isValid(String tag, ConstraintValidatorContext context) {
        return Tag.checkTag(tag);
    }
}

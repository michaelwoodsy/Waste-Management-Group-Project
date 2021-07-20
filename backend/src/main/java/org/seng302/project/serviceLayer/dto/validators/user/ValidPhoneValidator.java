package org.seng302.project.serviceLayer.dto.validators.user;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Defines the validation method performed on
 * fields annotated with @ValidPhone in DTOs
 */
public class ValidPhoneValidator implements ConstraintValidator<ValidPhone, String> {

    @Override
    public boolean isValid(String phoneNumber, ConstraintValidatorContext context) {
        //Phone regex includes checking for nothing in the phone number
        var phoneRegEx = "^((\\+\\d{1,2}\\s*)?\\(?\\d{1,6}\\)?[\\s.-]?\\d{3,6}[\\s.-]?\\d{3,8})?$";
        return phoneNumber == null || phoneNumber.equals("") || phoneNumber.matches(phoneRegEx);
    }
}

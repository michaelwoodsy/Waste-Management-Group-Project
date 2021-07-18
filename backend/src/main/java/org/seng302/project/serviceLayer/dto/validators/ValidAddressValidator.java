package org.seng302.project.serviceLayer.dto.validators;

import org.seng302.project.repositoryLayer.model.Address;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Defines the validation method performed on
 * fields annotated with @ValidAddress in DTOs
 */
public class ValidAddressValidator implements ConstraintValidator<ValidAddress, Address> {
    @Override
    public boolean isValid(Address address, ConstraintValidatorContext context) {
        //If country is empty
        if (address.getCountry() == null || address.getCountry().equals("")) {
            return false;
        }
        if (address.getStreetNumber() != null && !address.getStreetNumber().equals("")) {
            return address.getStreetName() != null && !address.getStreetName().equals("");
        }
        return true;
    }
}

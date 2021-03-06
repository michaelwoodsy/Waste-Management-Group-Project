package org.seng302.project.service_layer.dto.validators;

import org.seng302.project.service_layer.dto.address.AddressDTO;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Defines the validation method performed on
 * fields annotated with @ValidAddress in DTOs
 */
public class ValidAddressValidator implements ConstraintValidator<ValidAddress, AddressDTO> {
    @Override
    public boolean isValid(AddressDTO address, ConstraintValidatorContext context) {
        if (address.getStreetNumber() != null && !address.getStreetNumber().equals("")) {
            return address.getStreetName() != null && !address.getStreetName().equals("");
        }

        return !(address.getCountry() == null || address.getCountry().equals(""));
    }
}

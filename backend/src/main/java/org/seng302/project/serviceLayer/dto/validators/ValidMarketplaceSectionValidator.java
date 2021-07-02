package org.seng302.project.serviceLayer.dto.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ValidMarketplaceSectionValidator implements ConstraintValidator<ValidMarketplaceSection, String> {

    @Override
    public boolean isValid(String section, ConstraintValidatorContext context) {
        return section.equals("ForSale") || section.equals("Wanted") || section.equals("Exchange");
    }

}

package org.seng302.project.service_layer.dto.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ValidMarketplaceSectionValidator implements ConstraintValidator<ValidMarketplaceSection, String> {

    @Override
    public boolean isValid(String section, ConstraintValidatorContext context) {
        if (section == null) {
            return false;
        }
        return section.equals("ForSale") || section.equals("Wanted") || section.equals("Exchange");
    }

}

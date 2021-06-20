package org.seng302.project.serviceLayer.dto.validators;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Defines the @ValidAddress annotation we can use for DTOs
 */
@Target({FIELD})
@Retention(RUNTIME)
@Constraint(validatedBy = ValidAddressValidator.class)
@Documented
public @interface ValidAddress {
    String message() default "Address format is incorrect. A country must be included." +
            " If a street number is given, a street name must be provided";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

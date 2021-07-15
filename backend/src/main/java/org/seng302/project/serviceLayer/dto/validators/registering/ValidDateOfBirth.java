package org.seng302.project.serviceLayer.dto.validators.registering;


import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Defines the @ValidDateOfBirth annotation we can use for DTOs
 */
@Target({FIELD})
@Retention(RUNTIME)
@Constraint(validatedBy = ValidDateOfBirthValidator.class)
@Documented
public @interface ValidDateOfBirth {
    String message() default "DateOfBirthInvalid: This Date of Birth is not valid.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

package org.seng302.project.service_layer.dto.validators;


import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Defines the @ValidProductId annotation we can use for DTOs
 */
@Target({FIELD})
@Retention(RUNTIME)
@Constraint(validatedBy = ValidProductIdValidator.class)
@Documented
public @interface ValidProductId {
    String message() default "This productId contains invalid characters. " +
            "Acceptable characters are letters, numbers and dashes.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

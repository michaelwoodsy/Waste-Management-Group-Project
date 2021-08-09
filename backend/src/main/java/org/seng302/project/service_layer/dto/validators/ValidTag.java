package org.seng302.project.service_layer.dto.validators;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Defines the @ValidTag annotation we can use for DTOs
 */
@Target({FIELD})
@Retention(RUNTIME)
@Constraint(validatedBy = ValidTagValidator.class)
@Documented
public @interface ValidTag {
    String message() default "Invalid tag provided.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

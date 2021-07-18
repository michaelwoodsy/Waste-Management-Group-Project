package org.seng302.project.serviceLayer.dto.validators.user;


import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Defines the @ValidEditedPassword annotation we can use for DTOs
 */
@Target({FIELD})
@Retention(RUNTIME)
@Constraint(validatedBy = ValidEditedPasswordValidator.class)
@Documented
public @interface ValidEditedPassword {
    String message() default "PasswordInvalid: This Password is not valid.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

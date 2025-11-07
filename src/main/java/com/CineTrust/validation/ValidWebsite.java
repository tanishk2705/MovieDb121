package com.CineTrust.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = WebsiteValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidWebsite {
    String message() default "Invalid website URL format";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

package com.CineTrust.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = RegionCodeValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidRegionCode {
    String message() default "Invalid region code. Must be ISO 2 or 3 uppercase letters (e.g., US, IN, GBR)";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

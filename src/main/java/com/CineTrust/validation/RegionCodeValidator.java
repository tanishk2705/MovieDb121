package com.CineTrust.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegionCodeValidator implements ConstraintValidator<ValidRegionCode, String> {

    private static final Logger log = LoggerFactory.getLogger(RegionCodeValidator.class);

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        log.info("Validating region code: '{}'", value);

        if (value == null || value.isBlank()) {
            return false;
        }

        // ISO alpha-2 or alpha-3: only uppercase letters, length 2 or 3
        boolean valid = value.matches("^[A-Z]{2,3}$");

        if (!valid) {
            log.warn("Invalid region code format: {}", value);
        }
        return valid;
    }
}

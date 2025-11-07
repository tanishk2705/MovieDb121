package com.CineTrust.validation;


import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;

public class WebsiteValidator implements ConstraintValidator<ValidWebsite, String> {
    private static final Logger log = LoggerFactory.getLogger(WebsiteValidator.class);
    @Override
    public boolean isValid(String website, ConstraintValidatorContext context){
        log.info("Validating website URL: '{}'", website);
        if (website == null || website.isBlank()) return true; //optional field
        try {
            URL url = new URL(website);
            boolean validProtocol = url.getProtocol().matches("https?");
            log.info("Parsed URL: {}, protocol valid: {}", url, validProtocol);
            return validProtocol;
        } catch (MalformedURLException e){
            log.warn("Website URL is malformed: '{}'", website);
            return false;
        }

    }
}

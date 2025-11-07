package com.CineTrust.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        /**
         * This tells Spring to ignore trailing slashes in URLs
         */
        configurer.setUseTrailingSlashMatch(true);
    }
}

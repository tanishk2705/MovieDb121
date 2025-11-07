package com.CineTrust.config;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityScheme.In;
import io.swagger.v3.oas.models.security.SecurityScheme.Type;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {

        return new OpenAPI()
                .info(new Info()
                        .title("Region & Platform Management API")
                        .version("1.0")
                        .description("""
                    API for managing movie distribution platforms and geographical regions.
                    
                    ## Features:
                    - Platform CRUD operations (Admin only)
                    - Region CRUD operations (Admin only)
                    - Movie availability linking (Admin only)
                    - Movie discovery by region and platform (Public)
                    
                    ## Authentication:
                    All admin endpoints require JWT token with ADMIN role.
                    Use the /login endpoint to obtain a token.
                    """))


                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))


                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("Bearer Authentication",
                                new SecurityScheme()
                                        .type(Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .in(In.HEADER)
                                        .name("Authorization")));
    }
}

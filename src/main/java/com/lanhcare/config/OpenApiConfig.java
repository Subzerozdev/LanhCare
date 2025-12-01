package com.lanhcare.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Swagger/OpenAPI Configuration
 * Configures API documentation with JWT authentication support
 */
@Configuration
public class OpenApiConfig {
    
    @Value("${app.api.version:1.0.0}")
    private String apiVersion;
    
    @Bean
    public OpenAPI customOpenAPI() {
        // Security scheme for JWT
        final String securitySchemeName = "bearerAuth";
        
        return new OpenAPI()
                .info(new Info()
                        .title("LanhCare API")
                        .version(apiVersion)
                        .description("Health Tracking and Wellness Management System API\n\n" +
                                "This API provides endpoints for managing user accounts, health profiles, " +
                                "meal tracking, and wellness features.\n\n" +
                                "**Authentication**: Most endpoints require JWT authentication. " +
                                "Use the `/api/auth/login` or `/api/auth/register` endpoint to get a token, " +
                                "then click the 'Authorize' button and enter: `Bearer <your-token>`")
                        .contact(new Contact()
                                .name("LanhCare Team")
                                .email("support@lanhcare.com")
                                .url("https://lanhcare.com"))
                        .license(new License()
                                .name("LanhCare © 2024")
                                .url("https://lanhcare.com/license")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Local Development Server"),
                        new Server()
                                .url("https://api.lanhcare.com")
                                .description("Production Server")
                ))
                // Add JWT security scheme
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Enter JWT token obtained from /api/auth/login or /api/auth/register")))
                // Apply security globally
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName));
    }
}

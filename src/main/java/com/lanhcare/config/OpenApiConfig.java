package com.lanhcare.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * Swagger/OpenAPI Configuration
 * Configures API documentation with JWT authentication support
 */
@Configuration
public class OpenApiConfig {
    
    @Value("${app.api.version:1.0.0}")
    private String apiVersion;
    
    @Value("${render.external.url:}")
    private String renderExternalUrl;
    
    @Bean
    public OpenAPI customOpenAPI() {

        
        // Build server list dynamically
        List<Server> servers = new ArrayList<>();
        
        // Add Render URL if available (from environment variable)
        if (renderExternalUrl != null && !renderExternalUrl.isEmpty()) {
            servers.add(new Server()
                    .url(renderExternalUrl)
                    .description("Render Deployment Server"));
        }
        
        // Add default servers
        servers.add(new Server()
                .url("/")
                .description("Current Server (Auto-detected)"));
        servers.add(new Server()
                .url("http://localhost:8080")
                .description("Local Development Server"));
        
        return new OpenAPI()
                .info(new Info()
                        .title("LanhCare API")
                        .version(apiVersion)
                        .description("Health Tracking and Wellness Management System API\n\n" +
                                "This API provides endpoints for managing user accounts, health profiles, " +
                                "meal tracking, and wellness features.\n\n" +
                                "**Authentication**: Most endpoints require JWT authentication. " +
                                "Use the `/api/auth/login` or `/api/auth/register` endpoint to get a token, " +
                                "then enter `Bearer <your-token>` in the Authorization field of each endpoint.")
                        .contact(new Contact()
                                .name("LanhCare Team")
                                .email("support@lanhcare.com")
                                .url("https://lanhcare.com"))
                        .license(new License()
                                .name("LanhCare © 2024")
                                .url("https://lanhcare.com/license")))
                .servers(servers);
    }
}

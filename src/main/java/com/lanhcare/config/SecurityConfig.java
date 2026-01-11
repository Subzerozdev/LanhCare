package com.lanhcare.config;

import com.lanhcare.security.CustomUserDetailsService;
import com.lanhcare.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Security Configuration
 * Configures Spring Security with JWT authentication, CORS, and authorization rules
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
public class SecurityConfig {
    
    private final CustomUserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    
    // Manual constructor instead of @RequiredArgsConstructor
    public SecurityConfig(CustomUserDetailsService userDetailsService, 
                         JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.userDetailsService = userDetailsService;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }
    
    /**
     * Password encoder - BCrypt
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    /**
     * Authentication provider
     * FIXED for Spring Boot 4.0: DaoAuthenticationProvider now requires UserDetailsService in constructor
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
    
    /**
     * Authentication manager
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
    
    /**
     * CORS Configuration for frontend and Swagger UI
     * Supports local development, Render deployment, and production environments
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Allow specific origins for development and production
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:3000",      // Next.js dev server
                "http://localhost:3001",      // Alternative dev port
                "http://localhost:8080",      // Local Spring Boot
                "http://127.0.0.1:3000",
                "http://127.0.0.1:8080",
                "https://lanhcare.onrender.com"  // Render deployment
        ));
        
        // Allow Render.com and other production domains using patterns
        // This allows Swagger UI on Render to work properly
        configuration.setAllowedOriginPatterns(Arrays.asList(
                "https://*.onrender.com",     // All Render.com subdomains
                "https://*.vercel.app",       // Vercel deployments
                "https://*.netlify.app",      // Netlify deployments
                "https://your-production-domain.com"  // Replace with your actual domain
        ));
        
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS", "HEAD"));
        configuration.setAllowedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "Accept",
                "Origin",
                "X-Requested-With",
                "Access-Control-Request-Method",
                "Access-Control-Request-Headers"
        ));
        configuration.setExposedHeaders(Arrays.asList(
                "Authorization",
                "Content-Disposition"
        ));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
    
    /**
     * HTTP Security filter chain
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF for stateless REST API
                .csrf(AbstractHttpConfigurer::disable)
                
                // Enable CORS
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                
                // Session management - stateless
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                
                // Authorization rules
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints - no authentication required
                        .requestMatchers(
                                "/api/auth/**",           // Authentication endpoints
                                "/api/public/**",         // Public API endpoints
                                "/test/**",               // Test API endpoints
                                "/actuator/health",       // Health check
                                "/actuator/info",         // Info endpoint
                                "/v3/api-docs/**",        // OpenAPI docs
                                "/swagger-ui/**",         // Swagger UI
                                "/swagger-ui.html"        // Swagger UI HTML
                        ).permitAll()
                        
                        // Admin-only endpoints
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        
                        // All other requests require authentication
                        .anyRequest().authenticated()
                )
                
                // Authentication provider
                .authenticationProvider(authenticationProvider())
                
                // Add JWT filter before UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
}

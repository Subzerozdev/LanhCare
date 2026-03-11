package com.lanhcare.config;

import com.lanhcare.security.CustomUserDetailsService;
import com.lanhcare.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
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
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

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

    /// Process for Google Id Token From App
    @Bean
    @Order(1)
    public SecurityFilterChain googleResourceServerFilterChain(HttpSecurity http) throws Exception {
        http.securityMatcher("/api/auth/google/android-callback")
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(m -> m.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        .anyRequest().permitAll()
                )
                // Kích hoạt Resource Server JWT để xử lý Google ID Token
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> {
                            // Cấu hình Google Issuer URI
                            jwt.decoder(jwtDecoder());
                        })
                )
                .cors(cors -> cors.configurationSource(corsConfigurationSource()));

        return http.build();
    }

    /// Separate JwtDecoder to config Google Issuer
    @Bean
    public JwtDecoder jwtDecoder() {
        String jwksUri = "https://www.googleapis.com/oauth2/v3/certs";
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withJwkSetUri(jwksUri).build();

        // 1. Tạo trình xác thực tiêu chuẩn (Issuer và Time)
        OAuth2TokenValidator<Jwt> defaultValidators = JwtValidators.createDefaultWithIssuer("https://accounts.google.com");

        // 2. Kết hợp các Validator
        jwtDecoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(defaultValidators));

        return jwtDecoder;
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
                "http://localhost:3000",  // Next.js dev server
                "http://localhost:3001",
                "https://your-production-domain.com",  // Replace with your production domain
                "http://10.0.2.2:8080",
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
    @Order(2)
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
                                "/",                       // Root endpoint
                                "/api/media/upload",
                                "/api/deletion-requests/**",
                                "/api/auth/**",           // Authentication endpoints
                                "/api/public/**",         // Public API endpoints
                                "/test/**",               // Test API endpoints
                                "/actuator/**",           // All actuator endpoints
                                "/v3/api-docs/**",        // OpenAPI docs
                                "/v3/api-docs",           // OpenAPI docs base
                                "/swagger-ui/**",         // Swagger UI
                                "/swagger-ui.html",       // Swagger UI HTML
                                "/swagger-resources/**",  // Swagger resources
                                "/webjars/**",            // Webjars for Swagger
                                "/favicon.ico",           // Favicon
                                "/error"                  // Error page
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

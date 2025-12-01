package com.lanhcare.controller;

import com.lanhcare.dto.auth.AuthResponse;
import com.lanhcare.dto.auth.GoogleLoginRequest;
import com.lanhcare.dto.auth.LoginRequest;
import com.lanhcare.dto.auth.RegisterRequest;
import com.lanhcare.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Authentication Controller
 * Handles user authentication: login, registration, and Google OAuth
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "User authentication endpoints including login, registration, and OAuth")
public class AuthController {
    
    private final AuthService authService;
    
    // Manual constructor instead of @RequiredArgsConstructor
    public AuthController(AuthService authService) {
        this.authService = authService;
    }
    
    /**
     * Register new user
     */
    @PostMapping("/register")
    @Operation(
            summary = "Register a new user",
            description = "Create a new user account with email and password. Password will be hashed before storing."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "409", description = "Email already exists")
    })
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    /**
     * Login with email and password
     */
    @PostMapping("/login")
    @Operation(
            summary = "Login with email and password",
            description = "Authenticate user with email and password. Returns JWT token on success."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login successful"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Login with Google OAuth2
     */
    @PostMapping("/google")
    @Operation(
            summary = "Login with Google OAuth2",
            description = "Authenticate user using Google ID token. Creates new account if user doesn't exist."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Google login successful"),
            @ApiResponse(responseCode = "401", description = "Invalid Google token")
    })
    public ResponseEntity<AuthResponse> loginWithGoogle(@RequestBody GoogleLoginRequest request) {
        AuthResponse response = authService.loginWithGoogle(request);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Health check for auth service
     */
    @GetMapping("/health")
    @Operation(
            summary = "Auth service health check",
            description = "Check if authentication service is running"
    )
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Authentication service is running");
    }
}

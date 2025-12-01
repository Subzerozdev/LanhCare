package com.lanhcare.service;

import com.lanhcare.dto.auth.AuthResponse;
import com.lanhcare.dto.auth.GoogleLoginRequest;
import com.lanhcare.dto.auth.LoginRequest;
import com.lanhcare.dto.auth.RegisterRequest;
import com.lanhcare.entity.Account;
import com.lanhcare.entity.AccountRole;
import com.lanhcare.entity.AccountStatus;
import com.lanhcare.exception.AuthenticationException;
import com.lanhcare.exception.ResourceAlreadyExistsException;
import com.lanhcare.repository.AccountRepository;
import com.lanhcare.security.JwtTokenProvider;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

/**
 * Authentication Service
 * Handles user login, registration, and Google OAuth authentication
 */
@Service
public class AuthService {
    
    private static final Logger log = LoggerFactory.getLogger(AuthService.class);
    
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    
    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;
    
    // Manual constructor instead of @RequiredArgsConstructor
    public AuthService(
            AccountRepository accountRepository,
            PasswordEncoder passwordEncoder,
            JwtTokenProvider jwtTokenProvider,
            AuthenticationManager authenticationManager) {
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.authenticationManager = authenticationManager;
    }
    
    /**
     * Register new user with email and password
     */
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        log.info("Registering new user with email: {}", request.getEmail());
        
        // Check if email already exists
        if (accountRepository.existsByEmail(request.getEmail())) {
            throw new ResourceAlreadyExistsException("Account", "email", request.getEmail());
        }
        
        // Create new account
        Account account = Account.builder()
                .email(request.getEmail())
                .fullname(request.getFullname())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(AccountRole.USER)
                .status(AccountStatus.ACTIVE)
                .build();
        
        Account savedAccount = accountRepository.save(account);
        log.info("User registered successfully: {}", savedAccount.getEmail());
        
        // Generate JWT token
        String token = jwtTokenProvider.generateToken(savedAccount.getEmail());
        
        return new AuthResponse(
                token,
                savedAccount.getId(),
                savedAccount.getEmail(),
                savedAccount.getFullname(),
                savedAccount.getRole().name()
        );
    }
    
    /**
     * Login with email and password
     */
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        log.info("Login attempt for email: {}", request.getEmail());
        
        try {
            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
            
            // Get account details
            Account account = accountRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new AuthenticationException("Invalid credentials"));
            
            // Generate JWT token
            String token = jwtTokenProvider.generateToken(authentication);
            
            log.info("User logged in successfully: {}", request.getEmail());
            
            return new AuthResponse(
                    token,
                    account.getId(),
                    account.getEmail(),
                    account.getFullname(),
                    account.getRole().name()
            );
            
        } catch (org.springframework.security.core.AuthenticationException ex) {
            log.error("Login failed for email: {}", request.getEmail());
            throw new AuthenticationException("Invalid email or password");
        }
    }
    
    /**
     * Login with Google OAuth2
     */
    @Transactional
    public AuthResponse loginWithGoogle(GoogleLoginRequest request) {
        log.info("Google OAuth login attempt");
        
        try {
            // Verify Google ID token
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(),
                    new GsonFactory())
                    .setAudience(Collections.singletonList(googleClientId))
                    .build();
            
            GoogleIdToken idToken = verifier.verify(request.getIdToken());
            
            if (idToken == null) {
                throw new AuthenticationException("Invalid Google ID token");
            }
            
            GoogleIdToken.Payload payload = idToken.getPayload();
            String email = payload.getEmail();
            String name = (String) payload.get("name");
            
            log.info("Google OAuth verified for email: {}", email);
            
            // Find or create account
            Account account = accountRepository.findByEmail(email)
                    .orElseGet(() -> {
                        log.info("Creating new account for Google user: {}", email);
                        Account newAccount = Account.builder()
                                .email(email)
                                .fullname(name)
                                .password(passwordEncoder.encode("GOOGLE_OAUTH_" + System.currentTimeMillis()))
                                .role(AccountRole.USER)
                                .status(AccountStatus.ACTIVE)
                                .build();
                        return accountRepository.save(newAccount);
                    });
            
            // Generate JWT token
            String token = jwtTokenProvider.generateToken(account.getEmail());
            
            log.info("Google OAuth login successful for: {}", email);
            
            return new AuthResponse(
                    token,
                    account.getId(),
                    account.getEmail(),
                    account.getFullname(),
                    account.getRole().name()
            );
            
        } catch (Exception ex) {
            log.error("Google OAuth login failed", ex);
            throw new AuthenticationException("Google authentication failed: " + ex.getMessage());
        }
    }
}

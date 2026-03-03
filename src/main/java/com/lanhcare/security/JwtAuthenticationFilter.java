package com.lanhcare.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * JWT Authentication Filter
 * Intercepts requests, extracts JWT token, validates it, and sets authentication in SecurityContext
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Value("${app.jwt.secret}")
    private String jwtKey;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException
    {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String jwt = authHeader.substring(7);

            try {
                SecretKey key = Keys.hmacShaKeyFor(jwtKey.getBytes());
                Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(jwt).getPayload();
                String identifier = claims.get("identifier", String.class);
                String role = claims.get("authorities", String.class);
                updateAuthentication(identifier, role);
                log.debug("JWT authenticated user: {}, role: {}, path: {}",
                        identifier, role, request.getRequestURI());

            } catch (ExpiredJwtException exception) {
                log.warn("Expired JWT token for path: {}", request.getRequestURI());
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "The token is expired!");
                return;
            } catch (MalformedJwtException e) {
                log.warn("Malformed JWT token for path: {}", request.getRequestURI());
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "The token is not valid!");
                return;
            } catch (Exception e) {
                log.warn("JWT validation failed for path: {} - {}", request.getRequestURI(), e.getMessage());
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token validation failed");
                return;
            }
        } else if (authHeader != null) {
            log.warn("Authorization header present but does not start with 'Bearer ': path={}", 
                    request.getRequestURI());
        }

        filterChain.doFilter(request, response);
    }

    private void updateAuthentication(String username, String role) {
        List<GrantedAuthority> authorities = AuthorityUtils.commaSeparatedStringToAuthorityList(role);
        Authentication authentication = new UsernamePasswordAuthenticationToken(username, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        AntPathMatcher matcher = new AntPathMatcher();
        return Arrays
                .stream(WHITELIST)
                .anyMatch(p -> matcher.match(p, request.getRequestURI()));
    }

    public final String[] WHITELIST = {
            "/api/auth/google/android-callback",
            "/api/payments/vnpay/callback"
    };
}

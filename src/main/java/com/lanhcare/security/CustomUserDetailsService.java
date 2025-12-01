package com.lanhcare.security;

import com.lanhcare.entity.Account;
import com.lanhcare.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;

/**
 * Custom UserDetailsService implementation
 * Loads user data from database for Spring Security authentication
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    
    private final AccountRepository accountRepository;
    
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        
        return User.builder()
                .username(account.getEmail())
                .password(account.getPassword())
                .authorities(getAuthorities(account))
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(account.getStatus() != com.lanhcare.entity.AccountStatus.ACTIVE)
                .build();
    }
    
    /**
     * Get user authorities based on role
     */
    private Collection<? extends GrantedAuthority> getAuthorities(Account account) {
        return Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + account.getRole().name())
        );
    }
    
    /**
     * Load user by ID (useful for some operations)
     */
    @Transactional(readOnly = true)
    public UserDetails loadUserById(Integer id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + id));
        
        return User.builder()
                .username(account.getEmail())
                .password(account.getPassword())
                .authorities(getAuthorities(account))
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(account.getStatus() != com.lanhcare.entity.AccountStatus.ACTIVE)
                .build();
    }
}

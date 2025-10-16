package com.urlshortener.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security configuration for URL Shortener
 * 
 * @author URL Shortener Team
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authz -> authz
                // Allow public access to web pages
                .requestMatchers("/", "/about", "/info/**").permitAll()
                // Allow public access to shorten URLs
                .requestMatchers("/shorten").permitAll()
                // Allow public access to redirect URLs (short codes)
                .requestMatchers("/*").permitAll()
                // Allow public access to health check
                .requestMatchers("/api/urls/health").permitAll()
                // Allow public access to API endpoints
                .requestMatchers("/api/urls/**").permitAll()
                // Allow public access to static resources
                .requestMatchers("/css/**", "/js/**", "/images/**", "/favicon.ico").permitAll()
                // Allow public access to actuator endpoints
                .requestMatchers("/actuator/**").permitAll()
                // All other requests need authentication
                .anyRequest().authenticated()
            )
            .csrf(csrf -> csrf.disable()) // Disable CSRF for API endpoints
            .headers(headers -> headers.frameOptions().disable()); // Allow iframe for embedding
        
        return http.build();
    }
}

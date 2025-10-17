package com.urlshortener.util;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

/**
 * Utility class for generating short codes
 * 
 * @author URL Shortener Team
 */
@Component
public class ShortCodeGenerator {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * Generate a random short code of specified length
     * 
     * @param length the length of the short code
     * @return generated short code
     */
    public String generate(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("Length must be positive");
        }
        
        StringBuilder shortCode = new StringBuilder(length);
        
        for (int i = 0; i < length; i++) {
            shortCode.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        
        return shortCode.toString();
    }

    /**
     * Generate a random short code with default length
     * 
     * @return generated short code
     */
    public String generate() {
        return generate(6); // Default length
    }

    
}

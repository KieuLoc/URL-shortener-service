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

    /**
     * Generate a short code from a number (base62 encoding)
     * 
     * @param number the number to encode
     * @return encoded short code
     */
    public String generateFromNumber(long number) {
        if (number < 0) {
            throw new IllegalArgumentException("Number must be non-negative");
        }
        
        if (number == 0) {
            return String.valueOf(CHARACTERS.charAt(0));
        }
        
        StringBuilder shortCode = new StringBuilder();
        int base = CHARACTERS.length();
        
        while (number > 0) {
            shortCode.append(CHARACTERS.charAt((int) (number % base)));
            number /= base;
        }
        
        return shortCode.reverse().toString();
    }

    /**
     * Decode a short code back to a number
     * 
     * @param shortCode the short code to decode
     * @return decoded number
     */
    public long decodeToNumber(String shortCode) {
        if (shortCode == null || shortCode.isEmpty()) {
            throw new IllegalArgumentException("Short code cannot be null or empty");
        }
        
        long number = 0;
        int base = CHARACTERS.length();
        
        for (char c : shortCode.toCharArray()) {
            int index = CHARACTERS.indexOf(c);
            if (index == -1) {
                throw new IllegalArgumentException("Invalid character in short code: " + c);
            }
            number = number * base + index;
        }
        
        return number;
    }

    /**
     * Validate if a short code contains only valid characters
     * 
     * @param shortCode the short code to validate
     * @return true if valid, false otherwise
     */
    public boolean isValidShortCode(String shortCode) {
        if (shortCode == null || shortCode.isEmpty()) {
            return false;
        }
        
        for (char c : shortCode.toCharArray()) {
            if (CHARACTERS.indexOf(c) == -1) {
                return false;
            }
        }
        
        return true;
    }
}

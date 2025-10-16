package com.urlshortener.service;

import com.urlshortener.dto.ShortenUrlRequest;
import com.urlshortener.dto.ShortenUrlResponse;
import com.urlshortener.util.ShortCodeGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/**
 * Redis-based URL Shortener Service
 * Uses Redis for persistent, scalable caching
 * 
 * @author URL Shortener Team
 */
@Service
public class RedisUrlShortenerService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ShortCodeGenerator shortCodeGenerator;
    
    private static final String URL_KEY_PREFIX = "url:";
    private static final String URL_DATA_PREFIX = "url_data:";

    @Autowired
    public RedisUrlShortenerService(RedisTemplate<String, Object> redisTemplate, 
                                   ShortCodeGenerator shortCodeGenerator) {
        this.redisTemplate = redisTemplate;
        this.shortCodeGenerator = shortCodeGenerator;
    }
    
    /**
     * Shorten URL and store in Redis
     */
    public ShortenUrlResponse shortenUrl(ShortenUrlRequest request) {
        String originalUrl = request.getUrl();
        
        // Validate URL
        if (!isValidUrl(originalUrl)) {
            throw new IllegalArgumentException("Invalid URL format");
        }
        
        // Generate short code
        String shortCode = shortCodeGenerator.generate();
        
        // Calculate expiration date
        LocalDateTime expirationDate = null;
        long ttlSeconds = -1; // No expiration by default
        if (request.getExpirationDays() != null) {
            expirationDate = LocalDateTime.now().plusDays(request.getExpirationDays());
            ttlSeconds = request.getExpirationDays() * 24 * 60 * 60; // Convert days to seconds
        }
        
        // Create URL data
        UrlData urlData = new UrlData(
            shortCode,
            originalUrl,
            LocalDateTime.now(),
            expirationDate,
            true
        );
        
        // Store in Redis
        String urlKey = URL_KEY_PREFIX + shortCode;
        String dataKey = URL_DATA_PREFIX + shortCode;
        
        redisTemplate.opsForValue().set(urlKey, originalUrl);
        redisTemplate.opsForValue().set(dataKey, urlData);
        
        // Set TTL if specified
        if (ttlSeconds > 0) {
            redisTemplate.expire(urlKey, ttlSeconds, TimeUnit.SECONDS);
            redisTemplate.expire(dataKey, ttlSeconds, TimeUnit.SECONDS);
        }
        
        // Create response
        ShortenUrlResponse response = new ShortenUrlResponse();
        response.setShortCode(shortCode);
        response.setShortUrl("http://localhost:8080/" + shortCode);
        response.setOriginalUrl(originalUrl);
        response.setCreatedAt(LocalDateTime.now());
        response.setExpiresAt(expirationDate);
        response.setActive(true);
        
        return response;
    }
    
    /**
     * Get original URL by short code from Redis
     */
    public String getOriginalUrl(String shortCode) {
        String urlKey = URL_KEY_PREFIX + shortCode;
        String originalUrl = (String) redisTemplate.opsForValue().get(urlKey);
        
        if (originalUrl != null) {
            // Check if URL data exists and is active
            String dataKey = URL_DATA_PREFIX + shortCode;
            UrlData urlData = (UrlData) redisTemplate.opsForValue().get(dataKey);
            
            if (urlData != null && urlData.isActive()) {
                // Check expiration if set
                if (urlData.getExpiresAt() == null || LocalDateTime.now().isBefore(urlData.getExpiresAt())) {
                    return originalUrl;
                } else {
                    // Expired, remove from cache
                    redisTemplate.delete(urlKey);
                    redisTemplate.delete(dataKey);
                }
            }
        }
        
        return null;
    }
    
    /**
     * Get URL data by short code
     */
    public UrlData getUrlData(String shortCode) {
        String dataKey = URL_DATA_PREFIX + shortCode;
        return (UrlData) redisTemplate.opsForValue().get(dataKey);
    }
    
    /**
     * Delete URL from Redis
     */
    public boolean deleteUrl(String shortCode) {
        String urlKey = URL_KEY_PREFIX + shortCode;
        String dataKey = URL_DATA_PREFIX + shortCode;
        
        Boolean urlDeleted = redisTemplate.delete(urlKey);
        Boolean dataDeleted = redisTemplate.delete(dataKey);
        
        return (urlDeleted != null && urlDeleted) || (dataDeleted != null && dataDeleted);
    }
    
    /**
     * Check if URL is valid
     */
    private boolean isValidUrl(String url) {
        return url != null && (url.startsWith("http://") || url.startsWith("https://"));
    }

    /**
     * Inner class to hold URL data in Redis
     * Must implement Serializable for Redis storage
     */
    public static class UrlData implements java.io.Serializable {
        private String shortCode;
        private String originalUrl;
        private LocalDateTime createdAt;
        private LocalDateTime expiresAt;
        private boolean isActive;

        public UrlData() {}

        public UrlData(String shortCode, String originalUrl, LocalDateTime createdAt, LocalDateTime expiresAt, boolean isActive) {
            this.shortCode = shortCode;
            this.originalUrl = originalUrl;
            this.createdAt = createdAt;
            this.expiresAt = expiresAt;
            this.isActive = isActive;
        }

        // Getters and Setters
        public String getShortCode() { return shortCode; }
        public void setShortCode(String shortCode) { this.shortCode = shortCode; }
        
        public String getOriginalUrl() { return originalUrl; }
        public void setOriginalUrl(String originalUrl) { this.originalUrl = originalUrl; }
        
        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
        
        public LocalDateTime getExpiresAt() { return expiresAt; }
        public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
        
        public boolean isActive() { return isActive; }
        public void setActive(boolean active) { isActive = active; }

        @Override
        public String toString() {
            return "UrlData{" +
                    "shortCode='" + shortCode + '\'' +
                    ", originalUrl='" + originalUrl + '\'' +
                    ", createdAt=" + createdAt +
                    ", expiresAt=" + expiresAt +
                    ", isActive=" + isActive +
                    '}';
        }
    }
}

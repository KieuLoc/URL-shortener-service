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
public class RedisUrlShortenerService implements UrlShortenerService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ShortCodeGenerator shortCodeGenerator;
    
    private static final String URL_KEY_PREFIX = "url:";

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
        
        // Set default expiration to 1 day
        int defaultExpirationDays = 1;
        LocalDateTime expirationDate = LocalDateTime.now().plusDays(defaultExpirationDays);
        long ttlSeconds = defaultExpirationDays * 24 * 60 * 60; // Convert days to seconds
        
        // Store in Redis - simplified approach
        String urlKey = URL_KEY_PREFIX + shortCode;
        
        redisTemplate.opsForValue().set(urlKey, originalUrl);
        
        // Set TTL if specified
        if (ttlSeconds > 0) {
            redisTemplate.expire(urlKey, ttlSeconds, TimeUnit.SECONDS);
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
    @Override
    public String getOriginalUrl(String shortCode) {
        String urlKey = URL_KEY_PREFIX + shortCode;
        String originalUrl = (String) redisTemplate.opsForValue().get(urlKey);
        
        // Simple approach - if key exists and has TTL, it's valid
        // Redis will automatically expire the key when TTL is reached
        return originalUrl;
    }
    
    
    
    /**
     * Check if URL is valid
     */
    private boolean isValidUrl(String url) {
        return url != null && (url.startsWith("http://") || url.startsWith("https://"));
    }

}

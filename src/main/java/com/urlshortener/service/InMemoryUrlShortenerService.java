package com.urlshortener.service;

import com.urlshortener.dto.ShortenUrlRequest;
import com.urlshortener.dto.ShortenUrlResponse;
import com.urlshortener.util.ShortCodeGenerator;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * In-memory URL Shortener Service for testing
 * Uses ConcurrentHashMap instead of database
 * 
 * @author URL Shortener Team
 */
@Service
public class InMemoryUrlShortenerService implements UrlShortenerService {

    private final ConcurrentMap<String, Object> urlCache = new ConcurrentHashMap<>();
    
    /**
     * Shorten URL and store in memory cache
     */
    public ShortenUrlResponse shortenUrl(ShortenUrlRequest request) {
        String originalUrl = request.getUrl();
        
        // Validate URL
        if (!isValidUrl(originalUrl)) {
            throw new IllegalArgumentException("Invalid URL format");
        }
        
        // Generate short code
        String shortCode = new ShortCodeGenerator().generate();
        
        // Store in cache
        urlCache.put(shortCode, originalUrl);
        
        ShortenUrlResponse response = new ShortenUrlResponse();
        response.setShortCode(shortCode);
        response.setShortUrl("http://localhost:8080/" + shortCode);
        response.setOriginalUrl(originalUrl);
        response.setCreatedAt(LocalDateTime.now());
        response.setActive(true);
        
        return response;
    }
    
    /**
     * Get original URL by short code
     */
    public String getOriginalUrl(String shortCode) {
        if (shortCode == null) {
            return null;
        }
        
        Object data = urlCache.get(shortCode);
        if (data instanceof String) return (String) data;
        return null;
    }
    
    /**
     * Validate URL format
     */
    private boolean isValidUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            return false;
        }
        
        return url.startsWith("http://") || url.startsWith("https://");
    }
    
}

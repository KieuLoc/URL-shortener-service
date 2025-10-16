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
public class InMemoryUrlShortenerService {

    private final ConcurrentMap<String, UrlData> urlCache = new ConcurrentHashMap<>();
    
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
        
        // Calculate expiration date
        LocalDateTime expirationDate = null;
        if (request.getExpirationDays() != null) {
            expirationDate = LocalDateTime.now().plusDays(request.getExpirationDays());
        }
        
        // Store in cache
        UrlData urlData = new UrlData(
            shortCode,
            originalUrl,
            LocalDateTime.now(),
            expirationDate,
            true
        );
        
        urlCache.put(shortCode, urlData);
        
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
     * Get original URL by short code
     */
    public String getOriginalUrl(String shortCode) {
        UrlData urlData = urlCache.get(shortCode);
        
        if (urlData == null) {
            throw new RuntimeException("Short URL not found");
        }
        
        if (!urlData.isActive()) {
            throw new RuntimeException("Short URL is inactive");
        }
        
        if (urlData.isExpired()) {
            throw new RuntimeException("Short URL has expired");
        }
        
        return urlData.getOriginalUrl();
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
    
    /**
     * Internal data class for URL storage
     */
    private static class UrlData {
        private final String shortCode;
        private final String originalUrl;
        private final LocalDateTime createdAt;
        private final LocalDateTime expirationDate;
        private final boolean active;
        
        public UrlData(String shortCode, String originalUrl, LocalDateTime createdAt, 
                      LocalDateTime expirationDate, boolean active) {
            this.shortCode = shortCode;
            this.originalUrl = originalUrl;
            this.createdAt = createdAt;
            this.expirationDate = expirationDate;
            this.active = active;
        }
        
        public String getShortCode() { return shortCode; }
        public String getOriginalUrl() { return originalUrl; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public LocalDateTime getExpirationDate() { return expirationDate; }
        public boolean isActive() { return active; }
        
        public boolean isExpired() {
            if (expirationDate == null) {
                return false; // No expiration
            }
            return LocalDateTime.now().isAfter(expirationDate);
        }
    }
}

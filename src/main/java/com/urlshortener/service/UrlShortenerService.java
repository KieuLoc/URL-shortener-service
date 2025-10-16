package com.urlshortener.service;

import com.urlshortener.dto.ShortenUrlRequest;
import com.urlshortener.dto.ShortenUrlResponse;

/**
 * Interface for URL Shortener Service
 * 
 * @author URL Shortener Team
 */
public interface UrlShortenerService {
    
    /**
     * Shorten a URL
     * 
     * @param request URL shortening request
     * @return ShortenUrlResponse with short URL details
     * @throws IllegalArgumentException if URL is invalid
     */
    ShortenUrlResponse shortenUrl(ShortenUrlRequest request);
    
    /**
     * Get original URL by short code
     * 
     * @param shortCode The short code
     * @return Original URL or null if not found
     */
    String getOriginalUrl(String shortCode);
    
    /**
     * Get URL data by short code
     * 
     * @param shortCode The short code to look up
     * @return The UrlData object if found and active, otherwise null
     */
    UrlData getUrlData(String shortCode);
    
    /**
     * Inner class to hold URL data in cache
     */
    class UrlData implements java.io.Serializable {
        private String shortCode;
        private String originalUrl;
        private java.time.LocalDateTime createdAt;
        private java.time.LocalDateTime expiresAt;
        private boolean isActive;

        public UrlData() {}

        public UrlData(String shortCode, String originalUrl, java.time.LocalDateTime createdAt, java.time.LocalDateTime expiresAt, boolean isActive) {
            this.shortCode = shortCode;
            this.originalUrl = originalUrl;
            this.createdAt = createdAt;
            this.expiresAt = expiresAt;
            this.isActive = isActive;
        }

        public String getShortCode() { return shortCode; }
        public String getOriginalUrl() { return originalUrl; }
        public java.time.LocalDateTime getCreatedAt() { return createdAt; }
        public java.time.LocalDateTime getExpiresAt() { return expiresAt; }
        public boolean isActive() { return isActive; }
    }
}

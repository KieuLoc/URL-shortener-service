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
        
        // Set default expiration to 365 days
        int defaultExpirationDays = 365;
        LocalDateTime expirationDate = LocalDateTime.now().plusDays(defaultExpirationDays);
        
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
        if (shortCode == null) {
            return null;
        }
        
        UrlData urlData = urlCache.get(shortCode);
        if (urlData != null && urlData.isActive() && (urlData.getExpiresAt() == null || LocalDateTime.now().isBefore(urlData.getExpiresAt()))) {
            return urlData.getOriginalUrl();
        }
        return null;
    }

    @Override
    public UrlData getUrlData(String shortCode) {
        if (shortCode == null) {
            return null;
        }
        
        UrlData urlData = urlCache.get(shortCode);
        if (urlData != null && urlData.isActive() && (urlData.getExpiresAt() == null || LocalDateTime.now().isBefore(urlData.getExpiresAt()))) {
            return urlData;
        }
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

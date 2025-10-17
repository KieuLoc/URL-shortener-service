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
    
    
    
    
}

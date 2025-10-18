package com.urlshortener.service;

import com.urlshortener.dto.ShortenUrlRequest;
import com.urlshortener.dto.ShortenUrlResponse;
import com.urlshortener.dto.UrlAnalytics;
import com.urlshortener.dto.AnalyticsSummary;

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
     * Track click for analytics
     * 
     * @param shortCode The short code that was clicked
     */
    void trackClick(String shortCode);
    
    /**
     * Get analytics for a specific URL
     * 
     * @param shortCode The short code
     * @return UrlAnalytics or null if not found
     */
    UrlAnalytics getUrlAnalytics(String shortCode);
    
    /**
     * Get overall analytics summary
     * 
     * @return AnalyticsSummary
     */
    AnalyticsSummary getAnalyticsSummary();
}

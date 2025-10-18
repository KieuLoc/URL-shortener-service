package com.urlshortener.service;

import com.urlshortener.dto.ShortenUrlRequest;
import com.urlshortener.dto.ShortenUrlResponse;
import com.urlshortener.dto.UrlAnalytics;
import com.urlshortener.dto.AnalyticsSummary;
import com.urlshortener.util.ShortCodeGenerator;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;

/**
 * In-memory URL Shortener Service for testing
 * Uses ConcurrentHashMap instead of database
 * 
 * @author URL Shortener Team
 */
@Service
public class InMemoryUrlShortenerService implements UrlShortenerService {

    private final ConcurrentMap<String, Object> urlCache = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, UrlAnalytics> analyticsCache = new ConcurrentHashMap<>();
    
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
        
        // Create analytics entry
        UrlAnalytics analytics = new UrlAnalytics();
        analytics.setShortCode(shortCode);
        analytics.setOriginalUrl(originalUrl);
        analytics.setShortUrl("http://localhost:8080/" + shortCode);
        analytics.setCreatedAt(LocalDateTime.now().toString());
        analytics.setClickCount(0);
        analytics.setLastAccessedAt(null);
        analytics.setActive(true);
        analyticsCache.put(shortCode, analytics);
        
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
     * Track click for analytics
     */
    @Override
    public void trackClick(String shortCode) {
        UrlAnalytics analytics = analyticsCache.get(shortCode);
        if (analytics != null) {
            analytics.setClickCount(analytics.getClickCount() + 1);
                analytics.setLastAccessedAt(LocalDateTime.now().toString());
        }
    }
    
    /**
     * Get analytics for a specific URL
     */
    @Override
    public UrlAnalytics getUrlAnalytics(String shortCode) {
        return analyticsCache.get(shortCode);
    }
    
    /**
     * Get overall analytics summary
     */
    @Override
    public AnalyticsSummary getAnalyticsSummary() {
        List<UrlAnalytics> allAnalytics = new ArrayList<>(analyticsCache.values());
        
        long totalUrls = allAnalytics.size();
        long totalClicks = allAnalytics.stream().mapToLong(UrlAnalytics::getClickCount).sum();
        
        String today = LocalDateTime.now().toLocalDate().atStartOfDay().toString();
        long todayUrls = allAnalytics.stream()
            .filter(a -> a.getCreatedAt() != null && a.getCreatedAt().startsWith(today.substring(0, 10)))
            .count();
        long todayClicks = allAnalytics.stream()
            .filter(a -> a.getLastAccessedAt() != null && a.getLastAccessedAt().startsWith(today.substring(0, 10)))
            .mapToLong(UrlAnalytics::getClickCount)
            .sum();
        
        // Top 5 URLs by click count
        List<UrlAnalytics> topUrls = allAnalytics.stream()
            .sorted(Comparator.comparingInt(UrlAnalytics::getClickCount).reversed())
            .limit(5)
            .collect(Collectors.toList());
        
        // Recent 5 URLs
        List<UrlAnalytics> recentUrls = allAnalytics.stream()
            .sorted(Comparator.comparing(UrlAnalytics::getCreatedAt).reversed())
            .limit(5)
            .collect(Collectors.toList());
        
        AnalyticsSummary summary = new AnalyticsSummary();
        summary.setTotalUrls(totalUrls);
        summary.setTotalClicks(totalClicks);
        summary.setTodayUrls(todayUrls);
        summary.setTodayClicks(todayClicks);
        summary.setTopUrls(topUrls);
        summary.setRecentUrls(recentUrls);
        summary.setLastUpdated(LocalDateTime.now().toString());
        
        return summary;
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

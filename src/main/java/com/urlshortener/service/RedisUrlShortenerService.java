package com.urlshortener.service;

import com.urlshortener.dto.ShortenUrlRequest;
import com.urlshortener.dto.ShortenUrlResponse;
import com.urlshortener.dto.UrlAnalytics;
import com.urlshortener.dto.AnalyticsSummary;
import com.urlshortener.util.ShortCodeGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;

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
    private static final String ANALYTICS_KEY_PREFIX = "analytics:";

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
        System.out.println("🔍 RedisUrlShortenerService.shortenUrl called");
        String originalUrl = request.getUrl();
        
        // Validate URL
        if (!isValidUrl(originalUrl)) {
            throw new IllegalArgumentException("Invalid URL format");
        }
        
        // Generate short code
        String shortCode = shortCodeGenerator.generate();
        System.out.println("🔍 Generated shortCode: " + shortCode);
        
        // Set default TTL to 1 day (Redis will handle expiration)
        int defaultExpirationDays = 1;
        long ttlSeconds = defaultExpirationDays * 24 * 60 * 60; // Convert days to seconds
        
        // Store in Redis - simplified approach
        String urlKey = URL_KEY_PREFIX + shortCode;
        String analyticsKey = ANALYTICS_KEY_PREFIX + shortCode;
        
        System.out.println("🔍 Storing URL in Redis with key: " + urlKey);
        redisTemplate.opsForValue().set(urlKey, originalUrl);
        
        // Create and store analytics
        System.out.println("🔍 Creating analytics object");
        UrlAnalytics analytics = new UrlAnalytics();
        analytics.setShortCode(shortCode);
        analytics.setOriginalUrl(originalUrl);
        analytics.setShortUrl("http://localhost:8080/" + shortCode);
        analytics.setCreatedAt(LocalDateTime.now().toString());
        analytics.setClickCount(0);
        analytics.setLastAccessedAt(null);
        analytics.setActive(true);
        
        System.out.println("🔍 Storing analytics in Redis with key: " + analyticsKey);
        redisTemplate.opsForValue().set(analyticsKey, analytics);
        
        // Set TTL if specified
        if (ttlSeconds > 0) {
            redisTemplate.expire(urlKey, ttlSeconds, TimeUnit.SECONDS);
            redisTemplate.expire(analyticsKey, ttlSeconds, TimeUnit.SECONDS);
        }
        
        // Create response
        System.out.println("🔍 Creating response");
        ShortenUrlResponse response = new ShortenUrlResponse();
        response.setShortCode(shortCode);
        response.setShortUrl("http://localhost:8080/" + shortCode);
        response.setOriginalUrl(originalUrl);
        response.setCreatedAt(LocalDateTime.now());
        response.setActive(true);
        
        System.out.println("✅ RedisUrlShortenerService.shortenUrl SUCCESS");
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
     * Track click for analytics
     */
    @Override
    public void trackClick(String shortCode) {
        String analyticsKey = ANALYTICS_KEY_PREFIX + shortCode;
        UrlAnalytics analytics = (UrlAnalytics) redisTemplate.opsForValue().get(analyticsKey);
        if (analytics != null) {
            analytics.setClickCount(analytics.getClickCount() + 1);
            analytics.setLastAccessedAt(LocalDateTime.now().toString());
            redisTemplate.opsForValue().set(analyticsKey, analytics);
        }
    }
    
    /**
     * Get analytics for a specific URL
     */
    @Override
    public UrlAnalytics getUrlAnalytics(String shortCode) {
        String analyticsKey = ANALYTICS_KEY_PREFIX + shortCode;
        return (UrlAnalytics) redisTemplate.opsForValue().get(analyticsKey);
    }
    
    /**
     * Get overall analytics summary
     */
    @Override
    public AnalyticsSummary getAnalyticsSummary() {
        // Get all analytics keys
        String pattern = ANALYTICS_KEY_PREFIX + "*";
        List<String> keys = new ArrayList<>(redisTemplate.keys(pattern));
        
        List<UrlAnalytics> allAnalytics = new ArrayList<>();
        for (String key : keys) {
            UrlAnalytics analytics = (UrlAnalytics) redisTemplate.opsForValue().get(key);
            if (analytics != null) {
                allAnalytics.add(analytics);
            }
        }
        
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
     * Check if URL is valid
     */
    private boolean isValidUrl(String url) {
        return url != null && (url.startsWith("http://") || url.startsWith("https://"));
    }

}

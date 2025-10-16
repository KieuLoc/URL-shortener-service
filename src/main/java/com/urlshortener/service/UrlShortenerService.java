package com.urlshortener.service;

import com.urlshortener.entity.Url;
import com.urlshortener.entity.UrlAnalytics;
import com.urlshortener.repository.UrlAnalyticsRepository;
import com.urlshortener.repository.UrlRepository;
import com.urlshortener.util.ShortCodeGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service class for URL shortening business logic
 * 
 * @author URL Shortener Team
 */
@Service
@Transactional
public class UrlShortenerService {

    private final UrlRepository urlRepository;
    private final UrlAnalyticsRepository analyticsRepository;
    private final ShortCodeGenerator shortCodeGenerator;
    
    @Value("${app.base-url}")
    private String baseUrl;
    
    @Value("${app.short-code-length:6}")
    private int shortCodeLength;
    
    @Value("${app.default-expiration-days:365}")
    private int defaultExpirationDays;

    public UrlShortenerService(UrlRepository urlRepository, 
                              UrlAnalyticsRepository analyticsRepository,
                              ShortCodeGenerator shortCodeGenerator) {
        this.urlRepository = urlRepository;
        this.analyticsRepository = analyticsRepository;
        this.shortCodeGenerator = shortCodeGenerator;
    }

    /**
     * Create a new short URL from a long URL
     * 
     * @param originalUrl the original long URL
     * @return the created URL entity
     */
    public Url createShortUrl(String originalUrl) {
        return createShortUrl(originalUrl, null);
    }

    /**
     * Create a new short URL from a long URL with custom expiration
     * 
     * @param originalUrl the original long URL
     * @param expirationDays number of days until expiration (null for default)
     * @return the created URL entity
     */
    public Url createShortUrl(String originalUrl, Integer expirationDays) {
        // Validate URL format
        validateUrl(originalUrl);
        
        // Generate unique short code
        String shortCode = generateUniqueShortCode();
        
        // Calculate expiration date
        LocalDateTime expirationDate = null;
        if (expirationDays != null && expirationDays > 0) {
            expirationDate = LocalDateTime.now().plusDays(expirationDays);
        } else if (defaultExpirationDays > 0) {
            expirationDate = LocalDateTime.now().plusDays(defaultExpirationDays);
        }
        
        // Create and save URL entity
        Url url = new Url(shortCode, originalUrl, expirationDate);
        return urlRepository.save(url);
    }

    /**
     * Get original URL by short code and record analytics
     * 
     * @param shortCode the short code to look up
     * @param ipAddress client IP address
     * @param userAgent client user agent
     * @param referer referer header
     * @return the original URL if found and accessible
     */
    @Transactional(readOnly = true)
    public Optional<String> getOriginalUrl(String shortCode, String ipAddress, String userAgent, String referer) {
        LocalDateTime now = LocalDateTime.now();
        Optional<Url> urlOpt = urlRepository.findByShortCodeAndAccessible(shortCode, now);
        
        if (urlOpt.isPresent()) {
            Url url = urlOpt.get();
            
            // Record analytics in a separate transaction
            recordClickAnalytics(url.getId(), ipAddress, userAgent, referer);
            
            return Optional.of(url.getOriginalUrl());
        }
        
        return Optional.empty();
    }

    /**
     * Get URL details by short code
     * 
     * @param shortCode the short code to look up
     * @return the URL entity if found
     */
    @Transactional(readOnly = true)
    public Optional<Url> getUrlByShortCode(String shortCode) {
        return urlRepository.findByShortCode(shortCode);
    }

    /**
     * Check if a short code exists
     * 
     * @param shortCode the short code to check
     * @return true if exists, false otherwise
     */
    @Transactional(readOnly = true)
    public boolean shortCodeExists(String shortCode) {
        return urlRepository.existsByShortCode(shortCode);
    }

    /**
     * Deactivate a URL
     * 
     * @param shortCode the short code to deactivate
     * @return true if deactivated successfully, false if not found
     */
    public boolean deactivateUrl(String shortCode) {
        Optional<Url> urlOpt = urlRepository.findByShortCode(shortCode);
        if (urlOpt.isPresent()) {
            Url url = urlOpt.get();
            url.setIsActive(false);
            urlRepository.save(url);
            return true;
        }
        return false;
    }

    /**
     * Get click count for a URL
     * 
     * @param urlId the URL ID
     * @return total click count
     */
    @Transactional(readOnly = true)
    public long getClickCount(Long urlId) {
        return analyticsRepository.countByUrlId(urlId);
    }

    /**
     * Get recent analytics for a URL
     * 
     * @param urlId the URL ID
     * @param hours number of hours to look back
     * @return list of recent analytics records
     */
    @Transactional(readOnly = true)
    public List<UrlAnalytics> getRecentAnalytics(Long urlId, int hours) {
        LocalDateTime since = LocalDateTime.now().minusHours(hours);
        return analyticsRepository.findRecentAnalytics(urlId, since);
    }

    /**
     * Clean up expired URLs
     * 
     * @return number of URLs cleaned up
     */
    @Transactional
    public int cleanupExpiredUrls() {
        List<Url> expiredUrls = urlRepository.findExpiredUrls(LocalDateTime.now());
        int count = expiredUrls.size();
        
        // Deactivate expired URLs instead of deleting to preserve analytics
        expiredUrls.forEach(url -> url.setIsActive(false));
        urlRepository.saveAll(expiredUrls);
        
        return count;
    }

    /**
     * Generate a unique short code
     */
    private String generateUniqueShortCode() {
        String shortCode;
        int attempts = 0;
        int maxAttempts = 10;
        
        do {
            shortCode = shortCodeGenerator.generate(shortCodeLength);
            attempts++;
            
            if (attempts > maxAttempts) {
                throw new RuntimeException("Unable to generate unique short code after " + maxAttempts + " attempts");
            }
        } while (urlRepository.existsByShortCode(shortCode));
        
        return shortCode;
    }

    /**
     * Record click analytics
     */
    private void recordClickAnalytics(Long urlId, String ipAddress, String userAgent, String referer) {
        try {
            UrlAnalytics analytics = new UrlAnalytics(urlId, ipAddress, userAgent);
            if (referer != null && !referer.isEmpty()) {
                analytics.setReferer(referer);
            }
            analyticsRepository.save(analytics);
        } catch (Exception e) {
            // Log error but don't fail the redirect
            // Analytics failure shouldn't break the main functionality
        }
    }

    /**
     * Validate URL format
     */
    private void validateUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            throw new IllegalArgumentException("URL cannot be null or empty");
        }
        
        // Basic URL validation
        if (!url.toLowerCase().startsWith("http://") && !url.toLowerCase().startsWith("https://")) {
            throw new IllegalArgumentException("URL must start with http:// or https://");
        }
        
        // Check URL length
        if (url.length() > 2048) {
            throw new IllegalArgumentException("URL cannot exceed 2048 characters");
        }
    }
}

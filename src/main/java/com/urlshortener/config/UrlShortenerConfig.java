package com.urlshortener.config;

import com.urlshortener.service.InMemoryUrlShortenerService;
import com.urlshortener.service.RedisUrlShortenerService;
import com.urlshortener.service.UrlShortenerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Configuration for URL Shortener Service selection
 * 
 * @author URL Shortener Team
 */
@Configuration
public class UrlShortenerConfig {

    @Autowired(required = false)
    private RedisUrlShortenerService redisUrlShortenerService;

    @Autowired
    private InMemoryUrlShortenerService inMemoryUrlShortenerService;

    /**
     * Primary URL Shortener Service
     * Uses Redis if available, otherwise falls back to In-Memory
     */
    @Bean
    @Primary
    public UrlShortenerService urlShortenerService() {
        // Try Redis first, fallback to In-Memory
        if (redisUrlShortenerService != null) {
            try {
                // Test Redis connection
                redisUrlShortenerService.getOriginalUrl("test");
                System.out.println("✅ Using RedisUrlShortenerService");
                return redisUrlShortenerService;
            } catch (Exception e) {
                // Redis not available, use In-Memory
                System.out.println("❌ Redis failed, using InMemoryUrlShortenerService: " + e.getMessage());
                e.printStackTrace();
                return inMemoryUrlShortenerService;
            }
        }
        return inMemoryUrlShortenerService;
    }
}

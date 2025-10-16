package com.urlshortener.dto;

import com.urlshortener.entity.Url;
import java.time.LocalDateTime;

/**
 * DTO for URL shortening response
 * 
 * @author URL Shortener Team
 */
public class ShortenUrlResponse {

    private String shortCode;
    private String shortUrl;
    private String originalUrl;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private boolean isActive;

    // Constructors
    public ShortenUrlResponse() {}

    public ShortenUrlResponse(Url url, String baseUrl) {
        this.shortCode = url.getShortCode();
        this.shortUrl = baseUrl + "/" + url.getShortCode();
        this.originalUrl = url.getOriginalUrl();
        this.createdAt = url.getCreatedAt();
        this.expiresAt = url.getExpiresAt();
        this.isActive = url.getIsActive();
    }

    // Getters and Setters
    public String getShortCode() {
        return shortCode;
    }

    public void setShortCode(String shortCode) {
        this.shortCode = shortCode;
    }

    public String getShortUrl() {
        return shortUrl;
    }

    public void setShortUrl(String shortUrl) {
        this.shortUrl = shortUrl;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    @Override
    public String toString() {
        return "ShortenUrlResponse{" +
                "shortCode='" + shortCode + '\'' +
                ", shortUrl='" + shortUrl + '\'' +
                ", originalUrl='" + originalUrl + '\'' +
                ", createdAt=" + createdAt +
                ", expiresAt=" + expiresAt +
                ", isActive=" + isActive +
                '}';
    }
}

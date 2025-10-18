package com.urlshortener.dto;

import java.io.Serializable;

/**
 * DTO for URL Analytics
 * 
 * @author URL Shortener Team
 */
public class UrlAnalytics implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String shortCode;
    private String originalUrl;
    private String shortUrl;
    private String createdAt;
    private int clickCount;
    private String lastAccessedAt;
    private boolean isActive;
    
    // Constructors
    public UrlAnalytics() {}
    
    public UrlAnalytics(String shortCode, String originalUrl, String shortUrl, 
                       String createdAt, int clickCount, 
                       String lastAccessedAt, boolean isActive) {
        this.shortCode = shortCode;
        this.originalUrl = originalUrl;
        this.shortUrl = shortUrl;
        this.createdAt = createdAt;
        this.clickCount = clickCount;
        this.lastAccessedAt = lastAccessedAt;
        this.isActive = isActive;
    }
    
    // Getters and Setters
    public String getShortCode() {
        return shortCode;
    }
    
    public void setShortCode(String shortCode) {
        this.shortCode = shortCode;
    }
    
    public String getOriginalUrl() {
        return originalUrl;
    }
    
    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }
    
    public String getShortUrl() {
        return shortUrl;
    }
    
    public void setShortUrl(String shortUrl) {
        this.shortUrl = shortUrl;
    }
    
    public String getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
    
    public int getClickCount() {
        return clickCount;
    }
    
    public void setClickCount(int clickCount) {
        this.clickCount = clickCount;
    }
    
    public String getLastAccessedAt() {
        return lastAccessedAt;
    }
    
    public void setLastAccessedAt(String lastAccessedAt) {
        this.lastAccessedAt = lastAccessedAt;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public void setActive(boolean active) {
        isActive = active;
    }
    
    @Override
    public String toString() {
        return "UrlAnalytics{" +
                "shortCode='" + shortCode + '\'' +
                ", originalUrl='" + originalUrl + '\'' +
                ", shortUrl='" + shortUrl + '\'' +
                ", createdAt=" + createdAt +
                ", clickCount=" + clickCount +
                ", lastAccessedAt=" + lastAccessedAt +
                ", isActive=" + isActive +
                '}';
    }
}

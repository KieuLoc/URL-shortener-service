package com.urlshortener.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * DTO for URL shortening request
 * 
 * @author URL Shortener Team
 */
public class ShortenUrlRequest {

    @NotBlank(message = "URL is required")
    @Size(max = 2048, message = "URL cannot exceed 2048 characters")
    private String url;

    @Positive(message = "Expiration days must be positive")
    private Integer expirationDays;

    // Constructors
    public ShortenUrlRequest() {}

    public ShortenUrlRequest(String url) {
        this.url = url;
    }

    public ShortenUrlRequest(String url, Integer expirationDays) {
        this.url = url;
        this.expirationDays = expirationDays;
    }

    // Getters and Setters
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getExpirationDays() {
        return expirationDays;
    }

    public void setExpirationDays(Integer expirationDays) {
        this.expirationDays = expirationDays;
    }

    @Override
    public String toString() {
        return "ShortenUrlRequest{" +
                "url='" + url + '\'' +
                ", expirationDays=" + expirationDays +
                '}';
    }
}

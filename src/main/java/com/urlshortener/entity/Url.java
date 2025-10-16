package com.urlshortener.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Entity representing a shortened URL
 * 
 * @author URL Shortener Team
 */
@Entity
@Table(name = "urls", indexes = {
    @Index(name = "idx_short_code", columnList = "shortCode"),
    @Index(name = "idx_created_at", columnList = "createdAt"),
    @Index(name = "idx_is_active", columnList = "isActive")
})
public class Url {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "short_code", unique = true, nullable = false, length = 10)
    @NotBlank(message = "Short code cannot be blank")
    @Size(max = 10, message = "Short code cannot exceed 10 characters")
    private String shortCode;

    @Column(name = "original_url", nullable = false, columnDefinition = "TEXT")
    @NotBlank(message = "Original URL cannot be blank")
    @Size(max = 2048, message = "Original URL cannot exceed 2048 characters")
    private String originalUrl;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructors
    public Url() {}

    public Url(String shortCode, String originalUrl) {
        this.shortCode = shortCode;
        this.originalUrl = originalUrl;
    }

    public Url(String shortCode, String originalUrl, LocalDateTime expiresAt) {
        this.shortCode = shortCode;
        this.originalUrl = originalUrl;
        this.expiresAt = expiresAt;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * Check if the URL has expired
     */
    public boolean isExpired() {
        return expiresAt != null && LocalDateTime.now().isAfter(expiresAt);
    }

    /**
     * Check if the URL is accessible (active and not expired)
     */
    public boolean isAccessible() {
        return isActive && !isExpired();
    }

    @Override
    public String toString() {
        return "Url{" +
                "id=" + id +
                ", shortCode='" + shortCode + '\'' +
                ", originalUrl='" + originalUrl + '\'' +
                ", createdAt=" + createdAt +
                ", expiresAt=" + expiresAt +
                ", isActive=" + isActive +
                ", updatedAt=" + updatedAt +
                '}';
    }
}

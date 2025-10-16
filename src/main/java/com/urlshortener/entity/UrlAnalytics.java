package com.urlshortener.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Entity representing analytics data for URL clicks
 * 
 * @author URL Shortener Team
 */
@Entity
@Table(name = "url_analytics", indexes = {
    @Index(name = "idx_url_id", columnList = "urlId"),
    @Index(name = "idx_clicked_at", columnList = "clickedAt"),
    @Index(name = "idx_ip_address", columnList = "ipAddress")
})
public class UrlAnalytics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "url_id", nullable = false)
    @NotNull(message = "URL ID cannot be null")
    private Long urlId;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;

    @CreationTimestamp
    @Column(name = "clicked_at", nullable = false, updatable = false)
    private LocalDateTime clickedAt;

    @Column(name = "country", length = 100)
    private String country;

    @Column(name = "referer", columnDefinition = "TEXT")
    private String referer;

    // Constructors
    public UrlAnalytics() {}

    public UrlAnalytics(Long urlId, String ipAddress, String userAgent) {
        this.urlId = urlId;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
    }

    public UrlAnalytics(Long urlId, String ipAddress, String userAgent, String country, String referer) {
        this.urlId = urlId;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.country = country;
        this.referer = referer;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUrlId() {
        return urlId;
    }

    public void setUrlId(Long urlId) {
        this.urlId = urlId;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public LocalDateTime getClickedAt() {
        return clickedAt;
    }

    public void setClickedAt(LocalDateTime clickedAt) {
        this.clickedAt = clickedAt;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getReferer() {
        return referer;
    }

    public void setReferer(String referer) {
        this.referer = referer;
    }

    @Override
    public String toString() {
        return "UrlAnalytics{" +
                "id=" + id +
                ", urlId=" + urlId +
                ", ipAddress='" + ipAddress + '\'' +
                ", userAgent='" + userAgent + '\'' +
                ", clickedAt=" + clickedAt +
                ", country='" + country + '\'' +
                ", referer='" + referer + '\'' +
                '}';
    }
}

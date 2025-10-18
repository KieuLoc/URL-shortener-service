package com.urlshortener.dto;

import java.util.List;

/**
 * DTO for Analytics Summary
 * 
 * @author URL Shortener Team
 */
public class AnalyticsSummary {
    
    private long totalUrls;
    private long totalClicks;
    private long todayUrls;
    private long todayClicks;
    private List<UrlAnalytics> topUrls;
    private List<UrlAnalytics> recentUrls;
    private String lastUpdated;
    
    // Constructors
    public AnalyticsSummary() {}
    
    public AnalyticsSummary(long totalUrls, long totalClicks, long todayUrls, 
                           long todayClicks, List<UrlAnalytics> topUrls, 
                           List<UrlAnalytics> recentUrls, String lastUpdated) {
        this.totalUrls = totalUrls;
        this.totalClicks = totalClicks;
        this.todayUrls = todayUrls;
        this.todayClicks = todayClicks;
        this.topUrls = topUrls;
        this.recentUrls = recentUrls;
        this.lastUpdated = lastUpdated;
    }
    
    // Getters and Setters
    public long getTotalUrls() {
        return totalUrls;
    }
    
    public void setTotalUrls(long totalUrls) {
        this.totalUrls = totalUrls;
    }
    
    public long getTotalClicks() {
        return totalClicks;
    }
    
    public void setTotalClicks(long totalClicks) {
        this.totalClicks = totalClicks;
    }
    
    public long getTodayUrls() {
        return todayUrls;
    }
    
    public void setTodayUrls(long todayUrls) {
        this.todayUrls = todayUrls;
    }
    
    public long getTodayClicks() {
        return todayClicks;
    }
    
    public void setTodayClicks(long todayClicks) {
        this.todayClicks = todayClicks;
    }
    
    public List<UrlAnalytics> getTopUrls() {
        return topUrls;
    }
    
    public void setTopUrls(List<UrlAnalytics> topUrls) {
        this.topUrls = topUrls;
    }
    
    public List<UrlAnalytics> getRecentUrls() {
        return recentUrls;
    }
    
    public void setRecentUrls(List<UrlAnalytics> recentUrls) {
        this.recentUrls = recentUrls;
    }
    
    public String getLastUpdated() {
        return lastUpdated;
    }
    
    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
    
    @Override
    public String toString() {
        return "AnalyticsSummary{" +
                "totalUrls=" + totalUrls +
                ", totalClicks=" + totalClicks +
                ", todayUrls=" + todayUrls +
                ", todayClicks=" + todayClicks +
                ", topUrls=" + topUrls +
                ", recentUrls=" + recentUrls +
                ", lastUpdated=" + lastUpdated +
                '}';
    }
}

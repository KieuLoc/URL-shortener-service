package com.urlshortener.controller;

import com.urlshortener.dto.AnalyticsSummary;
import com.urlshortener.dto.UrlAnalytics;
import com.urlshortener.service.UrlShortenerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Controller for Analytics functionality
 * 
 * @author URL Shortener Team
 */
@Controller
public class AnalyticsController {

    @Autowired
    private UrlShortenerService urlShortenerService;

    /**
     * Analytics Dashboard
     */
    @GetMapping("/analytics")
    public String analyticsDashboard(Model model) {
        AnalyticsSummary summary = urlShortenerService.getAnalyticsSummary();
        model.addAttribute("analytics", summary);
        return "analytics";
    }

    /**
     * Get analytics summary as JSON
     */
    @GetMapping("/api/analytics/summary")
    @ResponseBody
    public ResponseEntity<AnalyticsSummary> getAnalyticsSummary() {
        AnalyticsSummary summary = urlShortenerService.getAnalyticsSummary();
        return ResponseEntity.ok(summary);
    }

    /**
     * Get analytics for specific URL
     */
    @GetMapping("/api/analytics/{shortCode}")
    @ResponseBody
    public ResponseEntity<UrlAnalytics> getUrlAnalytics(@PathVariable String shortCode) {
        UrlAnalytics analytics = urlShortenerService.getUrlAnalytics(shortCode);
        if (analytics == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(analytics);
    }

    /**
     * Track click for analytics
     */
    @GetMapping("/api/analytics/track/{shortCode}")
    @ResponseBody
    public ResponseEntity<String> trackClick(@PathVariable String shortCode) {
        urlShortenerService.trackClick(shortCode);
        return ResponseEntity.ok("Tracked");
    }
}

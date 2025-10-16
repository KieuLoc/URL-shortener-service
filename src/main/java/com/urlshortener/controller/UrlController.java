package com.urlshortener.controller;

import com.urlshortener.dto.ApiResponse;
import com.urlshortener.dto.ShortenUrlRequest;
import com.urlshortener.dto.ShortenUrlResponse;
import com.urlshortener.entity.Url;
import com.urlshortener.service.UrlShortenerService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * REST Controller for URL shortening operations
 * 
 * @author URL Shortener Team
 */
@RestController
@RequestMapping("/api/urls")
@CrossOrigin(origins = "*")
public class UrlController {

    private final UrlShortenerService urlShortenerService;
    
    @Value("${app.base-url}")
    private String baseUrl;

    public UrlController(UrlShortenerService urlShortenerService) {
        this.urlShortenerService = urlShortenerService;
    }

    /**
     * Create a new short URL
     * 
     * @param request the URL shortening request
     * @return the shortened URL response
     */
    @PostMapping("/shorten")
    public ResponseEntity<ApiResponse<ShortenUrlResponse>> shortenUrl(@Valid @RequestBody ShortenUrlRequest request) {
        try {
            Url url = urlShortenerService.createShortUrl(request.getUrl(), request.getExpirationDays());
            ShortenUrlResponse response = new ShortenUrlResponse(url, baseUrl);
            
            return ResponseEntity.ok(ApiResponse.success("URL shortened successfully", response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Invalid URL: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to shorten URL", e.getMessage()));
        }
    }

    /**
     * Get URL information by short code
     * 
     * @param shortCode the short code
     * @return the URL information
     */
    @GetMapping("/{shortCode}")
    public ResponseEntity<ApiResponse<ShortenUrlResponse>> getUrlInfo(@PathVariable String shortCode) {
        try {
            Optional<Url> urlOpt = urlShortenerService.getUrlByShortCode(shortCode);
            
            if (urlOpt.isPresent()) {
                Url url = urlOpt.get();
                ShortenUrlResponse response = new ShortenUrlResponse(url, baseUrl);
                return ResponseEntity.ok(ApiResponse.success("URL found", response));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve URL information", e.getMessage()));
        }
    }

    /**
     * Check if a short code exists
     * 
     * @param shortCode the short code to check
     * @return existence status
     */
    @GetMapping("/{shortCode}/exists")
    public ResponseEntity<ApiResponse<Boolean>> checkShortCodeExists(@PathVariable String shortCode) {
        try {
            boolean exists = urlShortenerService.shortCodeExists(shortCode);
            return ResponseEntity.ok(ApiResponse.success("Check completed", exists));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to check short code", e.getMessage()));
        }
    }

    /**
     * Deactivate a URL
     * 
     * @param shortCode the short code to deactivate
     * @return deactivation result
     */
    @DeleteMapping("/{shortCode}")
    public ResponseEntity<ApiResponse<Boolean>> deactivateUrl(@PathVariable String shortCode) {
        try {
            boolean deactivated = urlShortenerService.deactivateUrl(shortCode);
            
            if (deactivated) {
                return ResponseEntity.ok(ApiResponse.success("URL deactivated successfully", true));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to deactivate URL", e.getMessage()));
        }
    }

    /**
     * Get click count for a URL
     * 
     * @param shortCode the short code
     * @return click count
     */
    @GetMapping("/{shortCode}/stats/clicks")
    public ResponseEntity<ApiResponse<Long>> getClickCount(@PathVariable String shortCode) {
        try {
            Optional<Url> urlOpt = urlShortenerService.getUrlByShortCode(shortCode);
            
            if (urlOpt.isPresent()) {
                long clickCount = urlShortenerService.getClickCount(urlOpt.get().getId());
                return ResponseEntity.ok(ApiResponse.success("Click count retrieved", clickCount));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve click count", e.getMessage()));
        }
    }

    /**
     * Health check endpoint
     * 
     * @return health status
     */
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> health() {
        return ResponseEntity.ok(ApiResponse.success("URL Shortener Service is running", "OK"));
    }
}

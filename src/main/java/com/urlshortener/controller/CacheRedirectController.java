package com.urlshortener.controller;

import com.urlshortener.service.InMemoryUrlShortenerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for handling URL redirections using cache
 * 
 * @author URL Shortener Team
 */
@RestController
public class CacheRedirectController {

    @Autowired
    private InMemoryUrlShortenerService urlShortenerService;

    /**
     * Redirect short URL to original URL
     * Returns HTTP 302 (Temporary Redirect) with Location header
     * 
     * @param shortCode The short code from the URL
     * @return Redirect response to original URL
     */
    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirectToOriginalUrl(@PathVariable String shortCode) {
        
        try {
            // Get original URL from cache
            String originalUrl = urlShortenerService.getOriginalUrl(shortCode);
            
            // Return HTTP 302 redirect with Location header
            return ResponseEntity.status(HttpStatus.FOUND)
                    .header("Location", originalUrl)
                    .build();
                    
        } catch (RuntimeException e) {
            // Return 404 if URL not found or expired
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}

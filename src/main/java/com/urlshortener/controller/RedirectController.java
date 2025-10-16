package com.urlshortener.controller;

import com.urlshortener.service.UrlShortenerService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * Controller for handling URL redirections
 * 
 * @author URL Shortener Team
 */
//@RestController
@RequestMapping("/")
public class RedirectController {

    private final UrlShortenerService urlShortenerService;
    
    @Value("${app.base-url}")
    private String baseUrl;

    public RedirectController(UrlShortenerService urlShortenerService) {
        this.urlShortenerService = urlShortenerService;
    }

    /**
     * Redirect to original URL by short code
     * 
     * @param shortCode the short code
     * @param request HTTP request for analytics
     * @return redirect response or error
     */
    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirect(@PathVariable String shortCode, HttpServletRequest request) {
        try {
            // Extract client information for analytics
            String ipAddress = getClientIpAddress(request);
            String userAgent = request.getHeader("User-Agent");
            String referer = request.getHeader("Referer");

            // Get original URL and record analytics
            Optional<String> originalUrlOpt = urlShortenerService.getOriginalUrl(shortCode, ipAddress, userAgent, referer);

            if (originalUrlOpt.isPresent()) {
                // Return 302 redirect to original URL
                return ResponseEntity.status(HttpStatus.FOUND)
                        .header("Location", originalUrlOpt.get())
                        .build();
            } else {
                // URL not found or expired
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            // Log error and return 500
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Extract client IP address from request
     * 
     * @param request HTTP request
     * @return client IP address
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }

        return request.getRemoteAddr();
    }
}

package com.urlshortener.controller;

import com.urlshortener.dto.ShortenUrlRequest;
import com.urlshortener.dto.ShortenUrlResponse;
import com.urlshortener.service.InMemoryUrlShortenerService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Web Controller for Thymeleaf templates using cache
 * 
 * @author URL Shortener Team
 */
@Controller
public class CacheWebController {

    private final InMemoryUrlShortenerService urlShortenerService;
    
    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    public CacheWebController(InMemoryUrlShortenerService urlShortenerService) {
        this.urlShortenerService = urlShortenerService;
    }

    /**
     * Display the main page with URL shortening form
     */
    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("shortenRequest", new ShortenUrlRequest());
        model.addAttribute("baseUrl", baseUrl);
        return "index";
    }

    /**
     * Handle URL shortening form submission
     */
    @PostMapping("/shorten")
    public String shortenUrl(@ModelAttribute ShortenUrlRequest request, 
                           RedirectAttributes redirectAttributes) {
        try {
            ShortenUrlResponse response = urlShortenerService.shortenUrl(request);
            
            redirectAttributes.addFlashAttribute("successMessage", "URL shortened successfully!");
            redirectAttributes.addFlashAttribute("shortUrl", response);
            
            return "redirect:/";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid URL: " + e.getMessage());
            return "redirect:/";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to shorten URL. Please try again.");
            return "redirect:/";
        }
    }

    /**
     * Display about page
     */
    @GetMapping("/about")
    public String about(Model model) {
        model.addAttribute("baseUrl", baseUrl);
        return "about";
    }
}

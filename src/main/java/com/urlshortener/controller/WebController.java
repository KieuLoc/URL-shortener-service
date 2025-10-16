package com.urlshortener.controller;

import com.urlshortener.dto.ShortenUrlRequest;
import com.urlshortener.dto.ShortenUrlResponse;
import com.urlshortener.entity.Url;
import com.urlshortener.service.UrlShortenerService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Web Controller for Thymeleaf templates
 * 
 * @author URL Shortener Team
 */
//@Controller
public class WebController {

    private final UrlShortenerService urlShortenerService;
    
    @Value("${app.base-url}")
    private String baseUrl;

    public WebController(UrlShortenerService urlShortenerService) {
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
            Url url = urlShortenerService.createShortUrl(request.getUrl(), request.getExpirationDays());
            ShortenUrlResponse response = new ShortenUrlResponse(url, baseUrl);
            
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
     * Display URL information page
     */
    @GetMapping("/info/{shortCode}")
    public String urlInfo(@PathVariable String shortCode, Model model) {
        try {
            var urlOpt = urlShortenerService.getUrlByShortCode(shortCode);
            
            if (urlOpt.isPresent()) {
                Url url = urlOpt.get();
                ShortenUrlResponse response = new ShortenUrlResponse(url, baseUrl);
                long clickCount = urlShortenerService.getClickCount(url.getId());
                
                model.addAttribute("urlInfo", response);
                model.addAttribute("clickCount", clickCount);
                return "url-info";
            } else {
                model.addAttribute("errorMessage", "URL not found or has expired.");
                return "error";
            }
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Failed to retrieve URL information.");
            return "error";
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

package com.urlshortener.controller;

import com.urlshortener.dto.ShortenUrlRequest;
import com.urlshortener.dto.ShortenUrlResponse;
import com.urlshortener.service.UrlShortenerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;

/**
 * Integration tests for CacheWebController
 * 
 * @author URL Shortener Team
 */
@WebMvcTest(CacheWebController.class)
@WithMockUser
class CacheWebControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UrlShortenerService urlShortenerService;

    @Test
    void testIndexPage() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("shortenRequest"))
                .andExpect(model().attributeExists("baseUrl"));
    }

    @Test
    void testAboutPage() throws Exception {
        mockMvc.perform(get("/about"))
                .andExpect(status().isOk())
                .andExpect(view().name("about"))
                .andExpect(model().attributeExists("baseUrl"));
    }

    @Test
    void testShortenUrlWithValidInput() throws Exception {
        // Mock successful URL shortening
        ShortenUrlResponse mockResponse = new ShortenUrlResponse();
        mockResponse.setShortCode("abc123");
        mockResponse.setShortUrl("http://localhost:8080/abc123");
        mockResponse.setOriginalUrl("https://www.example.com");
        mockResponse.setCreatedAt(LocalDateTime.now());
        mockResponse.setActive(true);
        
        when(urlShortenerService.shortenUrl(any(ShortenUrlRequest.class)))
                .thenReturn(mockResponse);
        
        mockMvc.perform(post("/shorten")
                .param("url", "https://www.example.com")
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(flash().attributeExists("successMessage"))
                .andExpect(flash().attributeExists("shortUrl"));
    }

    @Test
    void testShortenUrlWithInvalidInput() throws Exception {
        // Mock invalid URL exception
        when(urlShortenerService.shortenUrl(any(ShortenUrlRequest.class)))
                .thenThrow(new IllegalArgumentException("Invalid URL format"));
        
        mockMvc.perform(post("/shorten")
                .param("url", "invalid-url")
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(flash().attributeExists("errorMessage"));
    }
}

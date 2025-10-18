package com.urlshortener.service;

import com.urlshortener.dto.ShortenUrlRequest;
import com.urlshortener.dto.ShortenUrlResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for InMemoryUrlShortenerService
 * 
 * @author URL Shortener Team
 */
@DisplayName("In-Memory URL Shortener Service Tests")
class InMemoryUrlShortenerServiceTest {

    private InMemoryUrlShortenerService urlShortenerService;

    @BeforeEach
    void setUp() {
        urlShortenerService = new InMemoryUrlShortenerService();
    }

    @Test
    @DisplayName("Should shorten valid URL successfully")
    void shouldShortenValidUrlSuccessfully() {
        // Given
        ShortenUrlRequest request = new ShortenUrlRequest("https://www.google.com");

        // When
        ShortenUrlResponse response = urlShortenerService.shortenUrl(request);

        // Then
        assertNotNull(response);
        assertNotNull(response.getShortCode());
        assertNotNull(response.getShortUrl());
        assertEquals("https://www.google.com", response.getOriginalUrl());
        assertTrue(response.getShortCode().length() >= 6);
        assertTrue(response.getShortUrl().startsWith("http://localhost:8080/"));
    }

    @Test
    @DisplayName("Should throw exception for invalid URL")
    void shouldThrowExceptionForInvalidUrl() {
        // Given
        ShortenUrlRequest request = new ShortenUrlRequest("invalid-url");

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> urlShortenerService.shortenUrl(request)
        );
        assertEquals("Invalid URL format", exception.getMessage());
    }


    @Test
    @DisplayName("Should redirect to original URL for valid short code")
    void shouldRedirectToOriginalUrlForValidShortCode() {
        // Given
        ShortenUrlRequest request = new ShortenUrlRequest("https://www.example.com");
        ShortenUrlResponse response = urlShortenerService.shortenUrl(request);
        String shortCode = response.getShortCode();

        // When
        String originalUrl = urlShortenerService.getOriginalUrl(shortCode);

        // Then
        assertEquals("https://www.example.com", originalUrl);
    }

    @Test
    @DisplayName("Should return null for invalid short code")
    void shouldReturnNullForInvalidShortCode() {
        // Given
        String invalidShortCode = "invalid";

        // When
        String originalUrl = urlShortenerService.getOriginalUrl(invalidShortCode);

        // Then
        assertNull(originalUrl);
    }

    @Test
    @DisplayName("Should return null for null short code")
    void shouldReturnNullForNullShortCode() {
        // When
        String originalUrl = urlShortenerService.getOriginalUrl(null);

        // Then
        assertNull(originalUrl);
    }


    @Test
    @DisplayName("Should generate unique short codes for different URLs")
    void shouldGenerateUniqueShortCodesForDifferentUrls() {
        // Given
        ShortenUrlRequest request1 = new ShortenUrlRequest("https://www.google.com");
        ShortenUrlRequest request2 = new ShortenUrlRequest("https://www.github.com");

        // When
        ShortenUrlResponse response1 = urlShortenerService.shortenUrl(request1);
        ShortenUrlResponse response2 = urlShortenerService.shortenUrl(request2);

        // Then
        assertNotEquals(response1.getShortCode(), response2.getShortCode());
    }
}

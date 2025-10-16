package com.urlshortener.controller;

import com.urlshortener.service.InMemoryUrlShortenerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for CacheWebController
 * 
 * @author URL Shortener Team
 */
@WebMvcTest(CacheWebController.class)
class CacheWebControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InMemoryUrlShortenerService urlShortenerService;

    @Test
    void testIndexPage() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("shortUrl"));
    }

    @Test
    void testAboutPage() throws Exception {
        mockMvc.perform(get("/about"))
                .andExpect(status().isOk())
                .andExpect(view().name("about"));
    }

    @Test
    void testShortenUrlWithValidInput() throws Exception {
        mockMvc.perform(post("/shorten")
                .param("url", "https://www.example.com"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    void testShortenUrlWithInvalidInput() throws Exception {
        mockMvc.perform(post("/shorten")
                .param("url", "invalid-url"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }
}

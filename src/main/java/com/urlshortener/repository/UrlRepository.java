package com.urlshortener.repository;

import com.urlshortener.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Url entity operations
 * 
 * @author URL Shortener Team
 */
@Repository
public interface UrlRepository extends JpaRepository<Url, Long> {

    /**
     * Find URL by short code
     * 
     * @param shortCode the short code to search for
     * @return Optional containing the URL if found
     */
    Optional<Url> findByShortCode(String shortCode);

    /**
     * Find URL by short code that is active and not expired
     * 
     * @param shortCode the short code to search for
     * @return Optional containing the accessible URL if found
     */
    @Query("SELECT u FROM Url u WHERE u.shortCode = :shortCode AND u.isActive = true AND (u.expiresAt IS NULL OR u.expiresAt > :currentTime)")
    Optional<Url> findByShortCodeAndAccessible(@Param("shortCode") String shortCode, @Param("currentTime") LocalDateTime currentTime);

    /**
     * Check if short code already exists
     * 
     * @param shortCode the short code to check
     * @return true if exists, false otherwise
     */
    boolean existsByShortCode(String shortCode);

    /**
     * Find URLs that have expired
     * 
     * @param currentTime current timestamp
     * @return List of expired URLs
     */
    @Query("SELECT u FROM Url u WHERE u.expiresAt IS NOT NULL AND u.expiresAt <= :currentTime")
    List<Url> findExpiredUrls(@Param("currentTime") LocalDateTime currentTime);

    /**
     * Find active URLs created after a specific date
     * 
     * @param createdAfter the date to filter by
     * @return List of active URLs created after the specified date
     */
    @Query("SELECT u FROM Url u WHERE u.isActive = true AND u.createdAt >= :createdAfter ORDER BY u.createdAt DESC")
    List<Url> findActiveUrlsCreatedAfter(@Param("createdAfter") LocalDateTime createdAfter);

    /**
     * Count total active URLs
     * 
     * @return number of active URLs
     */
    @Query("SELECT COUNT(u) FROM Url u WHERE u.isActive = true")
    long countActiveUrls();

    /**
     * Find most popular URLs by click count (requires analytics data)
     * 
     * @param limit maximum number of URLs to return
     * @return List of URLs ordered by click count
     */
    @Query(value = "SELECT u.* FROM urls u " +
                   "LEFT JOIN (SELECT url_id, COUNT(*) as click_count FROM url_analytics GROUP BY url_id) a " +
                   "ON u.id = a.url_id " +
                   "WHERE u.is_active = true " +
                   "ORDER BY COALESCE(a.click_count, 0) DESC " +
                   "LIMIT :limit", nativeQuery = true)
    List<Url> findMostPopularUrls(@Param("limit") int limit);
}

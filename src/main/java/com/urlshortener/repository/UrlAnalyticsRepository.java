package com.urlshortener.repository;

import com.urlshortener.entity.UrlAnalytics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for UrlAnalytics entity operations
 * 
 * @author URL Shortener Team
 */
@Repository
public interface UrlAnalyticsRepository extends JpaRepository<UrlAnalytics, Long> {

    /**
     * Find all analytics records for a specific URL
     * 
     * @param urlId the URL ID to search for
     * @return List of analytics records for the URL
     */
    List<UrlAnalytics> findByUrlIdOrderByClickedAtDesc(Long urlId);

    /**
     * Count total clicks for a specific URL
     * 
     * @param urlId the URL ID to count clicks for
     * @return number of clicks
     */
    long countByUrlId(Long urlId);

    /**
     * Find analytics records within a date range
     * 
     * @param urlId the URL ID to search for
     * @param startDate start of the date range
     * @param endDate end of the date range
     * @return List of analytics records within the date range
     */
    @Query("SELECT a FROM UrlAnalytics a WHERE a.urlId = :urlId AND a.clickedAt >= :startDate AND a.clickedAt <= :endDate ORDER BY a.clickedAt DESC")
    List<UrlAnalytics> findByUrlIdAndDateRange(@Param("urlId") Long urlId, 
                                               @Param("startDate") LocalDateTime startDate, 
                                               @Param("endDate") LocalDateTime endDate);

    /**
     * Count clicks by country for a specific URL
     * 
     * @param urlId the URL ID to analyze
     * @return List of country click counts
     */
    @Query("SELECT a.country, COUNT(a) FROM UrlAnalytics a WHERE a.urlId = :urlId AND a.country IS NOT NULL GROUP BY a.country ORDER BY COUNT(a) DESC")
    List<Object[]> countClicksByCountry(@Param("urlId") Long urlId);

    /**
     * Count clicks by hour of day for a specific URL
     * 
     * @param urlId the URL ID to analyze
     * @return List of hourly click counts
     */
    @Query("SELECT EXTRACT(HOUR FROM a.clickedAt) as hour, COUNT(a) FROM UrlAnalytics a WHERE a.urlId = :urlId GROUP BY EXTRACT(HOUR FROM a.clickedAt) ORDER BY hour")
    List<Object[]> countClicksByHour(@Param("urlId") Long urlId);

    /**
     * Find top referrers for a specific URL
     * 
     * @param urlId the URL ID to analyze
     * @param limit maximum number of referrers to return
     * @return List of top referrers
     */
    @Query("SELECT a.referer, COUNT(a) FROM UrlAnalytics a WHERE a.urlId = :urlId AND a.referer IS NOT NULL GROUP BY a.referer ORDER BY COUNT(a) DESC")
    List<Object[]> findTopReferrers(@Param("urlId") Long urlId);

    /**
     * Count unique IP addresses for a specific URL
     * 
     * @param urlId the URL ID to analyze
     * @return number of unique IP addresses
     */
    @Query("SELECT COUNT(DISTINCT a.ipAddress) FROM UrlAnalytics a WHERE a.urlId = :urlId AND a.ipAddress IS NOT NULL")
    long countUniqueVisitors(@Param("urlId") Long urlId);

    /**
     * Find recent analytics records (last 24 hours)
     * 
     * @param urlId the URL ID to search for
     * @param since timestamp from which to search
     * @return List of recent analytics records
     */
    @Query("SELECT a FROM UrlAnalytics a WHERE a.urlId = :urlId AND a.clickedAt >= :since ORDER BY a.clickedAt DESC")
    List<UrlAnalytics> findRecentAnalytics(@Param("urlId") Long urlId, @Param("since") LocalDateTime since);
}

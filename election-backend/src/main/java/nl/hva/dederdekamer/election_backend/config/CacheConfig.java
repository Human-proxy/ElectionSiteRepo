package nl.hva.dederdekamer.election_backend.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * Cache configuration for the application.
 * Configures in-memory caching for database queries
 */
@Configuration
@EnableCaching
@EnableScheduling
public class CacheConfig {

    public static final String PARTY_CACHE = "parties";
    public static final String ELECTED_PARTIES_CACHE = "electedParties";
    public static final String ELECTION_CACHE = "elections";
    public static final String TOP4_CACHE = "top4";
    public static final String TOP4_CONSTITUENCIES_CACHE = "top4Constituencies";
    public static final String MUNICIPALITY_SUMMARIES_CACHE = "municipalitySummaries";
    public static final String PARTY_VOTES_CACHE = "partyVotes";
    public static final String GOVERNMENT_NEWS_CACHE = "governmentNews";

    /**
     * Configure the cache manager with specific cache names.
     */
    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager(
                PARTY_CACHE,
                ELECTED_PARTIES_CACHE,
                ELECTION_CACHE,
                TOP4_CACHE,
                TOP4_CONSTITUENCIES_CACHE,
                MUNICIPALITY_SUMMARIES_CACHE,
                PARTY_VOTES_CACHE,
                GOVERNMENT_NEWS_CACHE);
    }

    /**
     * Clear election-related caches every hour to ensure data freshness.
     * This is useful when the database is updated externally.
     */
    @CacheEvict(value = { PARTY_CACHE, ELECTED_PARTIES_CACHE, ELECTION_CACHE, TOP4_CACHE,
            TOP4_CONSTITUENCIES_CACHE, MUNICIPALITY_SUMMARIES_CACHE,
            PARTY_VOTES_CACHE }, allEntries = true)
    @Scheduled(fixedRate = 3600000) // 1 hour in milliseconds
    public void clearElectionCachesHourly() {
        // Cache will be cleared automatically by @CacheEvict
    }

    /**
     * Clear government news cache once per day at 6:00 AM.
     * This ensures users get fresh news once daily.
     */
    @CacheEvict(value = GOVERNMENT_NEWS_CACHE, allEntries = true)
    @Scheduled(cron = "0 0 6 * * *") // Every day at 6:00 AM
    public void clearGovernmentNewsCacheDaily() {
        System.out.println("Clearing government news cache - scheduled daily refresh");
    }
}

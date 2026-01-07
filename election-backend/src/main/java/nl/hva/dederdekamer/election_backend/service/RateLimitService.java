package nl.hva.dederdekamer.election_backend.service;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service for rate limiting password reset requests
 * Prevents abuse by limiting requests per email address
 */
@Service
public class RateLimitService {

    // Store email -> last request time and count
    private final Map<String, RateLimitEntry> rateLimitMap = new ConcurrentHashMap<>();

    // Maximum requests per email within the time window
    private static final int MAX_REQUESTS = 3;
    
    // Time window in minutes
    private static final int TIME_WINDOW_MINUTES = 60;

    /**
     * Check if a password reset request is allowed for this email
     * @param email User's email address
     * @return true if request is allowed, false if rate limit exceeded
     */
    public boolean isAllowed(String email) {
        String normalizedEmail = email.toLowerCase().trim();
        LocalDateTime now = LocalDateTime.now();
        
        RateLimitEntry entry = rateLimitMap.get(normalizedEmail);
        
        // First request from this email
        if (entry == null) {
            rateLimitMap.put(normalizedEmail, new RateLimitEntry(now, 1));
            return true;
        }
        
        // Check if time window has passed
        LocalDateTime windowStart = now.minusMinutes(TIME_WINDOW_MINUTES);
        if (entry.firstRequestTime.isBefore(windowStart)) {
            // Reset counter - time window has passed
            rateLimitMap.put(normalizedEmail, new RateLimitEntry(now, 1));
            return true;
        }
        
        // Check if max requests exceeded
        if (entry.count >= MAX_REQUESTS) {
            return false;
        }
        
        // Increment counter
        entry.count++;
        return true;
    }

    /**
     * Get remaining time in minutes until rate limit resets
     * @param email User's email address
     * @return minutes until reset, or 0 if no limit active
     */
    public long getRemainingMinutes(String email) {
        String normalizedEmail = email.toLowerCase().trim();
        RateLimitEntry entry = rateLimitMap.get(normalizedEmail);
        
        if (entry == null) {
            return 0;
        }
        
        LocalDateTime resetTime = entry.firstRequestTime.plusMinutes(TIME_WINDOW_MINUTES);
        LocalDateTime now = LocalDateTime.now();
        
        if (now.isAfter(resetTime)) {
            return 0;
        }
        
        return java.time.Duration.between(now, resetTime).toMinutes() + 1;
    }

    /**
     * Clean up old entries (called by scheduled task)
     */
    public void cleanup() {
        LocalDateTime cutoff = LocalDateTime.now().minusMinutes(TIME_WINDOW_MINUTES);
        rateLimitMap.entrySet().removeIf(entry -> 
            entry.getValue().firstRequestTime.isBefore(cutoff)
        );
    }

    /**
     * Internal class to track rate limit data
     */
    private static class RateLimitEntry {
        LocalDateTime firstRequestTime;
        int count;

        RateLimitEntry(LocalDateTime firstRequestTime, int count) {
            this.firstRequestTime = firstRequestTime;
            this.count = count;
        }
    }
}

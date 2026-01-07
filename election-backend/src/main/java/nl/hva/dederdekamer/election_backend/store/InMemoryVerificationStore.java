package nl.hva.dederdekamer.election_backend.store;

import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InMemoryVerificationStore {

    /**
     * Temporary user data before verification
     */
    public static class PendingUserData {
        public final String username;
        public final String email;
        public final String passwordHash;
        public final String verificationCode;
        public final LocalDateTime expiresAt;
        public final LocalDateTime requestedAt;
        public final int resendCount; // Track how many times code was resent

        public PendingUserData(String username, String email, String passwordHash, 
                              String verificationCode, LocalDateTime expiresAt, 
                              LocalDateTime requestedAt, int resendCount) {
            this.username = username;
            this.email = email;
            this.passwordHash = passwordHash;
            this.verificationCode = verificationCode;
            this.expiresAt = expiresAt;
            this.requestedAt = requestedAt;
            this.resendCount = resendCount;
        }
    }

    // Key = username (lowercase)
    private final Map<String, PendingUserData> pendingUsers = new ConcurrentHashMap<>();
    private final Map<String, String> emailToUsername = new ConcurrentHashMap<>();

    /**
     * Store pending user registration data
     */
    public void storePendingUser(String username, String email, String passwordHash, 
                                   String verificationCode, LocalDateTime expiresAt) {
        String usernameLower = username.toLowerCase();
        String emailLower = email.toLowerCase();
        
        PendingUserData userData = new PendingUserData(
            usernameLower,
            emailLower,
            passwordHash,
            verificationCode,
            expiresAt,
            LocalDateTime.now(),
            0 // Initial resend count is 0
        );
        
        pendingUsers.put(usernameLower, userData);
        emailToUsername.put(emailLower, usernameLower);
    }

    /**
     * Get pending user data by username
     */
    public PendingUserData getPendingUser(String username) {
        return pendingUsers.get(username.toLowerCase());
    }

    /**
     * Check if email is already in use (in pending registrations)
     */
    public boolean isEmailPending(String email) {
        return emailToUsername.containsKey(email.toLowerCase());
    }

    /**
     * Check if username is already in use (in pending registrations)
     */
    public boolean isUsernamePending(String username) {
        return pendingUsers.containsKey(username.toLowerCase());
    }

    /**
     * Remove pending user data after successful verification
     */
    public void removePendingUser(String username) {
        PendingUserData userData = pendingUsers.remove(username.toLowerCase());
        if (userData != null) {
            emailToUsername.remove(userData.email);
        }
    }

    /**
     * Update pending user with new verification code (for resend)
     * Returns the new PendingUserData, or null if max resends reached
     */
    public PendingUserData updateVerificationCode(String username, String newCode, 
                                                   LocalDateTime newExpiresAt, int maxResends) {
        String usernameLower = username.toLowerCase();
        PendingUserData oldData = pendingUsers.get(usernameLower);
        
        if (oldData == null) {
            return null;
        }

        // Check if max resends reached
        if (oldData.resendCount >= maxResends) {
            return null;
        }

        // Create new data with incremented resend count and new code
        PendingUserData newData = new PendingUserData(
            oldData.username,
            oldData.email,
            oldData.passwordHash,
            newCode,
            newExpiresAt,
            LocalDateTime.now(), // Update requested time
            oldData.resendCount + 1
        );

        // Replace old data
        pendingUsers.put(usernameLower, newData);
        
        return newData;
    }

    /**
     * Clear expired pending registrations
     */
    public void clearExpiredPendingUsers() {
        LocalDateTime now = LocalDateTime.now();
        pendingUsers.entrySet().removeIf(entry -> {
            boolean expired = entry.getValue().expiresAt.isBefore(now);
            if (expired) {
                emailToUsername.remove(entry.getValue().email);
            }
            return expired;
        });
    }
}

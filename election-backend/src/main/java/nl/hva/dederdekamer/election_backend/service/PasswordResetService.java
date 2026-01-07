package nl.hva.dederdekamer.election_backend.service;

import nl.hva.dederdekamer.election_backend.entities.PasswordResetToken;
import nl.hva.dederdekamer.election_backend.entities.UserEntity;
import nl.hva.dederdekamer.election_backend.repository.PasswordResetTokenRepository;
import nl.hva.dederdekamer.election_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * Service for handling password reset functionality
 */
@Service
public class PasswordResetService {

    // Token expires after 1 hour
    private static final int EXPIRATION_HOURS = 1;

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RateLimitService rateLimitService;

    /**
     * Create a password reset token and send email to the user
     * Uses @Async to ensure consistent response time regardless of whether user exists
     * @param email User's email address
     */
    @Transactional
    @Async
    public void requestPasswordReset(String email) {
        Optional<UserEntity> userOpt = userRepository.findByEmail(email);
        
        if (userOpt.isEmpty()) {
            // Simulate processing time to prevent timing attacks
            try {
                Thread.sleep(100 + (long)(Math.random() * 200)); // Random delay 100-300ms
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return;
        }

        UserEntity user = userOpt.get();

        // Delete any existing tokens for this user
        tokenRepository.deleteByUser(user);

        // Generate a unique token
        String token = UUID.randomUUID().toString();

        // Create token with expiration time
        LocalDateTime expiryDate = LocalDateTime.now().plusHours(EXPIRATION_HOURS);
        PasswordResetToken resetToken = new PasswordResetToken(token, user, expiryDate);
        tokenRepository.save(resetToken);

        // Send email with reset link
        try {
            emailService.sendPasswordResetEmail(user.getEmail(), user.getUsername(), token);
        } catch (Exception e) {
            // Log error but don't expose to user
            System.err.println("Failed to send password reset email: " + e.getMessage());
        }
    }

    /**
     * Validate a password reset token
     * @param token The reset token
     * @return Optional containing the token if valid, empty otherwise
     */
    public Optional<PasswordResetToken> validateToken(String token) {
        Optional<PasswordResetToken> resetTokenOpt = tokenRepository.findByToken(token);

        if (resetTokenOpt.isEmpty()) {
            return Optional.empty();
        }

        PasswordResetToken resetToken = resetTokenOpt.get();

        if (!resetToken.isValid()) {
            return Optional.empty();
        }

        return Optional.of(resetToken);
    }

    /**
     * Reset the user's password using a valid token
     * @param token The reset token
     * @param newPassword The new password
     * @return true if password was reset successfully, false otherwise
     */
    @Transactional
    public boolean resetPassword(String token, String newPassword) {
        Optional<PasswordResetToken> resetTokenOpt = validateToken(token);

        if (resetTokenOpt.isEmpty()) {
            return false;
        }

        PasswordResetToken resetToken = resetTokenOpt.get();
        UserEntity user = resetToken.getUser();

        // Update password
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Mark token as used
        resetToken.setUsed(true);
        tokenRepository.save(resetToken);

        // Delete all tokens for this user
        tokenRepository.deleteByUser(user);

        return true;
    }

    /**
     * Clean up expired tokens - runs automatically every day at 3 AM
     */
    @Transactional
    @Scheduled(cron = "0 0 3 * * ?") // Every day at 3:00 AM
    public void cleanUpExpiredTokens() {
        // Delete expired tokens
        tokenRepository.deleteByExpiryDateBefore(LocalDateTime.now());
        
        // Also cleanup rate limit cache
        rateLimitService.cleanup();
    }
}

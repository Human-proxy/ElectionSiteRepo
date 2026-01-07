package nl.hva.dederdekamer.election_backend.service;

import jakarta.mail.MessagingException;
import nl.hva.dederdekamer.election_backend.entities.RoleEntity;
import nl.hva.dederdekamer.election_backend.entities.UserEntity;
import nl.hva.dederdekamer.election_backend.model.RoleName;
import nl.hva.dederdekamer.election_backend.repository.RoleRepository;
import nl.hva.dederdekamer.election_backend.repository.UserRepository;
import nl.hva.dederdekamer.election_backend.store.InMemoryVerificationStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.Set;

/**
 * Service for handling email verification functionality.
 * Stores pending registrations in memory until email is verified.
 */
@Service
public class VerificationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private InMemoryVerificationStore verificationStore;

    private static final int CODE_EXPIRATION_MINUTES = 15;
    private static final int RESEND_COOLDOWN_SECONDS = 60;
    private static final int MAX_RESEND_ATTEMPTS = 3;

    /**
     * Create a pending registration (in memory only) and send verification email
     * Returns the expiration timestamp with timezone
     */
    public java.time.ZonedDateTime createPendingRegistration(String username, String email, String passwordHash) 
            throws MessagingException {
        
        // Generate 4-digit code
        String code = generateFourDigitCode();
        
        // Set expiration time (using ZonedDateTime to preserve timezone info)
        LocalDateTime expiresAtLocal = LocalDateTime.now().plusMinutes(CODE_EXPIRATION_MINUTES);
        java.time.ZonedDateTime expiresAt = expiresAtLocal.atZone(java.time.ZoneId.systemDefault());
        
        // Store in memory (still using LocalDateTime internally)
        verificationStore.storePendingUser(username, email, passwordHash, code, expiresAtLocal);
        
        // Send email
        System.out.println("Attempting to send verification email to: " + email);
        System.out.println("Verification code: " + code);
        
        try {
            emailService.sendVerificationEmail(email, username, code);
            System.out.println("Email sent successfully!");
        } catch (MessagingException e) {
            System.err.println("Failed to send email: " + e.getMessage());
            // Remove from memory if email fails
            verificationStore.removePendingUser(username);
            throw e;
        } catch (Exception e) {
            System.err.println("Failed to send email: " + e.getClass().getName() + " - " + e.getMessage());
            e.printStackTrace();
            // Remove from memory if email fails
            verificationStore.removePendingUser(username);
            throw e;
        }
        
        return expiresAt;
    }

    /**
     * Verify email with username and code, then save user to database
     * @param username The username from registration
     * @param code The 4-digit code provided by user
     * @return The newly created user entity
     */
    public UserEntity verifyEmailAndCreateUser(String username, String code) {
        // Get pending user data
        InMemoryVerificationStore.PendingUserData pendingUser = 
            verificationStore.getPendingUser(username);
        
        if (pendingUser == null) {
            throw new IllegalStateException("No pending registration found for this username");
        }

        // Check if code is expired
        if (pendingUser.expiresAt.isBefore(LocalDateTime.now())) {
            verificationStore.removePendingUser(username);
            throw new IllegalStateException("Verification code has expired");
        }

        // Check if code matches
        if (!pendingUser.verificationCode.equals(code)) {
            throw new IllegalStateException("Invalid verification code");
        }

        // Check again if username/email still available (race condition protection)
        if (userRepository.findByUsername(pendingUser.username).isPresent()) {
            verificationStore.removePendingUser(username);
            throw new IllegalStateException("Gebruikersnaam is al in gebruik");
        }
        if (userRepository.findByEmail(pendingUser.email).isPresent()) {
            verificationStore.removePendingUser(username);
            throw new IllegalStateException("E-mailadres is al in gebruik");
        }

        // Create user in database
        RoleEntity userRole = roleRepository.findByName(RoleName.USER)
            .orElseGet(() -> roleRepository.save(new RoleEntity(RoleName.USER)));

        UserEntity user = new UserEntity(
            pendingUser.username,
            pendingUser.email,
            pendingUser.passwordHash,
            true,  // Enabled after verification
            new java.util.HashSet<>(Set.of(userRole))
        );
        user.setEmailVerified(true);  // Already verified

        UserEntity savedUser = userRepository.save(user);

        // Remove from memory
        verificationStore.removePendingUser(username);

        return savedUser;
    }

    /**
     * Check if username is pending verification
     */
    public boolean isUsernamePending(String username) {
        return verificationStore.isUsernamePending(username);
    }

    /**
     * Check if email is pending verification
     */
    public boolean isEmailPending(String email) {
        return verificationStore.isEmailPending(email);
    }

    /**
     * Resend verification code with new code (invalidates old one)
     * @param username The username to resend code for
     * @return The new expiration timestamp with timezone, or null if cooldown active or max resends reached
     */
    public java.time.ZonedDateTime resendVerificationCode(String username) throws MessagingException {
        InMemoryVerificationStore.PendingUserData userData = verificationStore.getPendingUser(username);
        
        if (userData == null) {
            throw new IllegalStateException("Geen pending registratie gevonden voor gebruiker");
        }

        // Check cooldown (60 seconds since last request)
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime cooldownEnd = userData.requestedAt.plusSeconds(RESEND_COOLDOWN_SECONDS);
        if (now.isBefore(cooldownEnd)) {
            long secondsRemaining = java.time.Duration.between(now, cooldownEnd).getSeconds();
            throw new IllegalStateException("Wacht nog " + secondsRemaining + " seconden voordat je opnieuw kunt verzenden");
        }

        // Generate new code
        String newCode = generateFourDigitCode();
        LocalDateTime newExpiresAtLocal = LocalDateTime.now().plusMinutes(CODE_EXPIRATION_MINUTES);

        // Update with new code (this also checks max resends)
        InMemoryVerificationStore.PendingUserData newData = verificationStore.updateVerificationCode(
            username, newCode, newExpiresAtLocal, MAX_RESEND_ATTEMPTS
        );

        if (newData == null) {
            throw new IllegalStateException("Maximaal aantal verstuur-pogingen bereikt. Probeer opnieuw te registreren.");
        }

        // Send new email
        try {
            emailService.sendVerificationEmail(userData.email, username, newCode);
            System.out.println("Resent verification code to: " + userData.email + " - New code: " + newCode);
        } catch (MessagingException e) {
            System.err.println("Failed to resend email: " + e.getMessage());
            throw e;
        }

        // Convert to ZonedDateTime with timezone info
        java.time.ZonedDateTime newExpiresAt = newExpiresAtLocal.atZone(java.time.ZoneId.systemDefault());
        return newExpiresAt;
    }

    /**
     * Generate a random 4-digit code
     */
    private String generateFourDigitCode() {
        Random random = new Random();
        int code = random.nextInt(9000) + 1000; // Range: 1000-9999
        return String.valueOf(code);
    }
}

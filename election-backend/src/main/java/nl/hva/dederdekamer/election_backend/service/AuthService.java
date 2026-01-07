package nl.hva.dederdekamer.election_backend.service;

import java.time.LocalDateTime;
import java.util.stream.Collectors;
import jakarta.mail.MessagingException;
import nl.hva.dederdekamer.election_backend.dto.JwtResponse;
import nl.hva.dederdekamer.election_backend.dto.LoginRequest;
import nl.hva.dederdekamer.election_backend.dto.RegisterRequest;
import nl.hva.dederdekamer.election_backend.dto.RegisterResponse;
import nl.hva.dederdekamer.election_backend.dto.UserResponse;
import nl.hva.dederdekamer.election_backend.entities.UserEntity;
import nl.hva.dederdekamer.election_backend.exception.UnauthorizedException;
import nl.hva.dederdekamer.election_backend.repository.RoleRepository;
import nl.hva.dederdekamer.election_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Authentication/business logic: register, login, and mapping to DTOs.
 */
@Service
public class AuthService {

    private final UserRepository users;
    private final RoleRepository roles;
    private final PasswordEncoder encoder;
    private final JwtService jwt;
    private final long expirationMinutes;
    private VerificationService verificationService;

    public AuthService(UserRepository users,
                       RoleRepository roles,
                       PasswordEncoder encoder,
                       JwtService jwt,
                       @Value("${app.jwt.expiration-minutes}") long expirationMinutes) {
        this.users = users;
        this.roles = roles;
        this.encoder = encoder;
        this.jwt = jwt;
        this.expirationMinutes = expirationMinutes;
    }

    @Autowired(required = false)
    public void setVerificationService(VerificationService verificationService) {
        this.verificationService = verificationService;
    }

    /**
     * Authenticates by username or email and returns a JWT + user info.
     * @throws UnauthorizedException when user not found/disabled or password mismatch.
     */
    public JwtResponse login(LoginRequest req) {
        final String id = req.getIdentifier().trim().toLowerCase();

        UserEntity user = users.findByUsername(id).orElseGet(() ->
            users.findByEmail(id).orElse(null)
        );

        // Check for soft delete status
        boolean isSoftDeleted = false;
        if (user != null && user.getDeletedAt() != null) {
            if (user.getDeletedAt().isAfter(LocalDateTime.now().minusDays(30))) {
                // Account is in soft-delete period, allow login to proceed so they can reactivate
                isSoftDeleted = true;
            } else {
                // Past 30 days, treat as permanently deleted
                throw new UnauthorizedException("Ongeldige gebruikersnaam/e-mail of wachtwoord");
            }
        }

        // If not soft deleted, check if enabled
        if (user == null || (!isSoftDeleted && !user.isEnabled())) {
            throw new UnauthorizedException("Ongeldige gebruikersnaam/e-mail of wachtwoord");
        }
        
        if (!encoder.matches(req.getPassword(), user.getPasswordHash())) {
            throw new UnauthorizedException("Ongeldige gebruikersnaam/e-mail of wachtwoord");
        }

        String token = jwt.generate(user.getUsername());
        return new JwtResponse(token, expirationMinutes * 60, toUserResponse(user));
    }

    /**
     * Reactivates a soft-deleted account.
     */
    public void reactivateAccount(String username) {
        UserEntity user = users.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        if (user.getDeletedAt() != null) {
            user.setDeletedAt(null);
            user.setEnabled(true);
            users.save(user);
        }
    }

    /**
     * Registers a user (unique username/email), assigns USER, hashes password.
     * Stores in memory until email verification.
     * Returns a response indicating verification email was sent.
     */
    public RegisterResponse register(RegisterRequest req) {
        if (!req.getPassword().equals(req.getConfirmPassword())) {
            throw new IllegalArgumentException("Wachtwoorden komen niet overeen");
        }

        String username = req.getUsername().trim().toLowerCase();
        String email    = req.getEmail().trim().toLowerCase();

        // Check if username/email already exists in DATABASE
        users.findByUsername(username).ifPresent(u -> { 
            throw new IllegalArgumentException("Gebruikersnaam bestaat al"); 
        });
        users.findByEmail(email).ifPresent(u -> { 
            throw new IllegalArgumentException("e-mailadres bestaat al"); 
        });

        // Check if username/email already exists in PENDING registrations
        if (verificationService != null) {
            if (verificationService.isUsernamePending(username)) {
                throw new IllegalArgumentException("Gebruikersnaam is al in gebruik (wacht op verificatie)");
            }
            if (verificationService.isEmailPending(email)) {
                throw new IllegalArgumentException("E-mailadres is al in gebruik (wacht op verificatie)");
            }
        }

        // Hash password but DON'T save to database yet
        String passwordHash = encoder.encode(req.getPassword());

        // Send verification email and store in memory
        boolean emailSent = false;
        java.time.ZonedDateTime expiresAt = null;
        if (verificationService != null) {
            try {
                expiresAt = verificationService.createPendingRegistration(username, email, passwordHash);
                emailSent = true;
            } catch (MessagingException e) {
                System.err.println("Failed to send verification email: " + e.getMessage());
                e.printStackTrace();
            } catch (Exception e) {
                System.err.println("Error sending verification email: " + e.getMessage());
                e.printStackTrace();
            }
        }

        // Return success response with username (used for verification)
        return new RegisterResponse(
            username,
            email,
            emailSent,
            "Registratie succesvol. Controleer je e-mail voor de verificatiecode.",
            expiresAt
        );
    }

    /** Returns user DTO for the current principal username. */
    public UserResponse me(String usernameLowercased) {
        var user = users.findByUsername(usernameLowercased).orElseThrow();
        return toUserResponse(user);
    }

    private UserResponse toUserResponse(UserEntity u) {
        var roles = u.getRoles().stream()
                .map(r -> r.getName().name())
                .collect(Collectors.toSet());
        return new UserResponse(
                String.valueOf(u.getId()),
                u.getUsername(),
                u.getEmail(),
                u.getProfileImageUrl(),
                u.getCreatedAt(),
                u.getVisitedPages().stream().collect(Collectors.toSet()),
                roles,
                u.getDeletedAt()
        );
    }
}

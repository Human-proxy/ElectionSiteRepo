package nl.hva.dederdekamer.election_backend.service;

import nl.hva.dederdekamer.election_backend.dto.ProfileUpdateResponse;
import nl.hva.dederdekamer.election_backend.dto.UserResponse;
import nl.hva.dederdekamer.election_backend.entities.UserEntity;
import nl.hva.dederdekamer.election_backend.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

/**
 * Service for user profile management operations.
 */
@Service
public class ProfileService {

    private final UserRepository users;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public ProfileService(UserRepository users, JwtService jwtService, PasswordEncoder passwordEncoder, EmailService emailService) {
        this.users = users;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    /**
     * Gets the current user's profile information.
     *
     * @param usernameLowercased the username (principal from JWT)
     * @return user profile response
     */
    public UserResponse getProfile(String usernameLowercased) {
        UserEntity user = users.findByUsername(usernameLowercased)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return toUserResponse(user);
    }

    /**
     * Updates the profile image URL for the given user.
     *
     * @param usernameLowercased the username (principal from JWT)
     * @param profileImageUrl the new profile image URL (can be a URL or base64 string)
     * @return updated user response
     */
    @Transactional
    public UserResponse updateProfileImage(String usernameLowercased, String profileImageUrl) {
        UserEntity user = users.findByUsername(usernameLowercased)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setProfileImageUrl(profileImageUrl);
        users.save(user);
        return toUserResponse(user);
    }

    /**
     * Updates the profile information for the given user.
     * If username is changed, a new JWT token is generated and returned.
     *
     * @param currentUsername the current username (principal from JWT)
     * @param newUsername the new username (can be null to keep current)
     * @param newEmail the new email (can be null to keep current)
     * @return ProfileUpdateResponse with user data and optionally new token
     */
    @Transactional
    public ProfileUpdateResponse updateProfile(String currentUsername, String newUsername, String newEmail) {
        UserEntity user = users.findByUsername(currentUsername)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        boolean usernameChanged = false;
        
        // Update username if provided and different from current
        if (newUsername != null && !newUsername.trim().isEmpty()) {
            String trimmedUsername = newUsername.trim().toLowerCase();
            // Only update if different from current username
            if (!trimmedUsername.equals(user.getUsername())) {
                // Check if username already exists
                if (users.findByUsername(trimmedUsername).isPresent()) {
                    throw new IllegalArgumentException("Username already exists");
                }
                user.setUsername(trimmedUsername);
                usernameChanged = true;
            }
        }
        
        // Update email if provided and different from current
        if (newEmail != null && !newEmail.trim().isEmpty()) {
            String trimmedEmail = newEmail.trim().toLowerCase();
            // Basic email validation
            if (!trimmedEmail.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
                throw new IllegalArgumentException("Invalid email format");
            }
            // Only update if different from current email
            if (!trimmedEmail.equals(user.getEmail())) {
                // Check if email already exists
                if (users.findByEmail(trimmedEmail).isPresent()) {
                    throw new IllegalArgumentException("Email already exists");
                }
                user.setEmail(trimmedEmail);
            }
        }
        
        users.save(user);
        
        // Generate new token if username was changed
        String newToken = usernameChanged ? jwtService.generate(user.getUsername()) : null;
        
        return new ProfileUpdateResponse(toUserResponse(user), newToken);
    }

    /**
     * Adds a visited page to the user's learning progress.
     *
     * @param usernameLowercased the username (principal from JWT)
     * @param pagePath the path of the visited page
     * @return updated user response
     */
    @Transactional
    public UserResponse addVisitedPage(String usernameLowercased, String pagePath) {
        UserEntity user = users.findByUsername(usernameLowercased)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
    // Remove old root path entries and add the new path
    user.getVisitedPages().remove("/");
    user.getVisitedPages().add(pagePath);
        users.save(user);
        return toUserResponse(user);
    }

    /**
     * Changes the password for the given user.
     *
     * @param usernameLowercased the username (principal from JWT)
     * @param currentPassword the current password for verification
     * @param newPassword the new password to set
     * @throws IllegalArgumentException if current password is incorrect or new password is invalid
     */
    @Transactional
    public void changePassword(String usernameLowercased, String currentPassword, String newPassword) {
        UserEntity user = users.findByUsername(usernameLowercased)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        // Verify current password
        if (!passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }
        
        // Validate new password
        if (newPassword == null || newPassword.trim().length() < 8) {
            throw new IllegalArgumentException("New password must be at least 8 characters long");
        }
        
        // Check if new password is different from current
        if (passwordEncoder.matches(newPassword, user.getPasswordHash())) {
            throw new IllegalArgumentException("New password cannot be the same as the current password");
        }
        
        // Hash and save new password
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        users.save(user);
    }

    /**
     * Soft deletes the user's account.
     *
     * @param usernameLowercased the username (principal from JWT)
     * @throws IllegalArgumentException if user is not found
     */
    @Transactional
    public void deleteAccount(String usernameLowercased) {
        UserEntity user = users.findByUsername(usernameLowercased)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        user.setDeletedAt(java.time.LocalDateTime.now());
        user.setEnabled(false);
        users.save(user);

        try {
            emailService.sendAccountDeactivationEmail(user.getEmail(), user.getUsername());
        } catch (Exception e) {
            // Log error but don't fail the transaction
            System.err.println("Failed to send deactivation email: " + e.getMessage());
        }
    }

    /**
     * Converts a UserEntity to a UserResponse DTO.
     */
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

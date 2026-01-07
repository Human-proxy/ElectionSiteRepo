package nl.hva.dederdekamer.election_backend.dto;

/**
 * Response DTO for profile updates.
 * Contains the updated user info and a new JWT token
 * (when username was changed).
 */
public class ProfileUpdateResponse {
    private final UserResponse user;
    private final String token; // New JWT token (only present if username changed)

    public ProfileUpdateResponse(UserResponse user, String token) {
        this.user = user;
        this.token = token;
    }

    public UserResponse getUser() {
        return user;
    }

    public String getToken() {
        return token;
    }
}

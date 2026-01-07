package nl.hva.dederdekamer.election_backend.dto;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * Lightweight user DTO returned to clients.
 * Avoids exposing sensitive fields (e.g., password hash).
 */
public class UserResponse {
    private final String id;
    private final String username;
    private final String email;
    private final String profileImageUrl;
    private final LocalDateTime createdAt;
    private final Set<String> visitedPages;
    private final Set<String> roles;
    private final LocalDateTime deletedAt;

    public UserResponse(String id, String username, String email, String profileImageUrl, LocalDateTime createdAt, Set<String> visitedPages, Set<String> roles, LocalDateTime deletedAt) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.profileImageUrl = profileImageUrl;
        this.createdAt = createdAt;
        this.visitedPages = visitedPages;
        this.roles = roles;
        this.deletedAt = deletedAt;
    }

    public String getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getProfileImageUrl() { return profileImageUrl; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public Set<String> getVisitedPages() { return visitedPages; }
    public Set<String> getRoles() { return roles; }
    public LocalDateTime getDeletedAt() { return deletedAt; }
}

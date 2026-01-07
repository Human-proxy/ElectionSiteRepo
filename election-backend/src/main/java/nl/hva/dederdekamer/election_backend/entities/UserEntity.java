package nl.hva.dederdekamer.election_backend.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * JPA entity representing an application user.
 *
 *   {@code username} and {@code email} are unique and stored in lowercase.
 *   {@code passwordHash} uses BCrypt.
 *   Roles are eagerly fetched for simplicity in auth flows.
 *
 */
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(
    name = "users",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = "username"),
        @UniqueConstraint(columnNames = "email")
    },
    indexes = {
        @Index(columnList = "username", unique = true),
        @Index(columnList = "email", unique = true)
    }
)
public class UserEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 64)
    private String username;        // lowercased

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(nullable = false, length = 120)
    private String email;           // lowercased

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(nullable = false, length = 100)
    private String passwordHash;

    @Column(columnDefinition = "TEXT")
    private String profileImageUrl;  // URL or base64 of profile image

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_visited_pages", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "page_path")
    private Set<String> visitedPages = new java.util.HashSet<>();

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(nullable = false)
    private boolean enabled = true;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<RoleEntity> roles;
    
    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private java.util.List<nl.hva.dederdekamer.election_backend.entities.Post> posts = new java.util.ArrayList<>();
    
    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private java.util.List<nl.hva.dederdekamer.election_backend.entities.Comment> comments = new java.util.ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private java.util.List<nl.hva.dederdekamer.election_backend.entities.PasswordResetToken> passwordResetTokens = new java.util.ArrayList<>();


    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(nullable = false)
    private boolean emailVerified = false;

    @Column
    private LocalDateTime deletedAt;


    /** JPA-only constructor. */
    public UserEntity() {}

    /**
     * Constructs a user with the given fields.
     *
     * @param username     lowercased unique username
     * @param email        lowercased unique email
     * @param passwordHash BCrypt hash
     * @param enabled      account enabled flag
     * @param roles        set of roles
     */
    public UserEntity(String username, String email, String passwordHash, boolean enabled, Set<RoleEntity> roles) {
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.enabled = enabled;
        this.roles = roles;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public String getProfileImageUrl() { return profileImageUrl; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public Set<String> getVisitedPages() { return visitedPages; }
    public boolean isEnabled() { return enabled; }
    public Set<RoleEntity> getRoles() { return roles; }

    public void setUsername(String username) { this.username = username; }
    public void setEmail(String email) { this.email = email; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }
    public void setVisitedPages(Set<String> visitedPages) { this.visitedPages = visitedPages; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public void setRoles(Set<RoleEntity> roles) { this.roles = roles; }

    public LocalDateTime getDeletedAt() { return deletedAt; }
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }

    // Email verification getters and setters (only status, code stored in-memory)
    public boolean isEmailVerified() { return emailVerified; }
    public void setEmailVerified(boolean emailVerified) { this.emailVerified = emailVerified; }
}

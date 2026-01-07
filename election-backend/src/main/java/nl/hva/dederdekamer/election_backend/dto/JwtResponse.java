package nl.hva.dederdekamer.election_backend.dto;

/**
 * Response payload containing an access token and the minimal user info.
 */
public class JwtResponse {
    private final String token;
    private final String tokenType = "Bearer";
    private final long expiresInSeconds;
    private final UserResponse user;

    public JwtResponse(String token, long expiresInSeconds, UserResponse user) {
        this.token = token;
        this.expiresInSeconds = expiresInSeconds;
        this.user = user;
    }

    public String getToken() { return token; }
    public String getTokenType() { return tokenType; }
    public long getExpiresInSeconds() { return expiresInSeconds; }
    public UserResponse getUser() { return user; }
}

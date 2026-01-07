package nl.hva.dederdekamer.election_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Login request.
 * <p>
 * {@code identifier} accepts either a username or an email (case-insensitive).
 * The service will normalize to lowercase and try username lookup first, then email.
 */
public class LoginRequest {

    @NotBlank
    @Size(min = 3, max = 120)
    private String identifier; // username OR email

    @NotBlank
    @Size(min = 8, max = 128)
    private String password;

    public String getIdentifier() { return identifier; }
    public void setIdentifier(String identifier) { this.identifier = identifier; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}

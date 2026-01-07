package nl.hva.dederdekamer.election_backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Registration payload sent by clients.
 *
 * Validations ensure basic hygiene and a minimal password policy:
 * at least one upper, one lower, and one digit.
 */
public class RegisterRequest {

    @NotBlank @Size(min = 3, max = 64)
    private String username;

    @NotBlank @Email @Size(max = 120)
    private String email;

    @NotBlank @Size(min = 8, max = 128)
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d).+$",
             message = "wachtwoord moet hoofdletters, kleine letters en cijfers bevatten")
    private String password;

    @NotBlank
    private String confirmPassword;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getConfirmPassword() { return confirmPassword; }
    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }
}

package nl.hva.dederdekamer.election_backend.controller;

import nl.hva.dederdekamer.election_backend.service.PasswordResetService;
import nl.hva.dederdekamer.election_backend.service.RateLimitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST Controller for password reset functionality
 */
@RestController
@RequestMapping("/api/password-reset")
public class PasswordResetController {

    @Autowired
    private PasswordResetService passwordResetService;

    @Autowired
    private RateLimitService rateLimitService;

    /**
     * Request a password reset - sends email with reset link
     * POST /api/password-reset/request
     * Body: { "email": "user@example.com" }
     */
    @PostMapping("/request")
    public ResponseEntity<Map<String, String>> requestPasswordReset(@RequestBody Map<String, String> request) {
        String email = request.get("email");

        if (email == null || email.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                .body(Map.of("message", "Email adres is verplicht"));
        }

        String normalizedEmail = email.toLowerCase().trim();
        
        // Check rate limit
        if (!rateLimitService.isAllowed(normalizedEmail)) {
            long minutesRemaining = rateLimitService.getRemainingMinutes(normalizedEmail);
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .body(Map.of(
                    "message", "Te veel reset verzoeken. Probeer het over " + minutesRemaining + " minuten opnieuw."
                ));
        }

        // Always return success for security (don't reveal if email exists)
        // Use async to ensure consistent response time
        passwordResetService.requestPasswordReset(normalizedEmail);
        
        return ResponseEntity.ok(Map.of(
            "message", "Als dit e-mailadres bij ons bekend is, ontvang je binnen enkele minuten een link om je wachtwoord te resetten."
        ));
    }

    /**
     * Validate a reset token
     * GET /api/password-reset/validate/{token}
     */
    @GetMapping("/validate/{token}")
    public ResponseEntity<Map<String, Object>> validateToken(@PathVariable String token) {
        boolean isValid = passwordResetService.validateToken(token).isPresent();
        
        if (isValid) {
            return ResponseEntity.ok(Map.of(
                "valid", true,
                "message", "Token is geldig"
            ));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                    "valid", false,
                    "message", "Deze link is verlopen of ongeldig. Vraag een nieuwe reset link aan."
                ));
        }
    }

    /**
     * Reset password using token
     * POST /api/password-reset/reset
     * Body: { "token": "uuid-token", "newPassword": "newpass123" }
     */
    @PostMapping("/reset")
    public ResponseEntity<Map<String, String>> resetPassword(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        String newPassword = request.get("newPassword");

        if (token == null || token.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                .body(Map.of("message", "Token is verplicht"));
        }

        if (newPassword == null || newPassword.length() < 8) {
            return ResponseEntity.badRequest()
                .body(Map.of("message", "Wachtwoord moet minimaal 8 karakters bevatten"));
        }

        boolean success = passwordResetService.resetPassword(token, newPassword);

        if (success) {
            return ResponseEntity.ok(Map.of(
                "message", "Je wachtwoord is succesvol gewijzigd. Je kunt nu inloggen met je nieuwe wachtwoord."
            ));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                    "message", "Deze link is verlopen of ongeldig. Vraag een nieuwe reset link aan."
                ));
        }
    }
}

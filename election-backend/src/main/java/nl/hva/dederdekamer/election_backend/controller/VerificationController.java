package nl.hva.dederdekamer.election_backend.controller;

import nl.hva.dederdekamer.election_backend.dto.JwtResponse;
import nl.hva.dederdekamer.election_backend.entities.UserEntity;
import nl.hva.dederdekamer.election_backend.service.VerificationService;
import nl.hva.dederdekamer.election_backend.service.JwtService;
import nl.hva.dederdekamer.election_backend.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * REST controller for email verification operations.
 * Handles verification of pending registrations (stored in memory).
 */
@RestController
@RequestMapping("/api/verification")
public class VerificationController {

    @Autowired
    private VerificationService verificationService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthService authService;

    @Value("${app.jwt.expiration-minutes}")
    private long expirationMinutes;

    /**
     * Verify email with 4-digit code using username
     * POST /api/verification/verify
     * Body: { "username": "name", "code": "1234" }
     * Creates user in database and returns JWT token on success
     */
    @PostMapping("/verify")
    public ResponseEntity<?> verifyEmail(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();

        String username = request.get("username");
        String code = request.get("code");

        if (username == null || username.trim().isEmpty()) {
            response.put("success", false);
            response.put("message", "Username is required");
            return ResponseEntity.badRequest().body(response);
        }

        if (code == null || code.trim().isEmpty()) {
            response.put("success", false);
            response.put("message", "Verification code is required");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            // Verify code and CREATE user in database
            UserEntity user = verificationService.verifyEmailAndCreateUser(
                username.trim().toLowerCase(), 
                code.trim()
            );
            
            // User created successfully - generate JWT token and log user in
            String token = jwtService.generate(user.getUsername());
            JwtResponse jwtResponse = new JwtResponse(
                token, 
                expirationMinutes * 60, 
                authService.me(user.getUsername())
            );
            return ResponseEntity.ok(jwtResponse);
            
        } catch (IllegalStateException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Resend verification code (generates new code, invalidates old one)
     * POST /api/verification/resend
     * Body: { "username": "name" }
     */
    @PostMapping("/resend")
    public ResponseEntity<?> resendVerificationCode(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();

        String username = request.get("username");

        if (username == null || username.trim().isEmpty()) {
            response.put("success", false);
            response.put("message", "Gebruikersnaam is verplicht");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            java.time.ZonedDateTime expiresAt = verificationService.resendVerificationCode(username.trim().toLowerCase());
            
            response.put("success", true);
            response.put("message", "Nieuwe verificatiecode verzonden");
            response.put("expiresAt", expiresAt.toString());
            return ResponseEntity.ok(response);
            
        } catch (IllegalStateException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Er is iets misgegaan bij het verzenden van de code");
            return ResponseEntity.status(500).body(response);
        }
    }
}

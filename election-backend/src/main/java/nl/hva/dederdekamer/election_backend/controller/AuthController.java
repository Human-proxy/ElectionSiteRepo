package nl.hva.dederdekamer.election_backend.controller;

import jakarta.validation.Valid;
import nl.hva.dederdekamer.election_backend.dto.JwtResponse;
import nl.hva.dederdekamer.election_backend.dto.LoginRequest;
import nl.hva.dederdekamer.election_backend.dto.RegisterRequest;
import nl.hva.dederdekamer.election_backend.dto.RegisterResponse;
import nl.hva.dederdekamer.election_backend.dto.UserResponse;
import nl.hva.dederdekamer.election_backend.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import nl.hva.dederdekamer.election_backend.security.CurrentUser;
import nl.hva.dederdekamer.election_backend.entities.UserEntity;
/**
 * Public authentication endpoints.
 * 
 * exposes registration
 *
 *  POST /api/auth/register — create account and receive a JWT
 *  POST /api/auth/login — login with username or email, receive a JWT
 *  GET  /api/auth/me — get current user (requires Bearer token)
 * 
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService auth;

    /**
     * Constructs an {@code AuthController}.
     *
     * @param auth authentication service
     */
    public AuthController(AuthService auth) {
        this.auth = auth;
    }

    /**
     * Registers a new user (unique username & email).
     * Does NOT log the user in they must verify their email first.
     *
     * @param request validated registration payload
     * @return Registration response with verification instructions
     */
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(auth.register(request));
    }

    /** Logs in by username or email (identifier) and returns a JWT. */
    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(auth.login(request));
    }

    /**
     * Reactivates a soft-deleted account.
     */
    @PostMapping("/reactivate")
    public ResponseEntity<?> reactivate(@CurrentUser UserEntity user) {
        auth.reactivateAccount(user.getUsername());
        return ResponseEntity.ok("Account reactivated successfully");
    }

    /**
     * Gets the current authenticated user.
     * <p>
     * The {@link Authentication#getPrincipal()} is set by {@code JwtAuthFilter}
     * to the lowercased username.
     */
    @GetMapping("/me")
   public ResponseEntity<UserResponse> me(@CurrentUser UserEntity user) {
    return ResponseEntity.ok(auth.me(user.getUsername()));
  }
}

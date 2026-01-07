package nl.hva.dederdekamer.election_backend.dto;

import java.time.ZonedDateTime;

/**
 * DTO for registration response
 * Does not include JWT token - user must verify email first
 * Username is used for verification
 */
public record RegisterResponse(
    String username,
    String email,
    boolean emailSent,
    String message,
    ZonedDateTime expiresAt
) {}

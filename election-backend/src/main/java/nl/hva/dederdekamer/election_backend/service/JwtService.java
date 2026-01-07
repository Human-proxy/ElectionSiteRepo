package nl.hva.dederdekamer.election_backend.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

/**
 * Stateless JWT creation and validation service.
 * <p>
 * Generates signed tokens containing minimal identity (subject=username) and
 * validates/decodes tokens on incoming requests.
 */
@Service
public class JwtService {

    private final SecretKey key;
    private final String issuer;
    private final long expirationMinutes;

    public JwtService(@Value("${app.jwt.secret}") String secret,
                      @Value("${app.jwt.issuer}") String issuer,
                      @Value("${app.jwt.expiration-minutes}") long expirationMinutes) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.issuer = issuer;
        this.expirationMinutes = expirationMinutes;
    }

    /**
     * Generates a signed JWT with subject set to the provided username.
     *
     * @param username unique username (principal)
     * @return compact JWT string
     */
    public String generate(String username) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(expirationMinutes * 60);
        return Jwts.builder()
                .issuer(issuer)
                .subject(username)
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .signWith(key)
                .compact();
    }

    /**
     * Validates the token signature, issuer and expiry; returns the username.
     *
     * @param token compact JWT without the "Bearer " prefix
     * @return subject (username)
     */
    public String validateAndGetUsername(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .requireIssuer(issuer)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }
}

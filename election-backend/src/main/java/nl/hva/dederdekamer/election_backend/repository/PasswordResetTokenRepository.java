package nl.hva.dederdekamer.election_backend.repository;

import nl.hva.dederdekamer.election_backend.entities.PasswordResetToken;
import nl.hva.dederdekamer.election_backend.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Repository for managing password reset tokens
 */
@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    /**
     * Find a password reset token by its token string
     */
    Optional<PasswordResetToken> findByToken(String token);

    /**
     * Find all tokens for a specific user
     */
    Optional<PasswordResetToken> findByUser(UserEntity user);

    /**
     * Delete all expired tokens (for cleanup)
     */
    void deleteByExpiryDateBefore(LocalDateTime date);

    /**
     * Delete all tokens for a user (when password is reset successfully)
     */
    void deleteByUser(UserEntity user);
}

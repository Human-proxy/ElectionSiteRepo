package nl.hva.dederdekamer.election_backend.repository;

import nl.hva.dederdekamer.election_backend.entities.UserEntity;

import java.util.List;
import java.util.Optional;

/**
 * Minimal user repository.
 *
 * This interface defines the user persistence operations required by the
 * authentication service. The implementation uses JPA's {@code EntityManager}
 * directly
 * Design notes
 *   Lookups are case-sensitive in SQL but i store lowercased username/email in the DB,
 *       so the service should normalize inputs before calling these methods.
 *   {@link #save(UserEntity)} handles both insert (no id) and update (has id).
 *   {@link #existsByUsername(String)} / {@link #existsByEmail(String)} avoid loading full entities.
 * 
 */
public interface UserRepository {

    /**
     * Finds a user by primary key.
     *
     * @param id database id
     * @return optional user
     */
    Optional<UserEntity> findById(Long id);

    /**
     * Finds a user by unique username (stored lowercased).
     *
     * @param username lowercased username
     * @return optional user
     */
    Optional<UserEntity> findByUsername(String username);

    /**
     * Finds a user by unique email (stored lowercased).
     *
     * @param email lowercased email
     * @return optional user
     */
    Optional<UserEntity> findByEmail(String email);

    /**
     * Returns all users (for admin/reporting use cases).
     *
     * @return immutable list of users
     */
    List<UserEntity> findAll();

    /**
     * Persists a new user or updates an existing one.
     * @param user managed or detached entity
     * @return the managed entity after INSERT/UPDATE
     */
    UserEntity save(UserEntity user);

    /**
     * Deletes a user. Accepts either a managed instance or a detached with id.
     *
     * @param user user to remove
     */
    void delete(UserEntity user);

    /**
     * Efficient existence check by username.
     *
     * @param username lowercased username
     * @return true if exists, false otherwise
     */
    boolean existsByUsername(String username);

    /**
     * Efficient existence check by email.
     *
     * @param email lowercased email
     * @return true if exists, false otherwise
     */
    boolean existsByEmail(String email);

    /**
     * Finds users that were soft-deleted before a given date (for cleanup).
     *
     * @param date the cutoff date
     * @return list of users deleted before this date
     */
    List<UserEntity> findByDeletedAtBefore(java.time.LocalDateTime date);
}

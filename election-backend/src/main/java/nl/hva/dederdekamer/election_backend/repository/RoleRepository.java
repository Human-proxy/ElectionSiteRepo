package nl.hva.dederdekamer.election_backend.repository;

import nl.hva.dederdekamer.election_backend.entities.RoleEntity;
import nl.hva.dederdekamer.election_backend.model.RoleName;

import java.util.List;
import java.util.Optional;

/**
 * Minimal role repository.
 * Only the operations auth layer needs are exposed.
 */
public interface RoleRepository {

    /**
     * Finds a role by id.
     */
    Optional<RoleEntity> findById(Long id);

    /**
     * Finds a role by enum name (unique).
     */
    Optional<RoleEntity> findByName(RoleName name);

    /**
     * Returns all roles (admin/reporting).
     */
    List<RoleEntity> findAll();

    /**
     * Persists a new role or updates an existing one.
     */
    RoleEntity save(RoleEntity role);
}

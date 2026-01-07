package nl.hva.dederdekamer.election_backend.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import nl.hva.dederdekamer.election_backend.entities.UserEntity;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * JPA implementation of {@link UserRepository} using {@link EntityManager} directly.
 *
 */
@Repository
@Transactional(readOnly = true)
public class UserRepositoryImpl implements UserRepository {

    /** Injected JPA context; Spring wires this to the configured persistence context. */
    @PersistenceContext
    private EntityManager em;

    @Override
    public Optional<UserEntity> findById(Long id) {
        return Optional.ofNullable(em.find(UserEntity.class, id));
    }

    @Override
    public Optional<UserEntity> findByUsername(String username) {
        // JPQL query against the entity (not table) using the field name.
        TypedQuery<UserEntity> q = em.createQuery(
            "SELECT u FROM UserEntity u WHERE u.username = :username", UserEntity.class);
        q.setParameter("username", username);
        List<UserEntity> list = q.getResultList();
        return list.isEmpty() ? Optional.empty() : Optional.of(list.getFirst());
    }

    @Override
    public Optional<UserEntity> findByEmail(String email) {
        TypedQuery<UserEntity> q = em.createQuery(
            "SELECT u FROM UserEntity u WHERE u.email = :email", UserEntity.class);
        q.setParameter("email", email);
        List<UserEntity> list = q.getResultList();
        return list.isEmpty() ? Optional.empty() : Optional.of(list.getFirst());
    }

    @Override
    public List<UserEntity> findAll() {
        return em.createQuery("SELECT u FROM UserEntity u ORDER BY u.id", UserEntity.class)
                 .getResultList();
    }

    @Override
    @Transactional // read-write override for INSERT/UPDATE
    public UserEntity save(UserEntity user) {
        if (user.getId() == null) {
            // New entity: persist performs INSERT; the entity becomes managed and gets its id.
            em.persist(user);
            return user;
        } else {
            // Detached entity with id: merge performs UPDATE; returns the managed instance.
            return em.merge(user);
        }
    }

    @Override
    @Transactional // read-write override for DELETE
    public void delete(UserEntity user) {
        if (user.getId() == null) {
            // If there's no id, try removing the instance directly (if managed).
            em.remove(user);
            return;
        }
        // Ensure we remove a managed instance; find will return managed or null.
        UserEntity managed = em.find(UserEntity.class, user.getId());
        if (managed != null) {
            em.remove(managed);
        }
    }

    @Override
    public boolean existsByUsername(String username) {
        Long count = em.createQuery(
            "SELECT COUNT(u) FROM UserEntity u WHERE u.username = :username", Long.class)
            .setParameter("username", username)
            .getSingleResult();
        return count != 0;
    }

    @Override
    public boolean existsByEmail(String email) {
        Long count = em.createQuery(
            "SELECT COUNT(u) FROM UserEntity u WHERE u.email = :email", Long.class)
            .setParameter("email", email)
            .getSingleResult();
        return count != 0;
    }

    @Override
    public List<UserEntity> findByDeletedAtBefore(java.time.LocalDateTime date) {
        TypedQuery<UserEntity> q = em.createQuery(
            "SELECT u FROM UserEntity u WHERE u.deletedAt IS NOT NULL AND u.deletedAt < :date", UserEntity.class);
        q.setParameter("date", date);
        return q.getResultList();
    }
}

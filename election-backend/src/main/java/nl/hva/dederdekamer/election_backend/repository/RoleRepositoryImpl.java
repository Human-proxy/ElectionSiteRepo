package nl.hva.dederdekamer.election_backend.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import nl.hva.dederdekamer.election_backend.entities.RoleEntity;
import nl.hva.dederdekamer.election_backend.model.RoleName;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * JPA implementation of {@link RoleRepository} using {@link EntityManager}.
 */
@Repository
@Transactional(readOnly = true)
public class RoleRepositoryImpl implements RoleRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Optional<RoleEntity> findById(Long id) {
        return Optional.ofNullable(em.find(RoleEntity.class, id));
    }

    @Override
    public Optional<RoleEntity> findByName(RoleName name) {
        TypedQuery<RoleEntity> q = em.createQuery(
            "SELECT r FROM RoleEntity r WHERE r.name = :name", RoleEntity.class);
        q.setParameter("name", name);
        List<RoleEntity> list = q.getResultList();
        return list.isEmpty() ? Optional.empty() : Optional.of(list.getFirst());
    }

    @Override
    public List<RoleEntity> findAll() {
        return em.createQuery("SELECT r FROM RoleEntity r ORDER BY r.id", RoleEntity.class)
                 .getResultList();
    }

    @Override
    @Transactional
    public RoleEntity save(RoleEntity role) {
        if (role.getId() == null) {
            em.persist(role);
            return role;
        } else {
            return em.merge(role);
        }
    }
}

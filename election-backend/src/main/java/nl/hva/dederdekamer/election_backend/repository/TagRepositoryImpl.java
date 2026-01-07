package nl.hva.dederdekamer.election_backend.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import nl.hva.dederdekamer.election_backend.entities.TagEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TagRepositoryImpl implements TagRepository{
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<TagEntity> findAllTags() {
        return entityManager.createQuery("SELECT t FROM TagEntity t ORDER BY t.tagName ASC", TagEntity.class)
                .getResultList();
    }
    @Override
    public List<TagEntity> findTagsById(List<Integer> ids) {
        return entityManager.createQuery(
                        "SELECT t FROM TagEntity t WHERE t.id IN :ids",
                        TagEntity.class
                )
                .setParameter("ids", ids)
                .getResultList();
    }
}

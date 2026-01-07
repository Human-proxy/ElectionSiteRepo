package nl.hva.dederdekamer.election_backend.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import nl.hva.dederdekamer.election_backend.entities.Post;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class PostRepositoryImpl implements PostRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Post> findById(int id) {
        return Optional.ofNullable(entityManager.find(Post.class, id));
    }

    @Override
    public Page<Post> findAll(Pageable pageable) {
        List<Post> posts = entityManager.createQuery("SELECT p FROM Post p ORDER BY p.created DESC", Post.class)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        Long total = entityManager.createQuery(
                        "SELECT COUNT(p) FROM Post p", Long.class)
                .getSingleResult();
        return new PageImpl<>(posts, pageable, total);
    }

    @Override
    public Post save(Post post) {
        Post managed = entityManager.merge(post);
        entityManager.flush();
        return managed;
    }

    @Override
    public Page<Post> findByTags(String name, Pageable pageable) {
        List<Post> posts = entityManager.createQuery(
                        "SELECT DISTINCT p FROM Post p JOIN p.tags t " +
                                "WHERE LOWER(t.tagName) LIKE LOWER(CONCAT('%', :tag, '%')) " +
                                "ORDER BY p.created DESC",
                        Post.class
                )
                .setParameter("tag", name)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        Long total = entityManager.createQuery(
                        "SELECT COUNT(DISTINCT p) FROM Post p JOIN p.tags t " +
                                "WHERE LOWER(t.tagName) LIKE LOWER(CONCAT('%', :tag, '%'))",
                        Long.class
                )
                .setParameter("tag", name)
                .getSingleResult();

        return new PageImpl<>(posts, pageable, total);

    }

    @Override
    public void deleteById(int id) {
        Post found = entityManager.find(Post.class, id);
        if (found != null) {
            entityManager.remove(found);
            entityManager.flush();
        }
    }
}

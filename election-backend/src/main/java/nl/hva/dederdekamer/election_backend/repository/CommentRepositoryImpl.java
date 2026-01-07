package nl.hva.dederdekamer.election_backend.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import nl.hva.dederdekamer.election_backend.entities.Comment;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CommentRepositoryImpl implements CommentRepository {

    @PersistenceContext
    private EntityManager entityManager;


    @Override
    public Optional<Comment> findById(Integer id) {
        return Optional.ofNullable(entityManager.find(Comment.class, id));
    }

    @Override
    public List<Comment> findAll() {
        return entityManager.createQuery("SELECT p FROM Comment p ORDER BY p.created DESC", Comment.class)
                .getResultList();
    }

    @Override
    public List<Comment> findByPostId(Integer postId) {
        return entityManager.createQuery(
                        "SELECT c FROM Comment c WHERE c.post.id = :postId ORDER BY c.created DESC",
                        Comment.class)
                .setParameter("postId", postId)
                .getResultList();
    }

    @Override
    public long countByPostId(Integer postId) {
        return entityManager.createQuery(
                "SELECT COUNT(c) FROM Comment c WHERE c.post.id = :postId", Long.class)
                .setParameter("postId", postId)
                .getSingleResult();
    }

    @Override
    public Comment save(Comment comment) {
        entityManager.persist(comment);
        return comment;
    }

    @Override
    public void deleteById(Integer id) {
        entityManager.remove(entityManager.find(Comment.class, id));
    }
}

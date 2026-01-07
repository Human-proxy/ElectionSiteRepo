package nl.hva.dederdekamer.election_backend.repository;

import nl.hva.dederdekamer.election_backend.entities.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentRepository {
    Optional<Comment> findById(Integer id);
    List<Comment> findAll();
    List<Comment> findByPostId(Integer postId);
    long countByPostId(Integer postId);
    Comment save(Comment comment);
    void deleteById(Integer id);
}

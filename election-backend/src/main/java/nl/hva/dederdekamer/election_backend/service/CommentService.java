package nl.hva.dederdekamer.election_backend.service;

import jakarta.validation.constraints.NotNull;
import nl.hva.dederdekamer.election_backend.entities.Comment;


import java.util.List;
import java.util.Optional;

public interface CommentService {
    Optional<Comment> findById(@NotNull Integer id);
    Comment save(Comment comment);
    List<Comment> findAll();
    void deleteById(@NotNull Integer id);
    long countByPostId(Integer postId);
    List<Comment> findByPostId(Integer postId);
}

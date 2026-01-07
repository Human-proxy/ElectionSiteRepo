package nl.hva.dederdekamer.election_backend.service;

import jakarta.validation.constraints.NotNull;
import nl.hva.dederdekamer.election_backend.entities.Comment;
import nl.hva.dederdekamer.election_backend.repository.CommentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
@Service
public class CommentServiceImpl implements CommentService {
    private final CommentRepository comments;

    public CommentServiceImpl(CommentRepository comments) {

        this.comments = comments;
    }

    @Transactional
    public Optional<Comment> findById(@NotNull Integer id) {
        return comments.findById(id);
    }

    @Transactional
    public Comment save(Comment comment) {
        return comments.save(comment);
    }

    @Transactional
    public List<Comment> findAll() {
        return comments.findAll();
    }

    @Transactional
    public void deleteById(@NotNull Integer id) {
        comments.deleteById(id);
    }

    @Transactional
    public long countByPostId(Integer postId) {
        return comments.countByPostId(postId);
    }

    @Transactional
    public List<Comment> findByPostId(Integer postId) {
        return comments.findByPostId(postId);
    }
}

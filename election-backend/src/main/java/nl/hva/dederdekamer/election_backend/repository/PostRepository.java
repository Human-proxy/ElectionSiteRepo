package nl.hva.dederdekamer.election_backend.repository;

import nl.hva.dederdekamer.election_backend.entities.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface PostRepository {

    Optional<Post> findById(int id);
    Page<Post> findAll(Pageable pageable);
    Post save(Post post);
    Page<Post> findByTags(String tagName, Pageable pageable);
    void deleteById(int id);

}

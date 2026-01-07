package nl.hva.dederdekamer.election_backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import nl.hva.dederdekamer.election_backend.entities.Post;
import nl.hva.dederdekamer.election_backend.entities.TagEntity;
import nl.hva.dederdekamer.election_backend.entities.UserEntity;
import nl.hva.dederdekamer.election_backend.repository.TagRepository;
import nl.hva.dederdekamer.election_backend.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import nl.hva.dederdekamer.election_backend.repository.PostRepository;
import nl.hva.dederdekamer.election_backend.security.CurrentUser;

import java.util.List;
import java.util.Optional;
/**
 * All CRUD operations for the Post entity are handled here.
 * Endpoints for creating, retrieving and deleting posts.
 */
@Tag(name = "post controller", description = "Allows CRUD operations on the post entity")
@RestController
@RequestMapping("/api/v1/posts")
public class PostController {

    private final PostRepository repository;
    private final UserRepository userRepository;
    private final TagRepository tagRepository;

    public PostController(PostRepository repository, UserRepository userRepository, TagRepository tagRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.tagRepository = tagRepository;
    }
    /**
     * Creates a new post
     *
     * @param post the Post object containing title, content and optional tags
     * @param currentUser the authenticated user who is creating the post
     * @return the saved Post entity
     * @throws RuntimeException if the user is not authenticated or missing in the database
     */
    @Operation(summary = "creates a post")
    @PostMapping
    public Post createPost(@Valid @RequestBody Post post, @CurrentUser UserEntity currentUser) {
        if (currentUser == null) {
            throw new RuntimeException("You must be logged in to post.");
        }
        var author = userRepository.findByUsername(currentUser.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        post.setAuthor(author);

        if (post.getTags() != null && !post.getTags().isEmpty()) {
            List<Integer> tagIds = post.getTags()
                    .stream()
                    .map(TagEntity::getId)
                    .toList();

            List<TagEntity> tagEntities = tagRepository.findTagsById(tagIds);
            post.setTags(tagEntities);

        }
        return repository.save(post);
    }
    /**
     * Creates a page of posts
     *
     * @param size the size of the page
     * @param page the number of the page
     * @return a list of posts with pagination
     */
    @Operation(summary = "Retrieves posts with pagination")
    @GetMapping
    public Page<Post> getAllPosts(@RequestParam(value = "page", defaultValue = "0") @Parameter (description = "Page number (0 = first page)") int page,
                                  @RequestParam(value = "size", defaultValue = "10") @Parameter (description = "Number of posts per page") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "created"));
        return repository.findAll(pageable);
    }
    /**
     * Finds post by id
     *
     * @param id the id of the post
     * @return post
     */
    @Operation(summary = "finds a post by its id")
    @GetMapping("/{id}")
    public Optional<Post> findPostById(@PathVariable @Parameter (description = "Id of the post") Integer id) {
        return repository.findById(id);
    }
    /**
     * Creates a page of posts related to a tag
     *
     * @param size the size of the page
     * @param page the number of the page
     * @return a list of posts with pagination based on tags
     */
    @Operation(summary = "Retrieves a posts with pagination matching a tag")
    @GetMapping("/search")
    public Page<Post> getPostsByTag(
            @RequestParam("tag") String tag,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "created"));
        return repository.findByTags(tag, pageable);
    }
    /**
     * Creates a page of posts
     *
     * @param id the id of the post
     * @return HTTP 204 No Content if successful
     */
    @Operation(summary = "Deletes a post")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable @Parameter (description = "Id of the post") int id) {
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}

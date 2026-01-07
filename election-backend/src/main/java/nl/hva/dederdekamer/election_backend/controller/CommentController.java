package nl.hva.dederdekamer.election_backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import nl.hva.dederdekamer.election_backend.entities.Comment;
import nl.hva.dederdekamer.election_backend.entities.UserEntity;
import nl.hva.dederdekamer.election_backend.repository.PostRepository;
import nl.hva.dederdekamer.election_backend.repository.UserRepository;
import nl.hva.dederdekamer.election_backend.security.CurrentUser;
import nl.hva.dederdekamer.election_backend.service.CommentServiceImpl;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * All CRUD operations for the Comment entity are handled here.
 * Endpoints for creating, retrieving, deleting, and counting comments.
 */

@Tag(name = "Comment controller", description = "Allows CRUD operations on the comment entity")
@RestController
@RequestMapping("/api/v1/comments")
public class CommentController {

private final CommentServiceImpl commentServiceImpl;
private final UserRepository userRepository;
private final PostRepository postRepository;

public CommentController(CommentServiceImpl commentService, UserRepository userRepository, PostRepository postRepository) {this.commentServiceImpl = commentService;
    this.userRepository = userRepository;
    this.postRepository = postRepository;
}
    public record CommentDTO(
            Integer id,
            String content,
            String created,
            AuthorDTO author
    ) {}
    public record PostCommentDTO(
            PostIdDTO post,
            String content
    ){}
    public record AuthorDTO(
            Integer id,
            String username,
            String profileImageUrl
    ) {}
    public record PostIdDTO(
            Integer id
    ) {}

    /**
     * Retrieves all comments in the system.
     *
     * @return a list of CommentDTO objects
     */
    @Operation(summary = "returns a list of comments")
    @GetMapping
   public List<CommentDTO> getComments() {
    return commentServiceImpl.findAll()
            .stream()
            .map(c -> new CommentDTO(
                    c.getId(),
                    c.getContent(),
                    c.getCreatedLocal(),
                    new AuthorDTO(
                            c.getAuthor().getId().intValue(),
                            c.getAuthor().getUsername(),
                            c.getAuthor().getProfileImageUrl()
                    )
            ))
            .toList();
   }
    /**
     * Retrieves a comment by its id.
     *
     * @param id the id of the comment thats retrieved
     * @return a CommentDTO containing the comment data
     * @throws ResponseStatusException if the comment is not found
     */
   @Operation(summary = "returns a comment by its id")
   @GetMapping("/{id}")
   public CommentDTO getCommentById(@PathVariable("id") @Parameter (description = "the value of the id belonging to the comment") @NotNull Integer id) {
    return commentServiceImpl.findById(id)
            .map(c -> new CommentDTO(
                    c.getId(),
                    c.getContent(),
                    c.getCreatedLocal(),
                    new AuthorDTO(
                            c.getAuthor().getId().intValue(),
                            c.getAuthor().getUsername(),
                            c.getAuthor().getProfileImageUrl()
                    ))
            ).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));


   }
    /**
     * Creates and saves a new comment on a post.
     *
     * @param user the user creating the comment
     * @param postCommentDTO contains the post ID and comment details
     * @return a CommentDTO containing the saved comment data
     * @throws ResponseStatusException if the user or post is not found
     */
    @Operation(summary = "saves a comment to a post")
   @PostMapping
   public CommentDTO save(@RequestBody PostCommentDTO postCommentDTO, @CurrentUser UserEntity user)
   {
       if (user.getId() == null) {
           throw new RuntimeException("You must be logged in to comment.");
       }
       if (postCommentDTO.post() == null || postCommentDTO.post().id() == null) {
           throw new IllegalArgumentException("post.id is required");
       }


       var author = userRepository.findById(user.getId())
               .orElseThrow(() -> new RuntimeException("User not found"));

       var post = postRepository.findById(postCommentDTO.post().id())
                .orElseThrow(() -> new RuntimeException("Post not found"));
       Comment comment = new Comment();

       comment.setAuthor(author);
       comment.setPost(post);
       comment.setContent(postCommentDTO.content());

       Comment saved = commentServiceImpl.save(comment);

       return new CommentDTO(
               saved.getId(),
               saved.getContent(),
               saved.getCreatedLocal(),
               new AuthorDTO(
                       saved.getAuthor().getId().intValue(),
                       saved.getAuthor().getUsername(),
                       saved.getAuthor().getProfileImageUrl()
               )
       );
   }
    /**
     * deletes a comment.
     *
     * @param id the id of the comment
     */
    @Operation(summary = "Deletes a comment by id")
   @DeleteMapping("/{id}")
   public void deleteById(@PathVariable @Parameter(description = "ID of the comment that wil be deleted") @NotNull Integer id) {
    commentServiceImpl.deleteById(id);
   }
    /**
     * Counts the amount of comments on a post.
     *
     * @param postId the id of the post
     * @return a number representing the amount of comments
     */
    @Operation(summary = "Gets the amount of comments connected on a post")
   @GetMapping("/count")
   public long countByPostId(@RequestParam @Parameter (description = "Id of the post") Integer postId) {
    return commentServiceImpl.countByPostId(postId);
   }
    /**
     * Finds comments based on id of a post.
     *
     * @param postId the ID of the post whose comments are returned
     * @return a list of CommentDTOs containing the saved comment data
     */
    @Operation(summary = "returns a list of comments belonging to a post")
   @GetMapping("/find")
    public List<CommentDTO> findByPostId(@RequestParam @Parameter(description = "Id of the post") Integer postId) {
       return commentServiceImpl.findByPostId(postId)
               .stream()
               .map(c -> new CommentDTO(
                       c.getId(),
                       c.getContent(),
                       c.getCreatedLocal(),
                       new AuthorDTO(
                               c.getAuthor().getId().intValue(),
                               c.getAuthor().getUsername(),
                               c.getAuthor().getProfileImageUrl()
                       )
               ))
               .toList();

    }

}

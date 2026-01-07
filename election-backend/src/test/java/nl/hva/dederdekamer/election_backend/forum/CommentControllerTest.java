package nl.hva.dederdekamer.election_backend.forum;
import nl.hva.dederdekamer.election_backend.controller.CommentController;
import nl.hva.dederdekamer.election_backend.entities.Comment;
import nl.hva.dederdekamer.election_backend.entities.Post;
import nl.hva.dederdekamer.election_backend.entities.UserEntity;
import nl.hva.dederdekamer.election_backend.repository.CommentRepository;
import nl.hva.dederdekamer.election_backend.repository.PostRepository;
import nl.hva.dederdekamer.election_backend.repository.TagRepository;
import nl.hva.dederdekamer.election_backend.repository.UserRepository;
import nl.hva.dederdekamer.election_backend.service.CommentServiceImpl;
import nl.hva.dederdekamer.election_backend.service.JwtService;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest (CommentController.class)
public class CommentControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CommentRepository commentRepository;

    @MockitoBean
    private PostRepository postRepository;

    @MockitoBean
    private CommentServiceImpl commentServiceImpl;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private TagRepository tagRepository;

    @Test
    void getAllComments_returnsOk() throws Exception {
        Comment comment = Mockito.mock(Comment.class);
        UserEntity author = Mockito.mock(UserEntity.class);

        when(author.getId()).thenReturn(1L);
        when(author.getUsername()).thenReturn("testUserName");
        when(author.getProfileImageUrl()).thenReturn(null);

        when(comment.getId()).thenReturn(10);
        when(comment.getContent()).thenReturn("This is a test comment");
        when(comment.getCreatedLocal()).thenReturn("2025-01-01T12:00");
        when(comment.getAuthor()).thenReturn(author);

        when(commentServiceImpl.findByPostId(1))
                .thenReturn(List.of(comment));


        mockMvc.perform(get("/api/v1/comments/find")
                        .param("postId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].content").value("This is a test comment"))
                .andExpect(jsonPath("$[0].author.username").value("testUserName"));
    }
    @Test
    void createComment_withUser_returns200() throws Exception {

        UserEntity user = Mockito.mock(UserEntity.class);
        Post post = Mockito.mock(Post.class);
        Comment savedComment = Mockito.mock(Comment.class);

        when(user.getUsername()).thenReturn("testuser");
        when(postRepository.findById(1)).thenReturn(Optional.of(post));
        when(user.getId()).thenReturn(1L);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(commentServiceImpl.save(any(Comment.class))).thenReturn(savedComment);
        when(savedComment.getId()).thenReturn(10);
        when(savedComment.getAuthor()).thenReturn(user);
        when(savedComment.getContent()).thenReturn("This is a text");
        when(savedComment.getCreatedLocal()).thenReturn("2025-01-01T12:00");
        when(savedComment.getPost()).thenReturn(post);

        mockMvc.perform(post("/api/v1/comments")
                        .contentType("application/json")
                        .requestAttr("currentUser", user)
                        .content("""
                        {"post": { "id": 1 },"content": "This is a text"}"""))
                .andExpect(status().isOk());
    }

    @Test
    void createComment_withoutUser_returns500() throws Exception {

        UserEntity user = Mockito.mock(UserEntity.class);



        when(user.getId()).thenReturn(null);


        mockMvc.perform(post("/api/v1/comments")
                        .contentType("application/json")
                        .requestAttr("currentUser", user)
                        .content("""
                        {"post": { "id": 1 },"content": "This is a text"}"""))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("You must be logged in to comment."));
    }
}


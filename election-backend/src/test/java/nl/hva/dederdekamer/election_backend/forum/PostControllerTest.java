package nl.hva.dederdekamer.election_backend.forum;

import nl.hva.dederdekamer.election_backend.controller.PostController;
import nl.hva.dederdekamer.election_backend.entities.UserEntity;
import nl.hva.dederdekamer.election_backend.repository.PostRepository;
import nl.hva.dederdekamer.election_backend.repository.TagRepository;
import nl.hva.dederdekamer.election_backend.repository.UserRepository;
import nl.hva.dederdekamer.election_backend.service.JwtService;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import nl.hva.dederdekamer.election_backend.entities.Post;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)

@WebMvcTest (PostController.class)
public class PostControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PostRepository postRepository;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private TagRepository tagRepository;


    @Test
    void getAllPosts_returnsOk() throws Exception {
        Page<Post> emptyPage = new PageImpl<>(List.of());

        when(postRepository.findAll(any(Pageable.class)))
                .thenReturn(emptyPage);

        mockMvc.perform(get("/api/v1/posts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }
    @Test
    void createPost_withUser_returns200() throws Exception {
        UserEntity user = Mockito.mock(UserEntity.class);

        when(user.getUsername()).thenReturn("testuser");

        when(userRepository.findByUsername("testuser"))
                .thenReturn(Optional.of(user));

        when(postRepository.save(any(Post.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(post("/api/v1/posts")
                        .contentType("application/json")
                        .requestAttr("currentUser", user)
                        .content("""
                {"title": "authorized post","content": "This is a text"}"""))
                .andExpect(status().isOk());
    }

    @Test
    void createPost_withoutUser_returns401() throws Exception {

        mockMvc.perform(post("/api/v1/posts")
                        .contentType("application/json")
                        .content("""
                {"title": "Unauthorized post","content": "This is a text"}"""))
                .andExpect(status().isUnauthorized())
          .andExpect(jsonPath("$.message").value("Login required"));
    }

    @Test
    void createPost_withOutTitle_returns400() throws Exception {
        UserEntity user = Mockito.mock(UserEntity.class);

        when(user.getUsername()).thenReturn("testuser");

        when(userRepository.findByUsername("testuser"))
                .thenReturn(Optional.of(user));

        when(postRepository.save(any(Post.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(post("/api/v1/posts")
                        .contentType("application/json")
                        .requestAttr("currentUser", user)
                        .content("""
                {"title": "","content": "This is a text"}"""))
                .andExpect(jsonPath("$.details.title").value("must not be blank"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createPost_withOutContent_returns400() throws Exception {
        UserEntity user = Mockito.mock(UserEntity.class);

        when(user.getUsername()).thenReturn("testuser");

        when(userRepository.findByUsername("testuser"))
                .thenReturn(Optional.of(user));

        when(postRepository.save(any(Post.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(post("/api/v1/posts")
                        .contentType("application/json")
                        .requestAttr("currentUser", user)
                        .content("""
                {"title": "this is a title","content": ""}"""))
                .andExpect(jsonPath("$.details.content").value("must not be blank"))
                .andExpect(status().isBadRequest());
    }
}

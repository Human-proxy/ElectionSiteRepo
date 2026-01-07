package nl.hva.dederdekamer.election_backend.forum;

import jakarta.transaction.Transactional;
import nl.hva.dederdekamer.election_backend.entities.Post;
import nl.hva.dederdekamer.election_backend.repository.PostRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Transactional
class PostIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PostRepository postRepository;

    @Test
    void getPosts_returnsPersistedPost() throws Exception {

        // Arrange
        Post post = new Post();
        post.setTitle("Post Title");
        post.setContent("Post Content");
//hallo
        postRepository.save(post);

        // Act & Assert
        mockMvc.perform(get("/api/v1/posts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[*].title")
                        .value(hasItem("Post Title")));
    }
}


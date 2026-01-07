package nl.hva.dederdekamer.election_backend.forum;

import nl.hva.dederdekamer.election_backend.controller.TagController;
import nl.hva.dederdekamer.election_backend.entities.TagEntity;
import nl.hva.dederdekamer.election_backend.repository.UserRepository;
import nl.hva.dederdekamer.election_backend.service.JwtService;
import nl.hva.dederdekamer.election_backend.service.TagService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
// disables security
@AutoConfigureMockMvc(addFilters = false)

@WebMvcTest(TagController.class)
public class TagControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TagService tagService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserRepository userRepository;

    @Test
    void getTags_returnsListOfTags() throws Exception {
        //Arrange
        TagEntity t = new TagEntity();
        t.setId(1);
        t.setTagName("tag1");

        when(tagService.findAll()).thenReturn(List.of(t));

        //Act and Assert
        mockMvc.perform(get("/api/v1/tag/findTags"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].tagName").value("tag1"));
    
    }
    @Test
    void returnEmptyList_ReturnsEmptyTags() throws Exception {
        //Arrange
        when(tagService.findAll()).thenReturn(List.of());

        //Act and Assert
        mockMvc.perform(get("/api/v1/tag/findTags"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    }





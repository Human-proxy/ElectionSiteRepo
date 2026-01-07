package nl.hva.dederdekamer.election_backend.Quiz;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.hva.dederdekamer.election_backend.controller.QuizController;
import nl.hva.dederdekamer.election_backend.dto.QuizOptionDTO;
import nl.hva.dederdekamer.election_backend.dto.QuizQuestionDTO;
import nl.hva.dederdekamer.election_backend.dto.QuizRequestDTO;
import nl.hva.dederdekamer.election_backend.dto.QuizResponseDTO;
import nl.hva.dederdekamer.election_backend.exception.ResourceNotFoundException;
import nl.hva.dederdekamer.election_backend.repository.UserRepository;
import nl.hva.dederdekamer.election_backend.service.JwtService;
import nl.hva.dederdekamer.election_backend.service.QuizService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit tests for the {@link QuizController}.
 *
 * Uses {@link WebMvcTest} to isolate the controller layer.
 * Mocks the {@link QuizService} for business logic and {@link JwtService}/{@link UserRepository}
 * to satisfy the security filter requirements during test startup.
 *
 */
@ActiveProfiles("test")
@WebMvcTest(QuizController.class)
class QuizControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // Mock dependencies required by JwtRequestFilter to prevent startup errors
    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserRepository userRepository;

    // Mock the service logic
    @MockitoBean
    private QuizService quizService;

    /**
     * Test GET /api/quiz/questions
     *
     * Happy Flow: The service returns a list of configured questions with options.
     * Expects HTTP 200 OK and a JSON list.
     *
     */
    @Test
    void testGetQuizQuestionsEndpoint() throws Exception {
        System.out.println(">>> TEST START: Get Quiz Questions");
        // Arrange - Prepare test data
        QuizOptionDTO option1 = new QuizOptionDTO("2023", "2023");
        QuizQuestionDTO q1 = new QuizQuestionDTO("q1", "In welk jaar wilt u de quiz doen?", "SELECT", List.of(option1));
        List<QuizQuestionDTO> mockQuestions = List.of(q1);

        // Tell the mock service what to return
        when(quizService.getQuestions("2023")).thenReturn(mockQuestions);

        // Act & Assert - Make the request and check expectations
        mockMvc.perform(get("/api/quiz/questions")
                        .param("year", "2023")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value("q1"))
                .andExpect(jsonPath("$[0].text").value("In welk jaar wilt u de quiz doen?"))
                .andExpect(jsonPath("$[0].type").value("SELECT"));

        // Verify the service method was called exactly once
        verify(quizService, times(1)).getQuestions("2023");
        System.out.println(">>> TEST END: Get Quiz Questions (Success)");
    }

    /**
     * Test POST /api/quiz/result
     * 
     * Happy Flow: The user submits valid answers, and the service calculates the result.
     * Expects HTTP 200 OK and a valid result DTO.
     * 
     */
    @Test
    void testGetQuizResultEndpoint() throws Exception {
        System.out.println(">>> TEST START: Get Quiz Result (Happy Flow)");
        // Arrange - Prepare request and expected response
        QuizRequestDTO request = new QuizRequestDTO();
        request.setYear("2023");
        request.setPartyId(29L);
        request.setRegion("Amsterdam");
        request.setDataType("VOTES");

        QuizResponseDTO response = new QuizResponseDTO(
                "VVD",
                "Amsterdam",
                "Aantal Stemmen",
                "21.964",
                "In 2023 kreeg VVD 21.964 stemmen."
        );

        when(quizService.processQuiz(any(QuizRequestDTO.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/api/quiz/result")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                        .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.partyName").value("VVD"))
                .andExpect(jsonPath("$.regionName").value("Amsterdam"))
                .andExpect(jsonPath("$.formattedValue").value("21.964"));

        // Verify the service was called
        verify(quizService, times(1)).processQuiz(any(QuizRequestDTO.class));
        System.out.println(">>> TEST END: Get Quiz Result (Success)");
    }

    /**
     * Test POST /api/quiz/result with invalid data
     * 
     * Unhappy Flow: The requested data (e.g., specific municipality results) does not exist.
     * The service throws {@link ResourceNotFoundException}, expecting HTTP 404 Not Found.
     * 
     */
    @Test
    void testGetQuizResult_NotFound() throws Exception {
        System.out.println(">>> TEST START: Get Quiz Result (Not Found)");
        // Arrange - Prepare request that will fail
        QuizRequestDTO request = new QuizRequestDTO();
        request.setYear("2017");
        request.setRegion("Urk");

        // Mock service to throw exception
        when(quizService.processQuiz(any(QuizRequestDTO.class)))
                .thenThrow(new ResourceNotFoundException("Resultaten", "Urk"));

        // Act & Assert
        mockMvc.perform(post("/api/quiz/result")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                        .andDo(print())
                .andExpect(status().isNotFound()); // 404

        // Verify service call
        verify(quizService, times(1)).processQuiz(any(QuizRequestDTO.class));
        System.out.println(">>> TEST END: Get Quiz Result (Not Found)");
    }
}
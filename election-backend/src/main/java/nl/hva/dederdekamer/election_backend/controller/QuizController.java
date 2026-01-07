package nl.hva.dederdekamer.election_backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import nl.hva.dederdekamer.election_backend.dto.QuizQuestionDTO;
import nl.hva.dederdekamer.election_backend.dto.QuizRequestDTO;
import nl.hva.dederdekamer.election_backend.dto.QuizResponseDTO;
import nl.hva.dederdekamer.election_backend.service.QuizService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpHeaders;

import java.util.List;

/**
 * Controller to handle Election Quiz interactions.
 * Provides endpoints for fetching questions and calculating results.
 */
@Tag(name = "Quiz", description = "Endpoints for the election quiz feature")
@RestController
@RequestMapping("api/quiz")
public class QuizController {

    private static final Logger logger = LoggerFactory.getLogger(QuizController.class);
    private final QuizService quizService;

    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }
    /**
     * Get the list of questions.
     * Accepts an optional 'year' parameter to filter Parties and Regions.
     * Example: GET /api/quiz/questions?year=2021
     */
    @Operation(summary = "Get quiz questions", description = "Retrieves the list of quiz questions, optionally filtered by election year")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved quiz questions",
                     content = @Content(schema = @Schema(implementation = QuizQuestionDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid year parameter")
    })
    @GetMapping("/questions")
    public ResponseEntity<List<QuizQuestionDTO>> getQuizQuestions(
            @RequestParam(name = "year", required = false, defaultValue = "2023") String year) {
        
        logger.info("Fetching quiz questions configuration for year: {}", year);
        List<QuizQuestionDTO> questions = quizService.getQuestions(year);
        return ResponseEntity.ok(questions);
    }
    /**
     * Calculate the result based on user answers.
     *
     * @param request The user's answers (Year, Party, Region, Type)
     * @return The filtered result data
     */
    @Operation(summary = "Calculate quiz result", description = "Processes user quiz answers and returns filtered election results")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully calculated quiz results",
                     content = @Content(schema = @Schema(implementation = QuizResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid quiz request data")
    })
    @PostMapping("/result")
    public ResponseEntity<QuizResponseDTO> getQuizResult(@RequestBody QuizRequestDTO request) {
        logger.info("Quiz result request: Year={}, Party={}, Region={}, Type={}",
                request.getYear(), request.getPartyId(), request.getRegion(), request.getDataType());

        QuizResponseDTO response = quizService.processQuiz(request);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Export the filtered quiz result as a CSV file.
     *
     * @param request The user's answers.
     * @return A CSV file download.
     */
    @Operation(summary = "Export quiz results", description = "Generates and downloads quiz results as a CSV file")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully generated CSV file"),
        @ApiResponse(responseCode = "400", description = "Invalid export request data")
    })
    @PostMapping("/export")
    public ResponseEntity<byte[]> exportQuizResult(@RequestBody QuizRequestDTO request) {
        logger.info("Received export request for Year: {}", request.getYear());

        byte[] csvData = quizService.generateExportCsv(request);

        String filename = "verkiezings_resultaat_" + request.getYear() + ".csv";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .header(HttpHeaders.CONTENT_TYPE, "text/csv; charset=UTF-8")
                .body(csvData);
    }
}
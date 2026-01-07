package nl.hva.dederdekamer.election_backend.Quiz;

import nl.hva.dederdekamer.election_backend.XMLParser.model.MunicipalityResult;
import nl.hva.dederdekamer.election_backend.XMLParser.model.Party;
import nl.hva.dederdekamer.election_backend.XMLParser.model.PartyResult;
import nl.hva.dederdekamer.election_backend.dto.QuizQuestionDTO;
import nl.hva.dederdekamer.election_backend.dto.QuizRequestDTO;
import nl.hva.dederdekamer.election_backend.dto.QuizResponseDTO;
import nl.hva.dederdekamer.election_backend.exception.ResourceNotFoundException;
import nl.hva.dederdekamer.election_backend.repository.MunicipalityResultRepository;
import nl.hva.dederdekamer.election_backend.repository.PartyRepository;
import nl.hva.dederdekamer.election_backend.repository.PartyResultRepository;
import nl.hva.dederdekamer.election_backend.service.MunicipalityService;
import nl.hva.dederdekamer.election_backend.service.PartyService;
import nl.hva.dederdekamer.election_backend.service.QuizService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the {@link QuizService} class.
 *
 * This test class verifies the business logic for:
 *
 * Generating dynamic quiz questions based on the selected year.
 * Processing user answers to return the correct Seat, Vote, or Percentage data.
 * Handling specific edge cases like Party ID matching and Merged Parties (e.g., GroenLinks/PvdA).
 *
 */
class QuizServiceTest {

    private PartyResultRepository partyResultRepo;
    private MunicipalityResultRepository municipalityResultRepo;
    private PartyService partyService;
    private MunicipalityService municipalityService;
    private PartyRepository partyRepo;
    private QuizService quizService;

    /**
     * Sets up the mocks and initializes the QuizService before each test.
     */
    @BeforeEach
    void beforeEach() {
        // Create mocks for all dependencies
        partyResultRepo = mock(PartyResultRepository.class);
        municipalityResultRepo = mock(MunicipalityResultRepository.class);
        partyService = mock(PartyService.class);
        municipalityService = mock(MunicipalityService.class);
        partyRepo = mock(PartyRepository.class);

        // Inject mocks into the service
        quizService = new QuizService(
            partyResultRepo, 
            municipalityResultRepo, 
            partyService, 
            municipalityService, 
            partyRepo
        );
    }

    /**
     * Tests related to retrieving the quiz configuration structure.
     */
    @Nested
    class GetQuestionsTests {

        /**
         * Test: getQuestions returns the correct structure and dynamic options.
         *
         * Scenario: User requests the quiz structure for the year 2023.
         *
         * Expected:
         *
         * 4 Questions should be returned
         * Q2 (Parties) should contain the mocked party (VVD).
         * Q3 (Regions) should contain the mocked municipality (Amsterdam).
         * The Option Value for parties should be the Database ID (29L), not the list ID.
         * 
         */
        @Test
        void successfullyReturnsQuestionsConfig() {
            System.out.println("\n[TEST] successfullyReturnsQuestionsConfig");
            
            String year = "2023";
            String electionId = "TK2023";

            // Mock Party data
            Party p1 = new Party();
            ReflectionTestUtils.setField(p1, "id", 29L); 
            p1.setName("VVD");
            
            System.out.println("  Mocking PartyService to return: " + p1.getName() + " (ID: 29)");
            when(partyService.getElectedPartiesByElection(electionId)).thenReturn(List.of(p1));

            // Mock Municipality data
            System.out.println("  Mocking MunicipalityService to return: Amsterdam");
            when(municipalityService.getAllMunicipalitiesForQuiz(electionId))
                    .thenReturn(List.of(Map.of("id", "0363", "name", "Amsterdam")));

            // Act
            List<QuizQuestionDTO> questions = quizService.getQuestions(year);

            // Assert
            System.out.println("  Questions generated: " + questions.size());
            questions.forEach(q -> System.out.println("    - Question: " + q.getText()));

            assertEquals(4, questions.size());
            assertEquals("q2", questions.get(1).getId());
            // Ensure we use the Primary Key (29L) as the value, NOT the list index
            assertEquals(29L, questions.get(1).getOptions().get(0).getValue()); 
        }
    }

    /**
     * Tests related to processing the user's quiz answers and calculating results.
     */
    @Nested
    class ProcessQuizTests {

        /**
         * Test: processQuiz returns SEATS correctly using Entity data.
         *
         * Scenario: User asks for the number of SEATS for VVD in 2023.
         *
         * Logic: The service should resolve the party by ID, find the result by name,
         * and crucially, pull the seat count from the {@code Party} entity to handle potential data inconsistencies.
         */
        @Test
        void returnsNationalSeatsFromEntity() {
            System.out.println("\n[TEST] returnsNationalSeatsFromEntity");
            
            // Arrange request
            QuizRequestDTO request = new QuizRequestDTO();
            request.setYear("2023");
            request.setPartyId(29L);
            request.setDataType("SEATS");

            // Mock Party Entity
            Party party = new Party();
            ReflectionTestUtils.setField(party, "id", 29L);
            party.setName("VVD");
            party.setSeats(24); 
            
            System.out.println("  Mocking Party: VVD with 24 seats");
            when(partyRepo.findById(29L)).thenReturn(Optional.of(party));

            // Mock PartyResult (needed for name matching)
            PartyResult pr = new PartyResult();
            pr.setParty(party); 
            when(partyResultRepo.findByElectionId("TK2023")).thenReturn(List.of(pr));

            // Act
            QuizResponseDTO result = quizService.processQuiz(request);

            // Assert
            System.out.println("  Result Narrative: " + result.getNarrative());
            System.out.println("  Result Value: " + result.getFormattedValue());

            assertEquals("VVD", result.getPartyName());
            assertEquals("24 zetels", result.getFormattedValue());
        }

        /**
         * Test: processQuiz returns Regional VOTES correctly.
         *
         * Scenario: User asks for the number of VOTES for VVD in "Amsterdam" in 2023.
         *
         * Expected:> The service finds the correct MunicipalityResult and formats the vote count.
         */
        @Test
        void returnsRegionalVotes() {
            System.out.println("\n[TEST] returnsRegionalVotes");
            
            // Arrange request
            QuizRequestDTO request = new QuizRequestDTO();
            request.setYear("2023");
            request.setPartyId(29L);
            request.setRegion("Amsterdam");
            request.setDataType("VOTES");

            // Mock Party
            Party party = new Party();
            ReflectionTestUtils.setField(party, "id", 29L);
            party.setName("VVD");
            when(partyRepo.findById(29L)).thenReturn(Optional.of(party));

            // Mock Municipality Result
            MunicipalityResult mr = new MunicipalityResult();
            mr.setParty(party);
            mr.setTotalVotes(15000);
            
            System.out.println("  Mocking Municipality Result: 15000 votes for VVD in Amsterdam");
            when(municipalityResultRepo.findByElectionAndMunicipalityName("TK2023", "Amsterdam"))
                    .thenReturn(List.of(mr));

            // Act 
            QuizResponseDTO result = quizService.processQuiz(request);

            // Assert
            System.out.println("  Result Formatted: " + result.getFormattedValue());
            
            assertEquals("VVD", result.getPartyName());
            assertEquals("15.000", result.getFormattedValue());
        }
        
        /**
         * Test: processQuiz handles fuzzy matching for merged parties.
         *
         * Scenario: A user selects "Partij van de Arbeid (P.v.d.A.)" (ID from older years) 
         * but requests data for 2023 where the party is named "GROENLINKS / Partij van de Arbeid (PvdA)".
         *
         * Expected: The service's normalize/fuzzy matching logic should detect the match 
         * and return the result for the merged party.
         */
        @Test
        void handlesFuzzyMatchingForMergers() {
            // Arrange request (using old Party ID)
            QuizRequestDTO request = new QuizRequestDTO();
            request.setYear("2023");
            request.setPartyId(2L); // ID for old PvdA
            request.setDataType("SEATS");

            // 1. Mock Party Lookup (User selected Old Name)
            Party oldParty = new Party();
            oldParty.setName("Partij van de Arbeid (P.v.d.A.)");
            ReflectionTestUtils.setField(oldParty, "id", 2L);
            
            when(partyRepo.findById(2L)).thenReturn(Optional.of(oldParty));

            // 2. Mock Result in DB (New Merged Name)
            Party mergedParty = new Party();
            mergedParty.setName("GROENLINKS / Partij van de Arbeid (PvdA)");
            mergedParty.setSeats(25);
            
            PartyResult pr = new PartyResult();
            pr.setParty(mergedParty);
            
            when(partyResultRepo.findByElectionId("TK2023")).thenReturn(List.of(pr));

            // Act
            QuizResponseDTO result = quizService.processQuiz(request);

            // Assert
            assertEquals("GROENLINKS / Partij van de Arbeid (PvdA)", result.getPartyName());
            assertEquals("25 zetels", result.getFormattedValue());
        }

        /**
         * Test: processQuiz throws exception when Party ID is invalid.
         * 
         * Scenario: The frontend sends a Party ID that does not exist in the database.
         * 
         * Expected: A {@link ResourceNotFoundException} should be thrown.
         */
        @Test
        void throwsExceptionWhenPartyIdInvalid() {
            // Arrange
            QuizRequestDTO request = new QuizRequestDTO();
            request.setPartyId(999L);
            request.setYear("2023");

            when(partyRepo.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(ResourceNotFoundException.class, 
                () -> quizService.processQuiz(request));
        }
        /**
         * Test: generateExportCsv creates a valid CSV byte array.
         * 
         * Scenario: User requests an export for VVD votes in Amsterdam.
         * 
         * Expected: The service returns a byte array containing the correct CSV headers and data values.
         */
        @Test
        void generatesValidCsvContent() {
            System.out.println("\n[TEST] generatesValidCsvContent");

            // Arrange
            QuizRequestDTO request = new QuizRequestDTO();
            request.setYear("2023");
            request.setPartyId(29L);
            request.setRegion("Amsterdam");
            request.setDataType("VOTES");

            // Mock dependencies to return a specific result
            Party party = new Party();
            ReflectionTestUtils.setField(party, "id", 29L);
            party.setName("VVD");
            when(partyRepo.findById(29L)).thenReturn(Optional.of(party));

            MunicipalityResult mr = new MunicipalityResult();
            mr.setParty(party);
            mr.setTotalVotes(1000);
            when(municipalityResultRepo.findByElectionAndMunicipalityName("TK2023", "Amsterdam"))
                    .thenReturn(List.of(mr));

            // Act
            byte[] result = quizService.generateExportCsv(request);
            String csvContent = new String(result);

            System.out.println("  Generated CSV:\n" + csvContent);

            // Assert
            assertNotNull(result);
            assertTrue(result.length > 0);
            // Check Header
            assertTrue(csvContent.contains("Verkiezingsjaar,Partij,Regio"));
            // Check Data Row
            assertTrue(csvContent.contains("2023,VVD,Amsterdam"));
            assertTrue(csvContent.contains("1.000")); // Check formatting
        }
    }
}
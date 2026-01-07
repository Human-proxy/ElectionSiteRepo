package nl.hva.dederdekamer.election_backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import nl.hva.dederdekamer.election_backend.XMLParser.model.Party;
import nl.hva.dederdekamer.election_backend.dto.ConstituencySummaryDTO;
import nl.hva.dederdekamer.election_backend.service.HomeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller dedicated to homepage data endpoints.
 * Keeps homepage-related API surface separated from other website controllers.
 */
@Tag(name = "Homepage", description = "Endpoints for homepage data including parties, elections, and constituency insights")
@RestController
@RequestMapping("api/v1")
public class HomeController {

    private final HomeService homeService;
    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    public HomeController(HomeService homeService) {
        this.homeService = homeService;
    }

    /**
     * Get party data from database for homepage chart.
     * Returns elected parties (seats > 0) directly from the database for a specific election.
     * This endpoint is specifically for the homepage visualization.
     * 
     * @param electionId the election ID to filter parties by (optional, defaults to TK2023)
     */
    @Operation(summary = "Get parties for homepage", description = "Retrieves elected parties with seats > 0 for homepage visualization")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved parties",
                     content = @Content(schema = @Schema(implementation = Party.class)))
    })
    @GetMapping("parties/homepage")
    public List<Party> getPartiesForHomepage(@RequestParam(required = false, defaultValue = "TK2023") String electionId) {
        return homeService.getHomepageParties(electionId);
    }

    /**
     * Get election metadata from database.
     * Returns basic election info (id, name, date) for the homepage.
     */
    @Operation(summary = "Get election metadata", description = "Retrieves basic election information (id, name, date) for the homepage")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved metadata"),
        @ApiResponse(responseCode = "404", description = "Election not found")
    })
    @GetMapping("elections/metadata/{electionId}")
    public ResponseEntity<Map<String, String>> getElectionMetadata(@PathVariable String electionId) {
    Map<String, String> metadata = homeService.getElectionMetadata(electionId);
    if (metadata == null || metadata.isEmpty()) return ResponseEntity.notFound().build();
    return ResponseEntity.ok(metadata);
    }

    /**
     * Get all available elections for the frontend.
     */
    @Operation(summary = "Get available elections", description = "Returns list of all available election IDs")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved elections")
    })
    @GetMapping("elections")
    public String[] getAvailableElections() {
        // Later we can make this dynamic
        return new String[]{"TK2023"};
    }

    /**
     * Get top-4 constituencies for the dashboard/insights on the homepage.
     * Returns constituency summaries ordered by total votes cast (descending).
     * Includes winning party information and vote statistics.
     * 
     * @param electionId the election ID to query (defaults to TK2023)
     * @return list of constituency summary DTOs
     */
    @Operation(summary = "Get top 4 constituencies", description = "Retrieves top 4 constituencies by total votes with winning party info")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved constituencies",
                     content = @Content(schema = @Schema(implementation = ConstituencySummaryDTO.class))),
        @ApiResponse(responseCode = "204", description = "No constituencies found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("election/top4")
    public ResponseEntity<List<ConstituencySummaryDTO>> getTop4Constituencies(
            @RequestParam(required = false, defaultValue = "TK2023") String electionId) {
        try {
            List<ConstituencySummaryDTO> result = homeService.getTop4Constituencies(electionId);
            if (result.isEmpty()) {
                logger.warn("No top-4 constituencies found for election: {}", electionId);
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Error fetching top-4 constituencies for election: {}", electionId, e);
            return ResponseEntity.internalServerError().build();
        }
    }
}

package nl.hva.dederdekamer.election_backend.controller;

import nl.hva.dederdekamer.election_backend.XMLParser.model.Party;
import nl.hva.dederdekamer.election_backend.dto.ElectionResultsDTO;
import nl.hva.dederdekamer.election_backend.service.PartyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for party-related endpoints
 * Handles requests for party data and election results
 */
@RestController
@RequestMapping("api")
public class PartyController {

    private static final Logger logger = LoggerFactory.getLogger(PartyController.class);
    private final PartyService partyService;

    public PartyController(PartyService partyService) {
        this.partyService = partyService;
    }

    /**
     * Get party data for the party page chart
     * Returns all parties with their votes and seats
     * 
     * @return List of all parties
     * @throws ResourceNotFoundException if no parties found
     */
    @GetMapping("party-data")
    public ResponseEntity<List<Party>> getPartiesForPartypage() {
        logger.info("Received request for all party data");
        List<Party> parties = partyService.getAllParties();
        return ResponseEntity.ok(parties);
    }

    /**
     * Get election results with KPI data for a specific election
     * Returns parties sorted by votes with aggregated KPI metrics
     * 
     * @param electionId the election ID (default: TK2023)
     * @return ElectionResultsDTO with parties and KPIs
     */
    @GetMapping("elections/{electionId}/results")
    public ResponseEntity<ElectionResultsDTO> getElectionResults(@PathVariable String electionId) {
        logger.info("Received request for election results: {}", electionId);
        ElectionResultsDTO results = partyService.getElectionResultsWithKPIs(electionId);
        return ResponseEntity.ok(results);
    }
}

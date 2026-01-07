package nl.hva.dederdekamer.election_backend.controller;

import nl.hva.dederdekamer.election_backend.XMLParser.model.Municipality;
import nl.hva.dederdekamer.election_backend.dto.MunicipalityResultsDTO;
import nl.hva.dederdekamer.election_backend.service.MunicipalityService;
import nl.hva.dederdekamer.election_backend.config.CacheConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.Map;

/**
 * Controller for municipality-related endpoints
 * Handles requests for municipality data and voting results
 */
@RestController
@RequestMapping("api")
public class MunicipalityController {

    private static final Logger logger = LoggerFactory.getLogger(MunicipalityController.class);
    private final MunicipalityService municipalityService;

    public MunicipalityController(MunicipalityService municipalityService) {
        this.municipalityService = municipalityService;
    }

    /**
     * Get municipality details by name
     * 
     * @param municipalityId the name or ID of the municipality
     * @return Municipality entity with details
     * @throws ResourceNotFoundException if municipality not found
     * @throws InvalidRequestException   if municipalityId is invalid
     */
    @GetMapping("elections/TK2023/municipalities/{municipalityId}")
    @Cacheable(value = CacheConfig.MUNICIPALITY_SUMMARIES_CACHE, key = "#municipalityId")
    public ResponseEntity<Municipality> getMunicipalityDetailsByName(@PathVariable String municipalityId) {
        logger.info("Received request for municipality details: {}", municipalityId);
        Municipality municipality = municipalityService.getMunicipalityDetails(municipalityId);
        return ResponseEntity.ok(municipality);
    }

    /**
     * Get municipality voting results by name (for map clicks)
     * 
     * @param electionId       the election identifier (e.g., "TK2023")
     * @param municipalityName the municipality name (e.g., "Amsterdam")
     * @return DTO containing party results for the municipality
     * @throws ResourceNotFoundException if municipality or results not found
     * @throws InvalidRequestException   if parameters are invalid
     */
    @GetMapping("elections/{electionId}/municipalities/{municipalityName}/results")
    @Cacheable(value = CacheConfig.MUNICIPALITY_SUMMARIES_CACHE, key = "#electionId + '-' + #municipalityName")
    public ResponseEntity<MunicipalityResultsDTO> getMunicipalityResults(
            @PathVariable String electionId,
            @PathVariable String municipalityName) {
        logger.info("Received request for municipality results: election={}, municipality={}", electionId,
                municipalityName);
        MunicipalityResultsDTO results = municipalityService.getMunicipalityResults(electionId, municipalityName);
        return ResponseEntity.ok(results);
    }

    /**
     * Get all available municipalities for a specific election
     * 
     * @param electionId the election identifier (e.g., "TK2023")
     * @return List of municipalities with basic info (id and name)
     * @throws ResourceNotFoundException if no municipalities found for election
     * @throws InvalidRequestException   if electionId is invalid
     */
    @GetMapping("elections/{electionId}/municipalities")
    public ResponseEntity<List<Object>> getAllMunicipalities(@PathVariable String electionId) {
        logger.info("Received request for all municipalities in election: {}", electionId);
        List<Object> municipalities = municipalityService.getAllMunicipalitiesBasic(electionId);
        return ResponseEntity.ok(municipalities);
    }

    /**
     * Get winning party for all municipalities in one request (for map coloring).
     * This replaces 633 individual requests with a single bulk request.
     * 
     * @param electionId the election identifier (e.g., "TK2023")
     * @return Map of municipality name to winning party info
     */
    @GetMapping("elections/{electionId}/municipalities/winners")
    @Cacheable(value = CacheConfig.MUNICIPALITY_SUMMARIES_CACHE, key = "'winners-' + #electionId")
    public ResponseEntity<Map<String, Map<String, String>>> getMunicipalityWinners(@PathVariable String electionId) {
        logger.info("Received request for bulk municipality winners for election: {}", electionId);
        Map<String, Map<String, String>> winners = municipalityService.getAllMunicipalityWinners(electionId);
        return ResponseEntity.ok(winners);
    }
}

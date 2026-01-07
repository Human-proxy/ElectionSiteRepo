package nl.hva.dederdekamer.election_backend.service;

import nl.hva.dederdekamer.election_backend.XMLParser.model.Municipality;
import nl.hva.dederdekamer.election_backend.XMLParser.model.MunicipalityResult;
import nl.hva.dederdekamer.election_backend.dto.MunicipalityResultsDTO;
import nl.hva.dederdekamer.election_backend.exception.InvalidRequestException;
import nl.hva.dederdekamer.election_backend.exception.ResourceNotFoundException;
import nl.hva.dederdekamer.election_backend.repository.MunicipalityRepository;
import nl.hva.dederdekamer.election_backend.repository.MunicipalityResultRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MunicipalityService {

    private static final Logger logger = LoggerFactory.getLogger(MunicipalityService.class);

    private final MunicipalityRepository municipalityRepository;
    private final MunicipalityResultRepository municipalityResultRepository;

    public MunicipalityService(MunicipalityRepository municipalityRepository,
                                MunicipalityResultRepository municipalityResultRepository) {
        this.municipalityRepository = municipalityRepository;
        this.municipalityResultRepository = municipalityResultRepository;
    }

    public Municipality getMunicipalityData(String municipalityName) {
        return municipalityRepository.findByName(municipalityName);
    }

    /**
     * Get municipality details with party votes (when user clicks on municipality
     * from map)
     */
    public Municipality getMunicipalityDetails(String municipalityName) {
        if (municipalityName == null || municipalityName.trim().isEmpty()) {
            logger.warn("Attempted to get municipality with null or empty name");
            throw new InvalidRequestException("municipality name", "cannot be null or empty");
        }

        Municipality municipality = municipalityRepository.findByName(municipalityName);
        if (municipality == null) {
            logger.warn("Municipality not found: {}", municipalityName);
            throw new ResourceNotFoundException("Municipality", municipalityName);
        }

        return municipality;
    }

    /**
     * Get municipality voting results by municipality name for map interaction
     */
    public MunicipalityResultsDTO getMunicipalityResults(String electionId, String municipalityName) {
        if (electionId == null || electionId.trim().isEmpty()) {
            logger.warn("Attempted to get results with null or empty election ID");
            throw new InvalidRequestException("election ID", "cannot be null or empty");
        }

        if (municipalityName == null || municipalityName.trim().isEmpty()) {
            logger.warn("Attempted to get results with null or empty municipality name");
            throw new InvalidRequestException("municipality name", "cannot be null or empty");
        }

        List<MunicipalityResult> results = municipalityResultRepository
                .findByElectionAndMunicipalityName(electionId, municipalityName);

        if (results.isEmpty()) {
            logger.warn("No results found for election '{}' and municipality '{}'", electionId, municipalityName);
            throw new ResourceNotFoundException(
                    String.format("Municipality results for '%s' in election '%s'", municipalityName, electionId));
        }

        MunicipalityResult firstResult = results.get(0);
        String municipalityId = firstResult.getMunicipality().getId();

        List<MunicipalityResultsDTO.PartyResultDTO> partyResults = results.stream()
                .map(result -> new MunicipalityResultsDTO.PartyResultDTO(
                        result.getParty().getPartyId(),
                        result.getParty().getName(),
                        result.getParty().getShortcode(),
                        result.getTotalVotes(),
                        result.getPercentage(),
                        result.getParty().getColor()))
                .collect(Collectors.toList());

        logger.info("Successfully retrieved {} party results for municipality '{}'", partyResults.size(),
                municipalityName);
        return new MunicipalityResultsDTO(municipalityName, municipalityId, partyResults);
    }

    /**
     * Get basic info for all municipalities (ID and name only) for mapping purposes
     */
    public List<Object> getAllMunicipalitiesBasic(String electionId) {
        if (electionId == null || electionId.trim().isEmpty()) {
            logger.warn("Attempted to get municipalities with null or empty election ID");
            throw new InvalidRequestException("election ID", "cannot be null or empty");
        }

        List<Municipality> municipalities = municipalityRepository.findByElectionId(electionId);

        if (municipalities.isEmpty()) {
            logger.warn("No municipalities found for election: {}", electionId);
            throw new ResourceNotFoundException("Municipalities for election", electionId);
        }

        logger.info("Retrieved {} municipalities for election '{}'", municipalities.size(), electionId);
        return municipalities.stream()
                .<Object>map(municipality -> Map.of(
                        "id", municipality.getId(),
                        "name", municipality.getName()))
                .collect(Collectors.toList());
    }

    /**
     * Get winning party for all municipalities in bulk (for map coloring).
     * Returns a map optimized for initial map loading.
     */
    public Map<String, Map<String, String>> getAllMunicipalityWinners(String electionId) {
        if (electionId == null || electionId.trim().isEmpty()) {
            logger.warn("Attempted to get municipality winners with null or empty election ID");
            throw new InvalidRequestException("election ID", "cannot be null or empty");
        }

        List<Object[]> results = municipalityResultRepository.findWinningPartyByMunicipality(electionId);
        
        Map<String, Map<String, String>> winnersMap = results.stream()
                .collect(Collectors.toMap(
                    row -> (String) row[0], // municipality name
                    row -> {
                        String partyName = (String) row[1];
                        String partyShortcode = row[2] != null ? (String) row[2] : partyName;
                        String color = (String) row[3];
                        
                        return Map.of(
                            "partyName", partyName,
                            "partyShortcode", partyShortcode,
                            "color", color
                        );
                    }
                ));
        
        logger.info("Retrieved winning parties for {} municipalities in election '{}'", winnersMap.size(), electionId);
        return winnersMap;
    }
   /**
     * Get municipalities for the quiz with a robust fallback strategy.
     * * Strategy:
     * 1. Try fetching from RESULTS (most accurate, proves participation).
     * 2. If empty, try fetching via CONSTITUENCY structure (works if results are missing but structure exists).
     * 3. If still empty, return ALL municipalities (fallback to ensure dropdown is never empty).
     */
    public List<Object> getAllMunicipalitiesForQuiz(String electionId) {
        if (electionId == null || electionId.trim().isEmpty()) {
            throw new InvalidRequestException("election ID", "cannot be null or empty");
        }

        // Attempt 1: Get from Results (Works for 2023)
        List<Municipality> municipalities = municipalityResultRepository.findDistinctMunicipalitiesByElectionId(electionId);

        // Attempt 2: Get from Constituency Link (Likely works for 2021)
        if (municipalities.isEmpty()) {
            logger.info("No result-based municipalities found for {}, trying constituency link...", electionId);
            municipalities = municipalityRepository.findByConstituencyElectionId(electionId);
        }

        // Attempt 3: Fallback to ALL (Likely needed for 2017 if no specific links exist)
        if (municipalities.isEmpty()) {
            logger.warn("No municipalities found specifically for {}, falling back to ALL municipalities.", electionId);
            municipalities = municipalityRepository.findAll();
        }

        if (municipalities.isEmpty()) {
            return List.of();
        }

        // Map to simple objects (id, name)
        return municipalities.stream()
                .map(municipality -> Map.of(
                        "id", municipality.getId(),
                        "name", municipality.getName()))
                // Sort alphabetically to make the dropdown user-friendly
                .sorted((m1, m2) -> ((String) m1.get("name")).compareTo((String) m2.get("name")))
                .collect(Collectors.toList());
    }
}

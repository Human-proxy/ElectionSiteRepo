package nl.hva.dederdekamer.election_backend.service;

import nl.hva.dederdekamer.election_backend.XMLParser.model.Party;
import nl.hva.dederdekamer.election_backend.config.CacheConfig;
import nl.hva.dederdekamer.election_backend.dto.ElectionResultsDTO;
import nl.hva.dederdekamer.election_backend.dto.ElectionResultsDTO.PartyResultDTO;
import nl.hva.dederdekamer.election_backend.dto.ElectionResultsDTO.KPIData;
import nl.hva.dederdekamer.election_backend.exception.InvalidRequestException;
import nl.hva.dederdekamer.election_backend.exception.ResourceNotFoundException;
import nl.hva.dederdekamer.election_backend.repository.PartyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for party-related operations.
 * Provides business logic for retrieving and processing party data.
 */
@Service
public class PartyService {

    private static final Logger logger = LoggerFactory.getLogger(PartyService.class);
    private final PartyRepository partyRepository;

    public PartyService(PartyRepository partyRepository) {
        this.partyRepository = partyRepository;
    }

    /**
     * Get all elected parties (seats > 0) from the database.
     * Results are cached to avoid repeated database queries.
     *
     * @return list of elected parties, sorted by seats descending
     * @throws ResourceNotFoundException if no elected parties found
     */
    @Cacheable(value = CacheConfig.ELECTED_PARTIES_CACHE)
    public List<Party> getElectedParties() {
        List<Party> parties = partyRepository.findAllElectedParties();

        if (parties.isEmpty()) {
            logger.warn("No elected parties found in database");
            throw new ResourceNotFoundException("Elected parties");
        }

        logger.info("Retrieved {} elected parties", parties.size());
        return parties;
    }

    /**
     * Get all parties from the database.
     * Results are cached to avoid repeated database queries.
     *
     * @return list of all parties
     * @throws ResourceNotFoundException if no parties found
     */
    @Cacheable(value = CacheConfig.PARTY_CACHE)
    public List<Party> getAllParties() {
        List<Party> parties = partyRepository.findAll();

        if (parties.isEmpty()) {
            logger.warn("No parties found in database");
            throw new ResourceNotFoundException("Parties");
        }

        logger.info("Retrieved {} parties", parties.size());
        return parties;
    }

    /**
     * Get elected parties for a specific election.
     * Results are cached to avoid repeated database queries.
     *
     * @param electionId the election ID to filter by
     * @return list of elected parties for the given election, sorted by seats
     *         descending
     * @throws InvalidRequestException   if electionId is null or empty
     * @throws ResourceNotFoundException if no elected parties found for election
     */
    @Cacheable(value = CacheConfig.ELECTED_PARTIES_CACHE, key = "#electionId")
    public List<Party> getElectedPartiesByElection(String electionId) {
        if (electionId == null || electionId.trim().isEmpty()) {
            logger.warn("Attempted to get parties with null or empty election ID");
            throw new InvalidRequestException("election ID", "cannot be null or empty");
        }

        List<Party> parties = partyRepository.findElectedPartiesByElection(electionId);

        if (parties.isEmpty()) {
            logger.warn("No elected parties found for election: {}", electionId);
            throw new ResourceNotFoundException("Elected parties for election", electionId);
        }

        logger.info("Retrieved {} elected parties for election '{}'", parties.size(), electionId);
        return parties;
    }

    /**
     * Get election results with KPI data for a specific election.
     * 
     * @param electionId the election ID to get results for
     * @return ElectionResultsDTO containing parties and KPI data
     * @throws InvalidRequestException   if electionId is null or empty
     * @throws ResourceNotFoundException if no parties found for election
     */
    @Cacheable(value = CacheConfig.PARTY_CACHE, key = "'results_' + #electionId")
    public ElectionResultsDTO getElectionResultsWithKPIs(String electionId) {
        if (electionId == null || electionId.trim().isEmpty()) {
            logger.warn("Attempted to get election results with null or empty election ID");
            throw new InvalidRequestException("election ID", "cannot be null or empty");
        }

        // Get all parties for this election
        List<Party> allParties = partyRepository.findPartiesByElection(electionId);
        
        if (allParties.isEmpty()) {
            logger.warn("No parties found for election: {}", electionId);
            throw new ResourceNotFoundException("Parties for election", electionId);
        }

        // Convert to DTOs
        List<PartyResultDTO> partyDTOs = allParties.stream()
            .map(p -> new PartyResultDTO(
                p.getId(),
                p.getName(),
                p.getPartyId(),
                p.getVotes(),
                p.getSeats(),
                p.isElected(),
                p.getColor()
            ))
            .collect(Collectors.toList());

        // Calculate KPIs
        long totalVotes = allParties.stream()
            .mapToLong(p -> p.getVotes() != null ? p.getVotes() : 0)
            .sum();

        List<Party> electedParties = allParties.stream()
            .filter(p -> p.getSeats() != null && p.getSeats() > 0)
            .sorted((a, b) -> Integer.compare(b.getSeats(), a.getSeats()))
            .collect(Collectors.toList());

        String winningPartyName = electedParties.isEmpty() ? null : electedParties.get(0).getName();
        Integer winningPartySeats = electedParties.isEmpty() ? 0 : electedParties.get(0).getSeats();

        KPIData kpis = new KPIData(
            winningPartyName,
            winningPartySeats,
            totalVotes,
            electedParties.size(),
            allParties.size()
        );

        logger.info("Generated election results with KPIs for election '{}': {} parties, {} votes", 
                   electionId, allParties.size(), totalVotes);

        return new ElectionResultsDTO(electionId, "Tweede Kamer " + electionId.substring(2), partyDTOs, kpis);
    }
}

package nl.hva.dederdekamer.election_backend.service;

import nl.hva.dederdekamer.election_backend.XMLParser.model.Election;
import nl.hva.dederdekamer.election_backend.XMLParser.model.Party;
import nl.hva.dederdekamer.election_backend.dto.ConstituencySummaryDTO;
import nl.hva.dederdekamer.election_backend.XMLParser.service.DutchElectionService;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Service that centralizes homepage-related business logic.
 */
@Service
public class HomeServiceImpl implements HomeService {

    private static final Logger logger = LoggerFactory.getLogger(HomeServiceImpl.class);

    private final DutchElectionService dutchElectionService;
    private final PartyService partyService;
    private final ConstituencyService constituencyService;

    public HomeServiceImpl(DutchElectionService dutchElectionService, PartyService partyService, ConstituencyService constituencyService) {
        this.dutchElectionService = dutchElectionService;
        this.partyService = partyService;
        this.constituencyService = constituencyService;
    }

    /**
     * Returns elected parties for the homepage chart filtered by election ID.
     */
    @Override
    public List<Party> getHomepageParties(String electionId) {
        return partyService.getElectedPartiesByElection(electionId);
    }

    /**
     * Returns a compact Election object containing only elected parties (for charting).
     */
    @Override
    public Election getHomepageElectionResults(String electionId) {
        Optional<Election> maybe = dutchElectionService.getElectionById(electionId);
        if (maybe.isEmpty()) return null;
        Election full = maybe.get();
        Election results = new Election(full.getId());
        results.setName(full.getName());
        results.setDate(full.getDate());
        full.getParties().stream()
                .filter(p -> p.isElected() && p.getSeats() > 0)
                .forEach(results::addParty);
        return results;
    }

    @Override
    public Map<String, String> getElectionMetadata(String electionId) {
        var e = getHomepageElectionResults(electionId);
        if (e == null) return Map.of();
        return Map.of(
                "id", e.getId(),
                "name", e.getName() != null ? e.getName() : "",
                "date", e.getDate() != null ? e.getDate() : ""
        );
    }

    /**
     * Returns top-4 constituencies for the dashboard with aggregated vote data.
     */
    @Override
    public List<ConstituencySummaryDTO> getTop4Constituencies(String electionId) {
        return constituencyService.getTop4Constituencies(electionId);
    }
}

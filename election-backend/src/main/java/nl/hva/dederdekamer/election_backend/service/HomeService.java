package nl.hva.dederdekamer.election_backend.service;

import nl.hva.dederdekamer.election_backend.XMLParser.model.Election;
import nl.hva.dederdekamer.election_backend.XMLParser.model.Party;
import nl.hva.dederdekamer.election_backend.dto.ConstituencySummaryDTO;
import java.util.List;
import java.util.Map;

public interface HomeService {
    // Return a compact Election object used for homepage charting
    Election getHomepageElectionResults(String electionId);

    // Convenience: return simple metadata map (id, name, date)
    Map<String, String> getElectionMetadata(String electionId);

    // Parties used on the homepage (elected parties for charts) filtered by election ID
    List<Party> getHomepageParties(String electionId);

    // Top-4 constituencies for the homepage dashboard
    List<ConstituencySummaryDTO> getTop4Constituencies(String electionId);
}

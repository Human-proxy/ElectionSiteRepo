package nl.hva.dederdekamer.election_backend.service;

import nl.hva.dederdekamer.election_backend.dto.ConstituencySummaryDTO;
import nl.hva.dederdekamer.election_backend.repository.MunicipalityResultRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Service for constituency-related operations.
 */
@Service
public class ConstituencyService {

    private static final Logger logger = LoggerFactory.getLogger(ConstituencyService.class);
    private static final int TOP_CONSTITUENCIES_LIMIT = 4;

    private final MunicipalityResultRepository municipalityResultRepository;

    public ConstituencyService(MunicipalityResultRepository municipalityResultRepository) {
        this.municipalityResultRepository = municipalityResultRepository;
    }

    /**
     * Get top-4 constituencies by total votes cast, including winning party information.
     * This method is optimized with caching and uses aggregated queries.
     * 
     * @param electionId the election ID to query (e.g., "TK2023")
     * @return list of constituency summary DTOs, ordered by total votes descending
     */
    @Cacheable(value = "top4Constituencies", key = "#electionId")
    public List<ConstituencySummaryDTO> getTop4Constituencies(String electionId) {
        
        try {
            // Get constituency summaries (aggregated from municipality results)
            List<Object[]> constituencySummaries = municipalityResultRepository
                    .findConstituencySummariesByElection(electionId);
            
            if (constituencySummaries.isEmpty()) {
                logger.warn("No constituency data found for election: {}", electionId);
                return List.of();
            }

            List<ConstituencySummaryDTO> result = new ArrayList<>();
            
            // Process top 4 constituencies
            for (int i = 0; i < Math.min(TOP_CONSTITUENCIES_LIMIT, constituencySummaries.size()); i++) {
                Object[] row = constituencySummaries.get(i);
                
                String constituencyId = (String) row[0];
                String constituencyName = (String) row[1];
                Long totalVotesLong = (Long) row[2];
                Long municipalityCountLong = (Long) row[3];
                
                Integer totalVotes = totalVotesLong != null ? totalVotesLong.intValue() : 0;
                Integer municipalityCount = municipalityCountLong != null ? municipalityCountLong.intValue() : 0;
                
                // Get top party for this constituency
                Object[] topPartyData = municipalityResultRepository
                        .findTopPartyForConstituency(electionId, constituencyId);
                
                String topPartyName = "Unknown";
                String topPartyColor = null;
                Integer topPartyVotes = 0;
                Double topPartyPercentage = 0.0;
                
                if (topPartyData != null && topPartyData.length >= 4) {
                    topPartyName = (String) topPartyData[0];
                    topPartyColor = (String) topPartyData[1];
                    Long topPartyVotesLong = (Long) topPartyData[2];
                    topPartyVotes = topPartyVotesLong != null ? topPartyVotesLong.intValue() : 0;
                    topPartyPercentage = (Double) topPartyData[3];
                    if (topPartyPercentage == null) {
                        topPartyPercentage = totalVotes > 0 
                            ? (topPartyVotes.doubleValue() / totalVotes) * 100 
                            : 0.0;
                    }
                }
                
                ConstituencySummaryDTO dto = new ConstituencySummaryDTO(
                    constituencyId,
                    constituencyName != null ? constituencyName : "Unknown",
                    totalVotes,
                    topPartyName,
                    topPartyColor,
                    topPartyVotes,
                    topPartyPercentage,
                    municipalityCount
                );
                
                result.add(dto);
            }
            
            logger.info("Successfully fetched {} top constituencies for election {}", 
                       result.size(), electionId);
            return result;
            
        } catch (Exception e) {
            logger.error("Error fetching top-4 constituencies for election: {}", electionId, e);
            return List.of();
        }
    }
}

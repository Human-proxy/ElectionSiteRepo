package nl.hva.dederdekamer.election_backend.XMLParser.utils.xml.transformers;

import nl.hva.dederdekamer.election_backend.XMLParser.model.Election;
import nl.hva.dederdekamer.election_backend.XMLParser.model.Party;
import nl.hva.dederdekamer.election_backend.XMLParser.model.Candidate;
import nl.hva.dederdekamer.election_backend.XMLParser.utils.xml.VotesTransformer;
import nl.hva.dederdekamer.election_backend.util.PartyColorUtil;

import java.util.Map;

/**
 * Transformer for Dutch election results
 * Only extracts the most important data: parties, elected candidates, and basic election info.
 * 
 * What this transformer does:
 * - Captures party names and whether they won seats
 * - Records elected candidates with their names and party affiliation
 * - Ignores less important details to keep it simple
 */
public class DutchResultTransformer implements VotesTransformer {
    private final Election election;
    private String currentPartyId = null;
    private String currentPartyName = null;

    /**
     * Creates a new transformer for handling the election results.
     * @param election the election in which the results will be stored.
     */
    public DutchResultTransformer(Election election) {
        this.election = election;
    }

    @Override
    public void registerPartyVotes(boolean aggregated, Map<String, String> electionData) {
        extractMetadataIfAvailable(electionData);
        
        String partyId = electionData.get("AffiliationIdentifier-Id");
        String partyName = electionData.get("RegisteredName");
        String electedStr = electionData.get("Elected");
        
        if (partyId != null && partyName != null) {
            boolean elected = "yes".equals(electedStr);
            
            Party party = new Party(partyId, partyName, elected);
            party.setColor(PartyColorUtil.getPartyColor(partyName, null));
            election.addParty(party);
            
            currentPartyId = partyId;
            currentPartyName = partyName;
        }
    }

    @Override
    public void registerCandidateVotes(boolean aggregated, Map<String, String> electionData) {
        extractMetadataIfAvailable(electionData);
        
        String candidateId = electionData.get("CandidateIdentifier-Id");
        String shortCode = electionData.get("CandidateIdentifier-ShortCode");
        String firstName = electionData.get("FirstName");
        String lastName = electionData.get("LastName");
        String electedStr = electionData.get("Elected");

        if (shortCode != null || candidateId != null) {
            boolean elected = "yes".equals(electedStr);

            Candidate candidate = new Candidate();
            candidate.setId(candidateId);
            candidate.setShortCode(shortCode);
            candidate.setFirstName(firstName);
            candidate.setLastName(lastName);
            candidate.setElection(election);
            candidate.setPartyId(currentPartyId);
            candidate.setElected(elected);

            election.addOrUpdateCandidate(candidate);
        }
    }

    @Override
    public void registerMetadata(boolean aggregated, Map<String, String> electionData) {
        extractMetadataIfAvailable(electionData);
    }
    
    private void extractMetadataIfAvailable(Map<String, String> electionData) {
        String electionName = electionData.get("ElectionName");
        if (electionName != null && election.getName() == null) {
            election.setName(electionName);
        }
        
        String electionDate = electionData.get("ElectionDate");
        if (electionDate != null && election.getDate() == null) {
            election.setDate(electionDate);
        }
    }
}

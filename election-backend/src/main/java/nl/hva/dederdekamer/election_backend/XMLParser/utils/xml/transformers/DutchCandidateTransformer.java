package nl.hva.dederdekamer.election_backend.XMLParser.utils.xml.transformers;

import nl.hva.dederdekamer.election_backend.XMLParser.model.Election;
import nl.hva.dederdekamer.election_backend.XMLParser.model.Candidate;
import nl.hva.dederdekamer.election_backend.XMLParser.model.Party;
import nl.hva.dederdekamer.election_backend.util.PartyColorUtil;
import nl.hva.dederdekamer.election_backend.XMLParser.utils.xml.CandidateTransformer;

import java.util.Map;
import java.util.Set;
import java.util.HashSet;

/**
 * Transforms candidate data from Dutch EML XML format while avoiding duplicates.
 * Candidates appear in multiple contest areas (cities) but should only be registered once.
 */
public class DutchCandidateTransformer implements CandidateTransformer {
    private final Election election;
    private final Set<String> registeredCandidates; // Track unique candidates by name+party

    /**
     * Creates a new transformer for handling the candidate lists. It expects an instance of Election that can
     * be used for storing the candidates lists.
     * @param election the election in which the candidate lists wil be stored.
     */
    public DutchCandidateTransformer(Election election) {
        this.election = election;
        this.registeredCandidates = new HashSet<>();
    }

    @Override
    public void registerCandidate(Map<String, String> electionData) {
        // Extract all relevant candidate data from XML using correct field names
        String candidateId = electionData.getOrDefault("CandidateIdentifier-Id", "");
        String firstName = electionData.getOrDefault("FirstName", "");
        String lastName = electionData.getOrDefault("LastName", "");
        String initials = electionData.getOrDefault("NameLine", ""); // For ShortCode/Initials
        String partyId = electionData.getOrDefault("AffiliationIdentifier-Id", "");
        String partyName = electionData.getOrDefault("RegisteredName", "");
        String contestName = electionData.getOrDefault("ContestName", ""); // City/area name

        // Create proper Candidate object using the separate Candidate model
        if (!firstName.isEmpty() && !lastName.isEmpty()) {
            // Create unique key for this candidate (name + party to detect duplicates)
            String uniqueKey = firstName + "|" + lastName + "|" + partyId;
            
            // Skip if we've already registered this candidate
            if (registeredCandidates.contains(uniqueKey)) {
                return;
            }
            
            // Mark this candidate as registered
            registeredCandidates.add(uniqueKey);
            
            Candidate candidate;
            
            if (!candidateId.isEmpty()) {
                // Use full constructor if we have ID - note: shortCode is second parameter
                candidate = new Candidate(candidateId, initials, firstName, lastName);
            } else {
                // Use basic constructor for names only
                candidate = new Candidate(firstName, lastName);
                // Set shortCode separately since constructor doesn't take it
                if (!initials.isEmpty()) {
                    candidate.setShortCode(initials);
                }
            }
            
            // Add to candidate lists FIRST so election is set
            election.addCandidateToList(candidate);
            election.addCandidateName(candidate.getFullName()); // For backward compatibility with string list
            
            // Create or find party entity and set the relationship
            if (!partyId.isEmpty() && !partyName.isEmpty()) {
                try {
                    Long partyIdLong = Long.parseLong(partyId);
                    Party party = Party.findPartyById(election, partyIdLong);

                    // If party doesn't exist yet, create it
                    if (party == null) {
                        party = new Party(partyId, partyName);
                        party.setColor(PartyColorUtil.getPartyColor(partyName, null));
                        party.setElection(election);
                        election.addParty(party);
                    }

                    // Set the party relationship
                    candidate.setParty(party);
                } catch (NumberFormatException e) {
                    System.err.println(
                            "Warning: Invalid party ID '" + partyId + "' for candidate " + candidate.getFullName());
                }
            }
        } else {
            System.err.println("Warning: Candidate missing required firstName/lastName, skipping");
            System.err.println("Available data: " + electionData);
        }
    }
}

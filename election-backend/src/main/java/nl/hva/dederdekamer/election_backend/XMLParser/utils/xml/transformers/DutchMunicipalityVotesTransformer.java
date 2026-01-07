package nl.hva.dederdekamer.election_backend.XMLParser.utils.xml.transformers;

import nl.hva.dederdekamer.election_backend.XMLParser.model.*;
import nl.hva.dederdekamer.election_backend.XMLParser.utils.xml.VotesTransformer;
import nl.hva.dederdekamer.election_backend.util.PartyColorUtil;

import java.util.Map;

/**
 * Transformer for municipality-level votes.
 * Reads votes per municipality and stores them in the election data.
 */
public class DutchMunicipalityVotesTransformer implements VotesTransformer {
    private final Election election;

    // Cache for when XML omits values
    private String lastMunicipalityId;
    private String lastMunicipalityName;
    private String lastConstituencyId;
    private String lastPartyId;
    private String lastPartyName;

    public DutchMunicipalityVotesTransformer(Election election) {
        this.election = election;
    }

    @Override
    public void registerPartyVotes(boolean aggregated, Map<String, String> electionData) {
        // Get data from XML
        String rawMunicipalityId = getValue(electionData, "ReportingUnitIdentifier-Id", "AuthorityIdentifier-Id");
        String rawConstituencyId = getValue(electionData, "ContestIdentifier-Id", "ContestIdentifier");
        // Get municipality name from ReportingUnitIdentifier, not ContestName (which is
        // the constituency name)
        String rawMunicipalityName = getValue(electionData, "ReportingUnitIdentifier", "AuthorityIdentifier");
        String rawPartyId = getValue(electionData, "AffiliationIdentifier-Id", "AffiliationIdentifier");
        String rawPartyName = electionData.get("RegisteredName");
        String votesStr = electionData.get("ValidVotes");

        // Use cached values if missing (make final for lambda usage)
        final String municipalityId = rawMunicipalityId != null ? rawMunicipalityId : lastMunicipalityId;
        final String municipalityName = rawMunicipalityName != null ? rawMunicipalityName : lastMunicipalityName;
        
        // Normalize constituency ID: if it's just a number, prefix with "HSB"
        String normalizedConstituencyId = rawConstituencyId != null ? rawConstituencyId : lastConstituencyId;
        if (normalizedConstituencyId != null && normalizedConstituencyId.matches("\\d+")) {
            normalizedConstituencyId = "HSB" + normalizedConstituencyId;
        }
        final String constituencyId = normalizedConstituencyId;
        
        final String partyId = rawPartyId != null ? rawPartyId : lastPartyId;
        final String partyName = rawPartyName != null ? rawPartyName : lastPartyName;

        if (municipalityId == null || constituencyId == null || partyId == null || votesStr == null) {
            return;
        }

        // Parse votes
        int votes;
        try {
            votes = Integer.parseInt(votesStr);
        } catch (NumberFormatException e) {
            return;
        }

        // Extract municipality ID and polling bureau ID
        // Example: "0363::SB1" -> "0363" (municipality), "SB1" (polling bureau)
        final String actualMunicipalityId;

        if (municipalityId.contains("::")) {
            actualMunicipalityId = municipalityId.substring(0, municipalityId.indexOf("::"));
        } else {
            actualMunicipalityId = municipalityId;
        }

        // Only process if actualMunicipalityId is a valid municipality code (4 digits)
        // Skip constituency-level identifiers like "HSB9"
        if (actualMunicipalityId.matches("\\d{4}")) {
            // Create or get existing constituency
            Constituency constituency = election.getOrCreateConstituency(constituencyId);

            // Get or create municipality
            Municipality municipality = constituency.getMunicipality().stream()
                    .filter(m -> m.getId().equals(actualMunicipalityId))
                    .findFirst()
                    .orElseGet(() -> {
                        Municipality newMunicipality = new Municipality(actualMunicipalityId, municipalityName);
                        newMunicipality.setConstituency(constituency);
                        constituency.addMunicipalityIfAbsent(newMunicipality);
                        return newMunicipality;
                    });

            // Find or create Party entity
            Party party = election.getParties().stream()
                    .filter(p -> partyId.equals(String.valueOf(p.getPartyId())))
                    .findFirst()
                    .orElseGet(() -> {
                        Party newParty = new Party(partyId, partyName);
                        newParty.setColor(PartyColorUtil.getPartyColor(partyName, null));
                        newParty.setElection(election);
                        election.addParty(newParty);
                        return newParty;
                    });
                
            // For now, aggregate polling bureau votes to municipality level
            MunicipalityResult existingResult = election.getMunicipalityResults().stream()
                    .filter(mr -> mr.getMunicipality().getId().equals(actualMunicipalityId)
                            && mr.getParty().getPartyId().equals(party.getPartyId()))
                    .findFirst()
                    .orElse(null);

            if (existingResult != null) {
                existingResult.setTotalVotes(existingResult.getTotalVotes() + votes);
            } else {
                MunicipalityResult municipalityResult = new MunicipalityResult(election, municipality, party, votes);
                election.addMunicipalityResult(municipalityResult);
            }

            // Remember these values for the next record
            cacheValues(constituencyId, actualMunicipalityId, municipalityName, partyId, partyName);
        }
    }

    @Override
    public void registerCandidateVotes(boolean aggregated, Map<String, String> electionData) {
        // Candidate votes per polling station are not stored to avoid database bloat (500k+ rows)
        // Candidate data is available at the national level via the Candidate entity
    }

    @Override
    public void registerMetadata(boolean aggregated, Map<String, String> electionData) {
    }

    /**
     * Try to get a value from XML using multiple keys.
     */
    private String getValue(Map<String, String> data, String... keys) {
        for (String key : keys) {
            String value = data.get(key);
            if (value != null) {
                return value;
            }
        }
        return null;
    }

    /**
     * Cache values for the next record when XML omits them.
     */
    private void cacheValues(String constituencyId, String municipalityId, String municipalityName,
            String partyId, String partyName) {
        if (constituencyId != null)
            lastConstituencyId = constituencyId;
        if (municipalityId != null)
            lastMunicipalityId = municipalityId;
        if (municipalityName != null)
            lastMunicipalityName = municipalityName;
        if (partyId != null)
            lastPartyId = partyId;
        if (partyName != null)
            lastPartyName = partyName;
    }
}

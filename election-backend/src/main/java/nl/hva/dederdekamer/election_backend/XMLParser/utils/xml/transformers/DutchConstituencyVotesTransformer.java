package nl.hva.dederdekamer.election_backend.XMLParser.utils.xml.transformers;

import nl.hva.dederdekamer.election_backend.XMLParser.model.*;
import nl.hva.dederdekamer.election_backend.XMLParser.utils.xml.VotesTransformer;

import java.util.Map;

/**
 * Transformer for constituency-level votes.
 * Reads votes per constituency and stores them in the election data.
 */
public class DutchConstituencyVotesTransformer implements VotesTransformer {
    private final Election election;

    // Cache for when XML omits values
    private String lastConstituencyId;
    private String lastConstituencyName;
    private String lastPartyId;
    private String lastPartyName;

    public DutchConstituencyVotesTransformer(Election election) {
        this.election = election;
    }

    @Override
    public void registerPartyVotes(boolean aggregated, Map<String, String> electionData) {
        // Get data from XML
        String constituencyId = getValue(electionData, "ContestIdentifier-Id", "ContestIdentifier");
        String constituencyName = electionData.get("ContestName");
        String municipalityId = getValue(electionData, "ReportingUnitIdentifier-Id", "AuthorityIdentifier-Id");
        String municipalityName = getValue(electionData, "ReportingUnitIdentifier", "AuthorityIdentifier");
        String partyId = getValue(electionData, "AffiliationIdentifier-Id", "AffiliationIdentifier");
        String partyName = electionData.get("RegisteredName");
        String votesStr = electionData.get("ValidVotes");

        // Use cached values if XML omitted them
        if (constituencyId == null)
            constituencyId = lastConstituencyId;
        if (constituencyName == null)
            constituencyName = lastConstituencyName;
        if (partyId == null)
            partyId = lastPartyId;
        if (partyName == null)
            partyName = lastPartyName;

        // Normalize constituency ID: if it's just a number, prefix with "HSB"
        if (constituencyId != null && constituencyId.matches("\\d+")) {
            constituencyId = "HSB" + constituencyId;
        }

        // Check if all required data is present
        if (constituencyId == null || partyId == null || votesStr == null) {
            return;
        }

        // Create or get existing constituency
        Constituency constituency = election.getOrCreateConstituency(constituencyId);
        if (constituencyName != null) {
            constituency.setName(constituencyName);
        }

        // If municipality data is present, create municipality and link it to
        // constituency
        if (municipalityId != null) {
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
                // Get or create municipality
                constituency.getMunicipality().stream()
                        .filter(m -> m.getId().equals(actualMunicipalityId))
                        .findFirst()
                        .orElseGet(() -> {
                            Municipality newMunicipality = new Municipality(actualMunicipalityId, municipalityName);
                            newMunicipality.setConstituency(constituency);
                            constituency.addMunicipalityIfAbsent(newMunicipality);
                            return newMunicipality;
                        });
            }
        }

        // Remember these values for the next record
        cacheValues(constituencyId, constituencyName, partyId, partyName);
    }

    @Override
    public void registerCandidateVotes(boolean aggregated, Map<String, String> electionData) {
        // Constituency-level transformer focuses on constituency and party data
        // Individual candidate votes are handled by the municipality transformer
        // This method can be left minimal or could handle constituency-level candidate
        // aggregation

        // Get basic constituency data to ensure consistency
        String constituencyId = getValue(electionData, "ContestIdentifier-Id", "ContestIdentifier");
        String constituencyName = electionData.get("ContestName");
        String municipalityId = getValue(electionData, "ReportingUnitIdentifier-Id", "AuthorityIdentifier-Id");
        String municipalityName = getValue(electionData, "ReportingUnitIdentifier", "AuthorityIdentifier");

        // Use cached values if XML omitted them
        if (constituencyId == null)
            constituencyId = lastConstituencyId;
        if (constituencyName == null)
            constituencyName = lastConstituencyName;

        // Normalize constituency ID: if it's just a number, prefix with "HSB"
        if (constituencyId != null && constituencyId.matches("\\d+")) {
            constituencyId = "HSB" + constituencyId;
        }

        if (constituencyId != null) {
            // Create or get existing constituency
            Constituency constituency = election.getOrCreateConstituency(constituencyId);
            if (constituencyName != null) {
                constituency.setName(constituencyName);
            }

            // If municipality data is present, create municipality and link it to
            // constituency
            if (municipalityId != null) {
                // Extract municipality ID and polling bureau ID
                final String actualMunicipalityId;

                if (municipalityId.contains("::")) {
                    actualMunicipalityId = municipalityId.substring(0, municipalityId.indexOf("::"));
                } else {
                    actualMunicipalityId = municipalityId;
                }

                // Only process if actualMunicipalityId is a valid municipality code (4 digits)
                if (actualMunicipalityId.matches("\\d{4}")) {
                    // Get or create municipality
                    constituency.getMunicipality().stream()
                            .filter(m -> m.getId().equals(actualMunicipalityId))
                            .findFirst()
                            .orElseGet(() -> {
                                Municipality newMunicipality = new Municipality(actualMunicipalityId, municipalityName);
                                newMunicipality.setConstituency(constituency);
                                constituency.addMunicipalityIfAbsent(newMunicipality);
                                return newMunicipality;
                            });
                }
            }

            // Remember these values for the next record
            cacheValues(constituencyId, constituencyName, lastPartyId, lastPartyName);
        }
    }

    @Override
    public void registerMetadata(boolean aggregated, Map<String, String> electionData) {
        // Get data from XML
        String constituencyId = getValue(electionData, "ContestIdentifier-Id", "ContestIdentifier");
        String constituencyName = electionData.get("ContestName");

        // Normalize constituency ID: if it's just a number, prefix with "HSB"
        if (constituencyId != null && constituencyId.matches("\\d+")) {
            constituencyId = "HSB" + constituencyId;
        }

        // Check if required IDs are present
        if (constituencyId == null) {
            return;
        }

        // Create or get existing constituency
        Constituency constituency = election.getOrCreateConstituency(constituencyId);

        // Update constituency name if provided
        if (constituencyName != null) {
            constituency.setName(constituencyName);
        }

        // Remember these values for the next record
        cacheValues(constituencyId, constituencyName, lastPartyId, lastPartyName);
    }

    /**
     * Try to get a value from XML using multiple keys.
     * XML does not always use the same names, so we try alternatives.
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
     * Remember the current values for the next record.
     * XML sometimes omits values, so we cache them.
     */
    private void cacheValues(String constituencyId, String constituencyName, String partyId, String partyName) {
        if (constituencyId != null)
            lastConstituencyId = constituencyId;
        if (constituencyName != null)
            lastConstituencyName = constituencyName;
        if (partyId != null)
            lastPartyId = partyId;
        if (partyName != null)
            lastPartyName = partyName;
    }

}

package nl.hva.dederdekamer.election_backend.XMLParser.utils.xml.transformers;

import nl.hva.dederdekamer.election_backend.XMLParser.model.*;
import nl.hva.dederdekamer.election_backend.XMLParser.utils.xml.VotesTransformer;
import nl.hva.dederdekamer.election_backend.util.PartyColorUtil;

import java.util.*;

/**
 * Simplified transformer that only captures essential national election data:
 * - Party names and their vote counts
 * - Basic election information (name and date)
 * - Candidate total votes ONLY from TotalVotes
 */
public record DutchNationalVotesTransformer(Election election) implements VotesTransformer {
    
    /**
     * Creates a new transformer for handling the votes at the national level.
     *
     * @param election the election in which the votes will be stored.
     */
    public DutchNationalVotesTransformer {
    }

    @Override
    public void registerPartyVotes(boolean aggregated, Map<String, String> electionData) {
        String partyId = electionData.get("AffiliationIdentifier-Id");
        String partyName = electionData.get("RegisteredName");
        String votesStr = electionData.get("ValidVotes");

        if (partyId != null && partyName != null && votesStr != null) {
            try {
                int votes = Integer.parseInt(votesStr);

                // Get reporting unit info for filtering
                String reportingUnitId = getValue(electionData, "ReportingUnitIdentifier-Id",
                        "ReportingUnitIdentifierId", "ReportingUnitIdentifier");

                // FILTER: Only process NATIONAL totals for main party votes, skip constituency
                // data
                if (reportingUnitId != null && !reportingUnitId.equals("alle")) {
                    // Normalize constituency ID: if it's just a number, prefix with "HSB"
                    String normalizedReportingUnitId = reportingUnitId;
                    if (reportingUnitId.matches("\\d+")) {
                        normalizedReportingUnitId = "HSB" + reportingUnitId;
                    }
                    
                    // This is constituency-level data, record it separately but don't add to main
                    // party totals
                    Constituency constituency = election.getOrCreateConstituency(normalizedReportingUnitId);
                    String ruName = getValue(electionData, "ReportingUnitIdentifier", "ReportingUnitIdentifier-Name");
                    if (ruName != null) {
                        constituency.setName(ruName);
                    }
                    constituency.addOrUpdatePartyVotes(partyId, partyName);
                    return; // Exit early, don't add to main party totals
                }

                // Check if party already exists at election level
                Party existingParty = election.getParties().stream()
                        .filter(p -> partyId.equals(String.valueOf(p.getPartyId())))
                        .findFirst()
                        .orElse(null);

                if (existingParty == null) {
                    Party party = new Party(partyId, partyName);
                    party.setVotes(votes);
                    party.setElection(election);
                    party.setColor(PartyColorUtil.getPartyColor(partyName, null));
                    election.addParty(party);

                    PartyResult partyResult = new PartyResult(election, party, votes, 0, false);
                    election.addPartyResult(partyResult);
                } else {
                    existingParty.setVotes(existingParty.getVotes() + votes);

                    PartyResult partyResult = new PartyResult(election, existingParty, existingParty.getVotes(), 0,
                            false);
                    election.addPartyResult(partyResult);
                }

                // Also record national-level constituency data if available
                if (reportingUnitId != null) {
                    // Normalize constituency ID: if it's just a number, prefix with "HSB"
                    String normalizedReportingUnitId = reportingUnitId;
                    if (reportingUnitId.matches("\\d+")) {
                        normalizedReportingUnitId = "HSB" + reportingUnitId;
                    }
                    
                    Constituency constituency = election.getOrCreateConstituency(normalizedReportingUnitId);
                    String ruName = getValue(electionData, "ReportingUnitIdentifier", "ReportingUnitIdentifier-Name");
                    if (ruName != null) {
                        constituency.setName(ruName);
                    }
                    constituency.addOrUpdatePartyVotes(partyId, partyName);
                }

            } catch (NumberFormatException e) {
                System.err.printf("Could not parse vote count '%s' for party %s\n", votesStr, partyName);
            }
        }
    }

    @Override
    public void registerCandidateVotes(boolean aggregated, Map<String, String> electionData) {
        // Only process candidate votes from TotalVotes
        String contestId = electionData.get("ContestIdentifier-Id");
        if (!aggregated || !"alle".equals(contestId)) {
            return;
        }

        String shortCode = electionData.get("CandidateIdentifier-ShortCode");
        String votesStr = electionData.get("ValidVotes");

        if (shortCode != null && votesStr != null) {
            try {
                int totalVotes = Integer.parseInt(votesStr);

                // Find existing elected candidate by shortCode and update total votes
                election.getElectedCandidates().stream()
                        .filter(c -> shortCode.equals(c.getShortCode()))
                        .findFirst()
                        .ifPresent(candidate -> candidate.setTotalVotes(totalVotes));

            } catch (NumberFormatException e) {
                System.err.printf("Could not parse vote count '%s' for candidate %s\n", votesStr, shortCode);
            }
        }
    }

    @Override
    public void registerMetadata(boolean aggregated, Map<String, String> electionData) {
        String constituencyId = getValue(electionData, "ReportingUnitIdentifier-Id");
        String constituencyName = electionData.get("ReportingUnitIdentifier");
        if (constituencyId == null) return;

        // Normalize constituency ID: if it's just a number, prefix with "HSB"
        if (constituencyId.matches("\\d+")) {
            constituencyId = "HSB" + constituencyId;
        }

        Constituency constituency = election.getOrCreateConstituency(constituencyId);
        constituency.setName(constituencyName);
    }

    private String getValue(Map<String, String> data, String... keys) {
        for (String key : keys) {
            String value = data.get(key);
            if (value != null) return value;
        }
        return null;
    }

    public List<Constituency> getTopConstituenciesByTotalVotes(int topN) {
        Collection<Constituency> constituencies = election.getConstituencies();
        List<Constituency> list = new ArrayList<>(constituencies);
        list.sort((a, b) -> Integer.compare(totalVotes(b), totalVotes(a)));
        return list.subList(0, Math.min(Math.max(topN, 0), list.size()));
    }

    private int totalVotes(Constituency c) {
        return c.getParties().values().stream().mapToInt(Party::getVotes).sum();
    }

    public Party getTopPartyInConstituency(Constituency constituency) {
        return constituency.getParties().values().stream()
                .max(Comparator.comparingInt(Party::getVotes))
                .orElse(null);
    }
}

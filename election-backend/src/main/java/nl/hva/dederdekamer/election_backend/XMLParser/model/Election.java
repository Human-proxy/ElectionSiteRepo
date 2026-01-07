package nl.hva.dederdekamer.election_backend.XMLParser.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Enhanced Election model that holds comprehensive election data.
 * This includes parties, candidates, and election results.
 */
@Entity
@Table(name = "election")
public class Election {
    @Id
    @Column(name = "election_id", unique = true, nullable = false)
    private String id;

    private String name;
    private String date;
    @OneToMany(mappedBy = "election", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Candidate> candidates = new ArrayList<>();

    // Persist constituencies so they show up in H2
    @OneToMany(mappedBy = "election", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Constituency> constituencies = new ArrayList<>();

    @Transient
    private final Map<String, Object> metadata = new HashMap<>();

    @Transient
    private List<String> candidateNames = new ArrayList<>();

    @Transient
    private final Map<String, Constituency> constituency = new HashMap<>();

    // Relationship: One election has many parties
    @OneToMany(mappedBy = "election", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Party> parties = new ArrayList<>();

    // Relationship: One election has many party results
    @OneToMany(mappedBy = "election", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<PartyResult> partyResults = new ArrayList<>();

    // Relationship: One election has many municipality results
    @OneToMany(mappedBy = "election", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<MunicipalityResult> municipalityResults = new ArrayList<>();

    public Election() {
    }

    public Election(String id) {
        this.id = id;
    }

    // --------- getters/setters ---------
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<Candidate> getCandidates() {
        return candidates;
    }

    public void addCandidateToList(Candidate candidate) {
        candidate.setElection(this);
        this.candidates.add(candidate);
    }

    /**
     * Find existing candidate by ID and party, or return null
     */
    public Candidate findCandidate(String candidateId, Long id) {
        if (candidateId == null || id == null) {
            return null;
        }
        return candidates.stream()
                .filter(c -> candidateId.equals(c.getId()) && id.equals(c.getPartyId()))
                .findFirst()
                .orElse(null);
    }

    /**
     * Update existing candidate or add new one if not found
     */
    public void addOrUpdateCandidate(Candidate candidate) {
        Candidate existing = findCandidate(candidate.getId(), candidate.getPartyId());
        if (existing != null) {
            // Update existing candidate
            existing.setElected(candidate.isElected());
            existing.setTotalVotes(candidate.getTotalVotes());
            // Update other fields if they were missing
            if (existing.getFirstName() == null && candidate.getFirstName() != null) {
                existing.setFirstName(candidate.getFirstName());
            }
            if (existing.getLastName() == null && candidate.getLastName() != null) {
                existing.setLastName(candidate.getLastName());
            }
            // ALWAYS update shortCode from result file (it's more accurate than initials from candidate list)
            if (candidate.getShortCode() != null) {
                existing.setShortCode(candidate.getShortCode());
            }
        } else {
            // Add new candidate
            addCandidateToList(candidate);
        }
    }

    public List<Candidate> getElectedCandidates() {
        return candidates.stream()
                .filter(Candidate::isElected)
                .collect(Collectors.toList());
    }

    public void addCandidateName(String candidateName) {
        this.candidateNames.add(candidateName);
    }

    public List<String> getCandidateNames() {
        return candidateNames;
    }

    public List<Party> getParties() {
        return parties;
    }

    public void addParty(Party party) {
        // Check if party with same partyId already exists
        boolean exists = this.parties.stream()
                .anyMatch(p -> p.getPartyId() != null && p.getPartyId().equals(party.getPartyId()));

        if (!exists) {
            party.setElection(this);
            this.parties.add(party);
        }
    }

    // Result collection getters and adders
    public List<PartyResult> getPartyResults() {
        return partyResults;
    }

    public void addPartyResult(PartyResult result) {
        result.setElection(this);
        this.partyResults.add(result);
    }

    public List<MunicipalityResult> getMunicipalityResults() {
        return municipalityResults;
    }

    public void addMunicipalityResult(MunicipalityResult result) {
        result.setElection(this);
        this.municipalityResults.add(result);
    }

    /**
     * Calculate seats per party based on elected candidates
     */
    public void calculateSeats() {
        Map<Long, Long> seatsPerParty = candidates.stream()
                .filter(Candidate::isElected)
                .filter(c -> c.getPartyId() != null)
                .collect(Collectors.groupingBy(
                        Candidate::getPartyId,
                        Collectors.counting()));

        for (Party party : parties) {
            long seats = seatsPerParty.getOrDefault(party.getPartyId(), 0L);
            party.setSeats((int) seats);
            if (seats > 0 && !party.getElected()) {
                party.setElected(true);
            }
        }
    }

    /**
     * Calculate percentages for all result entities
     */
    public void calculatePercentages() {
        Map<String, Integer> municipalityTotals = new HashMap<>();

        for (MunicipalityResult result : municipalityResults) {
            String munId = result.getMunicipality().getId();
            municipalityTotals.put(munId,
                    municipalityTotals.getOrDefault(munId, 0) + result.getTotalVotes());
        }

        for (MunicipalityResult result : municipalityResults) {
            String munId = result.getMunicipality().getId();
            int total = municipalityTotals.getOrDefault(munId, 1);
            if (total > 0) {
                double percentage = (result.getTotalVotes() * 100.0) / total;
                result.setPercentage(percentage);
            }
        }

        int nationalTotal = partyResults.stream()
                .mapToInt(PartyResult::getTotalVotes)
                .sum();

        if (nationalTotal > 0) {
            for (PartyResult result : partyResults) {
                double percentage = (result.getTotalVotes() * 100.0) / nationalTotal;
                result.setPercentage(percentage);
            }
        }
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void addMetadata(String key, Object value) {
        this.metadata.put(key, value);
    }

    @Override
    public String toString() {
        return String.format("Election{id='%s', name='%s', parties=%d, candidates=%d, elected=%d}",
                id, name, parties.size(), candidates.size(), getElectedCandidates().size());
    }

    // Constituency helpers
    public List<Constituency> getConstituencies() {
        return constituencies;
    }

    public Constituency getConstituency(String id) {
        if (id == null)
            return null;
        for (Constituency c : constituencies) {
            if (id.equals(c.getId()))
                return c;
        }
        return null;
    }

    public void addConstituency(Constituency con) {
        if (con == null)
            return;
        con.setElection(this);
        constituencies.add(con);
    }

    public Constituency getOrCreateConstituency(String id) {
        if (id == null) {
            return null;
        }
        Constituency existing = getConstituency(id);
        if (existing != null) {
            return existing;
        }
        Constituency con = new Constituency();
        con.setId(id);
        con.setElection(this);
        constituencies.add(con);
        return con;
    }

    public void getParty(Party party) {
        parties.add(party);
    }

}

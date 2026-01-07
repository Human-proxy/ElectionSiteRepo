package nl.hva.dederdekamer.election_backend.XMLParser.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;
import jakarta.persistence.*;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Table;

@Entity
@Table(name = "party", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"party_id", "election_id"}))
public class Party {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    @Column(name = "party_id", nullable = false)
    private Long partyId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "shortcode")
    private String shortcode;

    @Column(name = "color")
    private String color;

    // Relationship: Many parties belong to one election
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "election_id")
    @JsonIgnore
    private Election election;

    // Relationship: One party has many candidates
    @OneToMany(mappedBy = "party", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Candidate> candidates;

    // National results
    @Column(name = "total_votes")
    private Integer totalVotes = 0;

    @Column(name = "seats")
    private Integer seats = 0;

    @Column(name = "elected")
    private Boolean elected = false;

    public Party() {
    }

    public Party(Long partyId, String name, String shortcode) {
        this.partyId = partyId;
        this.name = name;
        this.shortcode = shortcode;
    }

    public Party(Long partyId, String name) {
        this(partyId, name, ""); // Default shortcode to empty string
    }

    // Constructor accepting String partyId (from XML) - converts to Long
    public Party(String partyIdStr, String name) {
        this(parseLongSafely(partyIdStr), name, "");
    }

    // Constructor accepting String partyId with elected flag (from XML)
    public Party(String partyIdStr, String name, boolean elected) {
        this(parseLongSafely(partyIdStr), name, "");
        this.elected = elected;
    }

    // Helper to safely parse String to Long
    private static Long parseLongSafely(String str) {
        if (str == null || str.trim().isEmpty()) {
            return 0L;
        }
        try {
            return Long.parseLong(str.trim());
        } catch (NumberFormatException e) {
            return 0L;
        }
    }

    public Long getId() {
        return id;
    }
    
    public Long getPartyId() {
        return partyId;
    }

    public void setPartyId(Long partyId) {
        this.partyId = partyId;
    }

    public Election getElection() {
        return election;
    }

    public void setElection(Election election) {
        this.election = election;
    }

    public List<Candidate> getCandidates() {
        return candidates;
    }

    public void setCandidates(List<Candidate> candidates) {
        this.candidates = candidates;
    }

    public void addCandidate(Candidate candidate) {
        this.candidates.add(candidate);
        candidate.setParty(this);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortcode() {
        return shortcode;
    }

    public void setShortcode(String shortcode) {
        this.shortcode = shortcode;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Integer getTotalVotes() {
        return totalVotes;
    }

    public void setTotalVotes(Integer totalVotes) {
        this.totalVotes = totalVotes;
    }

    public Integer getSeats() {
        return seats;
    }

    public void setSeats(Integer seats) {
        this.seats = seats;
    }

    public Boolean getElected() {
        return elected;
    }

    public void setElected(Boolean elected) {
        this.elected = elected;
    }

    // Backward compatibility methods
    public boolean isElected() {
        return elected != null && elected;
    }

    public Integer getVotes() {
        return totalVotes;
    }

    public void setVotes(Integer votes) {
        this.totalVotes = votes;
    }

    @Override
    public String toString() {
        return String.format(
                "Party{id='%s', partyId='%s', name='%s', shortcode='%s', totalVotes=%d, seats=%d, elected=%s}",
                id, partyId, name, shortcode, totalVotes, seats, elected);
    }

    /**
     * Finds a party by its party ID within the given election.
     * 
     * @param election The election to search within.
     * @param partyId  The party ID to find.
     * @return The Party if found, otherwise null.
     */
    public static Party findPartyById(Election election, Long partyId) {
        if (election == null || partyId == null) {
            return null;
        }

        for (Party party : election.getParties()) {
            if (partyId.equals(party.getPartyId())) {
                return party;
            }
        }

        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Party party = (Party) o;

        if (!partyId.equals(party.partyId))
            return false;
        if (election != null && party.election != null) {
            return election.getId().equals(party.election.getId());
        }
        return election == party.election;
    }

    /**
     * Generates a hash code for the party based on its party ID and election.
     * This ensures that parties are uniquely identified within the context of an
     * election.
     * 
     * @return hash code
     */
    @Override
    public int hashCode() {
        int result = partyId.hashCode();
        result = 31 * result + (election != null ? election.getId().hashCode() : 0);
        return result;
    }
}

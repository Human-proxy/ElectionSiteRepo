package nl.hva.dederdekamer.election_backend.XMLParser.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

/**
 * Represents election results for a party within a specific municipality.
 * This tracks vote counts and candidate results at the municipality level.
 * 
 * For example: VVD got 15,234 votes in Amsterdam municipality
 */
@Entity
@Table(name = "municipality_result", uniqueConstraints = @UniqueConstraint(columnNames = { "election_id",
        "municipality_id", "party_id" }))
public class MunicipalityResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "election_id", nullable = false)
    @JsonIgnore
    private Election election;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "municipality_id", nullable = false)
    private Municipality municipality;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "party_id", nullable = false)
    private Party party;

    // Vote data
    @Column(name = "total_votes", nullable = false)
    private Integer totalVotes = 0;

    @Column(name = "percentage")
    private Double percentage = 0.0;

    // Constructors
    public MunicipalityResult() {
    }

    public MunicipalityResult(Election election, Municipality municipality, Party party, Integer totalVotes) {
        this.election = election;
        this.municipality = municipality;
        this.party = party;
        this.totalVotes = totalVotes;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Election getElection() {
        return election;
    }

    public void setElection(Election election) {
        this.election = election;
    }

    public Municipality getMunicipality() {
        return municipality;
    }

    public void setMunicipality(Municipality municipality) {
        this.municipality = municipality;
    }

    public Party getParty() {
        return party;
    }

    public void setParty(Party party) {
        this.party = party;
    }

    public Integer getTotalVotes() {
        return totalVotes;
    }

    public void setTotalVotes(Integer totalVotes) {
        this.totalVotes = totalVotes;
    }

    public Double getPercentage() {
        return percentage;
    }

    public void setPercentage(Double percentage) {
        this.percentage = percentage;
    }

    @Override
    public String toString() {
        return String.format("MunicipalityResult{id=%d, municipality='%s', party='%s', votes=%d, percentage=%.2f%%}",
                id,
                municipality != null ? municipality.getName() : "null",
                party != null ? party.getName() : "null",
                totalVotes,
                percentage);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        MunicipalityResult that = (MunicipalityResult) o;

        if (election != null && that.election != null && !election.getId().equals(that.election.getId()))
            return false;
        if (municipality != null && that.municipality != null
                && !municipality.getId().equals(that.municipality.getId()))
            return false;
        if (party != null && that.party != null && !party.getPartyId().equals(that.party.getPartyId()))
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = election != null ? election.getId().hashCode() : 0;
        result = 31 * result + (municipality != null ? municipality.getId().hashCode() : 0);
        result = 31 * result + (party != null ? party.getPartyId().hashCode() : 0);
        return result;
    }
}

package nl.hva.dederdekamer.election_backend.XMLParser.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

/**
 * Represents national-level election results for a party.
 * This is a summary/aggregate view of a party's performance across the entire
 * election.
 * 
 * For example: VVD got 2,345,678 total votes nationally, won 34 seats, elected
 * status
 * 
 * Note: This duplicates some data from Party entity but provides a cleaner
 * separation
 * between party metadata (Party) and party results (PartyResult)
 */
@Entity
@Table(name = "party_result", uniqueConstraints = @UniqueConstraint(columnNames = { "election_id", "party_id" }))
public class PartyResult {

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
    @JoinColumn(name = "party_id", nullable = false)
    private Party party;

    // National results
    @Column(name = "total_votes", nullable = false)
    private Integer totalVotes = 0;

    @Column(name = "percentage")
    private Double percentage = 0.0;

    @Column(name = "seats")
    private Integer seats = 0;

    @Column(name = "elected")
    private Boolean elected = false;

    // Constructors
    public PartyResult() {
    }

    public PartyResult(Election election, Party party) {
        this.election = election;
        this.party = party;
    }

    public PartyResult(Election election, Party party, Integer totalVotes, Integer seats, Boolean elected) {
        this.election = election;
        this.party = party;
        this.totalVotes = totalVotes;
        this.seats = seats;
        this.elected = elected;
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

    @Override
    public String toString() {
        return String.format("PartyResult{id=%d, party='%s', votes=%d, seats=%d, elected=%s, percentage=%.2f%%}",
                id,
                party != null ? party.getName() : "null",
                totalVotes,
                seats,
                elected,
                percentage);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        PartyResult that = (PartyResult) o;

        if (election != null && that.election != null && !election.getId().equals(that.election.getId()))
            return false;
        if (party != null && that.party != null && !party.getPartyId().equals(that.party.getPartyId()))
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = election != null ? election.getId().hashCode() : 0;
        result = 31 * result + (party != null ? party.getPartyId().hashCode() : 0);
        return result;
    }
}

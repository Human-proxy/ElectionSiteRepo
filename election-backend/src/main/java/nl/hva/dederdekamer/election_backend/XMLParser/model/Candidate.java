package nl.hva.dederdekamer.election_backend.XMLParser.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.FetchType;

@Entity
@Table(name = "candidate")
public class Candidate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private Long pk;

    // External ID from XML (not the DB PK)
    @Column(name = "external_id")
    private String id;

    @Column(name = "short_code")
    private String shortCode;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    // Relationship: Many candidates belong to one party
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "party_id")
    @JsonIgnore
    private Party party;

    @Column(name = "elected")
    private boolean elected = false;

    @Column(name = "total_votes")
    private int totalVotes = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "election_id")
    @JsonIgnore
    private Election election;

    public Candidate() {}

    // Constructor for basic candidate with names only
    public Candidate(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    // Constructor for full candidate data
    public Candidate(String id, String shortCode, String firstName, String lastName) {
        this.id = id;
        this.shortCode = shortCode;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    // Getters and setters
    public Long getPk() { return pk; }
    public void setPk(Long pk) { this.pk = pk; }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getShortCode() { return shortCode; }
    public void setShortCode(String shortCode) { this.shortCode = shortCode; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public Party getParty() {
        return party;
    }

    public void setParty(Party party) {
        this.party = party;
    }

    // Helper method for backward compatibility
    public Long getPartyId() {
        return party != null ? party.getPartyId() : null;
    }

    // Helper setter for XML parsing - accepts String and converts to Long
    public void setPartyId(String partyIdStr) {
        if (partyIdStr == null || partyIdStr.trim().isEmpty()) {
            return;
        }
        try {
            Long partyId = Long.parseLong(partyIdStr.trim());
            // Find or create party with this ID
            if (this.election != null) {
                Party foundParty = Party.findPartyById(this.election, partyId);
                if (foundParty != null) {
                    this.party = foundParty;
                }
            }
        } catch (NumberFormatException e) {
            // Ignore invalid party IDs
        }
    }

    public boolean isElected() { return elected; }
    public void setElected(boolean elected) { this.elected = elected; }

    public int getTotalVotes() { return totalVotes; }
    public void setTotalVotes(int totalVotes) { this.totalVotes = totalVotes; }

    public Election getElection() { return election; }
    public void setElection(Election election) { this.election = election; }

    public String getFullName() {
        if (firstName != null && lastName != null) {
            return firstName + " " + lastName;
        }
        return shortCode != null ? shortCode : "";
    }

    @Override
    public String toString() {
        return String.format("Candidate{pk='%s', external_id='%s', name='%s', partyId='%s'}",
                pk, id, getFullName(), getPartyId());
    }
}

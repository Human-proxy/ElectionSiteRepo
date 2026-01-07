package nl.hva.dederdekamer.election_backend.XMLParser.model;

import jakarta.persistence.*;

@Entity
@Table(name = "municipality_party")
public class MunicipalityParty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "party_id")
    private String partyId;

    @Column(name = "name")
    private String name;

    @Column(name = "votes")
    private int votes;

    // Default constructor for JPA
    public MunicipalityParty() {
    }

    public MunicipalityParty(String partyId, String name) {
        this.partyId = partyId;
        this.name = name;
        this.votes = 0;
    }
    
    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPartyId() {
        return partyId;
    }

    public void setPartyId(String partyId) {
        this.partyId = partyId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getVotes() {
        return votes;
    }

    public void setVotes(int votes) {
        this.votes = votes;
    }

    public void addVotes(int votes) {
        this.votes += votes;
    }

    @Override
    public String toString() {
        return "MunicipalityParty{" +
                "id=" + id +
                ", partyId='" + partyId + '\'' +
                ", name='" + name + '\'' +
                ", votes=" + votes +
                '}';
    }
}
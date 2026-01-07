package nl.hva.dederdekamer.election_backend.XMLParser.model;

import jakarta.persistence.*;

@Entity
@Table(name = "municipality_candidate")
public class MunicipalityCandidate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "candidate_id")
    private String candidateId;

    @Column(name = "name")
    private String name;

    @Column(name = "party_id")
    private String partyId;

    @Column(name = "votes")
    private int votes;

    // Default constructor for JPA
    public MunicipalityCandidate() {
    }

    public MunicipalityCandidate(String candidateId, String name, String partyId) {
        this.candidateId = candidateId;
        this.name = name;
        this.partyId = partyId;
        this.votes = 0;
    }
    
    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCandidateId() { 
        return candidateId; 
    }

    public void setCandidateId(String candidateId) {
        this.candidateId = candidateId;
    }

    public String getName() { 
        return name; 
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPartyId() {
        return partyId;
    }

    public void setPartyId(String partyId) {
        this.partyId = partyId;
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
        return "MunicipalityCandidate{" +
                "id=" + id +
                ", candidateId='" + candidateId + '\'' +
                ", name='" + name + '\'' +
                ", partyId='" + partyId + '\'' +
                ", votes=" + votes +
                '}';
    }
}
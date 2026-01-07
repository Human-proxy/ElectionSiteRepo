package nl.hva.dederdekamer.election_backend.XMLParser.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "municipality")
public class Municipality {

    @Id
    private String id;

    @Column(name = "name")
    private String name;

    @ManyToOne
    @JoinColumn(name = "constituency_id")
    @JsonIgnore
    private Constituency constituency;

    public Municipality() {
    }
    
    public Municipality(String id, String name) {
        this.id = id;
        this.name = name;
    }
    
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

    public Constituency getConstituency() {
        return constituency;
    }

    public void setConstituency(Constituency constituency) {
        this.constituency = constituency;
    }

    /*
     * Finds a party by its ID within the given election.
     * 
     * @param election The election containing the parties.
     * 
     * @param id The ID of the party to find.
     */
    public Party getPartyInMunicipality(Election election, Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Party ID cannot be null");
        }

        return Party.findPartyById(election, id);
    }

    @Override
    public String toString() {
        return "Municipality{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Municipality that = (Municipality) o;
        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}

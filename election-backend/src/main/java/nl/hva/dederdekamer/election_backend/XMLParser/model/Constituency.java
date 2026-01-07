package nl.hva.dederdekamer.election_backend.XMLParser.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Transient;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "constituency")
public class Constituency {

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "name")
    private String name;

    @Transient
    private final Map<String, Party> parties = new HashMap<>();

    @ManyToOne
    @JoinColumn(name = "election_id")
    @JsonIgnore
    private Election election;

    @OneToMany(mappedBy = "constituency", cascade = CascadeType.ALL, fetch = FetchType.LAZY, targetEntity = Municipality.class)
    private List<Municipality> municipalities;

    public Constituency() {
    }

    public Constituency(String id, String name) {
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

    public List<Municipality> getMunicipalities() {
        return municipalities;
    }

    public void setMunicipalities(List<Municipality> municipalities) {
        this.municipalities = municipalities;
    }

    public Election getElection() {
        return election;
    }

    public void setElection(Election election) {
        this.election = election;
    }

    /**
     * Return the in-memory parties map for this constituency.
     */
    public Map<String, Party> getParties() {
        return parties;
    }

    /**
     * Add or update party vote counts for this constituency.
     * If the party does not exist yet, a new Party object is created and stored
     * transiently.
     * Votes are summed so multiple XML fragments contributing to the same party are
     * accumulated.
     */
    public void addOrUpdatePartyVotes(String partyId, String partyName) {
        if (partyId == null)
            return;
        Party existing = parties.get(partyId);
        if (existing == null) {
            Party p = new Party(partyId, partyName == null ? "" : partyName);
            p.setElection(this.election);
            parties.put(partyId, p);
        }
    }

    /**
     * Add a municipality to this constituency if it's not already present.
     * If a municipality with the same ID exists, return the existing one.
     */
    public void addMunicipalityIfAbsent(Municipality municipality) {
        if (municipality == null) {
            return;
        }
        
        // Initialize list if null
        if (this.municipalities == null) {
            this.municipalities = new ArrayList<>();
        }

        // Check if municipality already exists
        for (Municipality existing : this.municipalities) {
            if (existing.getId().equals(municipality.getId())) {
                return;
            }
        }
        
        // Not found, add it
        this.municipalities.add(municipality);
    }

    /**
     * Get the list of municipalities in this constituency
     */
    public List<Municipality> getMunicipality() {
        if (this.municipalities == null) {
            this.municipalities = new ArrayList<>();
        }
        return this.municipalities;
    }
}

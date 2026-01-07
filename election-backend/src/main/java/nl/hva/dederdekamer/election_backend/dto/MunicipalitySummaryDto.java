package nl.hva.dederdekamer.election_backend.dto;

/**
 * Lightweight municipality summary for map display
 */
public class MunicipalitySummaryDto {
    private String id;
    private String name;
    private int totalVotes;
    
    public MunicipalitySummaryDto() {}
    
    public MunicipalitySummaryDto(String id, String name, int totalVotes) {
        this.id = id;
        this.name = name;
        this.totalVotes = totalVotes;
    }
    
    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public int getTotalVotes() { return totalVotes; }

    public void setTotalVotes(int totalVotes) {
        this.totalVotes = totalVotes;
    }
}
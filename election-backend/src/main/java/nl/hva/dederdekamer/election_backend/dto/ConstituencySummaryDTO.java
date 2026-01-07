package nl.hva.dederdekamer.election_backend.dto;

/**
 * Data Transfer Object for constituency summary information.
 * Used for dashboard/homepage insights showing top constituencies.
 */
public class ConstituencySummaryDTO {
    
    private String constituencyId;
    private String constituencyName;
    private Integer totalVotesCast;
    private String topPartyName;
    private String topPartyColor;
    private Integer topPartyVotes;
    private Double topPartyPercentage;
    private Integer municipalityCount;

    public ConstituencySummaryDTO() {
    }

    public ConstituencySummaryDTO(String constituencyId, String constituencyName, Integer totalVotesCast, 
                                  String topPartyName, String topPartyColor, Integer topPartyVotes, 
                                  Double topPartyPercentage, Integer municipalityCount) {
        this.constituencyId = constituencyId;
        this.constituencyName = constituencyName;
        this.totalVotesCast = totalVotesCast;
        this.topPartyName = topPartyName;
        this.topPartyColor = topPartyColor;
        this.topPartyVotes = topPartyVotes;
        this.topPartyPercentage = topPartyPercentage;
        this.municipalityCount = municipalityCount;
    }

    // Getters and Setters
    public String getConstituencyId() {
        return constituencyId;
    }

    public void setConstituencyId(String constituencyId) {
        this.constituencyId = constituencyId;
    }

    public String getConstituencyName() {
        return constituencyName;
    }

    public void setConstituencyName(String constituencyName) {
        this.constituencyName = constituencyName;
    }

    public Integer getTotalVotesCast() {
        return totalVotesCast;
    }

    public void setTotalVotesCast(Integer totalVotesCast) {
        this.totalVotesCast = totalVotesCast;
    }

    public String getTopPartyName() {
        return topPartyName;
    }

    public void setTopPartyName(String topPartyName) {
        this.topPartyName = topPartyName;
    }

    public String getTopPartyColor() {
        return topPartyColor;
    }

    public void setTopPartyColor(String topPartyColor) {
        this.topPartyColor = topPartyColor;
    }

    public Integer getTopPartyVotes() {
        return topPartyVotes;
    }

    public void setTopPartyVotes(Integer topPartyVotes) {
        this.topPartyVotes = topPartyVotes;
    }

    public Double getTopPartyPercentage() {
        return topPartyPercentage;
    }

    public void setTopPartyPercentage(Double topPartyPercentage) {
        this.topPartyPercentage = topPartyPercentage;
    }

    public Integer getMunicipalityCount() {
        return municipalityCount;
    }

    public void setMunicipalityCount(Integer municipalityCount) {
        this.municipalityCount = municipalityCount;
    }

    @Override
    public String toString() {
        return "ConstituencySummaryDTO{" +
                "constituencyId='" + constituencyId + '\'' +
                ", constituencyName='" + constituencyName + '\'' +
                ", totalVotesCast=" + totalVotesCast +
                ", topPartyName='" + topPartyName + '\'' +
                ", topPartyColor='" + topPartyColor + '\'' +
                ", topPartyVotes=" + topPartyVotes +
                ", topPartyPercentage=" + topPartyPercentage +
                ", municipalityCount=" + municipalityCount +
                '}';
    }
}

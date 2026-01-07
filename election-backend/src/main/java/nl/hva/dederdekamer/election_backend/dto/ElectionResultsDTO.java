package nl.hva.dederdekamer.election_backend.dto;

import java.util.List;

/**
 * DTO for election results including KPI data
 */
public class ElectionResultsDTO {
    private String electionId;
    private String electionName;
    private List<PartyResultDTO> parties;
    private KPIData kpis;

    public static class PartyResultDTO {
        private Long id;
        private String name;
        private Long partyId;
        private Integer votes;
        private Integer seats;
        private Boolean elected;
        private String color;

        public PartyResultDTO(Long id, String name, Long partyId, Integer votes, Integer seats, Boolean elected, String color) {
            this.id = id;
            this.name = name;
            this.partyId = partyId;
            this.votes = votes;
            this.seats = seats;
            this.elected = elected;
            this.color = color;
        }

        // Getters and setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public Long getPartyId() { return partyId; }
        public void setPartyId(Long partyId) { this.partyId = partyId; }

        public Integer getVotes() { return votes; }
        public void setVotes(Integer votes) { this.votes = votes; }

        public Integer getSeats() { return seats; }
        public void setSeats(Integer seats) { this.seats = seats; }

        public Boolean getElected() { return elected; }
        public void setElected(Boolean elected) { this.elected = elected; }

        public String getColor() { return color; }
        public void setColor(String color) { this.color = color; }
    }

    public static class KPIData {
        private String winningPartyName;
        private Integer winningPartySeats;
        private Long totalVotes;
        private Integer electedPartiesCount;
        private Integer totalPartiesCount;

        public KPIData(String winningPartyName, Integer winningPartySeats, Long totalVotes, 
                      Integer electedPartiesCount, Integer totalPartiesCount) {
            this.winningPartyName = winningPartyName;
            this.winningPartySeats = winningPartySeats;
            this.totalVotes = totalVotes;
            this.electedPartiesCount = electedPartiesCount;
            this.totalPartiesCount = totalPartiesCount;
        }

        // Getters and setters
        public String getWinningPartyName() { return winningPartyName; }
        public void setWinningPartyName(String winningPartyName) { this.winningPartyName = winningPartyName; }

        public Integer getWinningPartySeats() { return winningPartySeats; }
        public void setWinningPartySeats(Integer winningPartySeats) { this.winningPartySeats = winningPartySeats; }

        public Long getTotalVotes() { return totalVotes; }
        public void setTotalVotes(Long totalVotes) { this.totalVotes = totalVotes; }

        public Integer getElectedPartiesCount() { return electedPartiesCount; }
        public void setElectedPartiesCount(Integer electedPartiesCount) { this.electedPartiesCount = electedPartiesCount; }

        public Integer getTotalPartiesCount() { return totalPartiesCount; }
        public void setTotalPartiesCount(Integer totalPartiesCount) { this.totalPartiesCount = totalPartiesCount; }
    }

    public ElectionResultsDTO(String electionId, String electionName, List<PartyResultDTO> parties, KPIData kpis) {
        this.electionId = electionId;
        this.electionName = electionName;
        this.parties = parties;
        this.kpis = kpis;
    }

    // Getters and setters
    public String getElectionId() { return electionId; }
    public void setElectionId(String electionId) { this.electionId = electionId; }

    public String getElectionName() { return electionName; }
    public void setElectionName(String electionName) { this.electionName = electionName; }

    public List<PartyResultDTO> getParties() { return parties; }
    public void setParties(List<PartyResultDTO> parties) { this.parties = parties; }

    public KPIData getKpis() { return kpis; }
    public void setKpis(KPIData kpis) { this.kpis = kpis; }
}

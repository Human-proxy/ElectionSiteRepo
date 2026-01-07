package nl.hva.dederdekamer.election_backend.dto;

import java.util.List;

public class MunicipalityResultsDTO {
    private String municipalityName;
    private String municipalityId;
    private List<PartyResultDTO> partyResults;

    public MunicipalityResultsDTO() {
    }

    public MunicipalityResultsDTO(String municipalityName, String municipalityId, List<PartyResultDTO> partyResults) {
        this.municipalityName = municipalityName;
        this.municipalityId = municipalityId;
        this.partyResults = partyResults;
    }

    public String getMunicipalityName() {
        return municipalityName;
    }

    public void setMunicipalityName(String municipalityName) {
        this.municipalityName = municipalityName;
    }

    public String getMunicipalityId() {
        return municipalityId;
    }

    public void setMunicipalityId(String municipalityId) {
        this.municipalityId = municipalityId;
    }

    public List<PartyResultDTO> getPartyResults() {
        return partyResults;
    }

    public void setPartyResults(List<PartyResultDTO> partyResults) {
        this.partyResults = partyResults;
    }

    public static class PartyResultDTO {
        private Long partyId;
        private String partyName;
        private String partyShortcode;
        private Integer totalVotes;
        private Double percentage;
        private String color;

        public PartyResultDTO() {
        }

        public PartyResultDTO(Long partyId, String partyName, String partyShortcode, Integer totalVotes,
                Double percentage, String color) {
            this.partyId = partyId;
            this.partyName = partyName;
            this.partyShortcode = partyShortcode;
            this.totalVotes = totalVotes;
            this.percentage = percentage;
            this.color = color;
        }

        public Long getPartyId() {
            return partyId;
        }

        public void setPartyId(Long partyId) {
            this.partyId = partyId;
        }

        public String getPartyName() {
            return partyName;
        }

        public void setPartyName(String partyName) {
            this.partyName = partyName;
        }

        public String getPartyShortcode() {
            return partyShortcode;
        }

        public void setPartyShortcode(String partyShortcode) {
            this.partyShortcode = partyShortcode;
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

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }
    }
}

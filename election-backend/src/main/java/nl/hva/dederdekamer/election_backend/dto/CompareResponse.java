package nl.hva.dederdekamer.election_backend.dto;

import java.util.List;

public class CompareResponse {
    private int year1;
    private int year2;
    private List<PartySeatsComparison> parties;

    public CompareResponse() {}

    public CompareResponse(int year1, int year2, List<PartySeatsComparison> parties) {
        this.year1 = year1;
        this.year2 = year2;
        this.parties = parties;
    }

    public int getYear1() { return year1; }
    public void setYear1(int year1) { this.year1 = year1; }

    public int getYear2() { return year2; }
    public void setYear2(int year2) { this.year2 = year2; }

    public List<PartySeatsComparison> getParties() { return parties; }
    public void setParties(List<PartySeatsComparison> parties) { this.parties = parties; }
}

package nl.hva.dederdekamer.election_backend.dto;

public class PartySeatsComparison {
    private String partyId;
    private String partyName;
    private int seatsYear1;
    private int seatsYear2;
    private int difference; // seatsYear2 - seatsYear1

    public PartySeatsComparison() {}

    public PartySeatsComparison(String partyId, String partyName, int seatsYear1, int seatsYear2) {
        this.partyId = partyId;
        this.partyName = partyName;
        this.seatsYear1 = seatsYear1;
        this.seatsYear2 = seatsYear2;
        this.difference = seatsYear2 - seatsYear1;
    }

    public String getPartyId() { return partyId; }
    public void setPartyId(String partyId) { this.partyId = partyId; }

    public String getPartyName() { return partyName; }
    public void setPartyName(String partyName) { this.partyName = partyName; }

    public int getSeatsYear1() { return seatsYear1; }
    public void setSeatsYear1(int seatsYear1) { this.seatsYear1 = seatsYear1; recalc(); }

    public int getSeatsYear2() { return seatsYear2; }
    public void setSeatsYear2(int seatsYear2) { this.seatsYear2 = seatsYear2; recalc(); }

    public int getDifference() { return difference; }

    private void recalc() { this.difference = this.seatsYear2 - this.seatsYear1; }
}

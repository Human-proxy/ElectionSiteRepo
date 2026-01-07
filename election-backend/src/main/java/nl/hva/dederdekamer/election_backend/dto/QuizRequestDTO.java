package nl.hva.dederdekamer.election_backend.dto;

/**
 * Data Transfer Object representing the user's quiz answers.
 *
 * This object captures the four values selected in the quiz:
 * - the election year
 * - the selected party ID
 * - the selected region (for example "Amsterdam" or "Nederland")
 * - the data type (for example "SEATS", "VOTES" or "PERCENTAGE")
 */
public class QuizRequestDTO {

    /** The selected election year. */
    private String year;

    /** The ID of the selected political party. */
    private Long partyId;

    /** The selected region for filtering results. */
    private String region;

    /** The type of data requested, such as SEATS, VOTES or PERCENTAGE. */
    private String dataType;

    /** Default constructor required for deserialization frameworks. */
    public QuizRequestDTO() {}

    /** @return the selected year */
    public String getYear() { return year; }

    /** @param year sets the selected year */
    public void setYear(String year) { this.year = year; }

    /** @return the selected party ID */
    public Long getPartyId() { return partyId; }

    /** @param partyId sets the selected party ID */
    public void setPartyId(Long partyId) { this.partyId = partyId; }

    /** @return the selected region */
    public String getRegion() { return region; }

    /** @param region sets the selected region */
    public void setRegion(String region) { this.region = region; }

    /** @return the requested data type */
    public String getDataType() { return dataType; }

    /** @param dataType sets the requested data type */
    public void setDataType(String dataType) { this.dataType = dataType; }
}

package nl.hva.dederdekamer.election_backend.dto;

/**
 * Data Transfer Object representing the quiz result.
 *
 * This object contains:
 * - the selected party's name
 * - the selected region's name
 * - the label of the requested metric (for example "Aantal Stemmen")
 * - the formatted value shown to the user (for example "1.230" or "12%")
 * - a narrative sentence describing the result
 */
public class QuizResponseDTO {

    /** The name of the selected political party. */
    private String partyName;

    /** The name of the selected region. */
    private String regionName;

    /** The label of the metric returned to the user. */
    private String metricLabel;

    /** The formatted metric value, ready for display. */
    private String formattedValue;

    /** A narrative sentence describing the result. */
    private String narrative;

    /**
     * Creates a result record containing all quiz response fields.
     *
     * @param partyName the name of the selected party
     * @param regionName the name of the selected region
     * @param metricLabel the human-readable label for the metric
     * @param formattedValue the formatted metric value
     * @param narrative the descriptive narrative sentence
     */
    public QuizResponseDTO(String partyName, String regionName, String metricLabel,
                           String formattedValue, String narrative) {
        this.partyName = partyName;
        this.regionName = regionName;
        this.metricLabel = metricLabel;
        this.formattedValue = formattedValue;
        this.narrative = narrative;
    }

    /** @return the selected party's name */
    public String getPartyName() { return partyName; }

    /** @param partyName sets the selected party's name */
    public void setPartyName(String partyName) { this.partyName = partyName; }

    /** @return the selected region's name */
    public String getRegionName() { return regionName; }

    /** @param regionName sets the selected region's name */
    public void setRegionName(String regionName) { this.regionName = regionName; }

    /** @return the label of the returned metric */
    public String getMetricLabel() { return metricLabel; }

    /** @param metricLabel sets the label of the returned metric */
    public void setMetricLabel(String metricLabel) { this.metricLabel = metricLabel; }

    /** @return the formatted metric value */
    public String getFormattedValue() { return formattedValue; }

    /** @param formattedValue sets the formatted metric value */
    public void setFormattedValue(String formattedValue) { this.formattedValue = formattedValue; }

    /** @return the narrative sentence */
    public String getNarrative() { return narrative; }

    /** @param narrative sets the narrative sentence */
    public void setNarrative(String narrative) { this.narrative = narrative; }
}

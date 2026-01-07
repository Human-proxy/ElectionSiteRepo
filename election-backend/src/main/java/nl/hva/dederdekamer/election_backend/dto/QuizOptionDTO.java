package nl.hva.dederdekamer.election_backend.dto;

/**
 * Data Transfer Object representing a selectable option in a quiz question.
 * 
 */
public class QuizOptionDTO {

    /** The raw value sent to the backend (e.g. an ID, enum, or integer). */
    private Object value;

    /** The label text displayed to the user. */
    private String label;

    /** Optional descriptive text shown under the label. */
    private String description;

    /**
     * Creates a quiz option with a value and a label.
     *
     * @param value the backend value associated with this option
     * @param label the user-facing text displayed for this option
     */
    public QuizOptionDTO(Object value, String label) {
        this.value = value;
        this.label = label;
    }

    /**
     * Creates a quiz option with a value, a label and a description.
     *
     * @param value the backend value associated with this option
     * @param label the user-facing text displayed for this option
     * @param description additional descriptive text for the UI
     */
    public QuizOptionDTO(Object value, String label, String description) {
        this.value = value;
        this.label = label;
        this.description = description;
    }

    /** @return the backend value of this option */
    public Object getValue() { return value; }

    /** @param value sets the backend value for this option */
    public void setValue(Object value) { this.value = value; }

    /** @return the label shown to the user */
    public String getLabel() { return label; }

    /** @param label sets the user-facing label text */
    public void setLabel(String label) { this.label = label; }

    /** @return the optional description */
    public String getDescription() { return description; }

    /** @param description sets the optional descriptive text */
    public void setDescription(String description) { this.description = description; }
}

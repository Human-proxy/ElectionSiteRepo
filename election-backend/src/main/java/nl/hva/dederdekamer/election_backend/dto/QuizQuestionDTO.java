package nl.hva.dederdekamer.election_backend.dto;

import java.util.List;

/**
 * Data Transfer Object representing a complete quiz question.
 *
 * A quiz question contains:
 * - a unique identifier
 * - the question text
 * - the question type (for example: SELECT, SEARCHABLE_SELECT, CARDS)
 * - a list of available answer options
 */
public class QuizQuestionDTO {

    /** The unique identifier of this question. */
    private String id;

    /** The full text displayed to the user as the question. */
    private String text;

    /** The type of question, such as SELECT, SEARCHABLE_SELECT or CARDS. */
    private String type;

    /** The list of answer options available for this question. */
    private List<QuizOptionDTO> options;

    /**
     * Creates a quiz question with all required fields.
     *
     * @param id the unique identifier of the question
     * @param text the text shown to the user
     * @param type the question type used by the frontend
     * @param options the list of selectable answer options
     */
    public QuizQuestionDTO(String id, String text, String type, List<QuizOptionDTO> options) {
        this.id = id;
        this.text = text;
        this.type = type;
        this.options = options;
    }

    /** @return the unique identifier of the question */
    public String getId() { return id; }

    /** @param id sets the unique identifier for the question */
    public void setId(String id) { this.id = id; }

    /** @return the question text */
    public String getText() { return text; }

    /** @param text sets the text shown to the user */
    public void setText(String text) { this.text = text; }

    /** @return the type of the question */
    public String getType() { return type; }

    /** @param type sets the question type */
    public void setType(String type) { this.type = type; }

    /** @return the list of answer options */
    public List<QuizOptionDTO> getOptions() { return options; }

    /** @param options sets the list of answer options */
    public void setOptions(List<QuizOptionDTO> options) { this.options = options; }
}

package nl.hva.dederdekamer.election_backend.XMLParser.exception;

/**
 * Custom exception for election processing errors.
 * This provides better error handling than generic exceptions and allows
 * for more specific error messages and handling strategies.
 */
public class ElectionProcessingException extends RuntimeException {

    private final String electionId;
    private final String folderName;

    /**
     * Creates a new ElectionProcessingException with detailed context information.
     *
     * @param message The error message describing what went wrong
     * @param cause The underlying cause of the exception
     * @param electionId The ID of the election being processed when the error occurred
     * @param folderName The folder name being processed when the error occurred
     */
    public ElectionProcessingException(String message, Throwable cause, String electionId, String folderName) {
        super(String.format("Failed to process election '%s' from folder '%s': %s", electionId, folderName, message), cause);
        this.electionId = electionId;
        this.folderName = folderName;
    }

    /**
     * Creates a new ElectionProcessingException with a simple message.
     *
     * @param message The error message
     * @param electionId The ID of the election being processed
     * @param folderName The folder name being processed
     */
    public ElectionProcessingException(String message, String electionId, String folderName) {
        super(String.format("Failed to process election '%s' from folder '%s': %s", electionId, folderName, message));
        this.electionId = electionId;
        this.folderName = folderName;
    }

    public String getElectionId() {
        return electionId;
    }

    public String getFolderName() {
        return folderName;
    }
}
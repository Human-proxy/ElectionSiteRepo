package nl.hva.dederdekamer.election_backend.exception;

/**
 * Exception thrown when a request contains invalid parameters
 */
public class InvalidRequestException extends RuntimeException {

    public InvalidRequestException(String message) {
        super(message);
    }

    public InvalidRequestException(String field, String reason) {
        super(String.format("Invalid %s: %s", field, reason));
    }
}

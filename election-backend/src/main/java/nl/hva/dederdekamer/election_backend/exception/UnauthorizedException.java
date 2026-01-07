package nl.hva.dederdekamer.election_backend.exception;

/**
 * Thrown when credentials are invalid or authentication fails.
 */
public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) { super(message); }
}

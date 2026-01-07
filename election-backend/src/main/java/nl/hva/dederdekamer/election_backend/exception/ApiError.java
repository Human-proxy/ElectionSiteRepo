package nl.hva.dederdekamer.election_backend.exception;

import java.time.OffsetDateTime;

/**
 * Uniform error response body for client-friendly errors.
 */
public class ApiError {
    private final String path;
    private final int status;
    private final String error;
    private final String message;
    private final OffsetDateTime timestamp = OffsetDateTime.now();

    public ApiError(String path, int status, String error, String message) {
        this.path = path;
        this.status = status;
        this.error = error;
        this.message = message;
    }

    public String getPath() { return path; }
    public int getStatus() { return status; }
    public String getError() { return error; }
    public String getMessage() { return message; }
    public OffsetDateTime getTimestamp() { return timestamp; }
}

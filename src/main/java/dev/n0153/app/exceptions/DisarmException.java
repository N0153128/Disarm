package dev.n0153.app.exceptions;

/**
 * Base exception for all j-disarm-img library errors.
 * All custom exceptions extend this for consistent hierarchy.
 */
public class DisarmException extends RuntimeException {

    public DisarmException(String message) {
        super(message);
    }

    public DisarmException(String message, Throwable cause) {
        super(message, cause);
    }

    public DisarmException(Throwable cause) {
        super(cause);
    }
}
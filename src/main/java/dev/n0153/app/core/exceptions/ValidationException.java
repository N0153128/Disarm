package dev.n0153.app.core.exceptions;

public class ValidationException extends DisarmException {

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}

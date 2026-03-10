package dev.n0153.app.exceptions;

public class DurationFormattingException extends RuntimeException {

    private final String detectedString;

    public DurationFormattingException(String message, String string) {
        super(message);
        this.detectedString = string;
    }

    public DurationFormattingException(String message, String string, Throwable cause) {
        super(message, cause);
        this.detectedString = string;
    }

    public String getDetectedString() {
        return detectedString;
    }

    @Override
    public String getMessage() {
        return super.getMessage() + "[detectedString: " + detectedString + "]";
    }
}

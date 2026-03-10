package dev.n0153.app.exceptions;

public class UnsupportedFileTypeException extends IllegalArgumentException {

    private final String detectedType;

    public UnsupportedFileTypeException(String message, String type) {
        super(message);
        this.detectedType = type;
    }

    public UnsupportedFileTypeException(String message, String detectedType, Throwable cause) {
        super(message, cause);
        this.detectedType = detectedType;
    }

    public String getDetectedType() {
        return detectedType;
    }

    @Override
    public String getMessage() {
        return super.getMessage() + "[detectedType: " + detectedType + "]";
    }
}


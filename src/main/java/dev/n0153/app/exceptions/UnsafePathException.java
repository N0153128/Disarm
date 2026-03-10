package dev.n0153.app.exceptions;

import java.nio.file.Path;

public class UnsafePathException extends DisarmException {

    private final Path detectedPath;

    public UnsafePathException(String message, Path path) {
        super(message);
        this.detectedPath = path;
    }

    public UnsafePathException(String message, Path path, Throwable cause) {
        super(message, cause);
        this.detectedPath = path;
    }

    public Path getDetectedPath() {
        return detectedPath;
    }

    @Override
    public String getMessage() {
        return super.getMessage()+"[detectedPath: " + detectedPath + "]";
    }

}

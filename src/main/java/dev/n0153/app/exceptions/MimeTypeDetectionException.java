package dev.n0153.app.exceptions;

import java.io.IOException;
import java.nio.file.Path;

public class MimeTypeDetectionException extends IOException {

    private final Path pathToFile;

    public MimeTypeDetectionException(String message, Path path) {
        super(message);
        this.pathToFile = path;
    }

    public MimeTypeDetectionException(String message, Path path, Throwable cause) {
        super(message, cause);
        this.pathToFile = path;
    }

    public Path getDetectedMime() {
        return pathToFile;
    }

    @Override
    public String getMessage() {
        return super.getMessage() + "[pathToFile: " + pathToFile + "]";
    }
}

package dev.n0153.app.exceptions;

import java.nio.file.Path;

public class CodecDetectionException extends RuntimeException {

    private final Path pathToFile;

    public CodecDetectionException(String message, Path path) {
        super(message);
        this.pathToFile = path;
    }

    public CodecDetectionException(String message, Path path, Throwable cause) {
        super(message, cause);
        this.pathToFile = path;
    }

    public Path getPathToFile() {
        return pathToFile;
    }

    @Override
    public String getMessage() {
        return super.getMessage() + "[pathToFile: " + pathToFile + "]";
    }
}

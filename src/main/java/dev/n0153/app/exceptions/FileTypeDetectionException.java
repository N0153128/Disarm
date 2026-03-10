package dev.n0153.app.exceptions;

import java.io.IOException;
import java.nio.file.Path;

public class FileTypeDetectionException extends IOException {

    private final Path pathToFile;

    public FileTypeDetectionException(String message, Path path) {
        super(message);
        this.pathToFile = path;
    }

    public FileTypeDetectionException(String message, Path path, Throwable cause) {
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

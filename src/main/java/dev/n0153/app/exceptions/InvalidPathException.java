package dev.n0153.app.exceptions;

import java.nio.file.Path;

public class InvalidPathException extends DisarmException {

    private final Path path;
    public InvalidPathException(String message, Path path) {
        super(message);
        this.path = path;
    }

    public InvalidPathException(String message, Path path, Throwable cause) {
        super(message, cause);
        this.path = path;
    }

    public Path getPath() {
        return path;
    }

    @Override
    public String getMessage() {
        return super.getMessage() + "[Path:" + path + "]";
    }
}

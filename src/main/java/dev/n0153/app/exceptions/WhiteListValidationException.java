package dev.n0153.app.exceptions;

import java.nio.file.Path;

public class WhiteListValidationException extends DisarmException{

    private final Path pathToFile;

    public WhiteListValidationException(String message, Path osTargetPath) {
        super(message);
        this.pathToFile = osTargetPath;
    }

    public WhiteListValidationException(String message, Path osTargetPath, Throwable cause) {
        super(message, cause);
        this.pathToFile = osTargetPath;
    }

    public Path getPathToText() {
        return pathToFile;
    }


    @Override
    public String getMessage() {
        return super.getMessage() + "[pathToFile: " + pathToFile + "]";
    }
}

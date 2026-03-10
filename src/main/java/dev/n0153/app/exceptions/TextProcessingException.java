package dev.n0153.app.exceptions;

import java.nio.file.Path;

public class TextProcessingException extends DisarmException{

    private final Path pathToText;

    public TextProcessingException(String message, Path osTargetPath) {
        super(message);
        this.pathToText = osTargetPath;
    }

    public TextProcessingException(String message, Path osTargetPath, Throwable cause) {
        super(message, cause);
        this.pathToText = osTargetPath;
    }

    public Path getPathToText() {
        return pathToText;
    }


    @Override
    public String getMessage() {
        return super.getMessage() + "[pathToText: " + pathToText + "]";
    }
}

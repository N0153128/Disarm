package dev.n0153.app.exceptions;

import java.nio.file.Path;

public class AudioProcessingException extends DisarmException{

    private final Path pathToAudio;

    public AudioProcessingException(String message, Path osTargetPath) {
        super(message);
        this.pathToAudio = osTargetPath;
    }

    public AudioProcessingException(String message, Path osTargetPath, Throwable cause) {
        super(message, cause);
        this.pathToAudio = osTargetPath;
    }

    public Path getPathToAudio() {
        return pathToAudio;
    }


    @Override
    public String getMessage() {
        return super.getMessage()+"[pathToAudio: " + pathToAudio + "]";
    }
}

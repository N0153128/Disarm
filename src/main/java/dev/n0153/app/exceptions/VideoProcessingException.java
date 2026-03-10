package dev.n0153.app.exceptions;

import java.nio.file.Path;

public class VideoProcessingException extends DisarmException{

    private final Path pathToVideo;

    public VideoProcessingException(String message, Path osTargetPath) {
        super(message);
        this.pathToVideo = osTargetPath;
    }

    public VideoProcessingException(String message, Path osTargetPath, Throwable cause) {
        super(message, cause);
        this.pathToVideo = osTargetPath;
    }

    public Path getPathToVideo() {
        return pathToVideo;
    }


    @Override
    public String getMessage() {
        return super.getMessage()+"[pathToVideo: " + pathToVideo + "]";
    }
}

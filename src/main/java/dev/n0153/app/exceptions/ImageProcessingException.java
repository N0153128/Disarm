package dev.n0153.app.exceptions;

import java.nio.file.Path;

public class ImageProcessingException extends DisarmException{

    private final Path imagePath;

    public ImageProcessingException(String message, Path osImagePath) {
        super(message);
        this.imagePath = osImagePath;
    }

    public ImageProcessingException(String message, Path osImagePath, Throwable cause) {
        super(message, cause);
        this.imagePath = osImagePath;
    }

    public Path getImagePath() {
        return imagePath;
    }

    @Override
    public String getMessage() {
        return super.getMessage() + "[Image:" + imagePath + "]";
    }
}

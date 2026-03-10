package dev.n0153.app.exceptions;

import java.nio.file.Path;

public class ImageScalingException extends  ImageProcessingException{

    public ImageScalingException(String message, Path imagePath, Throwable cause) {
        super(message, imagePath, cause);
    }
}

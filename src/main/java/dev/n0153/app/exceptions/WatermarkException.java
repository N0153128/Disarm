package dev.n0153.app.exceptions;

import java.nio.file.Path;

public class WatermarkException extends ImageProcessingException{

    public WatermarkException(String message, Path pathToImage, Throwable cause) {
        super(message, pathToImage, cause);
    }
}

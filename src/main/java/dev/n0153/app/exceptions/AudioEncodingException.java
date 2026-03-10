package dev.n0153.app.exceptions;

import java.nio.file.Path;

public class AudioEncodingException extends AudioProcessingException{

    public AudioEncodingException(String message, Path pathToAudio, Throwable cause) {
        super(message, pathToAudio, cause);
    }
}

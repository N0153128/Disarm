package dev.n0153.app.exceptions;

import java.nio.file.Path;

public class BitrateValidationException extends DisarmException{

    private final String mime;

    public BitrateValidationException(String message, String mimeType) {
        super(message);
        this.mime = mimeType;
    }

    public BitrateValidationException(String message, String mimeType, Throwable cause) {
        super(message, cause);
        this.mime = mimeType;
    }

    public String getMime() {
        return mime;
    }


    @Override
    public String getMessage() {
        return super.getMessage() + "[mime: " + mime + "]";
    }
}

package dev.n0153.app.exceptions;

import java.nio.file.Path;

public class SamplingRateValidationException extends DisarmException{

    private final String mime;

    public SamplingRateValidationException(String message, String mimeType) {
        super(message);
        this.mime = mimeType;
    }

    public SamplingRateValidationException(String message, String mimeType, Throwable cause) {
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

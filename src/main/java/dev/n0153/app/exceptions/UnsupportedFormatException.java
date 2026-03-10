package dev.n0153.app.exceptions;

import java.nio.file.Path;

public class UnsupportedFormatException extends DisarmException {

    private final String fileFormat;

    public UnsupportedFormatException(String message, String argFileFormat) {
        super(message);
        this.fileFormat = argFileFormat;
    }

    public UnsupportedFormatException(String message, String argFileFormat, Throwable cause) {
        super(message, cause);
        this.fileFormat = argFileFormat;
    }

    public String getFileFormat() {
        return fileFormat;
    }

    @Override
    public String getMessage() {
        return super.getMessage()+"[fileFormat: " + fileFormat + "]";
    }

}

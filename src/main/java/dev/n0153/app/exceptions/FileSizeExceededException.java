package dev.n0153.app.exceptions;

public class FileSizeExceededException extends DisarmException {

    private final int fileSize;
    private final int sizeLimit;

    public FileSizeExceededException(String message, int size, int limit) {
        super(message);
        this.fileSize = size;
        this.sizeLimit = limit;
    }

    public FileSizeExceededException(String message, int size, int limit, Throwable cause) {
        super(message, cause);
        this.fileSize = size;
        this.sizeLimit = limit;
    }

    public int getFileSize() {
        return fileSize;
    }

    public int getSizeLimit() {
        return sizeLimit;
    }

    @Override
    public String getMessage() {
        return super.getMessage() + "[fileSize: " + fileSize + ", sizeLimit: "+ sizeLimit +"]";
    }
}

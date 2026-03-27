package dev.n0153.app;

import java.nio.file.Path;

/**
 * This interface holds context data for runtime.
 */
public interface ProcessingContext {

    /**
     * Get unique ID for current file instance.
     * @return unique file ID
     */
    int getId();

    /**
     * Get MIME type for current file instance.
     * @return MIME type
     */
    String getMime();

    /**
     * Get path to file for current file instance.
     * @return path to file
     */
    Path getOsTargetPath();

    /**
     * Get file media type for current file instance.
     * @return media type (image, video, etc)
     */
    String getFileType();

    /**
     * Get unique file title for current file instance.
     * @return file title
     */
    String getGeneralFileTitle();

    /**
     *
     * @return file size
     */
    int getFileSize();

    /**
     * Sets unique ID for a file instance.
     */
    void setId();

    /**
     * Sets MIME type for a file instance.
     */
    void setMime();

    /**
     * Sets path to file.
     */
    void setOsTargetPath();

    /**
     * Sets file media type.
     */
    void setFileType();

    /**
     * Sets file unique title.
     */
    void setGeneralFileTitle();

    /**
     * Sets file size.
     */
    void setFileSize();
}

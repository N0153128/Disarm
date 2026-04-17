package dev.n0153.app;

import java.util.Set;

/**
 * This interface holds configuration for processors
 */
public interface MediaConfig {

    void put(String key, Object value);
    <ValueType> ValueType get (String key, Class <ValueType> type);
    void release();


    /**
     * Every plugin must contain its own unique name, that reflects it's functionality.
     * @return unique plugin name
     */
    String getName();

    /**
     * Every plugin must contain its own version number.
     * @return version number
     */
    double getVersion();

    /**
     * Describes supported formats and mime types
     */
    Set<String> supports();

    /**
     * Describes constrains of the supported media types and formats
     */
    enum MediaConstrains{};

    /**
     * Provides maximum allowed file size if global size limit wasn't applied.
     * @return maximum allowed file size for specified mime
     */
    int maxFileSizeInBytes(String mime);

    /**
     * Specifies log level for the plugin: Info, Debug, Warning, Error.
     * @return Log level
     */
    String getLogLevel();
}

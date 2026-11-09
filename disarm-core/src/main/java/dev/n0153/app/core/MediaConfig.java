package dev.n0153.app.core;

import java.util.Set;

/**
 * This interface holds configuration for processors.
 * Implementations back their settings with an internal key/value store;
 * {@link #put}, {@link #get}, and {@link #release} expose that store directly,
 * while the typed methods below describe a plugin's identity and limits.
 * @since 0.2
 */
public interface MediaConfig {

    /**
     * Sets a raw config value by key.
     * @param key the config key
     * @param value the new value to store
     * @since 0.2
     */
    void put(String key, Object value);

    /**
     * Retrieves a raw config value by key, cast to the requested type.
     * @param key the config key
     * @param type the expected value type
     * @return the stored value cast to {@code type}
     * @since 0.2
     */
    <$ValueType> $ValueType get (String key, Class <$ValueType> type);

    /**
     * Clears all stored config values.
     * @since 0.2
     */
    void release();


    /**
     * Every plugin must contain its own unique name, that reflects it's functionality.
     * @return unique plugin name
     * @since 0.2
     */
    String getName();

    /**
     * Every plugin must contain its own version number.
     * @return version number
     * @since 0.2
     */
    double getVersion();

    /**
     * Describes supported formats and mime types
     * @since 0.2
     */
    Set<String> supports();

    /**
     * Provides maximum allowed file size if global size limit wasn't applied.
     * @return maximum allowed file size for specified mime
     * @since 0.2
     */
    int maxFileSizeInBytes(String mime);

    /**
     * @return a human-readable snapshot of this config's current values, for debugging/logging.
     * @since 0.2
     */
    String toDebugString();

}

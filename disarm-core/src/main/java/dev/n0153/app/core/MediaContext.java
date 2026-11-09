package dev.n0153.app.core;

/**
 * Holds per-file runtime state for a single plugin's processing of one
 * input file (e.g. detected codec, bitrate, dimensions). Backed by an
 * internal key/value store, exposed via {@link #put} and {@link #get}.
 * Extends {@link AutoCloseable} so a context can be used in a
 * try-with-resources block alongside {@link #release}.
 * @since 0.2
 */
public interface MediaContext extends AutoCloseable {

    /**
     * Puts data into user's specified HashMap.
     * @param key identifier
     * @param value object to be stored
     * @since 0.2
     */
    void put(String key, Object value);

    /**
     * Fetches data from user's specified HashMap.
     * @param key identifier
     * @param type Object type
     * @return specified type's object
     * @param <$ValueType> specified type
     * @since 0.2
     */
    <$ValueType> $ValueType get (String key, Class <$ValueType> type);

    /**
     * A method used by plugin caller to free up resources once the file has been processed
     * @since 0.2
     */
    void release();

    /**
     * @return the output filename/title generated for the processed file
     * @since 0.2
     */
    String getOutputTitle();
}

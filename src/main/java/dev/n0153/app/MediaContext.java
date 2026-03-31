package dev.n0153.app;

import java.nio.file.Path;

public interface MediaContext {
    /**
     * Puts data into user's specified HashMap.
     * @param key identifier
     * @param value object to be stored
     */
    void put(String key, Object value);

    /**
     * Fetches data from user's specified HashMap.
     * @param key identifier
     * @param type Object type
     * @return specified type's object
     * @param <ValueType> specified type
     */
    <ValueType> ValueType get (String key, Class <ValueType> type);

    /**
     * A method used by plugin caller to free up resources once the file has been processed
     */
    void release();
}

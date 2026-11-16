package dev.n0153.app.core;

import dev.n0153.app.core.exceptions.MimeTypeDetectionException;

import java.nio.file.Path;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Per-file runtime context owned by the core. Holds file-agnostic processing
 * metadata (ID, filename, MIME type, file type, processing stage, resolved
 * plugin, warnings, error, start/completion timestamps, and a config snapshot)
 * for a single file as it moves through {@link MediaApp}.
 * <p>
 * Values are stored in a fixed-key map: only the predefined keys may be written,
 * and every value starts as {@code null} until set. Typed setters validate input
 * against the limits in {@link GlobalConfig} before storing it.
 * <p>
 * Not thread-safe. Intended for one file processed sequentially.
 * @since 0.2
 */
public class ProcessingContext {
    private final GlobalConfig config;

    /**
     * Creates an empty processing context with every key set to {@code null}.
     * @param config global config used to enforce length limits on the ID, filename and MIME type
     * @since 0.2
     */
    public ProcessingContext(GlobalConfig config) {
        this.config = config;
    }

    private final String KEY_ID = "id";
    private final String KEY_FILENAME = "fileName";
    private final String KEY_MIME_TYPE = "mimeType";
    private final String KEY_STAGE = "stage";
    private final String KEY_RESOLVED_PLUGIN = "resolvedPlugin";
    private final String KEY_WARNINGS = "warnings";
    private final String KEY_ERROR = "error";
    private final String KEY_STARTED_AT = "startedAt";
    private final String KEY_COMPLETED_AT = "completedAt";
    private final String KEY_CONFIG_SNAPSHOT = "configSnapshot";
    private final String KEY_FILE_TYPE= "fileType";


    private final Map<String, Object> processingContextStorage = new HashMap<>() {{
        put(KEY_ID, null);
        put(KEY_FILENAME, null);
        put(KEY_MIME_TYPE, null);
        put(KEY_STAGE, null);
        put(KEY_RESOLVED_PLUGIN, null);
        put(KEY_WARNINGS, null);
        put(KEY_ERROR, null);
        put(KEY_STARTED_AT, null);
        put(KEY_COMPLETED_AT, null);
        put(KEY_CONFIG_SNAPSHOT, null);
        put(KEY_FILE_TYPE, null);
    }};

    /**
     * Writes a raw value to an existing context key, skipping the validation done
     * by the typed setters. Only predefined keys are accepted; new keys cannot be added.
     * @param key one of the predefined context keys
     * @param value the value to store; may be {@code null}
     * @throws IllegalArgumentException if the key is not a predefined context key
     * @since 0.2
     */
    public void put(String key, Object value) {
        if (!processingContextStorage.containsKey(key)) {
            throw new IllegalArgumentException("Specified key doesn't exist");
        }
        processingContextStorage.replace(key, value);
    }

    /**
     * Clears all stored keys and values from this context. After this call,
     * getters return {@code null} and {@link #put(String, Object)} rejects every key,
     * so the context cannot be reused.
     * @since 0.2
     */
    public void release() {
        processingContextStorage.clear();
    }

    /**
     * Reads a value from the context and casts it to the requested type.
     * @param key the context key to read
     * @param type the expected type of the stored value
     * @param <$ValueType> the expected value type
     * @return the stored value, or {@code null} if the key is unset or unknown
     * @throws ClassCastException if the stored value is not an instance of {@code type}
     * @since 0.2
     */
    public <$ValueType> $ValueType get(String key, Class<$ValueType> type) {
        return type.cast(processingContextStorage.get(key));
    }

    /**
     * Fills in the initial context for a file: filename, detected MIME type,
     * stage (set to {@code "init"}), and the name of the resolved plugin.
     * Values are written directly and skip the typed setters' validation.
     * @param osTargetPath path to the file being processed
     * @param plugin name of the plugin resolved for this file
     * @param config the resolved plugin's config
     * @throws MimeTypeDetectionException if the file's MIME type cannot be detected
     * @since 0.2
     */
    public void populateContext(Path osTargetPath, String plugin, MediaConfig config) throws MimeTypeDetectionException {
        put(KEY_FILENAME, osTargetPath.getFileName().toString());
        put(KEY_MIME_TYPE, Utils.getMimeType(osTargetPath));
        put(KEY_STAGE, "init");
        put(KEY_RESOLVED_PLUGIN, plugin);
    }

    // getters

    /**
     * @return the file type (e.g. "image", "audio"), or {@code null} if not set
     * @since 0.2
     */
    public String getFileType() {
        return get(KEY_FILE_TYPE, String.class);
    }

    /**
     * @return the processing ID for this file, or {@code null} if not set
     * @since 0.2
     */
    public String getId() {
        return get(KEY_ID, String.class);
    };

    /**
     * @return the target file's name, or {@code null} if not set
     * @since 0.2
     */
    public String getFilename() {
        return get(KEY_FILENAME, String.class);
    }

    /**
     * @return the detected MIME type, or {@code null} if not set
     * @since 0.2
     */
    public String getMimeType() {
        return get(KEY_MIME_TYPE, String.class);
    }

    /**
     * @return the current processing stage label, or {@code null} if not set
     * @since 0.2
     */
    public String getStage() {
        return get(KEY_STAGE, String.class);
    }

    /**
     * @return the name of the plugin resolved for this file, or {@code null} if not set
     * @since 0.2
     */
    public String getResolvedPlugin() {
        return get(KEY_RESOLVED_PLUGIN, String.class);
    }

    /**
     * @return the warnings collected during processing, or {@code null} if not set
     * @since 0.2
     */
    public List<String> getWarnings() {
        return get(KEY_WARNINGS, List.class);
    }

    /**
     * @return the error message recorded during processing, or {@code null} if not set
     * @since 0.2
     */
    public String getError() {
        return get(KEY_ERROR, String.class);
    }

    /**
     * @return the timestamp when processing started, or {@code null} if not set
     * @since 0.2
     */
    public Instant getStartedAt() {
        return get(KEY_STARTED_AT, Instant.class);
    }

    /**
     * @return the timestamp when processing completed, or {@code null} if not set
     * @since 0.2
     */
    public Instant getCompletedAt() {
        return get(KEY_COMPLETED_AT, Instant.class);
    }

    /**
     * @return the config snapshot captured for this file, or {@code null} if not set
     * @since 0.2
     */
    public MediaConfig getConfigSnapshot() {
        return get(KEY_CONFIG_SNAPSHOT, MediaConfig.class);
    }

    // setters

    /**
     * Stores the config used to process this file.
     * @param newConfigSnapshot the config snapshot
     * @throws IllegalArgumentException if {@code newConfigSnapshot} is {@code null}
     * @since 0.2
     */
    public void setConfigSnapshot(MediaConfig newConfigSnapshot) {
        if (newConfigSnapshot == null) {
            throw new IllegalArgumentException("Config snapshot cannot be null");
        }
        put(KEY_CONFIG_SNAPSHOT, newConfigSnapshot);
    }

    /**
     * Stores the processing completion timestamp. The start timestamp must be set first.
     * @param newCompletedAt the completion timestamp
     * @throws IllegalArgumentException if {@code newCompletedAt} is {@code null}
     *         or earlier than the start timestamp
     * @since 0.2
     */
    public void setCompletedAt(Instant newCompletedAt) {
        if (newCompletedAt == null) {
            throw new IllegalArgumentException("The completion timestamp cannot be null");
        }
        if (getStartedAt().isAfter(newCompletedAt)) {
            throw new IllegalArgumentException("The completion timestamp cannot be earlier than the start timestamp");
        }
        put(KEY_COMPLETED_AT, newCompletedAt);
    }

    /**
     * Stores the processing start timestamp.
     * @param newStartedAt the start timestamp
     * @throws IllegalArgumentException if {@code newStartedAt} is {@code null}
     * @since 0.2
     */
    public void setStartedAt(Instant newStartedAt) {
        if (newStartedAt == null) {
            throw new IllegalArgumentException("The Start timestamp cannot be null");
        }
        put(KEY_STARTED_AT, newStartedAt);
    }

    /**
     * Records an error message for this file.
     * @param newError the error message
     * @throws IllegalArgumentException if {@code newError} is {@code null} or empty
     * @since 0.2
     */
    public void setError(String newError) {
        if (newError == null) {
            throw new IllegalArgumentException("Error message cannot be null");
        }
        if (newError.isEmpty()) {
            throw new IllegalArgumentException("Error message cannot be empty");
        }
        put(KEY_ERROR, newError);
    }

    /**
     * Replaces the warnings list for this file.
     * @param newWarnings the warnings to store
     * @throws IllegalArgumentException if {@code newWarnings} is {@code null} or empty
     * @since 0.2
     */
    public void setWarnings(List<String> newWarnings) {
        if (newWarnings == null) {
            throw new IllegalArgumentException("Warnings list cannot be null");
        }
        if (newWarnings.isEmpty()) {
            throw new IllegalArgumentException("Warnings list cannot be empty");
        }
        put(KEY_WARNINGS, newWarnings);
    }

    /**
     * Sets the name of the plugin resolved for this file.
     * @param newResolvedPlugin the plugin name
     * @throws IllegalArgumentException if {@code newResolvedPlugin} is {@code null} or empty
     * @since 0.2
     */
    public void setResolvedPlugin(String newResolvedPlugin) {
        if (newResolvedPlugin == null) {
            throw new IllegalArgumentException("Resolved plugin title cannot be null");
        }
        if (newResolvedPlugin.isEmpty()) {
            throw new IllegalArgumentException("Resolved plugin title cannot be empty");
        }
        put(KEY_RESOLVED_PLUGIN, newResolvedPlugin);
    }

    /**
     * Sets the current processing stage label.
     * @param newStage the stage label (e.g. {@code "init"})
     * @throws IllegalArgumentException if {@code newStage} is {@code null} or empty
     * @since 0.2
     */
    public void setStage(String newStage) {
        if (newStage == null) {
            throw new IllegalArgumentException("Stage cannot be null");
        }
        if (newStage.isEmpty()) {
            throw new IllegalArgumentException("Stage cannot be empty");
        }
        put(KEY_STAGE, newStage);
    }

    /**
     * Sets the file's MIME type.
     * @param newMimeType the MIME type
     * @throws IllegalArgumentException if {@code newMimeType} is {@code null}, empty,
     *         or longer than {@link GlobalConfig#getMimeLength()}
     * @since 0.2
     */
    public void setMimeType(String newMimeType) {
        if (newMimeType == null) {
            throw new IllegalArgumentException("Mime cannot be null");
        }
        if (newMimeType.isEmpty()) {
            throw new IllegalArgumentException("Mime type cannot be empty");
        }
        if (newMimeType.length() > config.getMimeLength()) { // this method only checks what comes after the "/", therefore the character count is restrictive.
            throw new IllegalArgumentException("Mime type cannot exceed mime length limit");
        }
        put(KEY_MIME_TYPE, newMimeType);
    }

    /**
     * Sets the target file's name.
     * @param newFilename the file name
     * @throws IllegalArgumentException if {@code newFilename} is {@code null}, empty,
     *         or longer than {@link GlobalConfig#getTargetFileLength()}
     * @since 0.2
     */
    public void setFilename(String newFilename) {
        if (newFilename == null) {
            throw new IllegalArgumentException("File name cannot be null");
        }
        if (newFilename.isEmpty()) {
            throw new IllegalArgumentException("File name cannot be empty");
        }
        if (newFilename.length() > config.getTargetFileLength()) {
            throw new IllegalArgumentException("File name cannot exceed " + config.getTargetFileLength());
        }
         put(KEY_FILENAME, newFilename);
    }

    /**
     * Sets the file's type (e.g. "image", "audio").
     * @param newFileType the file type
     * @throws IllegalArgumentException if {@code newFileType} is {@code null}, empty,
     *         or longer than 11 characters
     * @since 0.2
     */
    public void setFileType(String newFileType) {
        if (newFileType == null) {
            throw new IllegalArgumentException("File type cannot be null");
        }
        if (newFileType.isEmpty()) {
            throw new IllegalArgumentException("File type cannot be empty");
        }
        if (newFileType.length() > 11) { // "application" is the longest file type I can think of
            throw new IllegalArgumentException("File type is too long");
        }
        put(KEY_FILE_TYPE, newFileType);
    }

    /**
     * Sets the processing ID for this file.
     * @param newId the ID
     * @throws IllegalArgumentException if {@code newId} is {@code null}, empty,
     *         or longer than {@link GlobalConfig#getIDLength()}
     * @since 0.2
     */
    public void setId(String newId) {
        if (newId == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        if (newId.isEmpty()) {
            throw new IllegalArgumentException("ID cannot be empty");
        }
        if (newId.length() > config.getIDLength()) {
            throw new IllegalArgumentException("ID length cannot exceed " + config.getIDLength());
        }
        put(KEY_ID, newId);
    }
}

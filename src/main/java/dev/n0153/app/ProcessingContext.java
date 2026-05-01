package dev.n0153.app;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This interface holds context data for runtime.
 */
public class ProcessingContext {
    private final String KEY_ID = "id";
    private final String KEY_FILENAME = "fileName";
    private final String KEY_BYTE_SIZE = "byteSize";
    private final String KEY_MIME_TYPE = "mimeType";
    private final String KEY_INPUT_BUFFER = "inputBuffer";
    private final String KEY_STAGE = "stage";
    private final String KEY_RESOLVED_PLUGIN = "resolvedPlugin";
    private final String KEY_OUTPUT_BUFFER = "outputBuffer";
    private final String KEY_TRANSFORMATIONS_APPLIED = "transformationsApplied";
    private final String KEY_WARNINGS = "warnings";
    private final String KEY_ERROR = "error";
    private final String KEY_STARTED_AT = "startedAt";
    private final String KEY_COMPLETED_AT = "completedAt";
    private final String KEY_CONFIG_SNAPSHOT = "configSnapshot";


    private final Map<String, Object> processingContextStorage = new HashMap<>();

    public void put(String key, Object value) {
        if (!processingContextStorage.containsKey(key)) {
            throw new IllegalArgumentException("Specified key doesn't exist");
        }
        processingContextStorage.replace(key, value);
    }

    public <ValueType> ValueType get(String key, Class<ValueType> type) {
        return type.cast(processingContextStorage.get(key));
    }


    public String getId() {
        return get(KEY_ID, String.class);
    };

    public String getFilename() {
        return get(KEY_FILENAME, String.class);
    }

    public long getByteSize() {
        return get(KEY_BYTE_SIZE, long.class);
    }

    public String getMimeType() {
        return get(KEY_MIME_TYPE, String.class);
    }

    public byte[] getInputBuffer() {
        return get(KEY_INPUT_BUFFER, byte[].class);
    }

    public String getStage() {
        return get(KEY_STAGE, String.class);
    }

    public String getResolvedPlugin() {
        return get(KEY_RESOLVED_PLUGIN, String.class);
    }

    public byte[] getOutputBuffer() {
        return get(KEY_OUTPUT_BUFFER, byte[].class);
    }

    public List<String> getTransformationsApplied() {
        return get(KEY_TRANSFORMATIONS_APPLIED, List.class);
    }

    public List<String> getWarnings() {
        return get(KEY_WARNINGS, List.class);
    }

    public String getError() {
        return get(KEY_ERROR, String.class);
    }

    public Instant getStartedAt() {
        return get(KEY_STARTED_AT, Instant.class);
    }

    public Instant getCompletedAt() {
        return get(KEY_COMPLETED_AT, Instant.class);
    }

    public MediaConfig getConfigSnapshot() {
        return get(KEY_CONFIG_SNAPSHOT, MediaConfig.class);
    }

    public void setConfigSnapshot(MediaConfig newConfigSnapshot) {
        put(KEY_CONFIG_SNAPSHOT, newConfigSnapshot);
    }

    public void setCompletedAt(Instant newCompletedAt) {
        put(KEY_COMPLETED_AT, newCompletedAt);
    }

    public void setStartedAt(Instant newStartedAt) {
        put(KEY_STARTED_AT, newStartedAt);
    }

    public void setError(String newError) {
        put(KEY_ERROR, newError);
    }

    public void setWarnings(List<String> newWarnings) {
        put(KEY_WARNINGS, newWarnings);
    }

    public void setTransformationsApplied(List<String> newTransformationsApplied) {
        put(KEY_TRANSFORMATIONS_APPLIED, newTransformationsApplied);
    }

    public void setOutputBuffer(byte[] newOutputBuffer) {
        put(KEY_OUTPUT_BUFFER, newOutputBuffer);
    }

    public void setResolvedPlugin(String newResolvedPlugin) {
        put(KEY_RESOLVED_PLUGIN, newResolvedPlugin);
    }

    public void setInputBuffer(byte[] newInputBuffer) {
        put(KEY_INPUT_BUFFER, newInputBuffer);
    }

    public void setStage(String newStage) {
        put(KEY_STAGE, newStage);
    }

    public void setMimeType(String newMimeType) {
        put(KEY_MIME_TYPE, newMimeType);
    }

    public void setByteSize(long newByteSize) {
        put(KEY_BYTE_SIZE, newByteSize);
    }

    public void setFilename(String newFilename) {
        put(KEY_FILENAME, newFilename);
    }

    public void setId(String newId) {
        put(KEY_ID, newId);
    }
}

package dev.n0153.app;

import dev.n0153.app.exceptions.DisarmException;
import dev.n0153.app.exceptions.MimeTypeDetectionException;

import java.nio.file.Path;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This interface holds context data for runtime.
 */
public class ProcessingContext {
    private final GlobalConfig config;

    public ProcessingContext(GlobalConfig config) {
        this.config = config;
    }

    public final String KEY_ID = "id";
    public final String KEY_FILENAME = "fileName";
    public final String KEY_BYTE_SIZE = "byteSize";
    public final String KEY_MIME_TYPE = "mimeType";
    public final String KEY_INPUT_BUFFER = "inputBuffer";
    public final String KEY_STAGE = "stage";
    public final String KEY_RESOLVED_PLUGIN = "resolvedPlugin";
    public final String KEY_OUTPUT_BUFFER = "outputBuffer";
    public final String KEY_TRANSFORMATIONS_APPLIED = "transformationsApplied";
    public final String KEY_WARNINGS = "warnings";
    public final String KEY_ERROR = "error";
    public final String KEY_STARTED_AT = "startedAt";
    public final String KEY_COMPLETED_AT = "completedAt";
    public final String KEY_CONFIG_SNAPSHOT = "configSnapshot";
    public final String KEY_PLUGIN_PROCESSOR = "pluginProcessor";


    private final Map<String, Object> processingContextStorage = new HashMap<>() {{
        put(KEY_ID, null);
        put(KEY_FILENAME, null);
        put(KEY_BYTE_SIZE, null);
        put(KEY_MIME_TYPE, null);
        put(KEY_INPUT_BUFFER, null);
        put(KEY_STAGE, null);
        put(KEY_RESOLVED_PLUGIN, null);
        put(KEY_OUTPUT_BUFFER, null);
        put(KEY_TRANSFORMATIONS_APPLIED, null);
        put(KEY_WARNINGS, null);
        put(KEY_ERROR, null);
        put(KEY_STARTED_AT, null);
        put(KEY_COMPLETED_AT, null);
        put(KEY_CONFIG_SNAPSHOT, null);
        put(KEY_PLUGIN_PROCESSOR, null);
    }};

    public void put(String key, Object value) {
        if (!processingContextStorage.containsKey(key)) {
            throw new IllegalArgumentException("Specified key doesn't exist");
        }
        processingContextStorage.replace(key, value);
    }

    public <ValueType> ValueType get(String key, Class<ValueType> type) {
        return type.cast(processingContextStorage.get(key));
    }

    public void populateContext(Path osTargetPath, MediaPlugin plugin, MediaConfig config) {
        try {
            put(KEY_FILENAME, osTargetPath.getFileName());
            put(KEY_MIME_TYPE, Utils.getMimeType(osTargetPath));
            put(KEY_STAGE, "init");
            put(KEY_RESOLVED_PLUGIN, plugin);
            put(KEY_TRANSFORMATIONS_APPLIED, "none");
            put(KEY_STARTED_AT, Instant.now());
            put(KEY_CONFIG_SNAPSHOT, config);

        } catch (MimeTypeDetectionException e) {
            throw new DisarmException(e);
        }
    }

    public Object getProcessor() {
        return get(KEY_PLUGIN_PROCESSOR, Object.class);
    }

    // getters
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

    public MediaPlugin getResolvedPlugin() {
        return get(KEY_RESOLVED_PLUGIN, MediaPlugin.class);
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

    // setters

    public void setPluginProcessor(MediaProcessor<?> newProcessor) {
        if (newProcessor == null) {
            throw new IllegalArgumentException("Plugin processor cannot be null");
        }
        put(KEY_PLUGIN_PROCESSOR, newProcessor);
    }

    public void setConfigSnapshot(MediaConfig newConfigSnapshot) {
        if (newConfigSnapshot == null) {
            throw new IllegalArgumentException("Config snapshot cannot be null");
        }
        put(KEY_CONFIG_SNAPSHOT, newConfigSnapshot);
    }

    public void setCompletedAt(Instant newCompletedAt) {
        if (newCompletedAt == null) {
            throw new IllegalArgumentException("The completion timestamp cannot be null");
        }
        if (getStartedAt().isAfter(newCompletedAt)) {
            throw new IllegalArgumentException("The completion timestamp cannot be earlier than the start timestamp");
        }
        put(KEY_COMPLETED_AT, newCompletedAt);
    }

    public void setStartedAt(Instant newStartedAt) {
        if (newStartedAt == null) {
            throw new IllegalArgumentException("The Start timestamp cannot be null");
        }
        put(KEY_STARTED_AT, newStartedAt);
    }

    public void setError(String newError) {
        if (newError == null) {
            throw new IllegalArgumentException("Error message cannot be null");
        }
        if (newError.isEmpty()) {
            throw new IllegalArgumentException("Error message cannot be empty");
        }
        put(KEY_ERROR, newError);
    }

    public void setWarnings(List<String> newWarnings) {
        if (newWarnings == null) {
            throw new IllegalArgumentException("Warnings list cannot be null");
        }
        if (newWarnings.isEmpty()) {
            throw new IllegalArgumentException("Warnings list cannot be empty");
        }
        put(KEY_WARNINGS, newWarnings);
    }

    public void setTransformationsApplied(List<String> newTransformationsApplied) {
        if (newTransformationsApplied == null) {
            throw new IllegalArgumentException("Transformations list cannot be null");
        }
        if (newTransformationsApplied.isEmpty()) {
            throw new IllegalArgumentException("Transformations list cannot be empty");
        }
        put(KEY_TRANSFORMATIONS_APPLIED, newTransformationsApplied);
    }

    public void setOutputBuffer(byte[] newOutputBuffer) {
        if (newOutputBuffer == null) {
            throw new IllegalArgumentException("Output buffer cannot be null");
        }
        if (newOutputBuffer.length == 0) {
            throw new IllegalArgumentException("Output buffer cannot be empty");
        }
        if (newOutputBuffer.length > config.getGeneralFileSizeUpperBoundLimit()) {
            throw new IllegalArgumentException("Output buffer cannot exceed general file size limit");
        }
        put(KEY_OUTPUT_BUFFER, newOutputBuffer);
    }

    public void setResolvedPlugin(String newResolvedPlugin) {
        if (newResolvedPlugin == null) {
            throw new IllegalArgumentException("Resolved plugin title cannot be null");
        }
        if (newResolvedPlugin.isEmpty()) {
            throw new IllegalArgumentException("Resolved plugin title cannot be empty");
        }
        put(KEY_RESOLVED_PLUGIN, newResolvedPlugin);
    }

    public void setInputBuffer(byte[] newInputBuffer) {
        if (newInputBuffer == null) {
            throw new IllegalArgumentException("Input buffer cannot be null");
        }
        if (newInputBuffer.length == 0) {
            throw new IllegalArgumentException("Input buffer cannot be empty");
        }
        if (newInputBuffer.length > config.getGeneralFileSizeUpperBoundLimit()) {
            throw new IllegalArgumentException("Input buffer cannot exceed general file size limit");
        }
        put(KEY_INPUT_BUFFER, newInputBuffer);
    }

    public void setStage(String newStage) {
        if (newStage == null) {
            throw new IllegalArgumentException("Stage cannot be null");
        }
        if (newStage.isEmpty()) {
            throw new IllegalArgumentException("Stage cannot be empty");
        }
        put(KEY_STAGE, newStage);
    }

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

    public void setByteSize(long newByteSize) {
        if (newByteSize < 0) {
            throw new IllegalArgumentException("Byte size cannot be less than zero");
        }
        if (newByteSize == 0) {
            throw new IllegalArgumentException("Byte size cannot be zero");
        }
        put(KEY_BYTE_SIZE, newByteSize);
    }

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

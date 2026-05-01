package dev.n0153.app;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class GlobalConfig {
    private final int generalSizeLimit = 5_000_000; // 5MB
    private final boolean keepInputs = false;
    private final boolean keepResult = true;
    private final boolean keepOriginal = true;
    private final int generalFileSizeUpperBoundLimit = 10_000_000;
    private final Path generalOutputPath = Paths.get("resources/output");
    ;
    private final int targetFileLength = 150;

    private final String KEY_GENERAL_SIZE_LIMIT = "generalSizeLimit";
    private final String KEY_KEEP_INPUTS = "keepInputs";
    private final String KEY_KEEP_RESULT = "keepResult";
    private final String KEY_KEEP_ORIGINAL = "keepOriginal";
    private final String KEY_GENERAL_FILE_SIZE_UPPER_BOUND = "generalFileSizeUpperBoundLimit";
    private final String KEY_GENERAL_OUTPUT_PATH = "generalOutputPath";
    private final String KEY_TARGET_FILE_LENGTH = "targetFileLength";

    private final Map<String, Object> globalConfigStorage = new HashMap<>() {{
        put(KEY_GENERAL_SIZE_LIMIT, generalSizeLimit);
        put(KEY_KEEP_INPUTS, keepInputs);
        put(KEY_KEEP_RESULT, keepResult);
        put(KEY_KEEP_ORIGINAL, keepOriginal);
        put(KEY_GENERAL_FILE_SIZE_UPPER_BOUND, generalFileSizeUpperBoundLimit);
        put(KEY_GENERAL_OUTPUT_PATH, generalOutputPath);
        put(KEY_TARGET_FILE_LENGTH, targetFileLength);
    }};

    public void put(String key, Object value) {
        if (!globalConfigStorage.containsKey(key)) {
            throw new IllegalArgumentException("Specified key doesn't exist");
        }
        globalConfigStorage.replace(key, value);
    }

    public <ValueType> ValueType get(String key, Class<ValueType> type) {
        return type.cast(globalConfigStorage.get(key));
    }


    public int getGeneralFileSizeUpperBoundLimit() {
        return Objects.requireNonNullElse(
                get(KEY_GENERAL_FILE_SIZE_UPPER_BOUND, int.class),
                generalFileSizeUpperBoundLimit);
    }

    public boolean getKeepInputs() {
        return Objects.requireNonNullElse(
                get(KEY_KEEP_INPUTS, boolean.class),
                keepInputs
        );
    }

    public boolean getKeepResult() {
        return Objects.requireNonNullElse(
                get(KEY_KEEP_RESULT, boolean.class),
                keepResult
        );
    }

    public boolean isKeepOriginal() {
        return Objects.requireNonNullElse(
                get(KEY_KEEP_ORIGINAL, boolean.class),
                keepOriginal
        );
    }

    public Path getGeneralOutputPath() {
        return Objects.requireNonNullElse(
                get(KEY_GENERAL_OUTPUT_PATH, Path.class),
                generalOutputPath
        );
    }

    public int getTargetFileLength() {
        return Objects.requireNonNullElse(
                get(KEY_TARGET_FILE_LENGTH, int.class),
                targetFileLength
        );
    }

    public int getGeneralSizeLimit() {
        return Objects.requireNonNullElse(
                get(KEY_GENERAL_SIZE_LIMIT, int.class),
                generalSizeLimit
        );
    }


    public void setKeepInputs(boolean newKeepInputs) {
        put(KEY_KEEP_INPUTS, newKeepInputs);
    }

    public void setKeepResults(boolean newKeepResult) {
        put(KEY_KEEP_RESULT, newKeepResult);
    }

    public void setKeepOriginal(boolean newKeepOriginal) {
        put(KEY_KEEP_ORIGINAL, newKeepOriginal);
    }

    public void setGeneralSizeLimit(int newSizeLimit) {
        if (newSizeLimit < 0) {
            throw new IllegalArgumentException("Size limit cannot be less than zero");
        }
        if (newSizeLimit > generalFileSizeUpperBoundLimit) {
            throw new IllegalArgumentException("Size limit cannot exceed upper bound");
        }
        put(KEY_GENERAL_SIZE_LIMIT, newSizeLimit);
    }

    public void setGeneralOutputPath(Path newGeneralOutputPath) {
        if (newGeneralOutputPath == null) {
            throw new IllegalArgumentException("General output path cannot be empty");
        }
        put(KEY_GENERAL_OUTPUT_PATH, newGeneralOutputPath);
    }

    private void setTargetFileLength(int newTargetFileLength) {
        if (newTargetFileLength < 0) {
            throw new IllegalArgumentException("Target file length cannot be less than zero");
        }
        put(KEY_TARGET_FILE_LENGTH, newTargetFileLength);
    }
}

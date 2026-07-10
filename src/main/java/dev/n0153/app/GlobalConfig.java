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
    private final int targetFileLength = 150;
    private final int IDLength = 150;
    private final int mimeLength = 10;
    private final boolean benchmarking = false;
    private final boolean skipUnsupported = false;
    private final boolean skipCrashed = false;

    private final String KEY_GENERAL_SIZE_LIMIT = "generalSizeLimit";
    private final String KEY_KEEP_INPUTS = "keepInputs";
    private final String KEY_KEEP_RESULT = "keepResult";
    private final String KEY_KEEP_ORIGINAL = "keepOriginal";
    private final String KEY_GENERAL_FILE_SIZE_UPPER_BOUND = "generalFileSizeUpperBoundLimit";
    private final String KEY_GENERAL_OUTPUT_PATH = "generalOutputPath";
    private final String KEY_TARGET_FILE_LENGTH = "targetFileLength";
    private final String KEY_ID_LENGTH = "IDLength";
    private final String KEY_MIME_LENGTH = "mimeLength";
    private final String KEY_BENCHMARKING = "benchmarking";
    private final String KEY_SKIP_UNSUPPORTED = "skipUnsupported";
    private final String KEY_SKIP_CRASHED = "skipCrashed";


    private final Map<String, Object> globalConfigStorage = new HashMap<>() {{
        put(KEY_GENERAL_SIZE_LIMIT, generalSizeLimit);
        put(KEY_KEEP_INPUTS, keepInputs);
        put(KEY_KEEP_RESULT, keepResult);
        put(KEY_KEEP_ORIGINAL, keepOriginal);
        put(KEY_GENERAL_FILE_SIZE_UPPER_BOUND, generalFileSizeUpperBoundLimit);
        put(KEY_GENERAL_OUTPUT_PATH, generalOutputPath);
        put(KEY_TARGET_FILE_LENGTH, targetFileLength);
        put(KEY_ID_LENGTH, IDLength);
        put(KEY_MIME_LENGTH, mimeLength);
        put(KEY_BENCHMARKING, benchmarking);
        put(KEY_SKIP_UNSUPPORTED, skipUnsupported);
        put(KEY_SKIP_CRASHED, skipCrashed);
    }};

    public void put(String key, Object value) {
        if (!globalConfigStorage.containsKey(key)) {
            throw new IllegalArgumentException("Specified key doesn't exist");
        }
        globalConfigStorage.replace(key, value);
    }

    public <$ValueType> $ValueType get(String key, Class<$ValueType> type) {
        return type.cast(globalConfigStorage.get(key));
    }

    public boolean getSkipCrashed() {
        return Objects.requireNonNullElse(
                get(KEY_SKIP_CRASHED, Boolean.class),
                skipCrashed);
    }

    public boolean getSkipUnsupported() {
        return Objects.requireNonNullElse(
                get(KEY_SKIP_UNSUPPORTED, Boolean.class),
                skipUnsupported);
    }

    public boolean getBenchmarking() {
        return Objects.requireNonNullElse(
                get(KEY_BENCHMARKING, Boolean.class),
                benchmarking);
    }

    public int getIDLength() {
        return Objects.requireNonNullElse(
                get(KEY_ID_LENGTH, Integer.class),
                IDLength);
    }

    public int getMimeLength() {
        return Objects.requireNonNullElse(
                get(KEY_MIME_LENGTH, Integer.class),
                mimeLength);
    }

    public int getGeneralFileSizeUpperBoundLimit() {
        return Objects.requireNonNullElse(
                get(KEY_GENERAL_FILE_SIZE_UPPER_BOUND, Integer.class),
                generalFileSizeUpperBoundLimit);
    }

    public boolean getKeepInputs() {
        return Objects.requireNonNullElse(
                get(KEY_KEEP_INPUTS, Boolean.class),
                keepInputs
        );
    }

    public boolean getKeepResult() {
        return Objects.requireNonNullElse(
                get(KEY_KEEP_RESULT, Boolean.class),
                keepResult
        );
    }

    public boolean isKeepOriginal() {
        return Objects.requireNonNullElse(
                get(KEY_KEEP_ORIGINAL, Boolean.class),
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
                get(KEY_TARGET_FILE_LENGTH, Integer.class),
                targetFileLength
        );
    }

    public int getGeneralSizeLimit() {
        return Objects.requireNonNullElse(
                get(KEY_GENERAL_SIZE_LIMIT, Integer.class),
                generalSizeLimit
        );
    }

    public void setSkipCrashed(boolean newSkipCrashed) {
        put(KEY_SKIP_CRASHED, newSkipCrashed);
    }

    public void setSkipUnsupported(boolean newSkipUnsupported) {
        put(KEY_SKIP_UNSUPPORTED, newSkipUnsupported);
    }

    public void setBenchmarking(boolean newBenchmarking) {
        put(KEY_BENCHMARKING, newBenchmarking);
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

    public void setIDLength(int newIDLength) {
        if (newIDLength < 0) {
            throw new IllegalArgumentException("ID Length cannot be less than zero");
        }
        put(KEY_ID_LENGTH, newIDLength);
    }

    public void setMimeLength(int newMimeLength) {
        if (newMimeLength < 0) {
            throw new IllegalArgumentException("Mime length cannot be less than zero");
        }
        put(KEY_MIME_LENGTH, newMimeLength);
    }
}

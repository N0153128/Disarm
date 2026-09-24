package dev.n0153.app;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class GlobalConfig {
    private final int generalSizeLimit = 5_000_000; // 5MB
    private final boolean deleteResult = false;
    private final boolean keepOriginal = true;
    private final int generalFileSizeUpperBoundLimit = 10_000_000;
    private final Path generalOutputPath = Paths.get("resources/output");
    private final int targetFileLength = 150;
    private final int IDLength = 150;
    private final int mimeLength = 10;
    private final boolean benchmarking = false;
    private final boolean skipUnsupported = false;
    private final boolean skipCrashed = false;
    public enum VerboseErrors { OFF, CONSOLE, FILE, BOTH }
    private final VerboseErrors verboseErrors = VerboseErrors.OFF;
    private final Boolean trackTime = true;
    private final Instant bootTime = Instant.now();
    private final Path reportsOutputPath = Paths.get("resources/output/reports");
    private final boolean restrictInputFromRoot = false;
    private final Path inputRootPath = Paths.get("resources");
    public enum LoggingLevels { OFF, FATAL, ERROR, WARN, INFO, DEBUG, TRACE, ALL }
    private final LoggingLevels logLevel = LoggingLevels.DEBUG;
    private final boolean randomCharactersTitle = false;
    public final int sampleSizeInBytes = 8192;
    private final double maxControlCharacterRatio = 0.02;
    private final double minUtf16NulRatio = 0.3;
    private final double minUtf32BasicPlaneRatio = 0.5;



    private final String KEY_GENERAL_SIZE_LIMIT = "generalSizeLimit";
    private final String KEY_KEEP_ORIGINAL = "keepOriginal";
    private final String KEY_GENERAL_FILE_SIZE_UPPER_BOUND = "generalFileSizeUpperBoundLimit";
    private final String KEY_GENERAL_OUTPUT_PATH = "generalOutputPath";
    private final String KEY_TARGET_FILE_LENGTH = "targetFileLength";
    private final String KEY_ID_LENGTH = "IDLength";
    private final String KEY_MIME_LENGTH = "mimeLength";
    private final String KEY_BENCHMARKING = "benchmarking";
    private final String KEY_SKIP_UNSUPPORTED = "skipUnsupported";
    private final String KEY_SKIP_CRASHED = "skipCrashed";
    private final String KEY_VERBOSE_ERRORS = "verboseErrors";
    private final String KEY_TRACK_TIME = "trackTime";
    private final String KEY_BOOT_TIME = "bootTime";
    private final String KEY_REPORTS_OUTPUT_PATH = "reportsOutputPath";
    private final String KEY_RESTRICT_INPUT_FROM_ROOT = "restrictInputFromRoot";
    private final String KEY_INPUT_ROOT_PATH = "inputRootPath";
    private final String KEY_DELETE_RESULT = "deleteResult";
    private final String KEY_LOG_LEVEL = "logLevel";
    private final String RANDOM_CHARACTERS_TITLE = "randomCharactersTitle";
    private final String KEY_SAMPLE_SIZE_IN_BYTES = "sampleSizeInBytes";
    private final String KEY_MAX_CONTROL_CHARACTER_RATIO = "maxControlCharacterRatio";
    private final String KEY_MIN_UTF16_NUL_RATIO = "minUtf16NulRatio";
    private final String KEY_MIN_UTF32_BASIC_PLANE_RATIO = "minUtf32BasicPlaneRatio";


    private final Map<String, Object> globalConfigStorage = new HashMap<>() {{
        put(KEY_GENERAL_SIZE_LIMIT, generalSizeLimit);
        put(KEY_DELETE_RESULT, deleteResult);
        put(KEY_KEEP_ORIGINAL, keepOriginal);
        put(KEY_GENERAL_FILE_SIZE_UPPER_BOUND, generalFileSizeUpperBoundLimit);
        put(KEY_GENERAL_OUTPUT_PATH, generalOutputPath);
        put(KEY_TARGET_FILE_LENGTH, targetFileLength);
        put(KEY_ID_LENGTH, IDLength);
        put(KEY_MIME_LENGTH, mimeLength);
        put(KEY_BENCHMARKING, benchmarking);
        put(KEY_SKIP_UNSUPPORTED, skipUnsupported);
        put(KEY_SKIP_CRASHED, skipCrashed);
        put(KEY_VERBOSE_ERRORS, verboseErrors);
        put(KEY_TRACK_TIME, trackTime);
        put(KEY_BOOT_TIME, bootTime);
        put(KEY_REPORTS_OUTPUT_PATH, reportsOutputPath);
        put(KEY_RESTRICT_INPUT_FROM_ROOT, restrictInputFromRoot);
        put(KEY_INPUT_ROOT_PATH, inputRootPath);
        put(KEY_LOG_LEVEL, logLevel);
        put(RANDOM_CHARACTERS_TITLE, randomCharactersTitle);
        put(KEY_SAMPLE_SIZE_IN_BYTES, sampleSizeInBytes);
        put(KEY_MAX_CONTROL_CHARACTER_RATIO, maxControlCharacterRatio);
        put(KEY_MIN_UTF16_NUL_RATIO, minUtf16NulRatio);
        put(KEY_MIN_UTF32_BASIC_PLANE_RATIO, minUtf32BasicPlaneRatio);
    }};

    public void put(String key, Object value) {
        if (!globalConfigStorage.containsKey(key)) {
            throw new IllegalArgumentException("Specified key doesn't exist");
        }
        globalConfigStorage.replace(key, value);
    }

    public void release() {
        globalConfigStorage.clear();
    }

    public String toDebugString() {
        StringBuilder output = new StringBuilder("\n\n=== GLOBAL CONFIG === \n");
        for (Map.Entry<String, Object> entry : globalConfigStorage.entrySet()) {
            output.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
        output.append("\n=== END OF SNAPSHOT ===\n");
        return output.toString();
    }

    public <$ValueType> $ValueType get(String key, Class<$ValueType> type) {
        return type.cast(globalConfigStorage.get(key));
    }

    public int getSampleSizeInBytes() {
        return Objects.requireNonNullElse(
                get(KEY_SAMPLE_SIZE_IN_BYTES, Integer.class),
                sampleSizeInBytes);
    }

    public double getMaxControlCharacterRatio() {
        return Objects.requireNonNullElse(
                get(KEY_MAX_CONTROL_CHARACTER_RATIO, Double.class),
                maxControlCharacterRatio);
    }

    public double getMinUtf16NulRatio() {
        return Objects.requireNonNullElse(
                get(KEY_MIN_UTF16_NUL_RATIO, Double.class),
                minUtf16NulRatio);
    }

    public double getMinUtf32BasicPlaneRatio() {
        return Objects.requireNonNullElse(
                get(KEY_MIN_UTF32_BASIC_PLANE_RATIO, Double.class),
                minUtf32BasicPlaneRatio);
    }

    public boolean getRandomCharactersTitle() {
        return Objects.requireNonNullElse(
                get(RANDOM_CHARACTERS_TITLE, Boolean.class),
                randomCharactersTitle);
    }

    public LoggingLevels getLogLevel() {
        return Objects.requireNonNullElse(
                get(KEY_LOG_LEVEL, LoggingLevels.class),
                logLevel);
    }

    public boolean getRestrictInputFromRoot() {
        return Objects.requireNonNullElse(
                get(KEY_RESTRICT_INPUT_FROM_ROOT, Boolean.class),
                restrictInputFromRoot);
    }

    public Path getInputRootPath() {
        return Objects.requireNonNullElse(
                get(KEY_INPUT_ROOT_PATH, Path.class),
                inputRootPath);
    }

    public Path getReportsOutputPath() {
        return Objects.requireNonNullElse(
                get(KEY_REPORTS_OUTPUT_PATH, Path.class),
                reportsOutputPath);
    }

    public Instant getBootTime() {
        return Objects.requireNonNullElse(
                get(KEY_BOOT_TIME, Instant.class),
                bootTime);
    }

    public boolean getTrackTime() {
        return Objects.requireNonNullElse(
                get(KEY_TRACK_TIME, Boolean.class),
                trackTime);
    }

    public VerboseErrors getVerboseErrors() {
        return Objects.requireNonNullElse(
                get(KEY_VERBOSE_ERRORS, VerboseErrors.class),
                verboseErrors);
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

    public boolean getDeleteResult() {
        return Objects.requireNonNullElse(
                get(KEY_DELETE_RESULT, Boolean.class),
                deleteResult
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

    public void setSampleSizeInBytes(int newSampleSizeInBytes) {
        if (newSampleSizeInBytes < 0) {
            throw new IllegalArgumentException("Sample size cannot be less than zero");
        }
        if (newSampleSizeInBytes == 0) {
            throw new IllegalArgumentException("Sample size cannot be zero");
        }
        // max check required
        put(KEY_SAMPLE_SIZE_IN_BYTES, newSampleSizeInBytes);
    }

    public void setMaxControlCharacterRatio(double newMaxControlCharacterRatio) {
        if (newMaxControlCharacterRatio < 0.0) {
            throw new IllegalArgumentException("Max control character ratio cannot be less than zero");
        }
        if (newMaxControlCharacterRatio == 0.0) {
            throw new IllegalArgumentException("Max control character ratio cannot be zero");
        }
        if (newMaxControlCharacterRatio > 1.0) {
            throw new IllegalArgumentException("Max control character ratio cannot be greater than one");
        }
        put(KEY_MAX_CONTROL_CHARACTER_RATIO, newMaxControlCharacterRatio);
    }

    public void setMinUtf16NulRatio(double newMinUtf16NulRatio) {
        if (newMinUtf16NulRatio < 0.0) {
            throw new IllegalArgumentException("Min UTF16 nul ratio cannot be less than zero");
        }
        if (newMinUtf16NulRatio == 0.0) {
            throw new IllegalArgumentException("Min UTF16 nul ratio cannot be zero");
        }
        if (newMinUtf16NulRatio > 1.0) {
            throw new IllegalArgumentException("Min UTF16 nul ratio cannot be greater than one");
        }
        put(KEY_MIN_UTF16_NUL_RATIO, newMinUtf16NulRatio);
    }

    public void setMinUtf32BasicPlaneRatio(double newMinUtf32BasicPlaneRatio) {
        if (newMinUtf32BasicPlaneRatio < 0.0) {
            throw new IllegalArgumentException("Min UTF32 basic plane ratio cannot be less than zero");
        }
        if (newMinUtf32BasicPlaneRatio == 0.0) {
            throw new IllegalArgumentException("Min UTF32 basic plane ratio cannot be zero");
        }
        if (newMinUtf32BasicPlaneRatio > 1.0) {
            throw new IllegalArgumentException("Min UTF32 basic plane ratio cannot be greater than one");
        }
        put(KEY_MIN_UTF32_BASIC_PLANE_RATIO, newMinUtf32BasicPlaneRatio)
    }

        public void setRandomCharactersTitle(boolean newRandomCharactersTitle) {
        put(RANDOM_CHARACTERS_TITLE, newRandomCharactersTitle);
    }

    public void setLogLevel(LoggingLevels newLogLevel) {
        if (newLogLevel == null) {
            throw new IllegalArgumentException("Log level cannot be null");
        }
        put(KEY_LOG_LEVEL, newLogLevel);
    }

    public void setInputRootPath(Path newInputRootPath) {
        if (newInputRootPath == null) {
            throw new IllegalArgumentException("Input root path cannot be empty");
        }
        put(KEY_INPUT_ROOT_PATH, newInputRootPath);
    }

    public void setReportsOutputPath(Path newReportsOutputPath) {
        if (newReportsOutputPath == null) {
            throw new IllegalArgumentException("General output path cannot be empty");
        }
        put(KEY_REPORTS_OUTPUT_PATH, newReportsOutputPath);
    }

    public void setRestrictInputFromRoot(boolean newRestrictInputFromRoot) {
        put(KEY_RESTRICT_INPUT_FROM_ROOT, newRestrictInputFromRoot);
    }

    public void setBootTime(Instant newBootTime) {
        put(KEY_BOOT_TIME, newBootTime);
    }

    public void setTrackTime(boolean newTrackTime) {
        put(KEY_TRACK_TIME, newTrackTime);
    }

    public void setVerboseErrors(VerboseErrors newVerboseErrors) {
        if (newVerboseErrors == null) {
            throw new IllegalArgumentException("Verbose Errors cannot be null");
        }
        put(KEY_VERBOSE_ERRORS, newVerboseErrors);
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

    public void setDeleteResult(boolean newDeleteResult) {
        put(KEY_DELETE_RESULT, newDeleteResult);
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

    public void setTargetFileLength(int newTargetFileLength) {
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

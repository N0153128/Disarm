package dev.n0153.app.core;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Holds global, cross-plugin configuration for the disarm pipeline.
 * Values are stored in an internal key/value map ({@code globalConfigStorage})
 * behind typed getters and setters. Each getter falls back to a hardcoded
 * default via {@code Objects.requireNonNullElse} when nothing has been
 * explicitly set via the corresponding setter.
 */
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
    /** Controls how/whether validation and processing errors are surfaced. */
    public enum VerboseErrors { OFF, CONSOLE, FILE, BOTH }
    private final VerboseErrors verboseErrors = VerboseErrors.OFF;
    private final Boolean trackTime = true;
    private final Instant bootTime = Instant.now();
    private final Path reportsOutputPath = Paths.get("resources/output/reports");
    private final boolean restrictInputFromRoot = false;
    private final Path inputRootPath = Paths.get("resources");
    /** Log4j2-style verbosity levels used to drive runtime log output. */
    public enum LoggingLevels { OFF, FATAL, ERROR, WARN, INFO, DEBUG, TRACE, ALL }
    private final LoggingLevels logLevel = LoggingLevels.INFO;
    private final boolean randomCharactersTitle = false;
    /** Number of bytes sampled when sniffing file content (e.g. for encoding/MIME heuristics). */
    public final int sampleSizeInBytes = 8192;
    private final double maxControlCharacterRatio = 0.02;
    private final double minUtf16NulRatio = 0.3;
    private final double minUtf32BasicPlaneRatio = 0.5;
    private final boolean watchdog = false;
    private final long watchdogPollIntervalMs = 500;
    private final int watchdogRequiredStableChecks = 3;
    private final long watchdogTimeoutMs = 10 * 60_000;
    /** Controls where/whether an end-of-cycle summary report is emitted. */
    public enum EndOfCycleReporting { OFF, CONSOLE, FILE, BOTH }
    private final EndOfCycleReporting endOfCycleReport = EndOfCycleReporting.OFF;
    private final Path moveOriginalTo = Path.of("resources/originals");


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
    private final String KEY_RANDOM_CHARACTERS_TITLE = "randomCharactersTitle";
    private final String KEY_SAMPLE_SIZE_IN_BYTES = "sampleSizeInBytes";
    private final String KEY_MAX_CONTROL_CHARACTER_RATIO = "maxControlCharacterRatio";
    private final String KEY_MIN_UTF16_NUL_RATIO = "minUtf16NulRatio";
    private final String KEY_MIN_UTF32_BASIC_PLANE_RATIO = "minUtf32BasicPlaneRatio";
    private final String KEY_WATCHDOG = "watchdog";
    private final String KEY_WATCHDOG_POLL_INTERVAL_MS = "watchdogPollIntervalMs";
    private final String KEY_WATCHDOG_REQUIRED_STABLE_CHECKS = "watchdogRequiredStableChecks";
    private final String KEY_WATCHDOG_TIMEOUT_MS = "watchdogTimeoutMs";
    private final String KEY_END_OF_CYCLE_REPORT = "endOfCycleReport";
    private final String KEY_MOVE_ORIGINAL_TO = "moveOriginalTo";

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
        put(KEY_RANDOM_CHARACTERS_TITLE, randomCharactersTitle);
        put(KEY_SAMPLE_SIZE_IN_BYTES, sampleSizeInBytes);
        put(KEY_MAX_CONTROL_CHARACTER_RATIO, maxControlCharacterRatio);
        put(KEY_MIN_UTF16_NUL_RATIO, minUtf16NulRatio);
        put(KEY_MIN_UTF32_BASIC_PLANE_RATIO, minUtf32BasicPlaneRatio);
        put(KEY_WATCHDOG, watchdog);
        put(KEY_WATCHDOG_POLL_INTERVAL_MS, watchdogPollIntervalMs);
        put(KEY_WATCHDOG_REQUIRED_STABLE_CHECKS, watchdogRequiredStableChecks);
        put(KEY_WATCHDOG_TIMEOUT_MS, watchdogTimeoutMs);
        put(KEY_END_OF_CYCLE_REPORT, endOfCycleReport);
        put(KEY_MOVE_ORIGINAL_TO, moveOriginalTo);
    }};

    /**
     * Sets a raw config value by key.
     * @param key one of the internal {@code KEY_*} constants
     * @param value the new value to store
     * @throws IllegalArgumentException if {@code key} is not a recognized config key
     */
    public void put(String key, Object value) {
        if (!globalConfigStorage.containsKey(key)) {
            throw new IllegalArgumentException("Specified key doesn't exist");
        }
        globalConfigStorage.replace(key, value);
    }

    /** Clears all stored config values. */
    public void release() {
        globalConfigStorage.clear();
    }

    /** @return a human-readable snapshot of every config key and its current value, for debugging/logging. */
    public String toDebugString() {
        StringBuilder output = new StringBuilder("\n\n=== GLOBAL CONFIG === \n");
        for (Map.Entry<String, Object> entry : globalConfigStorage.entrySet()) {
            output.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
        output.append("\n=== END OF SNAPSHOT ===\n");
        return output.toString();
    }

    /**
     * Retrieves a raw config value by key, cast to the requested type.
     * @param key one of the internal {@code KEY_*} constants
     * @param type the expected value type
     * @return the stored value cast to {@code type}
     */
    public <$ValueType> $ValueType get(String key, Class<$ValueType> type) {
        return type.cast(globalConfigStorage.get(key));
    }

    /** @return how end-of-cycle summary reporting is handled; defaults to {@code OFF}. */
    public EndOfCycleReporting getEndOfCycleReport() {
        return Objects.requireNonNullElse(
                get(KEY_END_OF_CYCLE_REPORT, EndOfCycleReporting.class),
                endOfCycleReport);
    }

    /** @return the directory originals are moved to after processing; defaults to {@code resources/originals}. */
    public Path getMoveOriginalTo() {
        return Objects.requireNonNullElse(
                get(KEY_MOVE_ORIGINAL_TO, Path.class),
                moveOriginalTo
        );
    }

    /** @return how long (ms) the watchdog waits before timing out; defaults to 10 minutes. */
    public long getWatchdogTimeoutMs() {
        return Objects.requireNonNullElse(
                get(KEY_WATCHDOG_TIMEOUT_MS, Long.class),
                watchdogTimeoutMs);
    }

    /** @return how many consecutive stable polls the watchdog requires before acting; defaults to 3. */
    public int getWatchdogRequiredStableChecks() {
        return Objects.requireNonNullElse(
                get(KEY_WATCHDOG_REQUIRED_STABLE_CHECKS, Integer.class),
                watchdogRequiredStableChecks);
    }

    /** @return the watchdog's polling interval in ms; defaults to 500. */
    public long getWatchdogPollIntervalMs() {
        return Objects.requireNonNullElse(
                get(KEY_WATCHDOG_POLL_INTERVAL_MS, Long.class),
                watchdogPollIntervalMs);
    }

    /** @return whether watchdog mode is enabled; defaults to {@code false}. */
    public boolean getWatchdog() {
        return Objects.requireNonNullElse(
                get(KEY_WATCHDOG, Boolean.class),
                watchdog);
    }

    /** @return the number of bytes sampled for content sniffing; defaults to 8192. */
    public int getSampleSizeInBytes() {
        return Objects.requireNonNullElse(
                get(KEY_SAMPLE_SIZE_IN_BYTES, Integer.class),
                sampleSizeInBytes);
    }

    /** @return the maximum allowed ratio of control characters before content is flagged; defaults to 0.02. */
    public double getMaxControlCharacterRatio() {
        return Objects.requireNonNullElse(
                get(KEY_MAX_CONTROL_CHARACTER_RATIO, Double.class),
                maxControlCharacterRatio);
    }

    /** @return the minimum NUL-byte ratio used as a UTF-16 heuristic signal; defaults to 0.3. */
    public double getMinUtf16NulRatio() {
        return Objects.requireNonNullElse(
                get(KEY_MIN_UTF16_NUL_RATIO, Double.class),
                minUtf16NulRatio);
    }

    /** @return the minimum basic-plane character ratio used as a UTF-32 heuristic signal; defaults to 0.5. */
    public double getMinUtf32BasicPlaneRatio() {
        return Objects.requireNonNullElse(
                get(KEY_MIN_UTF32_BASIC_PLANE_RATIO, Double.class),
                minUtf32BasicPlaneRatio);
    }

    /** @return whether output filenames use randomized characters instead of timestamps; defaults to {@code false}. */
    public boolean getRandomCharactersTitle() {
        return Objects.requireNonNullElse(
                get(KEY_RANDOM_CHARACTERS_TITLE, Boolean.class),
                randomCharactersTitle);
    }

    /** @return the active logging verbosity level; defaults to {@code INFO}. */
    public LoggingLevels getLogLevel() {
        return Objects.requireNonNullElse(
                get(KEY_LOG_LEVEL, LoggingLevels.class),
                logLevel);
    }

    /** @return whether input files are restricted to residing under {@link #getInputRootPath()}; defaults to {@code false}. */
    public boolean getRestrictInputFromRoot() {
        return Objects.requireNonNullElse(
                get(KEY_RESTRICT_INPUT_FROM_ROOT, Boolean.class),
                restrictInputFromRoot);
    }

    /** @return the root directory input files must reside under, when restriction is enabled; defaults to {@code resources}. */
    public Path getInputRootPath() {
        return Objects.requireNonNullElse(
                get(KEY_INPUT_ROOT_PATH, Path.class),
                inputRootPath);
    }

    /** @return the directory reports are written to; defaults to {@code resources/output/reports}. */
    public Path getReportsOutputPath() {
        return Objects.requireNonNullElse(
                get(KEY_REPORTS_OUTPUT_PATH, Path.class),
                reportsOutputPath);
    }

    /** @return the timestamp the application booted at. */
    public Instant getBootTime() {
        return Objects.requireNonNullElse(
                get(KEY_BOOT_TIME, Instant.class),
                bootTime);
    }

    /** @return whether processing time is tracked/benchmarked per file; defaults to {@code true}. */
    public boolean getTrackTime() {
        return Objects.requireNonNullElse(
                get(KEY_TRACK_TIME, Boolean.class),
                trackTime);
    }

    /** @return how validation/processing errors are surfaced; defaults to {@code OFF}. */
    public VerboseErrors getVerboseErrors() {
        return Objects.requireNonNullElse(
                get(KEY_VERBOSE_ERRORS, VerboseErrors.class),
                verboseErrors);
    }

    /** @return whether a crashed file should be skipped rather than aborting the run; defaults to {@code false}. */
    public boolean getSkipCrashed() {
        return Objects.requireNonNullElse(
                get(KEY_SKIP_CRASHED, Boolean.class),
                skipCrashed);
    }

    /** @return whether an unsupported file type should be skipped rather than aborting the run; defaults to {@code false}. */
    public boolean getSkipUnsupported() {
        return Objects.requireNonNullElse(
                get(KEY_SKIP_UNSUPPORTED, Boolean.class),
                skipUnsupported);
    }

    /** @return whether benchmarking mode is enabled; defaults to {@code false}. */
    public boolean getBenchmarking() {
        return Objects.requireNonNullElse(
                get(KEY_BENCHMARKING, Boolean.class),
                benchmarking);
    }

    /** @return the maximum allowed length for generated IDs; defaults to 150. */
    public int getIDLength() {
        return Objects.requireNonNullElse(
                get(KEY_ID_LENGTH, Integer.class),
                IDLength);
    }

    /** @return the maximum allowed length for a MIME type string; defaults to 10. */
    public int getMimeLength() {
        return Objects.requireNonNullElse(
                get(KEY_MIME_LENGTH, Integer.class),
                mimeLength);
    }

    /** @return the hard upper bound a size limit may not exceed; defaults to 10MB. */
    public int getGeneralFileSizeUpperBoundLimit() {
        return Objects.requireNonNullElse(
                get(KEY_GENERAL_FILE_SIZE_UPPER_BOUND, Integer.class),
                generalFileSizeUpperBoundLimit);
    }

    /** @return whether the processed result is deleted after handling; defaults to {@code false}. */
    public boolean getDeleteResult() {
        return Objects.requireNonNullElse(
                get(KEY_DELETE_RESULT, Boolean.class),
                deleteResult
        );
    }

    /** @return whether the original input file is kept after processing; defaults to {@code true}. */
    public boolean isKeepOriginal() {
        return Objects.requireNonNullElse(
                get(KEY_KEEP_ORIGINAL, Boolean.class),
                keepOriginal
        );
    }

    /** @return the default output directory; defaults to {@code resources/output}. */
    public Path getGeneralOutputPath() {
        return Objects.requireNonNullElse(
                get(KEY_GENERAL_OUTPUT_PATH, Path.class),
                generalOutputPath
        );
    }

    /** @return the maximum allowed length for a generated target filename; defaults to 150. */
    public int getTargetFileLength() {
        return Objects.requireNonNullElse(
                get(KEY_TARGET_FILE_LENGTH, Integer.class),
                targetFileLength
        );
    }

    /** @return the default file size ceiling in bytes; defaults to 5MB. */
    public int getGeneralSizeLimit() {
        return Objects.requireNonNullElse(
                get(KEY_GENERAL_SIZE_LIMIT, Integer.class),
                generalSizeLimit
        );
    }

    /**
     * Sets the directory originals are moved to after processing.
     * @param newMoveOriginalTo the new destination path; normalized before storing
     * @throws IllegalArgumentException if {@code newMoveOriginalTo} is null
     */
    public void setMoveOriginalTo(Path newMoveOriginalTo) {
        if (newMoveOriginalTo == null) {
            throw new IllegalArgumentException("Move Original To cannot be null");
        }
        put(KEY_MOVE_ORIGINAL_TO, newMoveOriginalTo.normalize());
    }

    /**
     * Sets how end-of-cycle summary reporting is handled.
     * @throws IllegalArgumentException if {@code newEndOfCycleReport} is null
     */
    public void setEndOfCycleReport(EndOfCycleReporting newEndOfCycleReport) {
        if (newEndOfCycleReport == null) {
            throw new IllegalArgumentException("End of cycle report cannot be null");
        }
        put(KEY_END_OF_CYCLE_REPORT, newEndOfCycleReport);
    }

    /**
     * Sets the watchdog timeout in milliseconds.
     * @throws IllegalArgumentException if the value is zero or negative
     */
    public void setWatchdogTimeoutMs(long newWatchdogTimeoutMs) {
        if (newWatchdogTimeoutMs < 0) {
            throw new IllegalArgumentException("Watchdog timeout cannot be less than zero");
        }
        if (newWatchdogTimeoutMs == 0) {
            throw new IllegalArgumentException("Watchdog timeout cannot be zero");
        }
        put(KEY_WATCHDOG_TIMEOUT_MS, newWatchdogTimeoutMs);
    }

    /**
     * Sets how many consecutive stable polls the watchdog requires before acting.
     * @throws IllegalArgumentException if the value is zero or negative
     */
    public void setWatchdogRequiredStableChecks(int newWatchdogRequiredStableChecks) {
        if (newWatchdogRequiredStableChecks < 0) {
            throw new IllegalArgumentException("Watchdog required stable checks cannot be less than zero");
        }
        if (newWatchdogRequiredStableChecks == 0) {
            throw new IllegalArgumentException("Watchdog required stable checks cannot be zero");
        }
        put(KEY_WATCHDOG_REQUIRED_STABLE_CHECKS, newWatchdogRequiredStableChecks);
    }

    /**
     * Sets the watchdog's polling interval in milliseconds.
     * @throws IllegalArgumentException if the value is zero or negative
     */
    public void setWatchdogPollIntervalMs(long newWatchdogPollIntervalMs) {
        if (newWatchdogPollIntervalMs < 0) {
            throw new IllegalArgumentException("Watchdog poll interval cannot be less than zero");
        }
        if (newWatchdogPollIntervalMs == 0) {
            throw new IllegalArgumentException("Watchdog poll interval cannot be zero");
        }
        put(KEY_WATCHDOG_POLL_INTERVAL_MS, newWatchdogPollIntervalMs);
    }

    /** Enables or disables watchdog mode. */
    public void setWatchdog(boolean newWatchdog) {
        put(KEY_WATCHDOG, newWatchdog);
    }

    /**
     * Sets the number of bytes sampled for content sniffing.
     * @throws IllegalArgumentException if the value is zero or negative
     */
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

    /**
     * Sets the maximum allowed ratio of control characters before content is flagged.
     * @throws IllegalArgumentException if the value is not in (0.0, 1.0]
     */
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

    /**
     * Sets the minimum NUL-byte ratio used as a UTF-16 heuristic signal.
     * @throws IllegalArgumentException if the value is not in (0.0, 1.0]
     */
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

    /**
     * Sets the minimum basic-plane character ratio used as a UTF-32 heuristic signal.
     * @throws IllegalArgumentException if the value is not in (0.0, 1.0]
     */
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
        put(KEY_MIN_UTF32_BASIC_PLANE_RATIO, newMinUtf32BasicPlaneRatio);
    }

    /** Enables or disables randomized-character output filenames. */
    public void setRandomCharactersTitle(boolean newRandomCharactersTitle) {
        put(KEY_RANDOM_CHARACTERS_TITLE, newRandomCharactersTitle);
    }

    /**
     * Sets the active logging verbosity level.
     * @throws IllegalArgumentException if {@code newLogLevel} is null
     */
    public void setLogLevel(LoggingLevels newLogLevel) {
        if (newLogLevel == null) {
            throw new IllegalArgumentException("Log level cannot be null");
        }
        put(KEY_LOG_LEVEL, newLogLevel);
    }

    /**
     * Sets the root directory input files must reside under, when restriction is enabled.
     * @throws IllegalArgumentException if {@code newInputRootPath} is null
     */
    public void setInputRootPath(Path newInputRootPath) {
        if (newInputRootPath == null) {
            throw new IllegalArgumentException("Input root path cannot be empty");
        }
        put(KEY_INPUT_ROOT_PATH, newInputRootPath);
    }

    /**
     * Sets the directory reports are written to.
     * @throws IllegalArgumentException if {@code newReportsOutputPath} is null
     */
    public void setReportsOutputPath(Path newReportsOutputPath) {
        if (newReportsOutputPath == null) {
            throw new IllegalArgumentException("General output path cannot be empty");
        }
        put(KEY_REPORTS_OUTPUT_PATH, newReportsOutputPath);
    }

    /** Enables or disables restricting input files to {@link #getInputRootPath()}. */
    public void setRestrictInputFromRoot(boolean newRestrictInputFromRoot) {
        put(KEY_RESTRICT_INPUT_FROM_ROOT, newRestrictInputFromRoot);
    }

    /** Sets the application boot timestamp. */
    public void setBootTime(Instant newBootTime) {
        put(KEY_BOOT_TIME, newBootTime);
    }

    /** Enables or disables per-file time tracking/benchmarking. */
    public void setTrackTime(boolean newTrackTime) {
        put(KEY_TRACK_TIME, newTrackTime);
    }

    /**
     * Sets how validation/processing errors are surfaced.
     * @throws IllegalArgumentException if {@code newVerboseErrors} is null
     */
    public void setVerboseErrors(VerboseErrors newVerboseErrors) {
        if (newVerboseErrors == null) {
            throw new IllegalArgumentException("Verbose Errors cannot be null");
        }
        put(KEY_VERBOSE_ERRORS, newVerboseErrors);
    }

    /** Sets whether a crashed file is skipped rather than aborting the run. */
    public void setSkipCrashed(boolean newSkipCrashed) {
        put(KEY_SKIP_CRASHED, newSkipCrashed);
    }

    /** Sets whether an unsupported file type is skipped rather than aborting the run. */
    public void setSkipUnsupported(boolean newSkipUnsupported) {
        put(KEY_SKIP_UNSUPPORTED, newSkipUnsupported);
    }

    /** Enables or disables benchmarking mode. */
    public void setBenchmarking(boolean newBenchmarking) {
        put(KEY_BENCHMARKING, newBenchmarking);
    }

    /** Sets whether the processed result is deleted after handling. */
    public void setDeleteResult(boolean newDeleteResult) {
        put(KEY_DELETE_RESULT, newDeleteResult);
    }

    /** Sets whether the original input file is kept after processing. */
    public void setKeepOriginal(boolean newKeepOriginal) {
        put(KEY_KEEP_ORIGINAL, newKeepOriginal);
    }

    /**
     * Sets the default file size ceiling in bytes.
     * @throws IllegalArgumentException if negative or greater than {@link #getGeneralFileSizeUpperBoundLimit()}
     */
    public void setGeneralSizeLimit(int newSizeLimit) {
        if (newSizeLimit < 0) {
            throw new IllegalArgumentException("Size limit cannot be less than zero");
        }
        if (newSizeLimit > generalFileSizeUpperBoundLimit) {
            throw new IllegalArgumentException("Size limit cannot exceed upper bound");
        }
        put(KEY_GENERAL_SIZE_LIMIT, newSizeLimit);
    }

    /**
     * Sets the default output directory.
     * @throws IllegalArgumentException if {@code newGeneralOutputPath} is null
     */
    public void setGeneralOutputPath(Path newGeneralOutputPath) {
        if (newGeneralOutputPath == null) {
            throw new IllegalArgumentException("General output path cannot be empty");
        }
        put(KEY_GENERAL_OUTPUT_PATH, newGeneralOutputPath);
    }

    /**
     * Sets the maximum allowed length for a generated target filename.
     * @throws IllegalArgumentException if negative
     */
    public void setTargetFileLength(int newTargetFileLength) {
        if (newTargetFileLength < 0) {
            throw new IllegalArgumentException("Target file length cannot be less than zero");
        }
        put(KEY_TARGET_FILE_LENGTH, newTargetFileLength);
    }

    /**
     * Sets the maximum allowed length for generated IDs.
     * @throws IllegalArgumentException if negative
     */
    public void setIDLength(int newIDLength) {
        if (newIDLength < 0) {
            throw new IllegalArgumentException("ID Length cannot be less than zero");
        }
        put(KEY_ID_LENGTH, newIDLength);
    }

    /**
     * Sets the maximum allowed length for a MIME type string.
     * @throws IllegalArgumentException if negative
     */
    public void setMimeLength(int newMimeLength) {
        if (newMimeLength < 0) {
            throw new IllegalArgumentException("Mime length cannot be less than zero");
        }
        put(KEY_MIME_LENGTH, newMimeLength);
    }
}

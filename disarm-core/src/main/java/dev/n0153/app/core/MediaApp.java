package dev.n0153.app.core;

import dev.n0153.app.core.exceptions.DisarmException;
import dev.n0153.app.core.exceptions.UnsupportedFileTypeException;
import dev.n0153.app.core.exceptions.ValidationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.Duration;
import java.time.Instant;

public class MediaApp {
    private final PluginRegistry registry;
    private static final Logger logger = LogManager.getLogger(MediaApp.class);
    private final GlobalConfig globalConfig;
    private final ProcessingContext processingContext;

    /**
     * Creates the application instance responsible for disarming files.
     * A new ProcessingContext is created for every MediaApp instance.
     * @param registry Plugin registry used to resolve plugins, configs and processors.
     * @param globalConfig Global configuration instance.
     * @since 0.2
     */
    public MediaApp (PluginRegistry registry, GlobalConfig globalConfig) {
        this.registry = registry;
        this.globalConfig = globalConfig;
        this.processingContext = new ProcessingContext(globalConfig);
    }

    /**
     * Deletes the original input file once it has been processed, if keepOriginal is disabled (-do).
     * NOTE! Deletion is refused if moveOriginalTo (-mot) is configured, since both options are mutually exclusive.
     * Failures are logged and not rethrown.
     * @param osTargetPath Path to original input file.
     * @since 0.2
     */
    public void deleteOriginalIfRequired(Path osTargetPath) {
        if (!globalConfig.isKeepOriginal()) {
            if (globalConfig.getMoveOriginalTo().equals(Path.of("resources/originals"))) {
                try{
                    Utils.fileDispose(osTargetPath);
                } catch (IOException e) {
                    logger.error("Failed to delete specified file", e);
                }
            } else {
                logger.error("Cannot delete original file because moveOriginalTo is enabled.");
            }
        }
    }

    /**
     * Moves the original input file into the configured moveOriginalTo directory (-mot), if one is set.
     * The target directory is created if it doesn't exist.
     * NOTE! The move is refused if keepOriginal is disabled (-do), since both options are mutually exclusive.
     * Failures are logged and not rethrown.
     * @param osTargetPath Path to original input file.
     * @since 0.2
     */
    public void moveOriginalIfRequired(Path osTargetPath) {
        if (!globalConfig.getMoveOriginalTo().equals(Path.of("resources/originals"))) {
            if (globalConfig.isKeepOriginal()) {
                try {
                    Utils.fileMoveTo(osTargetPath, globalConfig.getMoveOriginalTo());
                } catch (IOException e) {
                    logger.error("Failed to move specified file", e);
                }
            } else {
                logger.error("Cannot move specified file because keepOriginal is disabled");
            }
        }
    }

    /**
     * Resolves which registered plugin is responsible for the specified mime type.
     * @param format Mime type of the input file, as returned by Utils.getMimeType().
     * @return Plugin registered for the specified mime type.
     * @throws UnsupportedFileTypeException If no plugin is registered for the specified mime type.
     * @since 0.2
     */
    public MediaPlugin getPlugin(String format) {
        return registry.resolve(format);
    }

    /**
     * Saves the specified report as a UTF-8 text file in the configured reports output path (-ro).
     * The reports directory is created if it doesn't exist.
     * The file title is "ERROR_REPORT" plus a timestamp, or plus random characters if -rct is enabled.
     * @param report Report contents.
     * @throws IOException If fails to write the report file.
     * @since 0.2
     */
    private void createReportFile(String report) throws IOException {
        if (!GlobalValidator.outputExist(globalConfig.getReportsOutputPath())) {
            logger.warn("Output directory for reports doesn't exist, attempting to create one...");
            Utils.createDirectory(globalConfig.getReportsOutputPath());
        }
        Path toFile;
        if (globalConfig.getRandomCharactersTitle()) {
            toFile = globalConfig.getReportsOutputPath().
                    resolve(Utils.getTitleForcedTagRandomCharacters("ERROR_REPORT", "txt"));
        } else {
            toFile = globalConfig.getReportsOutputPath().
                    resolve(Utils.getTitleForcedTagTimeStamp("ERROR_REPORT", "txt"));
        }
        Files.writeString(toFile, report, StandardCharsets.UTF_8);
    }

    /**
     * Generates an end of cycle report for the processed file (-eocr).
     * The report contains file info, the resolved plugin and its version, global settings,
     * config snapshots and timing.
     * The report is printed to the console, saved as a file or both, depending on the configured
     * end of cycle reporting mode. Nothing is generated if the mode is OFF.
     * NOTE! If no plugin can be resolved, plugin info and the config snapshot are reported as "unsupported".
     * Failures to save the report file are logged and not rethrown.
     * @param osTargetPath Path to input file.
     * @since 0.2
     */
    public void dumpReport(Path osTargetPath) {
        // file info
        String filename = osTargetPath.getFileName().toString();
        String mime = processingContext.getMimeType();
        String fileType = processingContext.getFileType();
        String pluginResolved;
        double pluginVersion;
        try {
             pluginResolved = getPlugin(mime).echo();
             pluginVersion = getPlugin(mime).getConfig().getVersion();
        } catch (UnsupportedFileTypeException e) {
            pluginResolved = "unsupported";
            pluginVersion = 0.0;
        }

        // configs
        String outputPath = globalConfig.getGeneralOutputPath().toString();
        int sizeLimit = globalConfig.getGeneralSizeLimit();
        boolean skipUnsupported = globalConfig.getSkipUnsupported();
        boolean skipCrashed = globalConfig.getSkipCrashed();
        boolean benchmarking = globalConfig.getBenchmarking();
        boolean keepOriginal = globalConfig.isKeepOriginal();
        GlobalConfig.VerboseErrors isVerbose = globalConfig.getVerboseErrors();
        String configSnapshot;
        try {
            configSnapshot = processingContext.getConfigSnapshot().toDebugString();
        } catch (NullPointerException e) {
            configSnapshot = "unsupported";
        }
        String globalConfigSnapshot = globalConfig.toDebugString();

        // timing
        String bootTime = "" + globalConfig.getBootTime();
        String failTime = "" + Instant.now();

        String report = """
                \n
                === DISARM ERROR REPORT ===
                # File info
                File name:             %s
                Mime type:             %s
                File type:             %s
                Plugin resolved:       %s
                Plugin version:        %s
                
                # Configs
                Output path:           %s
                Size limit:            %s
                Skip unsupported:      %s
                Skip crashed:          %s
                Benchmarking:          %s
                Keep original:         %s
                Verbosity:             %s
                Config snapshot:       %s
                Global Config Snapshot %s
                
                # Timing
                Boot time:             %s
                Failure time:          %s
                
                === END OF REPORT ===
                """.formatted(filename, mime, fileType,
                pluginResolved, pluginVersion,
                outputPath, sizeLimit,
                skipUnsupported, skipCrashed, benchmarking,
                keepOriginal, isVerbose, configSnapshot,
                globalConfigSnapshot, bootTime, failTime);
        if (globalConfig.getEndOfCycleReport() == GlobalConfig.EndOfCycleReporting.CONSOLE) {
            logger.info(report);
        }
        if (globalConfig.getEndOfCycleReport() == GlobalConfig.EndOfCycleReporting.FILE) {
            try {
                createReportFile(report);
            } catch (IOException e) {
                logger.error("Failed to create report file");
            }
        }
        if (globalConfig.getEndOfCycleReport() == GlobalConfig.EndOfCycleReporting.BOTH) {
            logger.info(report);
            try {
                createReportFile(report);
            } catch (IOException e) {
                logger.error("Failed to create report file");
            }
        }
    }

    /**
     * Generates a verbose error report for a file whose processing failed (-ve).
     * In addition to the end of cycle report contents, the error report contains the processing stage
     * at which the failure occurred, plus the exception name and message.
     * The report is printed to the console, saved as a file or both, depending on the configured
     * verbose errors mode. Nothing is generated if the mode is OFF.
     * Failures to save the report file are logged and not rethrown.
     * @param osTargetPath Path to input file.
     * @param exception Exception that caused the failure.
     * @since 0.2
     */
    public void dumpReport(Path osTargetPath, DisarmException exception) {
        // file info
        String filename = osTargetPath.getFileName().toString();
        String mime = processingContext.getMimeType();
        String fileType = processingContext.getFileType();
        String pluginResolved;
        double pluginVersion;
        try {
            pluginResolved = getPlugin(mime).echo();
            pluginVersion = getPlugin(mime).getConfig().getVersion();
        } catch (UnsupportedFileTypeException e) {
            pluginResolved = "unsupported";
            pluginVersion = 0.0;
        }

        // failure info
        String stage = processingContext.getStage();
        String exceptionName = exception.getClass().getSimpleName();
        String exceptionMessage = exception.getMessage();

        // configs
        String outputPath = globalConfig.getGeneralOutputPath().toString();
        int sizeLimit = globalConfig.getGeneralSizeLimit();
        boolean skipUnsupported = globalConfig.getSkipUnsupported();
        boolean skipCrashed = globalConfig.getSkipCrashed();
        boolean benchmarking = globalConfig.getBenchmarking();
        boolean keepOriginal = globalConfig.isKeepOriginal();
        GlobalConfig.VerboseErrors isVerbose = globalConfig.getVerboseErrors();
        String configSnapshot;
        try {
            configSnapshot = processingContext.getConfigSnapshot().toDebugString();
        } catch (NullPointerException e) {
            configSnapshot = "unsupported";
        }
        String globalConfigSnapshot = globalConfig.toDebugString();

        // timing
        String bootTime = "" + globalConfig.getBootTime();
        String failTime = "" + Instant.now();

        String report = """
                \n
                === DISARM ERROR REPORT ===
                # File info
                File name:             %s
                Mime type:             %s
                File type:             %s
                Plugin resolved:       %s
                Plugin version:        %s
                
                # Failure info
                Stage:                 %s
                Exception name:        %s
                Exception message:     %s
                
                # Configs
                Output path:           %s
                Size limit:            %s
                Skip unsupported:      %s
                Skip crashed:          %s
                Benchmarking:          %s
                Keep original:         %s
                Verbosity:             %s
                Config snapshot:       %s
                Global Config Snapshot %s
                
                # Timing
                Boot time:             %s
                Failure time:          %s
                
                === END OF REPORT ===
                """.formatted(filename, mime, fileType,
                pluginResolved, pluginVersion, stage, exceptionName,
                exceptionMessage, outputPath, sizeLimit,
                skipUnsupported, skipCrashed, benchmarking,
                keepOriginal, isVerbose, configSnapshot,
                globalConfigSnapshot, bootTime, failTime);
        if (isVerbose == GlobalConfig.VerboseErrors.CONSOLE) {
            logger.info(report);
        }
        if (isVerbose == GlobalConfig.VerboseErrors.FILE) {
            try {
                createReportFile(report);
            } catch (IOException e) {
                logger.error("Failed to create report file");
            }
        }
        if (isVerbose == GlobalConfig.VerboseErrors.BOTH) {
            logger.info(report);
            try {
                createReportFile(report);
            } catch (IOException e) {
                logger.error("Failed to create report file");
            }
        }
    }

    /**
     * Executes a full disarming cycle for a single file:
     * mime and file type detection, plugin resolution, context population, global validation,
     * plugin-level validation and processing, followed by optional deletion or move of the original
     * file, optional deletion of the result (-dr) and an optional end of cycle report.
     * If processing fails, the file is skipped with a warning when the matching skip option is enabled:
     * -su for unsupported files, -sc for any other failure. A verbose error report or end of cycle
     * report is generated for skipped files if enabled.
     * Failures to delete the result file are logged and not rethrown.
     * @param osTargetPath Path to input file.
     * @throws DisarmException If processing fails and the matching skip option isn't enabled.
     * @since 0.2
     */
    private void processFile(Path osTargetPath) {
        logger.warn("Verbose errors: {}", globalConfig.getVerboseErrors());
        try {
            // populate context
            String mime = Utils.getMimeType(osTargetPath);
            String fileType = Utils.getFileType(osTargetPath);
            logger.info("detected mime: {}, detected file type: {}", mime, fileType);
            MediaPlugin plugin = getPlugin(mime);
            logger.info("detected plugin: {}", plugin.echo());
            MediaConfig mediaConfig = registry.resolveConfig(fileType);
            processingContext.populateContext(osTargetPath, plugin.echo(), mediaConfig);
            processingContext.setConfigSnapshot(registry.resolveConfig(fileType));
            logger.info("general context populated");
            plugin.registerGlobalConfig(globalConfig);
            processingContext.setStage("context populated");

            // run validations
            if (!validateGlobal(osTargetPath)) {
                throw new ValidationException("Global validation failed");
            }
            processingContext.setStage("global validation passed");
            logger.info("Global validations passed");

            if (!validatePlugin(plugin, osTargetPath)) {
                throw new ValidationException(mediaConfig.getName() + " Plugin validation failed");
            }
            processingContext.setStage("plugin validation passed");
            logger.info("{} plugin validations passed", mediaConfig.getName());

            // process input
            getProcessor(plugin, mediaConfig).process(osTargetPath);

            // delete optionally
            deleteOriginalIfRequired(osTargetPath);
            moveOriginalIfRequired(osTargetPath);
            if (globalConfig.getDeleteResult()) {
                try {
                    Utils.fileDispose(globalConfig.getGeneralOutputPath()
                            .resolve(plugin.getProcessor(mediaConfig)
                                    .getContext().
                                    getOutputTitle()));
                } catch (IOException e) {
                    logger.error("Failed to delete result file: {}",
                            globalConfig.getGeneralOutputPath().resolve(processingContext.getFilename()));
                }
            }
            if (globalConfig.getEndOfCycleReport() != GlobalConfig.EndOfCycleReporting.OFF) {
                dumpReport(osTargetPath);
            }
        } catch (DisarmException e) {
            boolean isUnsupported = e instanceof UnsupportedFileTypeException;
            if (isUnsupported && globalConfig.getSkipUnsupported()) {
                logger.warn("Skipped unsupported [{}] - {}: {}",
                        osTargetPath.getFileName(),
                        e.getClass().getSimpleName(),
                        e.getMessage());
                if (globalConfig.getVerboseErrors() != GlobalConfig.VerboseErrors.OFF) {
                    dumpReport(osTargetPath, e);
                } else if (globalConfig.getEndOfCycleReport() != GlobalConfig.EndOfCycleReporting.OFF) {
                    dumpReport(osTargetPath);
                }
            } else if (!isUnsupported && globalConfig.getSkipCrashed()){
                logger.warn("Skipped crashed [{}] - {}: {}",
                        osTargetPath.getFileName(),
                        e.getClass().getSimpleName(),
                        e.getMessage());
                if (globalConfig.getVerboseErrors() != GlobalConfig.VerboseErrors.OFF) {
                    dumpReport(osTargetPath, e);
                } else if (globalConfig.getEndOfCycleReport() != GlobalConfig.EndOfCycleReporting.OFF) {
                    dumpReport(osTargetPath);
                }
            } else {
                throw e;
            }
        }
    }

    /**
     * Runs global validation checks on the specified file: path safety, readability,
     * output path availability, empty file check and global size limit.
     * The output directory is created if it doesn't exist.
     * @param osTargetPath Path to input file.
     * @return True if all global validation checks passed.
     * @throws InvalidPathException If the input file isn't readable.
     * @throws ValidationException If the input file is empty or exceeds the global size limit.
     * @since 0.2
     */
    public boolean validateGlobal(Path osTargetPath) {
        return GlobalValidator.validate(osTargetPath, globalConfig);
    }

    /**
     * Shortcut method, returns the processor of the specified plugin, configured with the specified config.
     * @param plugin Resolved plugin.
     * @param config Plugin config to process the file with.
     * @return Plugin's processor instance.
     * @since 0.2
     */
    public MediaProcessor<?> getProcessor(MediaPlugin plugin, MediaConfig config) {
        return plugin.getProcessor(config);
    }

    /**
     * Watchdog helper method, waits until the specified file has been fully written to disk.
     * The file size is polled at the configured interval (-wdpi). The file counts as finished once its size
     * stays unchanged for the required number of consecutive checks (-wdsc).
     * @param osTargetPath Path to newly created file.
     * @return True if the file is stable. False if the file disappeared, turned out to be a directory,
     * couldn't be read, or the configured timeout (-wdto) was reached.
     * @throws InterruptedException If the thread is interrupted while waiting between checks.
     * @since 0.2
     */
    public boolean validatePlugin(MediaPlugin plugin, Path osTargetPath) {
        if (plugin.getValidator(registry.resolveConfig(Utils.getFileType(osTargetPath))) == null) {
            logger.info("This plugin doesn't support plugin-level validation");
            return true;
        }
        return plugin.getValidator(registry.resolveConfig(Utils.getFileType(osTargetPath))).validate(osTargetPath);
    }

    /**
     * Watchdog helper method, waits until the specified file has been fully written to disk.
     * The file size is polled at the configured interval (-wdpi). The file counts as finished once its size
     * stays unchanged for the required number of consecutive checks (-wdsc).
     * @param osTargetPath Path to newly created file.
     * @return True if the file is stable. False if the file disappeared, turned out to be a directory,
     * couldn't be read, or the configured timeout (-wdto) was reached.
     * @throws InterruptedException If the thread is interrupted while waiting between checks.
     * @since 0.2
     */
    private boolean waitUntilStable(Path osTargetPath) throws InterruptedException {
        long startedAt = System.currentTimeMillis();
        long lastSize = -1;
        int stableChecks = 0;

        while (System.currentTimeMillis() - startedAt < globalConfig.getWatchdogTimeoutMs()) {
            try {
                if (!Files.exists(osTargetPath) || Files.isDirectory(osTargetPath)) {
                    return false;
                }
                long size = Files.size(osTargetPath);
                if (size == lastSize) {
                    stableChecks ++;
                } else {
                    stableChecks = 0;
                    lastSize = size;
                }
                if (stableChecks >= globalConfig.getWatchdogRequiredStableChecks()) {
                    return true;
                }
            } catch (IOException e) {
                return false;
            }
            Thread.sleep(globalConfig.getWatchdogPollIntervalMs());
        }
        logger.error("Timed out whiles waiting for: {}", osTargetPath);
        return false;
    }

    /**
     * Watchdog main loop, blocks until new files are created in the watched directory.
     * Each new file is processed once it is stable. Overflow events are ignored.
     * The loop ends when the watch key becomes invalid, e.g. when the watched directory is deleted.
     * NOTE! Processing failures that aren't skipped (-su, -sc) propagate and stop the loop.
     * @param watcher Watch service the directory is registered with.
     * @param osTargetPath Path to watched directory.
     * @throws InterruptedException If the thread is interrupted while waiting for events.
     * @since 0.2
     */
    private void watchLoop(WatchService watcher, Path osTargetPath) throws InterruptedException {
        while (true) {
            WatchKey key = watcher.take();
            for (WatchEvent<?> event : key.pollEvents()) {
                WatchEvent.Kind<?> kind = event.kind();
                if (kind == StandardWatchEventKinds.OVERFLOW) {
                    continue;
                }
                @SuppressWarnings("unchecked")
                WatchEvent<Path> ev = (WatchEvent<Path>) event;
                Path filename = ev.context();
                logger.info("Kind: {}, filename: {}", kind.name(), filename);
                Path fullPath = osTargetPath.resolve(filename);
                if (waitUntilStable(fullPath)) {
                    processFile(fullPath);
                }
            }
            boolean valid = key.reset();
            if (!valid) {
                break;
            }
        }
    }


    /**
     * Starts watchdog mode (-wd), monitoring a directory for newly created files.
     * If a regular file is specified, its parent directory is watched instead.
     * Runs until interrupted (Ctrl+C) or the watch key becomes invalid.
     * @param osTargetPath Path to directory to watch, or to a file inside it.
     * @throws DisarmException If fails to register the watch service or the watchdog is interrupted.
     * @since 0.2
     */
    private void watchdog(Path osTargetPath) {
        Path dir;
        if (Files.isRegularFile(osTargetPath)) {
            dir = osTargetPath.toAbsolutePath().getParent();
        } else {
            dir = osTargetPath.toAbsolutePath();
        }
        try (WatchService watcher = FileSystems.getDefault().newWatchService()) {
            dir.register(watcher, StandardWatchEventKinds.ENTRY_CREATE);
            logger.info("Directory {} was registered by watchdog, starting the loop...", dir);
            logger.info("Press Ctrl+c to stop Disarm");
            watchLoop(watcher, dir);
        } catch (IOException | InterruptedException e) {
            throw new DisarmException("File disarming failed");
        }
    }

    /**
     * Public disarming endpoint. Disarms the specified file, or starts watchdog mode if it is enabled (-wd).
     * If benchmarking is enabled (-b), processing time is measured and logged.
     * The processing context is released once processing finishes.
     * NOTE! In watchdog mode, the benchmark covers the entire watchdog session, not individual files.
     * @param osTargetPath Path to input file, or to the directory to watch in watchdog mode.
     * @throws DisarmException If processing fails and the matching skip option isn't enabled.
     * @since 0.2
     */
    public void fileDisarm(Path osTargetPath) {
        if (globalConfig.getBenchmarking()) {
            processingContext.setStartedAt(Instant.now());
        }
        processingContext.setStage("processing started");
        if (globalConfig.getWatchdog()) {
            logger.info("Watchdog mode enabled.");
            watchdog(osTargetPath);
        } else {
            processFile(osTargetPath);
        }
        if (globalConfig.getBenchmarking()) {
            processingContext.setCompletedAt(Instant.now());
            long duration = Duration.between(
                    processingContext.getStartedAt(),
                    processingContext.getCompletedAt())
                    .toMillis();
            logger.info("File processed in {}ms", duration);
            processingContext.setStage("processing finished");
        }
        processingContext.release();
    }
}

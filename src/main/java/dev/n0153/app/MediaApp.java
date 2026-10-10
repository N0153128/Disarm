package dev.n0153.app;

import dev.n0153.app.exceptions.*;
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

    public MediaApp (PluginRegistry registry, GlobalConfig globalConfig) {
        this.registry = registry;
        this.globalConfig = globalConfig;
        this.processingContext = new ProcessingContext(globalConfig);
    }

    public void deleteOriginalIfRequired(Path osTargetPath) {
        if (!globalConfig.isKeepOriginal()) {
            try{
                Utils.fileDispose(osTargetPath);
            } catch (IOException e) {
                logger.info("Failed to delete specified file");
            }
        }
    }

    public MediaPlugin getPlugin(String format) {
        return registry.resolve(format);
    }

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
            if (globalConfig.getDeleteResult()) {
                try {
                    Utils.fileDispose(globalConfig.getGeneralOutputPath()
                            .resolve(plugin.getProcessor(mediaConfig)
                                    .getContext().
                                    getOutputTitle()));
                } catch (IOException e) {
                    logger.error("Failed to delete resul file: {}",
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

    public boolean validateGlobal(Path osTargetPath) {
        return GlobalValidator.validate(osTargetPath, globalConfig);
    }

    public MediaProcessor<?> getProcessor(MediaPlugin plugin, MediaConfig config) {
        return plugin.getProcessor(config);
    }

    public boolean validatePlugin(MediaPlugin plugin, Path osTargetPath) {
        if (plugin.getValidator(registry.resolveConfig(Utils.getFileType(osTargetPath))) == null) {
            logger.info("This plugin doesn't support plugin-level validation");
            return true;
        }
        return plugin.getValidator(registry.resolveConfig(Utils.getFileType(osTargetPath))).validate(osTargetPath);
    }

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

package dev.n0153.app;

import dev.n0153.app.exceptions.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
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

    public void deleteOriginal(Path osTargetPath) {
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

    private void createReportFile(String report) {
        if (!GlobalValidator.outputExist(globalConfig.getReportsOutputPath())) {
            logger.warn("Output directory for reports doesn't exist, attempting to create one...");
            Utils.createDirectory(globalConfig.getReportsOutputPath());
        }
        try {
            Path toFile = globalConfig.getReportsOutputPath().
                    resolve(Utils.getTitle("ERROR_REPORT", "txt"));
            Files.writeString(toFile, report, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new DisarmException(e);
        }
    }

    public void dumpErrorReport(Path osTargetPath, DisarmException exception) {
        // file info
        String filename = osTargetPath.getFileName().toString();
        String mime = processingContext.getMimeType();
        String fileType = processingContext.getFileType();
        String pluginResolved;
        try {
             pluginResolved = getPlugin(mime).echo();
        } catch (UnsupportedFileTypeException e) {
            pluginResolved = "unsupported";
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
                pluginResolved, stage, exceptionName,
                exceptionMessage, outputPath, sizeLimit,
                skipUnsupported, skipCrashed, benchmarking,
                keepOriginal, isVerbose, configSnapshot,
                globalConfigSnapshot, bootTime, failTime);
        if (isVerbose == GlobalConfig.VerboseErrors.CONSOLE) {
            logger.info(report);
        }
        if (isVerbose == GlobalConfig.VerboseErrors.FILE) {
            createReportFile(report);
        }
        if (isVerbose == GlobalConfig.VerboseErrors.BOTH) {
            logger.info(report);
            createReportFile(report);
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
            processingContext.setConfigSnapshot(plugin.getConfig());
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
        } catch (DisarmException e) {
            boolean isUnsupported = e instanceof UnsupportedFileTypeException;
            if (isUnsupported && globalConfig.getSkipUnsupported()) {
                logger.warn("Skipped unsupported [{}] - {}: {}",
                        osTargetPath.getFileName(),
                        e.getClass().getSimpleName(),
                        e.getMessage());
                if (globalConfig.getVerboseErrors() != GlobalConfig.VerboseErrors.OFF) {
                    dumpErrorReport(osTargetPath, e);
                }
            } else if (!isUnsupported && globalConfig.getSkipCrashed()){
                logger.warn("Skipped crashed [{}] - {}: {}",
                        osTargetPath.getFileName(),
                        e.getClass().getSimpleName(),
                        e.getMessage());
                if (globalConfig.getVerboseErrors() != GlobalConfig.VerboseErrors.OFF) {
                    dumpErrorReport(osTargetPath, e);
                }            } else {
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
        if (plugin.getValidator() == null) {
            logger.info("This plugin doesn't support plugin-level validation");
            return true;
        }
        return plugin.getValidator().validate(osTargetPath);
    }

    public void fileDisarm(Path osTargetPath) {
        if (globalConfig.getBenchmarking()) {
            processingContext.setStartedAt(Instant.now());
        }
        processingContext.setStage("processing started");
        processFile(osTargetPath);
        if (globalConfig.getBenchmarking()) {
            processingContext.setCompletedAt(Instant.now());
            long duration = Duration.between(
                    processingContext.getStartedAt(),
                    processingContext.getCompletedAt())
                    .toMillis();
            logger.info("File processed in {}ms", duration);
            processingContext.setStage("processing finished");
        }
        deleteOriginal(osTargetPath);
    }
}

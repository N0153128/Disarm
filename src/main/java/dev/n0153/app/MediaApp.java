package dev.n0153.app;

import dev.n0153.app.exceptions.FileTypeDetectionException;
import dev.n0153.app.exceptions.MimeTypeDetectionException;
import dev.n0153.app.exceptions.ValidationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

    public MediaPlugin getPlugin(String format) {
        return registry.resolve(format);
    }

    private void processFile(Path osTargetPath) {
        try {
            // populate context
            String mime = Utils.getMimeType(osTargetPath);
            String fileType = Utils.getFileType(osTargetPath);
            logger.info("detected mime: {}, detected file type: {}", mime, fileType);
            MediaPlugin plugin = getPlugin(mime);
            logger.info("detected plugin: {}", plugin.echo());
            MediaConfig mediaConfig = registry.resolveConfig(fileType);
            processingContext.populateContext(osTargetPath, plugin.echo(), mediaConfig);
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
        } catch (MimeTypeDetectionException | FileTypeDetectionException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean validateGlobal(Path osTargetPath) {
        return GlobalValidator.validate(osTargetPath, globalConfig);
    }

    public MediaProcessor<?> getProcessor(MediaPlugin plugin, MediaConfig config) {
        return plugin.getProcessor(config);
    }

    public boolean validatePlugin(MediaPlugin plugin, Path osTargetPath) {
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
    }
}

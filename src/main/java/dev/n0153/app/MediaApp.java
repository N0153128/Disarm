package dev.n0153.app;

import dev.n0153.app.exceptions.MimeTypeDetectionException;
import dev.n0153.app.exceptions.ValidationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;

public class MediaApp {
    private final PluginRegistry registry;
    private static final Logger logger = LogManager.getLogger(MediaApp.class);
    private final GlobalConfig globalConfig = new GlobalConfig();
    private final ProcessingContext processingContext = new ProcessingContext(globalConfig);

    public MediaApp (PluginRegistry registry) {
        this.registry = registry;
    }

    public MediaPlugin getPlugin(String format) {
        return registry.resolve(format);
    }

    private void processFile(Path osTargetPath) {

    }

    public boolean validateGlobal(Path osTargetPath) {
        return GlobalValidator.validate(osTargetPath, globalConfig);
    }

    public boolean validatePlugin(Path osTargetPath) {
        return processingContext.getResolvedPlugin().getValidator().validate(osTargetPath);
    }

    public void fileDisarm(Path osTargetPath) {
        logger.info("fileDisarm hit");
        try {
            String mime = Utils.getMimeType(osTargetPath);
            logger.info("detected mime: {}", mime);
            MediaPlugin plugin = getPlugin(mime);
            logger.info("detected plugin: {}", plugin.echo());
            MediaConfig mediaConfig = plugin.getConfig();
            processingContext.populateContext(osTargetPath, plugin, mediaConfig);
            logger.info("general context populated");
            if (!validateGlobal(osTargetPath)) {
                throw new ValidationException("Global validation failed");
            }
            logger.info("Global validations passed");
            if (!validatePlugin(osTargetPath)) {
                throw new ValidationException(mediaConfig.getName() + " Plugin validation failed");
            }
            logger.info("{} plugin validations passed", mediaConfig.getName());
            processingContext.getResolvedPlugin().getProcessor().process(osTargetPath);
        } catch (MimeTypeDetectionException e) {
            throw new RuntimeException(e);
        }
    }

    public void fileDisarm(Path osTargetPath, Path osLogoPath) {

    }
}

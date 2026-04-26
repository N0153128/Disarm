package dev.n0153.app;

import dev.n0153.app.exceptions.FileRenameException;
import dev.n0153.app.exceptions.FileTypeDetectionException;
import dev.n0153.app.exceptions.MimeTypeDetectionException;
import dev.n0153.app.plugins.DisarmPlugins;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;

public class MediaApp {
    private final PluginRegistry registry;
    private static final Logger logger = LogManager.getLogger(MediaApp.class);

    public MediaApp (PluginRegistry registry) {
        this.registry = registry;
    }

    public void registerPlugins() {
        DisarmPlugins disarmPlugins = new DisarmPlugins();
        disarmPlugins.registerAll(registry);
        logger.info("plugins registered");
    }

    public MediaPlugin getPlugin(String format) {
        return registry.resolve(format);
    }

    public MediaValidator getValidator(MediaPlugin plugin) {
        return plugin.getValidator();
    }

    public MediaConfig getPlugin(MediaPlugin plugin) {
        return plugin.getConfig();
    }

    private void processFile(Path osTargetPath) {

    }

    public void fileDisarm(Path osTargetPath) {
        logger.info("fileDisarm hit");
        registerPlugins();
        try {
            String mime = Utils.getMimeType(osTargetPath);
            logger.info("detected mime: {}", mime);
            MediaPlugin plugin = getPlugin(mime);
            logger.info("detected plugin: {}", plugin.echo());
        } catch (MimeTypeDetectionException e) {
            throw new RuntimeException(e);
        }
    }

    public void fileDisarm(Path osTargetPath, Path osLogoPath) {

    }
}

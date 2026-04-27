package dev.n0153.app;

import dev.n0153.app.exceptions.UnsupportedFileTypeException;
import dev.n0153.app.plugins.image.ImagePicoCli;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

/**
 * This class holds information about all plugins and acts as a glue between App and plugins.
 */
public class PluginRegistry {
    private static final Logger logger = LogManager.getLogger(PluginRegistry.class);
    private final Map<String, MediaPlugin> processorRegistry = new HashMap<>();
    public final Map<String, Runnable> cliRegistry = new HashMap<>();


    /**
     * This method is a registry point for all plugins and their respective MIME types.
     * @param mimeType plugin's supported MIME type
     * @param plugin plugin instance
     */
    public void registerExperimental(
            Set<String> mimeType,
            MediaPlugin plugin,
            ImagePicoCli cliConfig,
            String fileType) {
        for (String mime : mimeType) {
            processorRegistry.put(mime, plugin);
        }
        cliRegistry.put(fileType, cliConfig);
        logger.info("CLI and Plugin were registered successfully");

    }

    /**
     * This method allows to resolve which plugin is responsible for specified MIME type.
     * @param mimeType MIME type to be resolved to its respective plugin
     * @return plugin class
     */
    public MediaPlugin resolve(String mimeType) {
        MediaPlugin plugin = processorRegistry.get(mimeType);
        if (plugin == null) {
            throw new UnsupportedFileTypeException("No plugin registered for specified MIME type", mimeType);
        }
        return plugin;
    }

    public Runnable resolveCli(String fileType) {
        return cliRegistry.get(fileType);
    }

    public boolean hasCliSupport(String mimeType) {
        return cliRegistry.containsKey(mimeType);
    }
}

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
    private final Map<Set<String>, MediaPlugin> processorRegistry = new HashMap<>();
    private final Map<String, MediaPlugin> processorRegistryExperimental = new HashMap<>();
    private final Map<Set<String>, CliConfig<?>> cliRegistry = new HashMap<>();
    public final Map<String, Runnable> cliRegistryExperimental = new HashMap<>();


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
            processorRegistryExperimental.put(mime, plugin);
        }
        cliRegistryExperimental.put(fileType, cliConfig);
        logger.info("CLI and Plugin were registered successfully");

    }

    /**
     * This method allows to resolve which plugin is responsible for specified MIME type.
     * @param mimeType MIME type to be resolved to its respective plugin
     * @return plugin class
     */
    public MediaPlugin resolve(String mimeType) {
        MediaPlugin plugin = processorRegistryExperimental.get(mimeType);
        if (plugin == null) {
            throw new UnsupportedFileTypeException("No plugin registered for specified MIME type", mimeType);
        }
        return plugin;
    }

    public Runnable resolveCli(String fileType) {
        return cliRegistryExperimental.get(fileType);
    }

    public Optional<CliConfig<?>> resolveCliConfig(String mimeType) {
        return Optional.ofNullable(cliRegistry.get(mimeType));
    }

    public boolean hasCliSupport(String mimeType) {
        return cliRegistry.containsKey(mimeType);
    }
}

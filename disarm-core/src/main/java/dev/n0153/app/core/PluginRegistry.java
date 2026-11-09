package dev.n0153.app.core;

import dev.n0153.app.core.exceptions.UnsupportedFileTypeException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

/**
 * Central registry mapping MIME types, file types, and plugin CLIs to their
 * registered {@link MediaPlugin} instances and {@link MediaConfig}s. Acts as
 * the glue between {@link MediaApp} and the plugins: plugins register
 * themselves here, and the core resolves a plugin/config/CLI by MIME type
 * or file type at processing time.
 * @since 0.2
 */
public class PluginRegistry {
    private static final Logger logger = LogManager.getLogger(PluginRegistry.class);
    private final Map<String, MediaPlugin> processorRegistry = new HashMap<>();
    private final Map<String, Runnable> cliRegistry = new HashMap<>();
    private final Map<String, MediaConfig> configRegistry = new HashMap<>();


    /**
     * This method is a registry point for all plugins and their respective MIME types.
     * @param mimeType plugin's supported MIME type
     * @param plugin plugin instance
     * @param cliConfig the plugin's CLI runner, registered alongside its MIME types and config
     * @param fileType the plugin's file type key (e.g. "image", "audio")
     * @param config the plugin's config instance
     * @since 0.2
     */
    public void register(
            Set<String> mimeType,
            MediaPlugin plugin,
            Runnable cliConfig,
            String fileType,
            MediaConfig config) {
        for (String mime : mimeType) {
            processorRegistry.put(mime, plugin);
        }
        configRegistry.put(fileType, config);
        cliRegistry.put(fileType, cliConfig);

        logger.info("CLI and Plugins were registered successfully");
    }

    /**
     * Registers a plugin and its config without a CLI runner.
     * @param mimeType plugin's supported MIME type
     * @param plugin plugin instance
     * @param fileType the plugin's file type key (e.g. "image", "audio")
     * @param config the plugin's config instance
     * @since 0.2
     */
    public void register(
            Set<String> mimeType,
            MediaPlugin plugin,
            String fileType,
            MediaConfig config) {
        for (String mime : mimeType) {
            processorRegistry.put(mime, plugin);
        }
        configRegistry.put(fileType, config);

        logger.info("Plugin was registered successfully");
    }

    /**
     * This method allows to resolve which plugin is responsible for specified MIME type.
     * @param mimeType MIME type to be resolved to its respective plugin
     * @return plugin class
     * @throws UnsupportedFileTypeException if no plugin is registered for the given MIME type
     * @since 0.2
     */
    public MediaPlugin resolve(String mimeType) {
        MediaPlugin plugin = processorRegistry.get(mimeType);
        if (plugin == null) {
            throw new UnsupportedFileTypeException("No plugin registered for specified MIME type", mimeType);
        }
        return plugin;
    }

    /**
     * @param fileType the file type key to look up
     * @return the registered CLI runner for the given file type, or null if none is registered
     * @since 0.2
     */
    public Runnable resolveCli(String fileType) {
        return cliRegistry.get(fileType);
    }

    /**
     * @return every registered plugin CLI runner
     * @since 0.2
     */
    public Collection<Runnable> getAllCliRunners() {
        return cliRegistry.values();
    }

    /**
     * @return every file type key with a registered CLI runner
     * @since 0.2
     */
    public Collection<String> getAllMimes() {
        return cliRegistry.keySet();
    }

    /**
     * @return the live file-type-to-CLI-runner registry map
     * @since 0.2
     */
    public Map<String, Runnable> getCliRegistry() {
        return cliRegistry;
    }

    /**
     * @param fileType the file type key to look up
     * @return the registered config for the given file type, or null if none is registered
     * @since 0.2
     */
    public MediaConfig resolveConfig(String fileType) {
        return configRegistry.get(fileType);
    }

    /**
     * Replaces the registered config for a file type, or registers it if not already present.
     * @param fileType the file type key
     * @param config the config to register or replace with
     * @since 0.2
     */
    public void updateConfig(String fileType, MediaConfig config) {
        if (configRegistry.containsKey(fileType)) {
            configRegistry.replace(fileType, config);
        } else {
            configRegistry.put(fileType, config);
        }
    }

    /**
     * Releases every registered plugin's config and processor context resources.
     * @since 0.2
     */
    public void wipePluginResources() {
        for (Map.Entry<String, MediaPlugin> plugin : processorRegistry.entrySet()) {
            MediaConfig config = plugin.getValue().getConfig();
            MediaContext context = plugin.getValue().getProcessor(config).getContext();
            config.release();
            context.release();
            logger.debug("Resources freed-up successfully: {}", config.getName());
        }
    }

    /**
     * Releases a single plugin's config and processor context resources.
     * @param plugin the plugin to release resources for
     * @since 0.2
     */
    public void wipePlugin(MediaPlugin plugin) {
        plugin.getConfig().release();
        plugin.getProcessor(plugin.getConfig()).getContext().release();
    }

    /**
     * Removes a single MIME type's entries from the processor, CLI, and config registries.
     * @param mime the MIME type key to remove
     * @since 0.2
     */
    public void removePluginEntry(String mime) {
        processorRegistry.remove(mime);
        cliRegistry.remove(mime);
        configRegistry.remove(mime);
        logger.debug("{} plugin was removed from registry", mime);
    }

    /**
     * Clears the processor, CLI, and config registries entirely.
     * @since 0.2
     */
    public void clearRegistry() {
        processorRegistry.clear();
        cliRegistry.clear();
        configRegistry.clear();
        logger.debug("Plugin registry was cleared");
    }

    /**
     * @return every registered plugin CLI runner
     * @since 0.2
     */
    public Collection<Runnable> listCLI() {
        return cliRegistry.values();
    }
}

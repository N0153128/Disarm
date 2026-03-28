package dev.n0153.app;

import dev.n0153.app.exceptions.UnsupportedFileTypeException;

import java.util.HashMap;
import java.util.Map;

/**
 * This class holds information about all plugins and acts as a glue between App and plugins.
 */
public class PluginRegistry {
    private final Map<String, MediaProcessor<?>> registry = new HashMap<>();

    /**
     * This method is a registry point for all plugins and their respective MIME types.
     * @param mimeType plugin's supported MIME type
     * @param plugin plugin instance
     * @param <Config> plugin's respective configuration.
     */
    public <Config extends ProcessorConfig> void register(
            String mimeType,
            MediaProcessor<Config> plugin) {
        registry.put(mimeType, plugin);
    }

    /**
     * This method allows to resolve which plugin is responsible for specified MIME type.
     * @param mimeType MIME type to be resolved to its respective plugin
     * @return plugin class
     */
    public MediaProcessor<?> resolve(String mimeType) {
        MediaProcessor<?> plugin = registry.get(mimeType);
        if (plugin == null) {
            throw new UnsupportedFileTypeException("No plugin registered for specified MIME type", mimeType);
        }
        return plugin;
    }
}

package dev.n0153.app.core;

/**
 * Contract every media-type plugin (audio/image/text/video) implements to
 * plug into the core pipeline: registering itself and its CLI against a
 * {@link PluginRegistry}, and exposing its config, context-backed
 * processor, and validator.
 * @since 0.2
 */
public interface MediaPlugin {

    /**
     * @return the shared global config this plugin was registered with
     * @since 0.2
     */
    GlobalConfig getGlobalConfig();

    /**
     * @param config the plugin's own config, used to resolve/configure the processor
     * @return the processor responsible for handling this plugin's media type
     * @since 0.2
     */
    public MediaProcessor<?> getProcessor(MediaConfig config);

    /**
     * @param config the plugin's own config, used to prepare the validator's metadata
     * @return the validator responsible for checking this plugin's media type
     * @since 0.2
     */
    public MediaValidator getValidator(MediaConfig config);

    /**
     * @return this plugin's own config instance
     * @since 0.2
     */
    public MediaConfig getConfig();

    /**
     * Registers this plugin's MIME types, CLI, and config against the given registry.
     * @param registry the registry to register against
     * @param config the plugin's config to store and register
     * @throws IllegalStateException if the plugin isn't ready to register (e.g. missing global config)
     * @since 0.2
     */
    void register(PluginRegistry registry, MediaConfig config) throws IllegalStateException;

    /**
     * Supplies the shared global config instance to this plugin.
     * @param globalConfig the global config to store
     * @since 0.2
     */
    void registerGlobalConfig(GlobalConfig globalConfig);

    /**
     * @return a short human-readable name identifying this plugin (e.g. "Audio"), used in log/error messages
     * @since 0.2
     */
    String echo();

    /**
     * Verifies that a global config has been supplied via {@link #registerGlobalConfig}.
     * @return true if a global config is present
     * @throws IllegalStateException if {@link #getGlobalConfig()} returns null
     * @since 0.2
     */
    default boolean ensureGlobalConfig() {
        if (getGlobalConfig() == null) {
            throw new IllegalStateException(echo() + " plugin requires Global Config instance to function");
        } else {
            return true;
        }
    }
}

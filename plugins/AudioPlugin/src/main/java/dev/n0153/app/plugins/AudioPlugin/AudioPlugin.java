package dev.n0153.app.plugins.AudioPlugin;

import dev.n0153.app.core.*;
import dev.n0153.app.core.exceptions.DisarmException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Entry point for the Audio plugin. It registers the plugin, its {@link AudioCLI}
 * and its {@link AudioConfig} with the core {@link PluginRegistry}, and supplies
 * the {@link AudioProcessor} and {@link AudioValidator} for each audio file.
 * <p>
 * Re-encoding uses JAVE/FFmpeg. For WAV, AIFF and AU there is a native
 * {@code javax.sound.sampled} fallback. One {@link AudioContext} is shared by the
 * processors and the validator that this plugin hands out.
 * @since 0.2
 */
public class AudioPlugin implements MediaPlugin {
    private AudioConfig config = new AudioConfig();
    private final AudioContext context = new AudioContext();
    private final AudioValidator validator = new AudioValidator();
    private GlobalConfig globalConfig;
    private static final Logger logger = LogManager.getLogger(AudioPlugin.class);

    /**
     * {@inheritDoc}
     * @return the global config supplied via {@link #registerGlobalConfig(GlobalConfig)},
     *         or {@code null} if none has been supplied yet
     * @since 0.2
     */
    @Override
    public GlobalConfig getGlobalConfig() {
        return globalConfig;
    }

    /**
     * {@inheritDoc}
     * Creates a new {@link AudioProcessor} on each call, bound to the given config,
     * this plugin's shared {@link AudioContext}, and the global config.
     * @param config the Audio plugin's config; must be an {@link AudioConfig}
     * @throws IllegalStateException if no global config has been supplied
     * @throws ClassCastException if {@code config} is not an {@link AudioConfig}
     * @since 0.2
     */
    @Override
    public MediaProcessor<?> getProcessor(MediaConfig config) {
        ensureGlobalConfig();
        return new AudioProcessor((AudioConfig) config, context, globalConfig);
    }

    /**
     * {@inheritDoc}
     * Binds the given config and this plugin's shared {@link AudioContext} to the plugin's
     * single {@link AudioValidator} instance before returning it.
     * @param config the Audio plugin's config; must be an {@link AudioConfig}
     * @throws ClassCastException if {@code config} is not an {@link AudioConfig}
     * @since 0.2
     */
    @Override
    public MediaValidator getValidator(MediaConfig config) {
        validator.createMeta((AudioConfig) config, context);
        return validator;
    }

    /**
     * {@inheritDoc}
     * @return this plugin's {@link AudioConfig}; the one passed to
     *         {@link #register(PluginRegistry, MediaConfig)} once registered, otherwise a default instance
     * @since 0.2
     */
    @Override
    public MediaConfig getConfig() {
        return config;
    }

    /**
     * {@inheritDoc}
     * <p>
     * First initialises JAVE's FFmpeg locator with this plugin's class loader set as the
     * thread context class loader, so the bundled FFmpeg binary is resolved from the plugin
     * jar. The previous context class loader is restored afterwards. Then it stores the given
     * config and registers this plugin, a new {@link AudioCLI} and the config under the
     * {@code "audio"} file type, for every MIME type returned by {@link MediaConfig#supports()}.
     * @param registry the registry to register against
     * @param config the Audio plugin's config; must be an {@link AudioConfig}
     * @throws IllegalStateException if no global config has been supplied
     * @throws ClassCastException if {@code config} is not an {@link AudioConfig}
     * @since 0.2
     */
    @Override
    public void register(PluginRegistry registry, MediaConfig config) throws DisarmException {
        ClassLoader previous = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(AudioPlugin.class.getClassLoader());
        try {
            new ws.schild.jave.process.ffmpeg.DefaultFFMPEGLocator();
        } finally {
            Thread.currentThread().setContextClassLoader(previous);
        }
        if (ensureGlobalConfig()) {
            this.config = (AudioConfig) config;
            registry.register(
                    config.supports(),
                    this,
                    new AudioCLI(registry),
                    "audio",
                    this.config
            );
        }
        logger.debug("AUDIO PLUGIN OBJECTS WERE CREATED AND REGISTERED");
    }

    /**
     * {@inheritDoc}
     * Must be called before {@link #register(PluginRegistry, MediaConfig)} and
     * {@link #getProcessor(MediaConfig)}.
     * @since 0.2
     */
    @Override
    public void registerGlobalConfig(GlobalConfig globalConfig) {
        this.globalConfig = globalConfig;
    }

    /**
     * {@inheritDoc}
     * @return {@code "Audio"}
     * @since 0.2
     */
    @Override
    public String echo() {
        return "Audio";
    }
}

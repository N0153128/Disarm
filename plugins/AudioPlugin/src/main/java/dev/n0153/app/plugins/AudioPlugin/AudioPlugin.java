package dev.n0153.app.plugins.AudioPlugin;

import dev.n0153.app.core.*;
import dev.n0153.app.core.exceptions.DisarmException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AudioPlugin implements MediaPlugin {
    private AudioConfig config = new AudioConfig();
    private final AudioContext context = new AudioContext();
    private final AudioValidator validator = new AudioValidator();
    private GlobalConfig globalConfig;
    private static final Logger logger = LogManager.getLogger(AudioPlugin.class);

    @Override
    public GlobalConfig getGlobalConfig() {
        return globalConfig;
    }

    @Override
    public MediaProcessor<?> getProcessor(MediaConfig config) {
        ensureGlobalConfig();
        return new AudioProcessor((AudioConfig) config, context, globalConfig);
    }

    @Override
    public MediaValidator getValidator(MediaConfig config) {
        validator.createMeta((AudioConfig) config, context);
        return validator;
    }

    @Override
    public MediaConfig getConfig() {
        return config;
    }

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

    @Override
    public void registerGlobalConfig(GlobalConfig globalConfig) {
        this.globalConfig = globalConfig;
    }

    @Override
    public String echo() {
        return "Audio";
    }
}

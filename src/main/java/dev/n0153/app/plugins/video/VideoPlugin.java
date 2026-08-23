package dev.n0153.app.plugins.video;

import dev.n0153.app.*;
import dev.n0153.app.exceptions.DisarmException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class VideoPlugin implements MediaPlugin {
    private MediaConfig config;
    private final VideoContext context = new VideoContext();
    private final VideoValidator validator = new VideoValidator();
    private GlobalConfig globalConfig;
    private static final Logger logger = LogManager.getLogger(VideoPlugin.class);

    @Override
    public GlobalConfig getGlobalConfig() {
        return globalConfig;
    }

    @Override
    public MediaProcessor<?> getProcessor(MediaConfig config) {
        ensureGlobalConfig();
        return new VideoProcessor((VideoConfig) config, context, globalConfig);
    }

    @Override
    public MediaValidator getValidator() {
        validator.createMeta((VideoConfig) config, context);
        return validator;
    }

    @Override
    public MediaConfig getConfig() {
        return config;
    }

    @Override
    public void register(PluginRegistry registry, MediaConfig config) throws DisarmException {
        if (ensureGlobalConfig()) {
            this.config = config;
            registry.register(
                    config.supports(),
                    this,
                    new VideoCLI(registry),
                    "video",
                    config
            );
        }
        logger.debug("VIDEO PLUGIN OBJECTS WERE CREATED AND REGISTERED");
    }

    @Override
    public void registerGlobalConfig(GlobalConfig globalConfig) {
        this.globalConfig = globalConfig;
    }

    @Override
    public String echo() {
        return "video";
    }
}

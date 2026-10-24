package dev.n0153.app.plugins.ImagePlugin;

import dev.n0153.app.core.*;
import dev.n0153.app.core.exceptions.DisarmException;
import dev.n0153.app.core.exceptions.ValidationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ImagePlugin implements MediaPlugin {
    private ImageConfig config = new ImageConfig();
    private final ImageContext context = new ImageContext();;
    private final ImageValidator validator = new ImageValidator();
    private GlobalConfig globalConfig;
    private static final Logger logger = LogManager.getLogger(ImagePlugin.class);

    @Override
    public void registerGlobalConfig(GlobalConfig globalConfig) {
        this.globalConfig = globalConfig;
    }

    @Override
    public GlobalConfig getGlobalConfig() {
        return globalConfig;
    }

    @Override
    public MediaValidator getValidator(MediaConfig config) {
        validator.createMeta((ImageConfig) config, context);
        return validator;
    }

    @Override
    public MediaProcessor<?> getProcessor(MediaConfig config) {
        ensureGlobalConfig();
        return new ImageProcessor((ImageConfig) config, context, globalConfig);
    }

    @Override
    public void register(PluginRegistry registry, MediaConfig config) throws DisarmException {
        if (ensureGlobalConfig()) {
            this.config = (ImageConfig) config;
            registry.register(
                    config.supports(),
                    this,
                    new ImageCLI(registry, context),
                    "image",
                    this.config);
        } else {
            throw new ValidationException("Global config must be provided");
        }
        logger.debug("IMAGE PLUGIN OBJECTS WERE CREATED AND REGISTERED");
    }

    @Override
    public MediaConfig getConfig() {
        return config;
    }

    @Override
    public String echo() {
        return "Image";
    }
}

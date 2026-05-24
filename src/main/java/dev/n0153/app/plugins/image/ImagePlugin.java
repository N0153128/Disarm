package dev.n0153.app.plugins.image;

import dev.n0153.app.*;
import dev.n0153.app.exceptions.DisarmException;
import dev.n0153.app.exceptions.ValidationException;

public class ImagePlugin implements MediaPlugin {
    private MediaConfig config;
    private final ImageContext context = new ImageContext();;
    private final ImageValidator validator = new ImageValidator();
    private GlobalConfig globalConfig;

    @Override
    public void registerGlobalConfig(GlobalConfig globalConfig) {
        this.globalConfig = globalConfig;
    }

    @Override
    public GlobalConfig getGlobalConfig() {
        return globalConfig;
    }

    @Override
    public MediaValidator getValidator() {
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
            this.config = config;
            registry.register(
                    config.supports(),
                    this,
                    new ImageCLI(registry, context),
                    "image",
                    config);
        } else {
            throw new ValidationException("Global config must be provided");
        }
    }

    @Override
    public MediaConfig getConfig() {
        return config;
    }

    @Override
    public String echo() {
        return config.getName();
    }
}

package dev.n0153.app.plugins.image;

import dev.n0153.app.*;
import dev.n0153.app.exceptions.DisarmException;

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
    public boolean ensureGlobalConfig() {
        if (globalConfig == null) {
            throw new DisarmException(echo() + " plugin requires Global Config instance to function");
        } else {
            return true;
        }
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
        this.config = config;
        registry.register(
                config.supports(),
                this,
                new ImageCLI(registry),
                "image",
                config);
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

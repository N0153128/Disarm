package dev.n0153.app.plugins.image;

import dev.n0153.app.*;
import dev.n0153.app.exceptions.DisarmException;

public class ImagePlugin implements MediaPlugin {
    private final ImageConfig config = new ImageConfig();
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
        validator.createMeta(config, context);
        return validator;
    }

    @Override
    public MediaProcessor<?> getProcessor() {
        ensureGlobalConfig();
        return new ImageProcessor(config, context, globalConfig);
    }

    @Override
    public Runnable getCLI() {
        return new ImageCLI();
    }

    @Override
    public void register(PluginRegistry registry) throws DisarmException {
        registry.register(
                config.supports(),
                new ImagePlugin(),
                getCLI(),
                "image");
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

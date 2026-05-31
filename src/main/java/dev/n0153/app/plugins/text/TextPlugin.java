package dev.n0153.app.plugins.text;

import dev.n0153.app.*;
import dev.n0153.app.exceptions.DisarmException;

public class TextPlugin implements MediaPlugin {
    private MediaConfig config;
    private final TextContext context = new TextContext();
    private final TextValidator validator = new TextValidator();
    private GlobalConfig globalConfig;

    @Override
    public GlobalConfig getGlobalConfig() {
        return globalConfig;
    }

    @Override
    public MediaProcessor<?> getProcessor(MediaConfig config) {
        ensureGlobalConfig();
        return new TextProcessor((TextConfig) config, context, globalConfig);
    }

    @Override
    public MediaValidator getValidator() {
        validator.createMeta((TextConfig) config, context);
        return validator;
    }

    @Override
    public MediaConfig getConfig() {
        return config;
    }

    @Override
    public void register(PluginRegistry registry, MediaConfig config) throws DisarmException {

    }

    @Override
    public void registerGlobalConfig(GlobalConfig globalConfig) {
        this.globalConfig = globalConfig;
    }

    @Override
    public String echo() {
        return config.getName();
    }
}

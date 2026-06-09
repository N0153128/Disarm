package dev.n0153.app.plugins.audio;

import dev.n0153.app.*;
import dev.n0153.app.exceptions.DisarmException;

public class AudioPlugin implements MediaPlugin {
    private MediaConfig config;
    private final AudioContext context = new AudioContext();
    private final AudioValidator validator = new AudioValidator();
    private GlobalConfig globalConfig;

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
    public MediaValidator getValidator() {
        validator.createMeta((AudioConfig) config, context);
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
                    new AudioCLI(registry),
                    "audio",
                    config
            );
        }
    }

    @Override
    public void registerGlobalConfig(GlobalConfig globalConfig) {
        this.globalConfig = globalConfig;
    }

    @Override
    public String echo() {
        return "audio";
    }
}

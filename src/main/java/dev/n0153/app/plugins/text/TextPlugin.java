package dev.n0153.app.plugins.text;

import dev.n0153.app.*;
import dev.n0153.app.exceptions.DisarmException;
import dev.n0153.app.plugins.image.ImagePlugin;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TextPlugin implements MediaPlugin {
    private MediaConfig config;
    private final TextContext context = new TextContext();
    private final TextValidator validator = new TextValidator();
    private GlobalConfig globalConfig;
    private static final Logger logger = LogManager.getLogger(TextPlugin.class);

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
        if (ensureGlobalConfig()) {
            this.config = config;
            registry.register(
                    config.supports(),
                    this,
                    new TextCLI(registry),
                    "text",
                    config
            );
        }
        logger.debug("TEXT PLUGIN OBJECTS WERE CREATED AND REGISTERED");
    }

    @Override
    public void registerGlobalConfig(GlobalConfig globalConfig) {
        this.globalConfig = globalConfig;
    }

    @Override
    public String echo() {
        return "text";
    }
}

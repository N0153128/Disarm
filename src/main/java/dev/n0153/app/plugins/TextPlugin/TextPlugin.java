package dev.n0153.app.plugins.TextPlugin;

import dev.n0153.app.core.*;
import dev.n0153.app.core.exceptions.DisarmException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TextPlugin implements MediaPlugin {
    private TextConfig config = new TextConfig();
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
    public MediaValidator getValidator(MediaConfig config) {
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
            this.config = (TextConfig) config;
            registry.register(
                    config.supports(),
                    this,
                    new TextCLI(registry),
                    "text",
                    this.config
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
        return "Text";
    }
}

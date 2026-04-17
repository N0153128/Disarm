package dev.n0153.app;

import dev.n0153.app.exceptions.DisarmException;
import dev.n0153.app.plugins.image.ImageCli;
import dev.n0153.app.plugins.image.ImageValidator;

public interface MediaPlugin {
    public MediaProcessor<?> getProcessor();
    public CliConfig<? extends MediaConfig, ?> getCLI();
    public MediaValidator getValidator();
    void register(PluginRegistry registry) throws DisarmException;
}

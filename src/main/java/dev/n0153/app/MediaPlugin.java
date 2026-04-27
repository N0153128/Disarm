package dev.n0153.app;

import dev.n0153.app.exceptions.DisarmException;
import dev.n0153.app.plugins.image.ImageCli;
import dev.n0153.app.plugins.image.ImageValidator;

import java.util.Objects;

public interface MediaPlugin {
    public MediaProcessor<?> getProcessor();
    public Runnable getCLI();
    public MediaValidator getValidator();
    public MediaConfig getConfig();
    void register(PluginRegistry registry) throws DisarmException;
    String echo();
}

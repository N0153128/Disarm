package dev.n0153.app;

import dev.n0153.app.exceptions.DisarmException;

public interface MediaPlugin {
    public MediaProcessor<?> getProcessor();
    public Runnable getCLI();
    public MediaValidator getValidator();
    public MediaConfig getConfig();
    void register(PluginRegistry registry) throws DisarmException;
    String echo();
}

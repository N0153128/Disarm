package dev.n0153.app;

import dev.n0153.app.exceptions.DisarmException;

public interface MediaPlugin {
    public MediaProcessor<?> getProcessor(MediaConfig config);
    public MediaValidator getValidator();
    public MediaConfig getConfig();
    void register(PluginRegistry registry, MediaConfig config) throws DisarmException;
    String echo();
    void registerGlobalConfig(GlobalConfig globalConfig);
    boolean ensureGlobalConfig();
}

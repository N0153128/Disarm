package dev.n0153.app;

public interface MediaPlugin {
    GlobalConfig getGlobalConfig();
    public MediaProcessor<?> getProcessor(MediaConfig config);
    public MediaValidator getValidator();
    public MediaConfig getConfig();
    void register(PluginRegistry registry, MediaConfig config) throws IllegalStateException;
    void registerGlobalConfig(GlobalConfig globalConfig);
    String echo();

    default boolean ensureGlobalConfig() {
        if (getGlobalConfig() == null) {
            throw new IllegalStateException(echo() + " plugin requires Global Config instance to function");
        } else {
            return true;
        }
    }
}

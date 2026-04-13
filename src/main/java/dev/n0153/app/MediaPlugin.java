package dev.n0153.app;

public interface MediaPlugin {
    public MediaProcessor<?> getProcessor();
    public MediaConfig getConfig();
}

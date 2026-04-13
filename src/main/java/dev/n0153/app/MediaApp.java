package dev.n0153.app;

public class MediaApp {
    private final PluginRegistry registry;
    public MediaApp (PluginRegistry registry) {
        this.registry = registry;
    }

    public MediaProcessor<? extends MediaConfig> getPlugin() {
        return registry.resolve("png");
    }
}

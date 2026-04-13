package dev.n0153.app;

public class MediaApp {
    private final PluginRegistry registry;
    public MediaApp (PluginRegistry registry) {
        this.registry = registry;
    }

    public MediaPlugin getPlugin() {
        return registry.resolve("png");
    }
}

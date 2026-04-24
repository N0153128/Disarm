package dev.n0153.app;

import dev.n0153.app.plugins.DisarmPlugins;

import java.nio.file.Path;

public class MediaApp {
    private final PluginRegistry registry;

    public MediaApp (PluginRegistry registry) {
        this.registry = registry;
    }

    public void registerPlugins() {
        DisarmPlugins disarmPlugins = new DisarmPlugins();
        disarmPlugins.registerAll(registry);
    }

    public MediaPlugin getPlugin(String format) {
        return registry.resolve(format);
    }

    public MediaValidator getValidator(MediaPlugin plugin) {
        return plugin.getValidator();
    }

    public MediaConfig getPlugin(MediaPlugin plugin) {
        return plugin.getConfig();
    }

    private void processFile(Path osTargetPath) {
        
    }

    public void fileDisarm(Path osTargetPath) {

    }

    public void fileDisarm(Path osTargetPath, Path osLogoPath) {

    }
}

package dev.n0153.app;

import dev.n0153.app.plugins.DisarmPlugins;

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

    public void initiateMeta() {

    }
}

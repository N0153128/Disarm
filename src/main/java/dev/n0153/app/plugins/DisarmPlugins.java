package dev.n0153.app.plugins;

import dev.n0153.app.PluginRegistry;
import dev.n0153.app.plugins.image.ImagePlugin;

public class DisarmPlugins {
    public void registerAll(PluginRegistry registry) {
        new ImagePlugin().register(registry);
    }
}

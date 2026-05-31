package dev.n0153.app.plugins;

import dev.n0153.app.GlobalConfig;
import dev.n0153.app.MediaConfig;
import dev.n0153.app.MediaPlugin;
import dev.n0153.app.PluginRegistry;
import dev.n0153.app.plugins.image.ImageConfig;
import dev.n0153.app.plugins.image.ImagePlugin;
import dev.n0153.app.plugins.text.TextConfig;
import dev.n0153.app.plugins.text.TextPlugin;

public class DisarmPlugins {
    public void registerAll(PluginRegistry registry) {
        ImageConfig imageConfig = new ImageConfig();
        new ImagePlugin().register(registry, imageConfig);
        TextConfig textConfig = new TextConfig();
        new TextPlugin().register(registry, textConfig);
        //add new plugins as they become available
    }
}

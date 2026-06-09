package dev.n0153.app.plugins;

import dev.n0153.app.GlobalConfig;
import dev.n0153.app.MediaConfig;
import dev.n0153.app.MediaPlugin;
import dev.n0153.app.PluginRegistry;
import dev.n0153.app.plugins.audio.AudioConfig;
import dev.n0153.app.plugins.audio.AudioPlugin;
import dev.n0153.app.plugins.image.ImageConfig;
import dev.n0153.app.plugins.image.ImagePlugin;
import dev.n0153.app.plugins.text.TextConfig;
import dev.n0153.app.plugins.text.TextPlugin;

public class DisarmPlugins {
    private final GlobalConfig globalConfig;
    public DisarmPlugins(GlobalConfig globalConfig) {
        this.globalConfig = globalConfig;
    }

    public void registerAll(PluginRegistry registry) {
        ImageConfig imageConfig = new ImageConfig();
        ImagePlugin image = new ImagePlugin();
        image.registerGlobalConfig(globalConfig);
        image.register(registry, imageConfig);

        TextConfig textConfig = new TextConfig();
        TextPlugin text = new TextPlugin();
        text.registerGlobalConfig(globalConfig);
        text.register(registry, textConfig);

        AudioConfig audioConfig = new AudioConfig();
        AudioPlugin audioPlugin = new AudioPlugin();
        audioPlugin.registerGlobalConfig(globalConfig);
        audioPlugin.register(registry, audioConfig);
        //add new plugins as they become available
    }
}

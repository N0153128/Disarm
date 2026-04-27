package dev.n0153.app.plugins.image;

import dev.n0153.app.*;
import dev.n0153.app.exceptions.DisarmException;

public class ImagePlugin implements MediaPlugin {

    @Override
    public MediaValidator getValidator() {
        return new ImageValidator();
    }

    @Override
    public MediaProcessor<?> getProcessor() {
        return new ImageProcessor();
    }

    @Override
    public Runnable getCLI() {
        return new ImageCLI();
    }

    @Override
    public void register(PluginRegistry registry) throws DisarmException {
        registry.registerExperimental(
                new ImageConfig().supports(),
                new ImagePlugin(),
                new ImageCLI(),
                "image");
    }

    @Override
    public MediaConfig getConfig() {
        return new ImageConfig();
    }

    @Override
    public String echo() {
        return new ImageConfig().getName();
    }

    public void registerWithCli(PluginRegistry registry) throws DisarmException {
        registry.registerExperimental(
                new ImageConfig().supports(),
                new ImagePlugin(),
                new ImageCLI(),
                "image");
    }
}

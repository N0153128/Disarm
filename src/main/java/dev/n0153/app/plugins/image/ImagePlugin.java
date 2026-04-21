package dev.n0153.app.plugins.image;

import dev.n0153.app.*;
import dev.n0153.app.exceptions.DisarmException;
import org.opencv.core.*;

import java.util.*;

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
    public ImageCli getCLI() {
        return new ImageCli();
    }

    @Override
    public void register(PluginRegistry registry) throws DisarmException {
        registry.register(
                new ImageConfig().supports(),
                new ImagePlugin());
    }

    @Override
    public MediaConfig getConfig() {
        return new ImageConfig();
    }

    public void registerWithCli(PluginRegistry registry) throws DisarmException {
        registry.register(
                new ImageConfig().supports(),
                new ImagePlugin(),
                new ImageCli());
    }

}

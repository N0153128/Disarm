package dev.n0153.app.plugins.audio;

import dev.n0153.app.DisarmCLI;
import dev.n0153.app.PluginRegistry;
import dev.n0153.app.Utils;
import dev.n0153.app.plugins.image.ImageConfig;
import dev.n0153.app.plugins.image.ImageContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opencv.imgcodecs.Imgcodecs;
import picocli.CommandLine;

import java.nio.file.Path;

public class AudioCLI implements Runnable {
@CommandLine.Command(name = "image", description = "Image Processing Plugin")
    private static final Logger logger = LogManager.getLogger(AudioCLI.class);

    private final ImageConfig config = new ImageConfig();
    private final PluginRegistry registry;
    private final ImageContext context;

    public AudioCLI (PluginRegistry registry, ImageContext context) {
        this.registry = registry;
        this.context = context;
        registry.updateConfig("audio", config);
    }

    @CommandLine.ParentCommand
    DisarmCLI inputPath;

    @CommandLine.Option(names = {"-mad", "--max-audio-duration"}, description = "Apply watermark")
    private Path logo;

    @Override
    public void run() {

        registry.updateConfig("image", config);
    }
}
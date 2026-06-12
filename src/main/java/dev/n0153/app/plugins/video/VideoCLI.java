package dev.n0153.app.plugins.video;

import dev.n0153.app.DisarmCLI;
import dev.n0153.app.PluginRegistry;
import dev.n0153.app.plugins.audio.AudioCLI;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import picocli.CommandLine;

@CommandLine.Command(name = "video", description = "Video Processing Plugin")
public class VideoCLI implements Runnable {
    private static final Logger logger = LogManager.getLogger(VideoCLI.class);

    private final VideoConfig config = new VideoConfig();
    private final PluginRegistry registry;

    public VideoCLI(PluginRegistry registry) {
        this.registry = registry;
        registry.updateConfig("video", config);
    }

    @CommandLine.ParentCommand
    DisarmCLI inputPath;


    @Override
    public void run() {

    }
}

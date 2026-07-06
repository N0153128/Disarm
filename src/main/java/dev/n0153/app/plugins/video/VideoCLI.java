package dev.n0153.app.plugins.video;

import dev.n0153.app.DisarmCLI;
import dev.n0153.app.PluginRegistry;
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

    @CommandLine.Option(names = {"-mvd", "--max-video-duration"}, description = "Change max video duration")
    private int maxVideoDuration;

    @CommandLine.Option(names = {"-tvb", "--target-video-bitrate"}, description = "Change target video bitrate")
    private int targetVideoBitrate;

    @CommandLine.Option(names = {"-tfr", "--target-frame-rate"}, description = "Change target frame rate")
    private int targetFrameRate;

    @CommandLine.Option(names = {"-tvc", "--target-video-codec"}, description = "Change target video codec")
    private String targetVideoCodec;

    @CommandLine.Option(names = {"-tacv", "--target-audio-codec-video"}, description = "Change target audio codec for video")
    private String targetAudioCodec;

    @CommandLine.Option(names = {"-sav1", "--swap-av1"}, description = "Swap AV1 codec with a more performant. Accepted values: vp8 (default), vp9, av1")
    private String swapAV1;

    @Override
    public void run() {
        if (maxVideoDuration > 0) {
            this.config.setMaxVideoDuration(maxVideoDuration);
        }
        if (targetVideoBitrate > 0) {
            this.config.setOutputVideoBitrate(targetVideoBitrate);
        }
        if (targetFrameRate > 0) {
            this.config.setOutputFrameRate(targetFrameRate);
        }
        if (targetVideoCodec != null && !targetVideoCodec.isEmpty()) {
            this.config.setOutputVideoCodec(targetVideoCodec);
        }
        if (targetAudioCodec != null && !targetAudioCodec.isEmpty()) {
            this.config.setOutputAudioCodec(targetAudioCodec);
        }
        if (swapAV1 != null) {
            this.config.setSwapAV1(swapAV1);
        }

        registry.updateConfig("video", config);
    }
}

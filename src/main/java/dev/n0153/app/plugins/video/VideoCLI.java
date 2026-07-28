package dev.n0153.app.plugins.video;

import dev.n0153.app.DisarmCLI;
import dev.n0153.app.PluginRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import picocli.CommandLine;

@CommandLine.Command(name = "video", description = "Video Processing Plugin.", mixinStandardHelpOptions = true)
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

    @CommandLine.Option(names = {"-mvd", "--max-video-duration"}, description =
            "Changes maximum video duration limit in milliseconds. " +
                    "Default limit is set to 5 minutes (300,000). Example: -mvd 600000")
    private int maxVideoDuration;

    @CommandLine.Option(names = {"-tvb", "--target-video-bitrate"}, description =
            "Changes output video bitrate, accepts an integer. " +
                    "Defaults to 8,000,000. Example: -tvb 10000000")
    private int targetVideoBitrate;

    @CommandLine.Option(names = {"-tfr", "--target-frame-rate"}, description =
            "Changes output frame rate, accepts an integer. " +
                    "Defaults to 30. Example: -tfr 60")
    private int targetFrameRate;

    @CommandLine.Option(names = {"-tvc", "--target-video-codec"}, description =
            "Changes output video codec, accepts a string. " +
                    "Defaults to an input video file's codec. Example: -tvc h.264")
    private String targetVideoCodec;

    @CommandLine.Option(names = {"-tacv", "--target-audio-codec-video"}, description =
            "Changes output audio codec for video, accepts a string. " +
                    "Defaults to an input video file's audio codec. Example: -tacv ac3")
    private String targetAudioCodec;

    @CommandLine.Option(names = {"-sav1", "--swap-av1"}, description =
            "Swaps AV1 codec with a more performant VP8. Accepted values: vp8, vp9, av1. " +
                    "Defaults to vp8. Example: -sav1 vp9")
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

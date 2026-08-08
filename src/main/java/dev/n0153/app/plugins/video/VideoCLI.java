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

    @CommandLine.Option(names = {"-dsv", "--dont-save-video"}, description =
            "Executes an entire video disarming cycle for benchmarking purposes. " +
                    "Resulting file will be deleted immediately after processing. " +
                    "Similar to -dr, but video-specific. Example: -dsv")
    private boolean dontSaveVideo;

    @CommandLine.Option(names = {"-vof", "--video-output-format"}, description =
            "Changes output file's format, accepts one of supported formats: mp4, mov, mkv (matroska), webm" +
                    "WARNING: certain container combinations will fail to save. " +
                    "Example: -vof wav")
    private String videoOutputFormat;

    @CommandLine.Option(names = {"-mvsff", "--max-video-size-for-format"}, description =
            "Changes video file size ceiling for specified supported video format. " +
                    "Accepts the following format: format:size, " +
                    "where size is the amount of bytes expressed as integer." +
                    " Example: -mvsff mp4:5000000")
    private String maxVideoSizeForFormat;

    @CommandLine.Option(names = {"-mvbr", "--max-video-bitrate"}, description =
            "Changes video bitrate ceiling for specified supported video format. " +
                    "Accepts the following format: format:bitrate, " +
                    "where bitrate is expressed as integer. " +
                    "Example: -mvbr mp4:10000000")
    private String maxVideoBitrate;

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
        if (dontSaveVideo) {
            this.config.setDontSaveVideo(true);
        }
        if (videoOutputFormat != null) {
            this.config.setDefaultOutputTo(videoOutputFormat);
        }
        if (maxVideoSizeForFormat != null) {
            String format = maxVideoSizeForFormat.toLowerCase().split(":")[0];
            int size = Integer.parseInt(maxVideoSizeForFormat.toLowerCase().split(":")[1]);
            this.config.setMaxFileSize(format, size);
        }
        if (maxVideoBitrate != null) {
            String format = maxVideoBitrate.toLowerCase().split(":")[0];
            int bitrate = Integer.parseInt(maxVideoBitrate.toLowerCase().split(":")[1]);
            this.config.setMaxVideoBitrate(format, bitrate);
        }
        registry.updateConfig("video", config);
    }
}

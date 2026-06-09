package dev.n0153.app.plugins.audio;

import dev.n0153.app.DisarmCLI;
import dev.n0153.app.PluginRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import picocli.CommandLine;

public class AudioCLI implements Runnable {
@CommandLine.Command(name = "audio", description = "Audio Processing Plugin")
    private static final Logger logger = LogManager.getLogger(AudioCLI.class);

    private final AudioConfig config = new AudioConfig();
    private final PluginRegistry registry;

    public AudioCLI (PluginRegistry registry) {
        this.registry = registry;
        registry.updateConfig("audio", config);
    }

    @CommandLine.ParentCommand
    DisarmCLI inputPath;

    @CommandLine.Option(names = {"-mad", "--max-audio-duration"}, description = "Change audio duration limit")
    private int maxAudioDuration;

    @CommandLine.Option(names = {"-tb", "--target-bitrate"}, description = "Change output bitrate")
    private int outputBitrate;

    @CommandLine.Option(names = {"-tsr", "--target-sample-rate"}, description = "Change output sample rate")
    private int outputSampleRate;

    @CommandLine.Option(names = {"-tc", "--target-channels"}, description = "Change output channels. Defaults to 2 (Stereo)")
    private int outputChannels;

    @Override
    public void run() {
        if (maxAudioDuration > 0) {
            this.config.setMaxAudioDuration(maxAudioDuration);
        }
        if (outputBitrate > 0) {
            this.config.setOutputBitrate(outputBitrate);
        }
        if (outputSampleRate > 0) {
            this.config.setOutputSampleRate(outputSampleRate);
        }
        if (outputChannels > 0) {
            this.config.setOutputChannels(outputChannels);
        }

        registry.updateConfig("image", config);
    }
}
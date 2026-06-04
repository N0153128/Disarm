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
    private final AudioContext context;

    public AudioCLI (PluginRegistry registry, AudioContext context) {
        this.registry = registry;
        this.context = context;
        registry.updateConfig("audio", config);
    }

    @CommandLine.ParentCommand
    DisarmCLI inputPath;

    @CommandLine.Option(names = {"-mad", "--max-audio-duration"}, description = "Change audio duration limit")
    private int maxAudioDuration;

    @CommandLine.Option(names = {"-tb", "--target-bitrate"}, description = "Change target bitrate")
    private int targetBitrate;

    @CommandLine.Option(names = {"-tsr", "--target-sample-rate"}, description = "Change target sample rate")
    private int targetSampleRate;

    @CommandLine.Option(names = {"-tc", "--target-channels"}, description = "Change target channels. Defaults to 2 (Stereo)")
    private int targetChannels;

    @Override
    public void run() {
        if (maxAudioDuration > 0) {
            this.config.setMaxAudioDuration(maxAudioDuration);
        }
        if (targetBitrate > 0) {
            this.config.setOutputBitrate(targetBitrate);
        }
        if (targetSampleRate > 0) {
            this.config.setOutputSampleRate(targetSampleRate);
        }
        if (targetChannels > 0) {
            this.config.setOutputChannels(targetChannels);
        }

        registry.updateConfig("image", config);
    }
}
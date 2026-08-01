package dev.n0153.app.plugins.audio;

import dev.n0153.app.DisarmCLI;
import dev.n0153.app.PluginRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import picocli.CommandLine;

@CommandLine.Command(name = "audio", description = "Audio Processing Plugin.", mixinStandardHelpOptions = true)
public class AudioCLI implements Runnable {
    private static final Logger logger = LogManager.getLogger(AudioCLI.class);

    private final AudioConfig config = new AudioConfig();
    private final PluginRegistry registry;

    public AudioCLI (PluginRegistry registry) {
        this.registry = registry;
        registry.updateConfig("audio", config);
    }

    @CommandLine.ParentCommand
    DisarmCLI inputPath;

    @CommandLine.Option(names = {"-mad", "--max-audio-duration"}, description =
            "Changes maximum audio duration limit in milliseconds. " +
                    "Default limit is set to 5 minutes (300,000). Example: -mad 600000")
    private int maxAudioDuration;

    @CommandLine.Option(names = {"-tb", "--target-bitrate"}, description =
            "Changes output bitrate, accepts an integer. " +
                    "Default output bitrate is set to 128000. Example: -tb 64000")
    private int outputBitrate;

    @CommandLine.Option(names = {"-tsr", "--target-sample-rate"}, description =
            "Changes output sample rate, accepts an integer. " +
                    "Default output sample rate is set to 44100. Example: -tsr 42100")
    private int outputSampleRate;

    @CommandLine.Option(names = {"-tc", "--target-channels"}, description =
            "Changes the amount of output channels. Accepts an integer, defaults to 2 (Stereo). " +
                    "Example: -tc 1")
    private int outputChannels;

    @CommandLine.Option(names = {"-dsa", "--dont-save-audio"}, description =
            "Executes an entire audio disarming cycle for benchmarking purposes. " +
                    "Resulting file will be deleted immediately after processing. " +
                    "Similar to -dr, but audio-specific. Example: -dsa")
    private boolean dontSaveAudio;

    @CommandLine.Option(names = {"-aof", "--audio-output-format"}, description =
            "Changes output file's format, accepts one of supported formats: mp3, ogg, au, " +
                    "flac, wav, aif (aiff, aifc). " +
                    "WARNING: certain container combinations will fail to save. " +
                    "Example: -aof wav")
    private String audioOutputFormat;

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
        if (dontSaveAudio) {
            this.config.setDontSaveAudio(true);
        }
        if (audioOutputFormat != null) {
            this.config.setDefaultOutputTo(audioOutputFormat);
        }
        registry.updateConfig("audio", config);
    }
}
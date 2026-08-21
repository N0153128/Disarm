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

    @CommandLine.Option(names = {"-mafs", "--max-audio-file-size"}, description =
            "Changes maximum allowed file size for an audio file, " +
                    "accepts bytes as integer, defaults to 5000000 (5MB). " +
                    "!WARNING: this value is sensitive and is used for validation." +
                    " Example: -mafs 10000000")
    private int maxAudioFileSize;

    @CommandLine.Option(names = {"-smbf", "--set-max-bitrate-for"}, description =
            "Changes bitrate ceiling for specified format. " +
                    "Accepts pair of format:bitrate, where format is a string containing one of the supported formats " +
                    "and bitrate represented as integer." +
                    "!WARNING: this value is sensitive and is used for validation. " +
                    "Example: -smbf mp3:128000")
    private String maxBitrateFor;

    @CommandLine.Option(names = {"-smsrf", "--set-max-sampling-rate-for"}, description =
            "Changes sampling rate ceiling for specified format. " +
                    "Accepts pair of format:sampling rate, where format is a string containing one of the supported formats " +
                    "and bitrate represented as integer." +
                    "!WARNING: this value is sensitive and is used for validation. " +
                    "Example: -smsrf mp3:44000")
    private String maxSamplingRateFor;

    @CommandLine.Option(names = {"-mac", "--max-audio-channels"}, description =
            "Changes ceiling for audio channels, accepts amount of channels as integers, defaults to 2." +
                    " !WARNING: this value is sensitive and is used for validation." +
                    " Example: -mac 1")
    private int maxAudioChannels;

    @CommandLine.Option(names = {"-abf", "--audio-bitrate-fallback"}, description =
            "Changes bitrate fallback value for audio tracks, accepts new bitrate as integer," +
                    " defaults to 320000 and is applied to an output file if an input file had bad/unknown bitrate" +
                    "for passthrough." +
                    " !WARNING: this value is sensitive and is used for validation." +
                    " Example: -abf 128000")
    private int audioBitrateFallback;

    @CommandLine.Option(names = {"-asrf", "--audio-sampling-rate-fallback"}, description =
            "Changes sampling rate fallback value for audio tracks, accepts new sampling rate as integer," +
                    " defaults to 48000 and is applied to an output file if an input file had bad/unknown sampling rate" +
                    " !WARNING: this value is sensitive and is used for validation." +
                    " Example: -asrf 44_000")
    private int audioSamplingRateFallback;

    @CommandLine.Option(names = {"-dnpf", "--disable-native-processing-fallback"}, description =
            "Disables native processing fallback if FFMpeg processing fails. Defaults to false." +
                    "Example: -dnpf")
    private boolean disableNativeProcessingFallback;

    @CommandLine.Option(names = {"-mp3fs", "--mp3-file-size"}, description =
            "Changes file size ceiling specifically for mp3 audio. " +
                    "Accepts bytes as integers. " +
                    "Example for 3MB ceiling: -mp3fs 3000000")
    private int mp3FileSize;

    @CommandLine.Option(names = {"-oggfs", "--ogg-file-size"}, description =
            "Changes file size ceiling specifically for ogg audio. " +
                    "Accepts bytes as integers. " +
                    "Example for 3MB ceiling: -oggfs 3000000")
    private int oggFileSize;

    @CommandLine.Option(names = {"-flacfs", "--flac-file-size"}, description =
            "Changes file size ceiling specifically for flac audio. " +
                    "Accepts bytes as integers. " +
                    "Example for 3MB ceiling: -flacfs 3000000")
    private int flacFileSize;

    @CommandLine.Option(names = {"-wavfs", "--wav-file-size"}, description =
            "Changes file size ceiling specifically for wav audio. " +
                    "Accepts bytes as integers. " +
                    "Example for 3MB ceiling: -wavfs 3000000")
    private int wavFileSize;

    @CommandLine.Option(names = {"-aufs", "--au-file-size"}, description =
            "Changes file size ceiling specifically for au audio. " +
                    "Accepts bytes as integers. " +
                    "Example for 3MB ceiling: -aufs 3000000")
    private int auFileSize;

    @CommandLine.Option(names = {"-aiffs", "--aif-file-size"}, description =
            "Changes file size ceiling specifically for aif/aiff/aifc audio. " +
                    "Accepts bytes as integers. " +
                    "Example for 3MB ceiling: -aiffs 3000000")
    private int aifFileSize;

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
        if (maxAudioFileSize > 0) {
            this.config.setMaxFileSize(maxAudioFileSize);
        }
        if (maxBitrateFor != null) {
            String format = maxBitrateFor.split(":")[0];
            int bitrate = Integer.parseInt(maxBitrateFor.split(":")[1]);
            this.config.setMaxBitrates(format, bitrate);
        }
        if (maxSamplingRateFor != null) {
            String format = maxSamplingRateFor.split(":")[0];
            int samplingRate = Integer.parseInt(maxSamplingRateFor.split(":")[1]);
            this.config.setMaxSampleRates(format, samplingRate);
        }
        if (maxAudioChannels > 0) {
            this.config.setMaxChannels(maxAudioChannels);
        }
        if (audioBitrateFallback > 0) {
            this.config.setBitrateFallback(audioBitrateFallback);
        }
        if (audioSamplingRateFallback > 0) {
            this.config.setSamplingRateFallback(audioSamplingRateFallback);
        }
        if (disableNativeProcessingFallback) {
            this.config.setDisableNativeProcessingFallback(true);
        }
        if (mp3FileSize > 0) {
            this.config.setMp3SizeLimit(mp3FileSize);
        }
        if (oggFileSize > 0) {
            this.config.setOggSizeLimit(oggFileSize);
        }
        if (flacFileSize > 0) {
            this.config.setFlacSizeLimit(flacFileSize);
        }
        if (wavFileSize > 0) {
            this.config.setWavSizeLimit(wavFileSize);
        }
        if (auFileSize > 0) {
            this.config.setAuSizeLimit(auFileSize);
        }
        if (aifFileSize > 0) {
            this.config.setAifSizeLimit(aifFileSize);
        }
        registry.updateConfig("audio", config);
    }
}
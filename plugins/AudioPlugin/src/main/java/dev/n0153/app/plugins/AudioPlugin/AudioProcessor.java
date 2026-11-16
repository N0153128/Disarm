package dev.n0153.app.plugins.AudioPlugin;

import dev.n0153.app.core.GlobalConfig;
import dev.n0153.app.core.MediaContext;
import dev.n0153.app.core.MediaProcessor;
import dev.n0153.app.core.Utils;
import dev.n0153.app.core.exceptions.AudioProcessingException;
import dev.n0153.app.core.exceptions.MimeTypeDetectionException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ws.schild.jave.Encoder;
import ws.schild.jave.EncoderException;
import ws.schild.jave.MultimediaObject;
import ws.schild.jave.encode.AudioAttributes;
import ws.schild.jave.encode.EncodingAttributes;
import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;

/**
 * Re-encodes audio files for the Audio plugin. The main path uses JAVE/FFmpeg.
 * For WAV, AIFF and AU there is a native {@code javax.sound.sampled} fallback.
 * Before re-encoding, the input's MIME type, bitrate, sampling rate and channel
 * count are stored in the {@link AudioContext} and an output title is generated.
 * Output settings in {@link AudioConfig} override those detected values when set.
 * <p>
 * Output is written to {@link GlobalConfig#getGeneralOutputPath()}.
 * @since 0.2
 */
public class AudioProcessor implements MediaProcessor<AudioConfig> {
    private final AudioConfig config;
    private final AudioContext context;
    private final GlobalConfig globalConfig;
    private static final Logger logger = LogManager.getLogger(AudioProcessor.class);

    /**
     * @param config the Audio plugin's config, supplying output settings and fallbacks
     * @param context the per-file context used to store detected input values and the output title
     * @param globalConfig the global config, supplying the output path and title mode
     * @since 0.2
     */
    public AudioProcessor(AudioConfig config, AudioContext context, GlobalConfig globalConfig) {
        this.config = config;
        this.context = context;
        this.globalConfig = globalConfig;
    }

    /**
     * Re-encodes audio with {@code javax.sound.sampled}, used as the fallback when JAVE/FFmpeg
     * re-encoding fails. Supports WAV, AIFF and AU only; MP3, OGG and FLAC are not supported.
     * <p>
     * Output is 16-bit signed little-endian PCM, keeping the input's sample rate and channel
     * count. If direct conversion isn't supported, a plain PCM conversion is attempted instead.
     * The output container matches the input's audio format, or WAVE if the file isn't
     * detected as audio. The output is written to the general output path under the
     * context's audio title.
     * @param osTargetPath path to the input file
     * @throws UnsupportedAudioFileException if the input format is not supported by {@code javax.sound.sampled}
     * @throws AudioProcessingException if reading or writing the audio fails
     * @since 0.1
     */
    public void reEncodeAudioNative(Path osTargetPath) throws UnsupportedAudioFileException {
        try {
            //defining i/o
            File output = globalConfig.getGeneralOutputPath().resolve(context.getAudioTitle()).toFile();

            //establishing given format and audio stream
            AudioInputStream sourceStream = AudioSystem.getAudioInputStream(osTargetPath.toFile());
            AudioFormat sourceFormat = sourceStream.getFormat();

            //defining output format
            AudioFormat targetFormat = new AudioFormat(
                    AudioFormat.Encoding.PCM_SIGNED,
                    sourceFormat.getSampleRate(),
                    16,
                    sourceFormat.getChannels(),
                    sourceFormat.getChannels() * 2,
                    sourceFormat.getSampleRate(),
                    false
            );

            AudioInputStream targetStream;
            if (AudioSystem.isConversionSupported(targetFormat, sourceFormat)) {
                targetStream = AudioSystem.getAudioInputStream(targetFormat, sourceStream);
            } else {
                logger.debug("Direct conversion is not supported, attempting PCM conversion");
                AudioFormat pcmFormat = new AudioFormat(
                        sourceFormat.getSampleRate(),
                        16,
                        sourceFormat.getChannels(),
                        true,
                        false
                );
                targetStream = AudioSystem.getAudioInputStream(pcmFormat, sourceStream);
            }
            AudioFileFormat.Type audioType;
            if (Objects.equals(Utils.getFileType(osTargetPath), "audio")) {
                audioType = AudioUtils.getAudioType(osTargetPath, config.getFormatFromMime(Utils.getMimeType(osTargetPath))); //REFACTOR THIS
            } else {
                audioType = AudioFileFormat.Type.WAVE; // REFACTOR THIS
            }
            int bytesWritten = AudioSystem.write(targetStream, audioType, output);
            logger.debug("re-encoding complete, bytes written: {}", bytesWritten);

            targetStream.close();
            sourceStream.close();
        } catch (UnsupportedAudioFileException e) {
            logger.info("Unsupported format");
            throw new UnsupportedAudioFileException("Unsupported format");
        } catch (IOException e) {
            throw new AudioProcessingException("unable to re-encode audio", osTargetPath, e);
        }
    }

    /**
     * Builds the JAVE encoding attributes for the output. Bitrate, sampling rate and channels
     * each use, in order of priority:
     * <ol>
     *   <li>The configured output value, if greater than {@code 0};</li>
     *   <li>The configured fallback, if the detected input value is {@code 0} or less
     *       (bitrate and sampling rate only);</li>
     *   <li>The detected input value from the context.</li>
     * </ol>
     * Bitrate is not set when the detected MIME type is FLAC.
     * @param audioAttrs the audio attributes to fill in; the codec should already be set
     * @return encoding attributes wrapping {@code audioAttrs}
     * @since 0.2
     */
    private EncodingAttributes setAttributes(AudioAttributes audioAttrs) {
        if (!"flac".equals(context.getDetectedMime())) {
            int bitrate;
            if (config.getOutputBitrate() > 0) {
                bitrate = config.getOutputBitrate();
            } else if (context.getAudioBitrate() <= 0){
                bitrate = config.getBitrateFallback();
            } else {
                bitrate = context.getAudioBitrate();
            }
            audioAttrs.setBitRate(bitrate);
        }
        int channels = config.getOutputChannels() > 0 ? config.getOutputChannels() : context.getAudioChannels();
        audioAttrs.setChannels(channels);

        int samplingRate;
        if (config.getOutputSampleRate() > 0) {
            samplingRate = config.getOutputSampleRate();
        } else if (context.getSamplingRate() <= 0) {
            samplingRate = config.getSamplingRateFallback();
        } else {
            samplingRate = context.getSamplingRate();
        }
        audioAttrs.setSamplingRate(samplingRate); //mp3 sampling rate

        EncodingAttributes attrs = new EncodingAttributes();
        attrs.setAudioAttributes(audioAttrs);
        return attrs;
    }

    /**
     * Re-encodes audio with JAVE/FFmpeg into the given output format. The codec comes from the
     * context, with {@code vorbis} mapped to {@code libvorbis}. If no codec is set, FFmpeg
     * picks its default codec for the output format. Bitrate, sampling rate and channels are
     * set as described in {@link #setAttributes(AudioAttributes)}. The output is written to the
     * general output path under the context's audio title.
     * @param osTargetPath path to the input file
     * @param format the FFmpeg output format (e.g. {@code "mp3"}, {@code "wav"})
     * @throws AudioProcessingException if encoding fails
     * @since 0.1
     */
    public void reEncodeDefault(Path osTargetPath, String format) {
        try {
            //create vars
            MultimediaObject input = new MultimediaObject(osTargetPath.toFile());
            AudioAttributes audioAttrs = new AudioAttributes();
            Path outputPath = globalConfig.getGeneralOutputPath().resolve(context.getAudioTitle());

            // re-encode
            audioAttrs.setCodec("vorbis".equals(context.getAudioCodec()) ? "libvorbis" : context.getAudioCodec());
            EncodingAttributes attrs = setAttributes(audioAttrs);
            attrs.setOutputFormat(format);
            Encoder encoder = new Encoder();
            encoder.encode(input, outputPath.toFile(), attrs);
        } catch (EncoderException | MimeTypeDetectionException e) {
            throw new AudioProcessingException("encoding failed", osTargetPath, e);
        }
    }

    /**
     * Re-encodes the input into the configured output format, or into the input's own format
     * if the output format is {@code "default"}. If JAVE/FFmpeg re-encoding fails, falls back to
     * {@link #reEncodeAudioNative(Path)} unless that fallback is turned off in the config.
     * <p>
     * In no-save mode, the output file is deleted after re-encoding; if that deletion fails,
     * an error is logged and no exception is thrown.
     * @param osTargetPath path to the input file
     * @throws AudioProcessingException if MIME detection fails, re-encoding fails with the
     *         native fallback turned off, or the native fallback also fails
     * @since 0.2
     */
    public void reEncode(Path osTargetPath) {
        try {
            if (Objects.equals(config.getDefaultOutputTo(), "default")) {
                String targetFormat = config.getFormatFromMime(context.getDetectedMime());
                reEncodeDefault(osTargetPath, targetFormat);
            } else {
                reEncodeDefault(osTargetPath, config.getDefaultOutputTo());
            }
        } catch (MimeTypeDetectionException e) {
            throw new AudioProcessingException("Audio Processor: Failed to detect mime", osTargetPath);
        } catch (AudioProcessingException e) {
            try {
                if (config.getDisableNativeProcessingFallback()) {
                    throw new AudioProcessingException(
                            "Audio Processor: failed to process audio file. Native fallback processing is disabled.",
                            osTargetPath);
                } else {
                    reEncodeAudioNative(osTargetPath);
                }
            } catch (UnsupportedAudioFileException ex) {
                throw new AudioProcessingException("Audio Processor: native fallback failed", osTargetPath);
            }
        }
        if (config.getDontSaveAudio()) {
            try {
                Utils.fileDispose(globalConfig.getGeneralOutputPath().resolve(context.getAudioTitle()));
            } catch (IOException e) {
                logger.error("failed to delete audio file");
            }
        }

    }

    /**
     * {@inheritDoc}
     * <p>
     * Stores the input's details in the context: the detected MIME type, the bitrate (skipped
     * for FLAC), the sampling rate, the channel count, and a generated output title whose
     * extension matches the output format. Then runs {@link #reEncode(Path)}.
     * @param osTargetPath path to the input file
     * @throws AudioProcessingException if reading the input's details or re-encoding fails
     * @throws MimeTypeDetectionException if the input's MIME type cannot be detected
     * @since 0.2
     */
    @Override
    public void process(Path osTargetPath) throws AudioProcessingException {
        String format;
        context.setDetectedMime(Utils.getMimeType(osTargetPath));
        try {
            format = config.getFormatFromMime(context.getDetectedMime());
            if (!Objects.equals(format, "flac")) {
                context.setAudioBitrate(AudioUtils.getBitrate(osTargetPath, "audio"));
            }
            if (Objects.equals(config.getDefaultOutputTo(), "default")) {
                context.setAudioTitle(Utils.getTitle(osTargetPath,
                        config.getFormatFromMime(context.getDetectedMime()), globalConfig.getRandomCharactersTitle()));
            } else {
                context.setAudioTitle(Utils.getTitle(
                        osTargetPath,
                        config.getDefaultOutputTo(),
                        globalConfig.getRandomCharactersTitle()));
            }

            context.setAudioSamplingRate(AudioUtils.getSamplingRate(osTargetPath));
            context.setAudioChannels(AudioUtils.getAudioChannels(osTargetPath));
            reEncode(osTargetPath);
        } catch (IOException | EncoderException e) {
            throw new AudioProcessingException("failed to process audio", osTargetPath);
        }
        logger.info("audio re-encoded successfully");
    }

    /**
     * {@inheritDoc}
     * @return this processor's {@link AudioContext}
     * @since 0.2
     */
    @Override
    public MediaContext getContext() {
        return context;
    }
}

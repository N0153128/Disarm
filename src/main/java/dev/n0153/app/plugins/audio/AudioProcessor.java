package dev.n0153.app.plugins.audio;

import dev.n0153.app.GlobalConfig;
import dev.n0153.app.MediaContext;
import dev.n0153.app.MediaProcessor;
import dev.n0153.app.Utils;
import dev.n0153.app.exceptions.AudioProcessingException;
import dev.n0153.app.exceptions.MimeTypeDetectionException;
import dev.n0153.app.plugins.MediaUtils;
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

public class AudioProcessor implements MediaProcessor<AudioConfig> {
    private final AudioConfig config;
    private final AudioContext context;
    private final GlobalConfig globalConfig;
    private static final Logger logger = LogManager.getLogger(AudioProcessor.class);

    public AudioProcessor(AudioConfig config, AudioContext context, GlobalConfig globalConfig) {
        this.config = config;
        this.context = context;
        this.globalConfig = globalConfig;
    }

    /**
     * Native javax.sound.sampled implementation. Doesn't support MP3, OGG and FLAC.
     * supports only WAV, AIFF, AU.
     * @param osTargetPath Path to required file
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

    private EncodingAttributes setAttributes(String mime, AudioAttributes audioAttrs) {
        if (!"flac".equals(mime)) {
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
     * Main audio re-encoding method, utilises Jave (FFmpeg).
     * Accepts an input file path and format to encode to.
     * Sanitized output is saved to configured output path.
     * @param osTargetPath Path to input file
     * @param format Output format
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
            EncodingAttributes attrs = setAttributes(Utils.getMimeType(osTargetPath), audioAttrs);
            attrs.setOutputFormat(format);
            Encoder encoder = new Encoder();
            encoder.encode(input, outputPath.toFile(), attrs);
            if (config.getDontSaveAudio()) {
                try {
                    Utils.fileDispose(globalConfig.getGeneralOutputPath().resolve(context.getAudioTitle()));
                } catch (IOException e) {
                    logger.error("failed to delete audio file");
                }
            }

        } catch (EncoderException | MimeTypeDetectionException e) {
            throw new AudioProcessingException("encoding failed", osTargetPath, e);
        }
    }

    public void reEncode(Path osTargetPath) {
        try {
            if (Objects.equals(config.getDefaultOutputTo(), "default")) {
                String targetFormat = config.getFormatFromMime(Utils.getMimeType(osTargetPath));
                reEncodeDefault(osTargetPath, targetFormat);
            } else {
                reEncodeDefault(osTargetPath, config.getDefaultOutputTo());
            }
        } catch (MimeTypeDetectionException e) {
            throw new AudioProcessingException("Audio Processor: Failed to detect mime", osTargetPath);
        } catch (AudioProcessingException e) {
            try {
                reEncodeAudioNative(osTargetPath);
            } catch (UnsupportedAudioFileException ex) {
                throw new AudioProcessingException("Audio Processor: native fallback failed", osTargetPath);
            }
        }            if (config.getDontSaveAudio()) {
            try {
                Utils.fileDispose(globalConfig.getGeneralOutputPath().resolve(context.getAudioTitle()));
            } catch (IOException e) {
                logger.error("failed to delete audio file");
            }
        }

    }

    @Override
    public void process(Path osTargetPath) throws AudioProcessingException {
        String format;
        try {
            format = config.getFormatFromMime(Utils.getMimeType(osTargetPath));
            if (!Objects.equals(format, "flac")) {
                context.setAudioBitrate(MediaUtils.getBitrate(osTargetPath, "audio"));
            }
            if (Objects.equals(config.getDefaultOutputTo(), "default")) {
                context.setAudioTitle(Utils.getTitle(osTargetPath,
                        config.getFormatFromMime(Utils.getMimeType(osTargetPath)),
                        false));
            } else {
                context.setAudioTitle(Utils.getTitle(osTargetPath, config.getDefaultOutputTo(),
                        false));
            }

            context.setAudioSamplingRate(MediaUtils.getSamplingRate(osTargetPath));
            context.setAudioChannels(MediaUtils.getAudioChannels(osTargetPath));
            reEncode(osTargetPath);
        } catch (IOException | EncoderException e) {
            throw new AudioProcessingException("failed to process audio", osTargetPath);
        }
        logger.info("audio re-encoded successfully");
    }

    @Override
    public MediaContext getContext() {
        return context;
    }
}

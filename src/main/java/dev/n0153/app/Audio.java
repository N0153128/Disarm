package dev.n0153.app;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

import dev.n0153.app.exceptions.AudioProcessingException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ws.schild.jave.*;
import ws.schild.jave.encode.AudioAttributes;
import ws.schild.jave.encode.EncodingAttributes;

import java.nio.file.Path;

/**
 * Handles audio re-encoding for audio file sanitization.
 * Supports both Jave (FFmpeg) and native javax.sound.sampled implementations.
 *
 * @since 0.1
 */
public class Audio {
    private final DisarmConfig config;
    private final DisarmState state;

    /**
     * Public constructor for Audio class.
     * @param state Mutable runtime state.
     * @param config Immutable configuration.
     * @since 0.1
     */
    public Audio(DisarmState state, DisarmConfig config) {
        this.state = state;
        this.config = config;
    }
    private static final Logger logger = LogManager.getLogger(Audio.class);

    /**
     * Native javax.sound.sampled implementation. Doesn't support MP3, OGG and FLAC.
     * supports only WAV, AIFF, AU.
     * @param osTargetPath Path to required file
     * @since 0.1
     */
    public void reEncodeAudioNative(Path osTargetPath) throws UnsupportedAudioFileException {
        try {
            //defining i/o
            File output = config.getGeneralOutputPath().resolve(state.getGeneralFileTitle()).toFile();

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
            if (Objects.equals(state.getFileType(), "audio")) {
                audioType = Utils.getAudioType(osTargetPath); //REFACTOR THIS
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
            audioAttrs.setBitRate(state.getAudioBitrate());
        }
        audioAttrs.setChannels(state.getAudioChannels());
        audioAttrs.setSamplingRate(state.getAudioSamplingRate()); //mp3 sampling rate

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
    public void reEncode(Path osTargetPath, String format) {
        try {
            //create vars
            MultimediaObject input = new MultimediaObject(osTargetPath.toFile());
            AudioAttributes audioAttrs = new AudioAttributes();
            Path outputPath = config.getGeneralOutputPath().resolve(state.getGeneralFileTitle());

            // re-encode
            audioAttrs.setCodec("vorbis".equals(state.getAudioCodec()) ? "libvorbis" : state.getAudioCodec());
            EncodingAttributes attrs = setAttributes(state.getMime(), audioAttrs);
            attrs.setOutputFormat(format);
            Encoder encoder = new Encoder();
            encoder.encode(input, outputPath.toFile(), attrs);

        } catch (EncoderException e) {
            throw new AudioProcessingException("encoding failed", osTargetPath, e);
        }
    }

    /**
     * Shortcut method to re-encode any input file to WAV using FFmpeg.
     * @param osTargetPath Path to input file.
     * @since 0.1
     */
    public void reEncodeWav(Path osTargetPath) {
        reEncode(osTargetPath, "wav");
    }

    /**
     * Shortcut method to re-encode any input file to AU using FFmpeg.
     * @param osTargetPath Path to input file.
     * @since 0.1
     */
    public void reEncodeAu(Path osTargetPath) {
        reEncode(osTargetPath, "au");
    }

    /**
     * Shortcut method to re-encode any input file to AIFF using FFmpeg.
     * @param osTargetPath Path to input file.
     * @since 0.1
     */
    public void reEncodeAif(Path osTargetPath) {
        reEncode(osTargetPath, "aiff");
    }

    /**
     * Shortcut method to re-encode any input file to MP3 using FFmpeg.
     * @param osTargetPath Path to input file.
     * @since 0.1
     */
    public void reEncodeMp3(Path osTargetPath) {
        reEncode(osTargetPath, "mp3");
    }

    /**
     * Shortcut method to re-encode any input file to OGG using FFmpeg.
     * @param osTargetPath Path to input file.
     * @since 0.1
     */
    public void reEncodeOgg(Path osTargetPath) {
        reEncode(osTargetPath, "ogg");
    }

    /**
     * Shortcut method to re-encode any input file to FLAC using FFmpeg.
     * @param osTargetPath Path to input file.
     * @since 0.1
     */
    public void reEncodeFlac(Path osTargetPath) {
        reEncode(osTargetPath, "flac");
    }
}

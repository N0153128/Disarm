package dev.n0153.app;

import dev.n0153.app.exceptions.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ws.schild.jave.Encoder;
import ws.schild.jave.EncoderException;
import ws.schild.jave.MultimediaObject;
import ws.schild.jave.encode.EncodingAttributes;
import ws.schild.jave.encode.VideoAttributes;
import ws.schild.jave.encode.AudioAttributes;
import java.nio.file.Path;

/**
 * Handles video re-encoding for video file sanitization.
 * Utilises FFmpeg.
 *
 * @since 0.1
 */
public class Video {
    private final DisarmConfig config;
    private final DisarmState state;

    /**
     * Public constructor for Video class.
     * @param state Mutable runtime state.
     * @param config Immutable configuration.
     * @since 0.1
     */
    public Video (DisarmState state, DisarmConfig config) {
        this.state = state;
        this.config = config;
    }

    private static final Logger logger = LogManager.getLogger(Video.class);

    /**
     * Main video re-encoding method, utilises Jave (FFmpeg).
     * Accepts an input file path and format to encode to.
     * Sanitized output is saved to configured output path.
     * @param osTargetPath Path to input file
     * @param format Output format
     * @since 0.1
     */
    public void reEncodeVideo(Path osTargetPath, String format) {
        try {
            logger.info("params codec: {}, detected codec: {}", state.getVideoCodec(), state.getVideoCodec());

            MultimediaObject input = new MultimediaObject(osTargetPath.toFile());
            Path outputPath = config.getGeneralOutputPath().resolve(state.getGeneralFileTitle());

            VideoAttributes videoAttrs = new VideoAttributes();
            videoAttrs.setCodec(state.getVideoCodec());
            AudioAttributes audioAttrs = new AudioAttributes();
            audioAttrs.setCodec(state.getAudioCodec());
            EncodingAttributes attrs = setAttributes(audioAttrs, videoAttrs);
            attrs.setOutputFormat(format);

            logger.info("Video bitrate: {},\n " +
                            "video framerate: {},\n" +
                            "video size: {},\n" +
                            "audio bitrate: {},\n" +
                            "audio samplerate: {},\n"+
                    "video codec detected: {}", state.getVideoBitrate(),
                    state.getVideoFrameRate(),
                    state.getVideoSize(), state.getAudioBitrate(),
                    state.getAudioSamplingRate(), state.getVideoCodec());
            Encoder encoder = new Encoder();
            encoder.encode(input, outputPath.toFile(), attrs);
            logger.info("re-encoded successfully");

        } catch (EncoderException e) {
            throw new VideoProcessingException("Failed to process video", osTargetPath, e);
        }
    }

    private EncodingAttributes setAttributes(AudioAttributes audioAttrs, VideoAttributes videoAttrs) {
        videoAttrs.setBitRate(state.getVideoBitrate());
        videoAttrs.setFrameRate(state.getVideoFrameRate());
        videoAttrs.setSize(state.getVideoSize());

        audioAttrs.setBitRate(state.getAudioBitrate());
        audioAttrs.setChannels(state.getAudioChannels());
        audioAttrs.setSamplingRate(state.getAudioSamplingRate());

        EncodingAttributes attrs = new EncodingAttributes();
        attrs.setVideoAttributes(videoAttrs);
        attrs.setAudioAttributes(audioAttrs);
        return attrs;
    }

    /**
     * Shortcut method to re-encode any input file to MP4 using FFmpeg.
     * @since 0.1
     */
    public void reEncodeMp4(Path osTargetPath) {
        reEncodeVideo(osTargetPath, "mp4");
    }

    /**
     * Shortcut method to re-encode any input file to WEBM using FFmpeg.
     * @since 0.1
     */
    public void reEncodeWebm(Path osTargetPath) {
        reEncodeVideo(osTargetPath, "webm");
    }

    /**
     * Shortcut method to re-encode any input file to MKV using FFmpeg.
     * @since 0.1
     */
    public void reEncodeMkv(Path osTargetPath) {
        reEncodeVideo(osTargetPath, "matroska");
    }

    /**
     * Shortcut method to re-encode any input file to MOV using FFmpeg.
     * @since 0.1
     */
    public void reEncodeMov(Path osTargetPath){
        reEncodeVideo(osTargetPath, "mov");
    }
}
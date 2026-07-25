package dev.n0153.app.plugins.video;

import dev.n0153.app.GlobalConfig;
import dev.n0153.app.MediaContext;
import dev.n0153.app.MediaProcessor;
import dev.n0153.app.Utils;
import dev.n0153.app.exceptions.VideoProcessingException;
import dev.n0153.app.plugins.MediaUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ws.schild.jave.Encoder;
import ws.schild.jave.EncoderException;
import ws.schild.jave.MultimediaObject;
import ws.schild.jave.encode.AudioAttributes;
import ws.schild.jave.encode.EncodingAttributes;
import ws.schild.jave.encode.VideoAttributes;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;

public class VideoProcessor implements MediaProcessor<VideoConfig> {
    private final VideoConfig config;
    private final VideoContext context;
    private final GlobalConfig globalConfig;
    private static final Logger logger = LogManager.getLogger(VideoProcessor.class);

    public VideoProcessor(VideoConfig config, VideoContext context, GlobalConfig globalConfig) {
        this.config = config;
        this.context = context;
        this.globalConfig = globalConfig;
    }

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
            logger.info("params codec: {}, detected codec: {}", config.getOutputVideoCodec(), context.getVideoCodec());

            MultimediaObject input = new MultimediaObject(osTargetPath.toFile());
            Path outputPath = globalConfig.getGeneralOutputPath().resolve(context.getVideoTitle());

            VideoAttributes videoAttrs = new VideoAttributes();
            if (Objects.equals(config.getOutputVideoCodec(), "default")) {
                videoAttrs.setCodec(context.getVideoCodec());
            } else {
                videoAttrs.setCodec(config.getOutputVideoCodec());
            }
            AudioAttributes audioAttrs = new AudioAttributes();
            if (Objects.equals(config.getOutputAudioCodec(), "default")) {
                audioAttrs.setCodec(context.getAudioCodec());
            } else {
                audioAttrs.setCodec(config.getOutputAudioCodec());
            }
            EncodingAttributes attrs = setAttributes(audioAttrs, videoAttrs);
            attrs.setOutputFormat(format);

            logger.info("Video bitrate: {},\n " +
                            "video framerate: {},\n" +
                            "video size: {},\n" +
                            "audio bitrate: {},\n" +
                            "audio samplerate: {},\n"+
                            "video codec detected: {}", context.getVideoBitrate(),
                    context.getVideoFrameRate(),
                    context.getVideoSize(), context.getAudioBitrate(),
                    context.getAudioSamplingRate(), context.getVideoCodec());
            Encoder encoder = new Encoder();
            encoder.encode(input, outputPath.toFile(), attrs);
            logger.info("re-encoded successfully");

        } catch (EncoderException e) {
            throw new VideoProcessingException("Failed to process video", osTargetPath, e);
        }
    }

    private EncodingAttributes setAttributes(AudioAttributes audioAttrs, VideoAttributes videoAttrs) {
        videoAttrs.setBitRate(context.getVideoBitrate());
        videoAttrs.setFrameRate(context.getVideoFrameRate());
        videoAttrs.setSize(context.getVideoSize());

        audioAttrs.setBitRate(context.getAudioBitrate());
        audioAttrs.setChannels(context.getAudioChannels());
        audioAttrs.setSamplingRate(context.getAudioSamplingRate());

        EncodingAttributes attrs = new EncodingAttributes();
        attrs.setVideoAttributes(videoAttrs);
        attrs.setAudioAttributes(audioAttrs);
        return attrs;
    }

    @Override
    public void process(Path osTargetPath) throws VideoProcessingException {
        String mime;
        String format;
        try {
            mime = Utils.getMimeType(osTargetPath);
            format = config.getFormatFromMime(mime);
            context.setVideoTitle(Utils.getTitle(osTargetPath,
                    config.getFormatFromMime(Utils.getMimeType(osTargetPath)),
                    false));
            context.setVideoBitrate(MediaUtils.getBitrate(osTargetPath, "video"));
            context.setVideoFrameRate(VideoUtils.getVideoFrameRate(osTargetPath));
            context.setVideoSize(VideoUtils.getVideoDimensions(osTargetPath));
            context.setVideoCodec(VideoUtils.getVideoCodec(osTargetPath, config.getSwapAV1()));
            context.setAudioCodec(MediaUtils.getCodec(osTargetPath, "audio"));
            context.setAudioChannels(MediaUtils.getAudioChannels(osTargetPath));

            int audioBitrate = MediaUtils.getBitrate(osTargetPath, "audio");
            context.setAudioBitrate(audioBitrate > 0 ? audioBitrate : config.getOutputAudioBitrate());
            context.setAudioSamplingRate(MediaUtils.getSamplingRate(osTargetPath));

            reEncodeVideo(osTargetPath, format);
        } catch (IOException | EncoderException e) {
            throw new VideoProcessingException("Video Processor: failed to detect mime", osTargetPath);
        }
    }

    @Override
    public MediaContext getContext() {
        return context;
    }
}

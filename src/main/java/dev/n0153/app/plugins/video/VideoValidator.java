package dev.n0153.app.plugins.video;

import dev.n0153.app.MediaValidator;
import dev.n0153.app.Utils;
import dev.n0153.app.exceptions.MimeTypeDetectionException;
import dev.n0153.app.exceptions.ValidationException;
import dev.n0153.app.plugins.MediaUtils;
import ws.schild.jave.EncoderException;
import ws.schild.jave.info.VideoSize;

import java.io.IOException;
import java.nio.file.Path;

public class VideoValidator implements MediaValidator {
    private VideoConfig config;
    private VideoContext context;

    public void createMeta(VideoConfig config, VideoContext context) {
        this.config = config;
        this.context = context;
    }

    public boolean checkMeta() {
        return this.config != null && this.context != null;
    }

    /**
     * Check video bitrate for given video format.
     * @param format Detected video format.
     * @param bitrate Detected bitrate.
     * @return True if bitrate is within configured bounds for specified format.
     * @since 0.1
     */
    public boolean checkVideoBitrate(String format, int bitrate) {
        if (bitrate < -1) {
            return false;
        }
        if (format == null) {
            return false;
        }
        return bitrate <= config.getMaxVideoBitrate(format);
    }

    /**
     * Check audio bitrate for given video format.
     * @param format Detected video format.
     * @param bitrate Detected bitrate.
     * @return True if bitrate is within configured bounds for specified format.
     * @since 0.1
     */
    public boolean checkAudioBitrateForVideo(String format, int bitrate) {
        if (bitrate < -1) {
            return false;
        }
        if (format == null) {
            return false;
        }
        return bitrate <= config.getMaxAudioBitrate(format);
    }

    /**
     * Check audio sampling rate for given video format.
     * @param format Detected video format.
     * @param samplingRate Detected sampling rate.
     * @return True if sampling rate is within configured bounds for specified format.
     * @since 0.1
     */
    public boolean checkAudioSampleRateForVideo(String format, int samplingRate) {
        if (samplingRate == 0) {
            return false;
        }
        if (format == null) {
            return false;
        }
        return samplingRate <= config.getMaxSampleRate(format);
    }

    /**
     * Ensures that the specified video codec is allowed.
     * @param format Detected mime type.
     * @param codec Detected codec.
     * @return True if codec is whitelisted.
     * @since 0.1
     */
    public boolean checkVideoCodecWhiteList(String format, String codec) {
        if (format == null) {
            return false;
        }
        if (codec == null) {
            return false;
        }
        return config.isVideoCodecAllowedFor(format, codec);
    }

    /**
     * Ensures that the specified audio codec within a video file is allowed.
     * @param format Detected mime type.
     * @param codec Detected codec.
     * @return True if codec is whitelisted.
     * @since 0.1
     */
    public boolean checkAudioCodecWhiteListForVideo(String format, String codec) {
        if (format == null) {
            return false;
        }
        if (codec == null) {
            return false;
        }
        return config.isAudioCodecAllowedFor(format, codec);
    }

    /**
     * Ensures that the video file duration doesn't exceed its configured limit.
     * @param osTargetPath Path to input file.
     * @return True if duration is appropriate.
     * @since 0.1
     */
    public boolean validateVideoDuration(Path osTargetPath) {
        long videoDuration = MediaUtils.getTrackLength(osTargetPath);
        return videoDuration <= config.getMaxVideoDuration();
    }

    /**
     * Ensures that the specified file doesn't exceed configured size limits per format.
     * @param osTargetPath Path to input file.
     * @return True if file size doesn't exceed the limits.
     * @since 0.1
     */
    public boolean ensureSizeLimit(Path osTargetPath) {
        long size = Utils.getSize(osTargetPath);
        try {
            return size <= config.getMaxFileSize(config.getFormatFromMime(Utils.getMimeType(osTargetPath)));
        } catch (MimeTypeDetectionException e) {
            throw new ValidationException("Failed to detect mime type");
        }
    }

    public boolean checkFrameRate(int frameRate) {
        if (frameRate <= 0) {
            return false;
        }
        return frameRate <= config.getMaxVideoFrameRate();
    }

    public boolean checkVideoDimensions(VideoSize size) {
        if (size.getWidth() <= 0) {
            return false;
        }
        if (size.getHeight() <= 0) {
            return false;
        }
        if (size.getWidth() > config.getMaxVideoWidth()) {
            return false;
        }
        return size.getHeight() <= config.getMaxVideoHeight();
    }

    @Override
    public boolean validate(Path osTargetPath) {
        String mime;
        String format;
        try {
            mime = Utils.getMimeType(osTargetPath);
            format = config.getFormatFromMime(mime);
        } catch (MimeTypeDetectionException e) {
            throw new ValidationException("Video validator: failed to detect mime type");
        }
        if (!checkMeta()) {
            throw new ValidationException("Video Validator: meta is empty");
        }
        try {
            if (!checkAudioBitrateForVideo(format, MediaUtils.getBitrate(osTargetPath, "audio"))) {
                throw new ValidationException("Video Validator: Audio bitrate validation failed");
            }
            if (!checkVideoBitrate(format, MediaUtils.getBitrate(osTargetPath, "video"))) {
                throw new ValidationException("Video Validator: Video bitrate validation failed");
            }
            if (!checkAudioSampleRateForVideo(format, MediaUtils.getSamplingRate(osTargetPath))) {
                throw new ValidationException("Video Validator: Audio sampling rate validation failed");
            }
            if (!checkFrameRate(VideoUtils.getVideoFrameRate(osTargetPath))) {
                throw new ValidationException("Video Validator: Video frame rate validation failed");
            }
            if (!checkVideoDimensions(VideoUtils.getVideoDimensions(osTargetPath))) {
                throw new ValidationException("Video Validator: Video dimensions validation failed");
            }
        } catch (EncoderException | IOException e) {
            throw new ValidationException("Failed to detect bitrate");
        }
        if (!checkVideoCodecWhiteList(format, MediaUtils.getCodec(osTargetPath, "video"))) {
            throw new ValidationException("Video Validator: Video codec validation failed");
        }
        if (!checkAudioCodecWhiteListForVideo(format, MediaUtils.getCodec(osTargetPath, "audio"))) {
            throw new ValidationException("Video Validator: Audio codec validation failed");
        }
        if (!validateVideoDuration(osTargetPath)) {
            throw new ValidationException("Video Validator: Video duration validation failed");
        }
        if (!ensureSizeLimit(osTargetPath)) {
            throw new ValidationException("Video Validator: format size validation failed");
        }
        return true;
    }
}

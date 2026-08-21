package dev.n0153.app.plugins.audio;

import dev.n0153.app.*;
import dev.n0153.app.exceptions.MimeTypeDetectionException;
import dev.n0153.app.exceptions.ValidationException;
import dev.n0153.app.plugins.MediaUtils;
import ws.schild.jave.EncoderException;

import java.io.IOException;
import java.nio.file.Path;

public class AudioValidator implements MediaValidator {
    private AudioConfig config;
    private AudioContext context;

    public void createMeta(AudioConfig config, AudioContext context) {
        this.config = config;
        this.context = context;
    }

    public boolean checkMeta() {
        return this.config != null && this.context != null;
    }

    /**
     * Ensures that the audio track duration doesn't exceed its configured limit.
     * @param osTargetPath Path to input file.
     * @return True if duration is appropriate.
     * @since 0.1
     */
    public boolean validateAudioDuration(Path osTargetPath) {
        long audioDuration = MediaUtils.getTrackLength(osTargetPath);
        return audioDuration <= config.getMaxAudioDuration();
    }

    /**
     * Ensures that the specified audio codec is allowed.
     * @param mimeType Detected file type.
     * @param codec Detected codec.
     * @return True if codec is whitelisted.
     * @since 0.1
     */
    public boolean checkAudioCodecWhiteList(String mimeType, String codec) {
        if (mimeType == null) {
            return false;
        }
        if (codec == null) {
            return false;
        }
        return config.isCodecAllowed(mimeType, codec);
    }

    /**
     * Ensures that the specified file doesn't exceed configured size limits per format.
     * @param osTargetPath Path to input file.
     * @return True if file size doesn't exceed the limits.
     * @since 0.1
     */
    public boolean ensureSizeLimit(Path osTargetPath) {
        long size = Utils.getSize(osTargetPath);
        return size <= config.getMaxFileSize();
    }

    /**
     * Check audio bitrate for given audio format.
     * @param mimeType Detected audio format.
     * @param bitrate Detected bitrate.
     * @return True if bitrate is within configured bounds for specified format.
     * @since 0.1
     */
    public boolean checkAudioBitrate(String mimeType, int bitrate) {
        if (bitrate < -1) {
            return false;
        }
        if (mimeType == null) {
            return false;
        }
        return bitrate <= config.getMaxBitrates(mimeType);
    }

    public boolean checkAudioSamplingRate(String mimeType, int samplingRate) {
        if (samplingRate < -1) {
            return false;
        }
        if (mimeType == null) {
            return false;
        }
        return samplingRate <= config.getMaxSampleRates(mimeType);
    }

    public boolean checkChannels(int channels) {
        if (channels < 0) {
            return false;
        }
        return channels <= config.getMaxChannels();
    }

    @Override
    public boolean validateFileSize(Path osTargetPath) {
        String mime = config.getFormatFromMime(Utils.getMimeType(osTargetPath));
        int size = (int) Utils.getSize(osTargetPath);
        return size <= config.maxFileSizeInBytes(mime);
    }

    @Override
    public boolean validate(Path osTargetPath) {
        if (!checkMeta()) {
            throw new ValidationException("Audio Validator: meta is empty");
        }
        String mime;
        try {
            mime = config.getFormatFromMime(Utils.getMimeType(osTargetPath));
        } catch (MimeTypeDetectionException e) {
            throw new ValidationException("Audio Validator: failed to detect mime type");
        }
        if (!validateFileSize(osTargetPath)) {
            throw new ValidationException("Audio Validator: Audio file size validation failed");
        }
        if (!validateAudioDuration(osTargetPath)) {
            throw new ValidationException("Audio Validator: Audio duration validation failed");
        }
        if (!checkAudioCodecWhiteList(mime, MediaUtils.getCodec(osTargetPath, "audio"))) {
            throw new ValidationException("Audio Validator: Codec whitelist validation failed");
        }
        if (!ensureSizeLimit(osTargetPath)) {
            throw new ValidationException("Audio Validator: Size limit validation failed");
        }
        try {
            if (!checkAudioBitrate(mime, MediaUtils.getBitrate(osTargetPath, "audio"))) {
                throw new ValidationException("Audio Validator: Bitrate validation failed");
            }
            if (!checkAudioSamplingRate(mime, MediaUtils.getSamplingRate(osTargetPath))) {
                throw new ValidationException("Audio Validator: Sampling rate validation failed");
            }
            if (!checkChannels(MediaUtils.getAudioChannels(osTargetPath))) {
                throw new ValidationException("Audio Validator: Channels validation failed");
            }
        } catch (EncoderException | IOException e) {
            throw new ValidationException("Audio Validator: failed to detect bitrate or sampling rate");
        }
        return true;
    }
}

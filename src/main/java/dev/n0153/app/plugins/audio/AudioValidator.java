package dev.n0153.app.plugins.audio;

import dev.n0153.app.*;
import dev.n0153.app.exceptions.ValidationException;

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
        long audioDuration = Utils.getTrackLength(osTargetPath);
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
            throw new ValidationException("Audio Validator: Mime is null");
        }
        if (codec == null) {
            throw new ValidationException("Audio Validator: Codec is null");
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
        if (bitrate <= 0) {
            throw new ValidationException("Audio Validator: corrupt bitrate detected");
        }
        if (mimeType == null) {
            throw new ValidationException("Audio Validator: mime is null");
        }
        return bitrate <= config.getMaxBitrates(mimeType);
    }

    @Override
    public boolean validate(Path osTargetPath) {
        if (!checkMeta()) {
            throw new ValidationException("Audio Validator: meta is empty");
        }
        return false;
    }
}

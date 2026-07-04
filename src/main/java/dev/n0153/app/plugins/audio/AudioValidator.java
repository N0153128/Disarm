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
        if (bitrate < -1) {
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
        String mime;
        try {
            mime = config.getFormatFromMime(Utils.getMimeType(osTargetPath));
        } catch (MimeTypeDetectionException e) {
            throw new ValidationException("Audio Validator: failed to detect mime type");
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
        } catch (EncoderException | IOException e) {
            throw new ValidationException("Audio Validator: failed to detect bitrate");
        }
        return true;
    }
}

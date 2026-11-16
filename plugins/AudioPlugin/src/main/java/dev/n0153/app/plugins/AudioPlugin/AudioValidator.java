package dev.n0153.app.plugins.AudioPlugin;

import dev.n0153.app.core.GlobalValidator;
import dev.n0153.app.core.MediaConfig;
import dev.n0153.app.core.MediaValidator;
import dev.n0153.app.core.Utils;
import dev.n0153.app.core.exceptions.MimeTypeDetectionException;
import dev.n0153.app.core.exceptions.ValidationException;
import ws.schild.jave.EncoderException;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Audio-specific validation for the Audio plugin. It runs after {@link GlobalValidator}
 * and before {@link AudioProcessor}. Each input file is checked against the ceilings in
 * {@link AudioConfig}: file size (per format and overall), duration, the codec allowlist,
 * bitrate, sampling rate and channel count. Media details are read through JAVE/FFmpeg
 * via {@link AudioUtils}.
 * <p>
 * Before {@link #validate(Path)} is called, a config and context must be bound with
 * {@link #createMeta(AudioConfig, AudioContext)}. {@link this.getValidator( MediaConfig )}
 * does this.
 * @since 0.2
 */
public class AudioValidator implements MediaValidator {
    private AudioConfig config;
    private AudioContext context;

    /**
     * Binds the config and context this validator checks against. Replaces any previously bound pair.
     * @param config the Audio plugin's config, supplying validation ceilings
     * @param context the per-file audio context
     * @since 0.2
     */
    public void createMeta(AudioConfig config, AudioContext context) {
        this.config = config;
        this.context = context;
    }

    /**
     * @return {@code true} if both a config and a context have been bound via
     *         {@link #createMeta(AudioConfig, AudioContext)}
     * @since 0.2
     */
    public boolean checkMeta() {
        return this.config != null && this.context != null;
    }

    /**
     * Checks that the audio track's duration doesn't exceed {@link AudioConfig#getMaxAudioDuration()}.
     * @param osTargetPath path to the input file
     * @return {@code true} if the duration is within the limit
     * @since 0.1
     */
    public boolean validateAudioDuration(Path osTargetPath) {
        long audioDuration = AudioUtils.getTrackLength(osTargetPath);
        return audioDuration <= config.getMaxAudioDuration();
    }

    /**
     * Checks a codec against the codec allowlist for a format.
     * @param mimeType the input's format (e.g. {@code "mp3"}, {@code "wav"})
     * @param codec the detected codec name
     * @return {@code true} if the codec is allowed; {@code false} if it isn't, or if either argument is {@code null}
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
     * Checks that the file's size doesn't exceed the overall audio ceiling,
     * {@link AudioConfig#getMaxFileSize()}.
     * @param osTargetPath path to the input file
     * @return {@code true} if the file size is within the overall ceiling
     * @since 0.1
     */
    public boolean ensureSizeLimit(Path osTargetPath) {
        long size = Utils.getSize(osTargetPath);
        return size <= config.getMaxFileSize();
    }

    /**
     * Checks a bitrate against the configured ceiling for a format. {@code -1}, which JAVE
     * returns when it cannot determine the bitrate, is accepted.
     * @param mimeType the input's format (e.g. {@code "mp3"})
     * @param bitrate the detected bitrate in bits per second
     * @return {@code true} if the bitrate is within the format's ceiling; {@code false} if it
     *         exceeds it, is below {@code -1}, or {@code mimeType} is {@code null}. A format with
     *         no configured ceiling has a ceiling of {@code 0}.
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

    /**
     * Checks a sampling rate against the configured ceiling for a format. {@code -1}, which JAVE
     * returns when it cannot determine the sampling rate, is accepted.
     * @param mimeType the input's format (e.g. {@code "mp3"})
     * @param samplingRate the detected sampling rate in Hz
     * @return {@code true} if the sampling rate is within the format's ceiling; {@code false} if it
     *         exceeds it, is below {@code -1}, or {@code mimeType} is {@code null}. A format with
     *         no configured ceiling has a ceiling of {@code 0}.
     * @since 0.2
     */
    public boolean checkAudioSamplingRate(String mimeType, int samplingRate) {
        if (samplingRate < -1) {
            return false;
        }
        if (mimeType == null) {
            return false;
        }
        return samplingRate <= config.getMaxSampleRates(mimeType);
    }

    /**
     * Checks a channel count against {@link AudioConfig#getMaxChannels()}.
     * @param channels the detected channel count
     * @return {@code true} if the count is within the ceiling; {@code false} if it exceeds it or is negative
     * @since 0.2
     */
    public boolean checkChannels(int channels) {
        if (channels < 0) {
            return false;
        }
        return channels <= config.getMaxChannels();
    }

    /**
     * {@inheritDoc}
     * The file's format is detected from its signature, and its size is checked against that
     * format's ceiling (e.g. {@link AudioConfig#getMp3SizeLimit()}).
     * @param osTargetPath path to the input file
     * @return {@code true} if the file size is within its format's ceiling
     * @throws MimeTypeDetectionException if the file's MIME type cannot be detected
     * @throws IllegalArgumentException if the detected format is not a supported audio format
     * @since 0.2
     */
    @Override
    public boolean validateFileSize(Path osTargetPath) {
        String mime = config.getFormatFromMime(Utils.getMimeType(osTargetPath));
        int size = (int) Utils.getSize(osTargetPath);
        if (size > config.maxFileSizeInBytes(mime)) {
            return false;
        }
        if (mime.equals("mp3") && size > config.getMp3SizeLimit()) {
            return false;
        } else if (mime.equals("ogg") && size > config.getOggSizeLimit()) {
            return false;
        } else if (mime.equals("flac") && size > config.getFlacSizeLimit()) {
            return false;
        } else if (mime.equals("wav") && size > config.getWavSizeLimit()) {
            return false;
        } else if (mime.equals("au") && size > config.getAuSizeLimit()) {
            return false;
        } else if (mime.equals("aif") && size > config.getAifSizeLimit()) {
            return false;
        }
        return true;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Checks run in this order: bound meta, MIME detection, per-format file size, duration,
     * codec allowlist, overall file size, bitrate, sampling rate, channel count. Validation
     * stops at the first failure.
     * @param osTargetPath path to the input file
     * @return {@code true} if all checks pass; a failure throws rather than returning {@code false}
     * @throws ValidationException if no config/context is bound, MIME detection fails, any check
     *         fails, or the bitrate, sampling rate or channel count cannot be read
     * @since 0.2
     */
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
        if (!checkAudioCodecWhiteList(mime, AudioUtils.getCodec(osTargetPath, "audio"))) {
            throw new ValidationException("Audio Validator: Codec whitelist validation failed");
        }
        if (!ensureSizeLimit(osTargetPath)) {
            throw new ValidationException("Audio Validator: Size limit validation failed");
        }
        try {
            if (!checkAudioBitrate(mime, AudioUtils.getBitrate(osTargetPath, "audio"))) {
                throw new ValidationException("Audio Validator: Bitrate validation failed");
            }
            if (!checkAudioSamplingRate(mime, AudioUtils.getSamplingRate(osTargetPath))) {
                throw new ValidationException("Audio Validator: Sampling rate validation failed");
            }
            if (!checkChannels(AudioUtils.getAudioChannels(osTargetPath))) {
                throw new ValidationException("Audio Validator: Channels validation failed");
            }
        } catch (EncoderException | IOException e) {
            throw new ValidationException("Audio Validator: failed to detect bitrate or sampling rate");
        }
        return true;
    }
}

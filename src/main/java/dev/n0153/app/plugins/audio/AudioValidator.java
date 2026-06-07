package dev.n0153.app.plugins.audio;

import dev.n0153.app.*;
import dev.n0153.app.exceptions.BitrateValidationException;
import dev.n0153.app.exceptions.SamplingRateValidationException;
import dev.n0153.app.exceptions.UnsupportedFileTypeException;

import java.nio.file.Path;

public class AudioValidator implements MediaValidator {

    /**
     * Ensures that the audio track duration doesn't exceed its configured limit.
     * @param osTargetPath Path to input file.
     * @param config Immutable configuration.
     * @return True if duration is appropriate.
     * @since 0.1
     */
    public static boolean validateAudioDuration(Path osTargetPath, DisarmConfig config) {
        long audioDuration = Utils.getTrackLength(osTargetPath);
        return audioDuration <= config.getAudioMaxDuration();
    }

    /**
     * Check audio sampling rate for given video format.
     * @param mimeType Detected video format.
     * @param samplingRate Detected sampling rate.
     * @return True if sampling rate is within configured bounds for specified format.
     * @since 0.1
     */
    public static boolean checkAudioSampleRateForVideo(String mimeType, int samplingRate) {
        if (samplingRate == 0 || mimeType == null || samplingRate < 0) {
            return false;
        }
        try {
            return FormatRegistry.VideoFormatLimits.isValidSampleRateFor(mimeType, samplingRate);
        } catch(IllegalArgumentException e) {
            throw new SamplingRateValidationException("failed to validate audio sampling rate for video", mimeType, e);
        }
    }

    /**
     * Ensures that the specified audio codec is allowed.
     * @param fileType Detected file type.
     * @param codec Detected codec.
     * @return True if codec is whitelisted.
     * @since 0.1
     */
    public static boolean checkAudioCodecWhiteList(String fileType, String codec) {
        if (fileType == null || codec == null) {
            return false;
        }
        try {
            FormatRegistry.AllowedAudioCodecs list = FormatRegistry.AllowedAudioCodecs.fromFormat(fileType.toUpperCase());
            if (list == null) {
                throw new UnsupportedFileTypeException("Failed to detect codec", fileType+"/"+codec, null);
            }
            return list.isValidFormat(codec.toLowerCase());
        } catch(IllegalArgumentException e) {
            throw new UnsupportedFileTypeException("failed to detect codec", fileType+"/"+codec, e);
        }
    }

    /**
     * Ensures that the specified file doesn't exceed configured size limits per format.
     * @param osTargetPath Path to input file.
     * @param config Immutable configuration.
     * @param state Runtime state.
     * @return True if file size doesn't exceed the limits.
     * @since 0.1
     */
    public static boolean ensureSizeLimit(Path osTargetPath, DisarmConfig config, DisarmState state) {
        long size = Utils.getSize(osTargetPath);
        int errorTrigger = -1; // deliberate error trigger, in case if size limit returns null
        if (config.getGeneralSizeLimit() > 0) {
            return size <= config.getGeneralSizeLimit();
        } else {
            return switch (state.getFileType().toLowerCase()) { // checkable file is either below/equal to size limit or error is thrown
                case "image" -> size <= DisarmConfig.getImageSizeLimits().getOrDefault(state.getMime(), errorTrigger);
                case "text" -> size <= DisarmConfig.getTextSizeLimits().getOrDefault(state.getMime(), errorTrigger);
                case "video" -> size <= DisarmConfig.getVideoSizeLimits().getOrDefault(state.getMime(), errorTrigger);
                case "audio" -> size <= DisarmConfig.getAudioSizeLimits().getOrDefault(state.getMime(), errorTrigger);
                case "logo" -> size <= config.getLogoSizeLimit();
                default -> false;
            };
        }
    }

    /**
     * Check audio bitrate for given audio format.
     * @param mimeType Detected audio format.
     * @param bitrate Detected bitrate.
     * @return True if bitrate is within configured bounds for specified format.
     * @since 0.1
     */
    public static boolean checkAudioBitrate(String mimeType, int bitrate) {
        if (bitrate == 0 || mimeType == null || bitrate < 0) {
            return false;
        }
        try {
            return FormatRegistry.AudioFormatLimits.isValidBitrateFor(mimeType, bitrate);
        } catch(IllegalArgumentException e) {
            throw new BitrateValidationException("failed to validate audio bitrate", mimeType, e);
        }
    }

    @Override
    public boolean validate(Path osTargetPath) {
        return false;
    }
}

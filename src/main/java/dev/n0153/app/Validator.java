package dev.n0153.app;

import dev.n0153.app.exceptions.*;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Validator is a static utility class that handles all input validation before files get processed.
 * @since 0.1
 */
public class Validator {

    /**
     * Path validation method, ensures that the user cannot get out of bounds and reach sensitive files.
     * NOTE!: Absolute path checking is disabled for release 0.1 due to local nature of the project.
     * This check will be re-enabled once networking features are implemented.
     * @param osTargetPath Path to input file.
     * @return True if path is valid, throws UnsafePathException if validation checks fail.
     * @since 0.1
     */
    public static boolean validatePath(Path osTargetPath) {
        boolean singleDot = osTargetPath.toString().contains("./");
        boolean doubleDot = osTargetPath.toString().contains("../");
//        boolean absolute = osTargetPath.toString().startsWith("/");
        if (singleDot) {
            throw new UnsafePathException("potentially dangerous pathing, \"./\" ", osTargetPath);
        }
        if (doubleDot) {
            throw new UnsafePathException("potentially dangerous pathing, \"../\"", osTargetPath);
        }
        // disabled absolute path checking util networking features implemented
//        if (absolute) {
//            throw new UnsafePathException("potentially dangerous absolute pathing", osTargetPath);
//        }
        if (Files.isSymbolicLink(osTargetPath)) {
            throw new UnsafePathException("potentially dangerous symlink pathing", osTargetPath);
        }
        if (!Files.exists(osTargetPath)) {
            throw new UnsafePathException("Path doesnt exist", osTargetPath);
        }
        return true;
    }

    /**
     * Ensures that the specified mime is allowed.
     * @param fileType Media type, such as Image, Video, Text or Audio.
     * @param mimeType Detected mime type to check.
     * @return True if file is whitelisted.
     * @throws UnsupportedFileTypeException If detected mime isn't supported.
     * @since 0.1
     */
    public static boolean checkMimeWhiteList(String fileType, String mimeType) throws UnsupportedFileTypeException {
        if (fileType == null || mimeType == null) {
            return false;
        }
        try {
            FormatRegistry.AllowList list = FormatRegistry.AllowList.valueOf(fileType.toUpperCase());
            return list.isValidFormat(mimeType.toLowerCase());
        } catch(IllegalArgumentException e) {
            throw new UnsupportedFileTypeException("failed to detect file type", fileType, e);
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

    /**
     * Check video bitrate for given video format.
     * @param mimeType Detected video format.
     * @param bitrate Detected bitrate.
     * @return True if bitrate is within configured bounds for specified format.
     * @since 0.1
     */
    public static boolean checkVideoBitrate(String mimeType, int bitrate) {
        if (bitrate == 0 || mimeType == null || bitrate < 0) {
            return false;
        }
        try {
            return FormatRegistry.VideoFormatLimits.isValidVideoBitrateFor(mimeType, bitrate);
        } catch(IllegalArgumentException e) {
            throw new BitrateValidationException("failed to validate video bitrate", mimeType, e);
        }
    }

    /**
     * Check audio bitrate for given video format.
     * @param mimeType Detected video format.
     * @param bitrate Detected bitrate.
     * @return True if bitrate is within configured bounds for specified format.
     * @since 0.1
     */
    public static boolean checkAudioBitrateForVideo(String mimeType, int bitrate) {
        if (bitrate == 0 || mimeType == null || bitrate < 0) {
            return false;
        }
        try {
            return FormatRegistry.VideoFormatLimits.isValidAudioBitrateFor(mimeType, bitrate);
        } catch(IllegalArgumentException e) {
            throw new BitrateValidationException("failed to validate audio bitrate for video", mimeType, e);
        }
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
     * Check audio sampling rate for given audio format.
     * @param mimeType Detected audio format.
     * @param samplingRate Detected sampling rate.
     * @return True if sampling rate is within configured bounds for specified format.
     * @since 0.1
     */
    public static boolean checkAudioSampleRate(String mimeType, int samplingRate) {
        if (samplingRate == 0 || mimeType == null || samplingRate < 0) {
            return false;
        }
        try {
            return FormatRegistry.AudioFormatLimits.isValidSampleRateFor(mimeType, samplingRate);
        } catch(IllegalArgumentException e) {
            throw new SamplingRateValidationException("failed to validate audio sampling rate", mimeType, e);
        }
    }

    /**
     * Ensures that the specified video codec is allowed.
     * @param mime Detected mime type.
     * @param codec Detected codec.
     * @return True if codec is whitelisted.
     * @since 0.1
     */
    public static boolean checkVideoCodecWhiteList(String mime, String codec) {
        if (mime == null || codec == null) {
            return false;
        }
        try {
            FormatRegistry.AllowedVideoCodecs list = FormatRegistry.AllowedVideoCodecs.fromFormat(mime.toUpperCase());
            if (list == null) {
                throw new UnsupportedFileTypeException("Failed to detect codec", mime, null);
            }
            return list.isValidFormat(codec.toLowerCase());
        } catch(IllegalArgumentException e) {
            throw new UnsupportedFileTypeException("failed to detect codec", mime, e);
        }
    }

    /**
     * Ensures that the specified audio codec within a video file is allowed.
     * @param mime Detected mime type.
     * @param codec Detected codec.
     * @return True if codec is whitelisted.
     * @since 0.1
     */
    public static boolean checkAudioCodecWhiteListForVideo(String mime, String codec) {
        if (mime == null || codec == null) {
            return false;
        }
        try {
            FormatRegistry.AllowedAudioCodecsVideo list = FormatRegistry.AllowedAudioCodecsVideo.fromFormat(mime.toUpperCase());
            if (list == null) {
                throw new UnsupportedFileTypeException("Failed to detect codec", mime+"/"+codec, null);
            }
            return list.isValidFormat(codec.toLowerCase());
        } catch(IllegalArgumentException e) {
            throw new UnsupportedFileTypeException("failed to detect codec", mime+"/"+codec, e);
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
     * Ensures that the video file duration doesn't exceed its configured limit.
     * @param osTargetPath Path to input file.
     * @param config Immutable configuration.
     * @return True if duration is appropriate.
     * @since 0.1
     */
    public static boolean validateVideoDuration(Path osTargetPath, DisarmConfig config) {
        long videoDuration = Utils.getTrackLength(osTargetPath);
        return videoDuration <= config.getVideoMaxDuration();
    }

    /**
     * Ensures that specified text object utilises supported encoding.
     * @param text Input text object.
     * @param state Runtime state.
     * @return True if encoding is appropriate.
     * @since 0.1
     */
    public static boolean validateEncoding(Text text, DisarmState state) {
        byte[] data = state.getTextFile().getBytes();
        if (text.isBom(data)) {
            return true;
        } else if (text.isUTF8(data)) {
            return true;
        } else return text.isASCII(data);
    }

    /**
     * Checks if provided file is readable.
     * @param osTargetPath Path to input file.
     * @return True if file is readable.
     * @since 0.1
     */
    public static boolean isInputReadable(Path osTargetPath) {
        return Files.isReadable(osTargetPath);
    }

    /**
     * Checks if configured output path is writeable.
     * @param osTargetPath Path to output directory.
     * @return True if path is writeable.
     * @since 0.1
     */
    public static boolean isOutputPathWritable(Path osTargetPath) {
        return Files.isWritable(osTargetPath);
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
}

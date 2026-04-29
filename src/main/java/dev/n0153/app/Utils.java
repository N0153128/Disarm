package dev.n0153.app;

import dev.n0153.app.exceptions.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ws.schild.jave.EncoderException;
import ws.schild.jave.MultimediaObject;
import ws.schild.jave.info.MultimediaInfo;
import ws.schild.jave.info.VideoSize;

import javax.sound.sampled.AudioFileFormat;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystemException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * General utilities class, immutable.
 * @since 0.1
 */
public class Utils {
    private static final Logger logger = LogManager.getLogger(Utils.class);

    /**
     * Shortcut method, generates title for a file that is currently in processing.
     * The title is then saved to state and can be accessed with state.getGeneralFileTitle().
     * @param state mutable runtime state.
     * @param logo determines if file in processing is a logo or media file
     * @since 0.1
     */
    public static void getTitle(DisarmState state, boolean logo) {
        LocalDateTime now = LocalDateTime.now();
        String day = String.valueOf(now.getDayOfMonth());
        String month = String.valueOf(now.getMonthValue());
        String year = String.valueOf(now.getYear());
        String hour = String.valueOf(now.getHour());
        String minute = String.valueOf(now.getMinute());
        String second = String.valueOf(now.getSecond());
        String nanoSecond = String.valueOf(now.getNano());
        String milliSecond = String.valueOf(now.getNano() / 1_000_000);
        String objectName = state.getFileType() +"_"+year+"_"+month+"_"+day+
                "_"+hour+"_"+minute+"_"+second+
                "_"+nanoSecond+"_"+milliSecond+"."+ Utils.getFormatFromMime(state.getMime());
        if (logo){
            state.setLogoTitle(objectName);
        } else {
            state.setGeneralFileTitle(objectName);
        }
    }

    /**
     * Renames specified file.
     * @param osTargetFile Path to file.
     * @param newTitle New title.
     * @throws FileRenameException If file isn't supported.
     * @since 0.1
     */
    public static void renameFile(Path osTargetFile, String newTitle) throws FileRenameException {
        try {
            Files.move(osTargetFile, osTargetFile.getParent().resolve(newTitle));
        } catch (IOException e) {
            throw new FileRenameException("failed to rename file",  osTargetFile, e);
        }
    }

    /**
     * Returns file type of given file.
     * @param osTargetFile Path to file
     * @return File type.
     * @throws FileTypeDetectionException If fails to detect file type.
     * @since 0.1
     */
    public static String getFileType(Path osTargetFile) throws FileTypeDetectionException {
        try {
            if (!Files.exists(osTargetFile)) {
                throw new IOException("File doesn't exist");
            }; // skip non-existent paths
            if (!Files.isRegularFile(osTargetFile)) {
                throw new IOException("Not a file");
            }
            String fileType = Files.probeContentType(osTargetFile);
            if (fileType == null) {
                throw new FileTypeDetectionException("failed to detect file type, object is null: ", osTargetFile);
            }
            return fileType.split("/")[0];

        } catch (IOException e) {
            throw new FileTypeDetectionException("failed to detect file type", osTargetFile, e);
        }
    }

    /**
     * Returns an appropriate AudioFileFormat.Type for specified audio file.
     * @param osTargetFilePath Path to file.
     * @return Specific AudioFileFormat.Type
     * @throws AudioTypeDetectionException if specified file isn't an audio file.
     * @since 0.1
     */
    public static AudioFileFormat.Type getAudioType(Path osTargetFilePath) throws AudioTypeDetectionException {
        try {
            String mime = getFormatFromMime(getMimeType(osTargetFilePath));
            if (mime != null) {
                AudioFileFormat.Type type = DisarmConfig.getExtToType().get(mime.toLowerCase());
                if (type != null) {
                    return type;
                }
            } else {
                logger.error("non-audio MIME type detected");
            }
        } catch (IOException e) {
            throw new AudioTypeDetectionException("failed to detect audio type", osTargetFilePath, e );
        }
        // Fallback to WAVE
        logger.debug("Fallback to WAVE");
        return AudioFileFormat.Type.WAVE;
    }

    /**
     * Shortcut method, get mime type of given file.
     * NOTE! This method relies on Files.probeContentType(), which uses platform-specific implementations, which
     * don't provide any content validation whatsoever.
     * Proper magic bytes check coming in version 0.2.
     * @param osTargetFilePath Path to file
     * @return Subtype of a specified file's mime type.
     * @throws MimeTypeDetectionException if fails to detect mime type.
     * @since 0.1
     */
    public static String getMimeType(Path osTargetFilePath) throws MimeTypeDetectionException {
        try {
            String mime = Files.probeContentType(osTargetFilePath);
            return mime.split("/")[1];
        } catch (IOException e) {
            throw new MimeTypeDetectionException("failed to detect mime type", osTargetFilePath, e);
        } catch (NullPointerException e) {
            throw new MimeTypeDetectionException("specified mime type isn't whitelisted", osTargetFilePath, e);
        }
    }

    /**
     * Returns proper format string from mime type.
     * Example: mpeg-4 -> mp4, quicktime -> mov.
     * @param mime Original mime type.
     * @return String format.
     * @since 0.1
     */
    public static String getFormatFromMime(String mime) {
        FormatRegistry.MimeToFormat format = FormatRegistry.MimeToFormat.fromMime(mime);
        if (format == null) {
            throw new ValidationException("Mime doesn't have valid format bind: "+mime);
        } else {
            return format.name().toLowerCase();
        }
    }

    /**
     * Check if specified file is an image file.
     * @param osTargetFilePath Path to file.
     * @return True if file is an image file, false otherwise.
     * @since 0.1
     */
    public static boolean isImage(Path osTargetFilePath) {
        try {
            String fileType = getFileType(osTargetFilePath);
            return Objects.equals(fileType, "image");

        } catch (FileTypeDetectionException e) {
            throw new ValidationException("Specified file is not an image");
        }
    }

    /**
     * Check if specified file is an audio file.
     * @param osTargetFilePath Path to file.
     * @return True if file is an audio file, false otherwise.
     * @since 0.1
     */
    public static boolean isAudio(Path osTargetFilePath) {
        try {
            String fileType = getFileType(osTargetFilePath);
            return Objects.equals(fileType, "audio");
        } catch (FileTypeDetectionException e) {
            throw new ValidationException("Specified file is not an audio");
        }
    }

    /**
     * Check if specified file is a video file.
     * @param osTargetFilePath Path to file.
     * @return True if file is a video file, false otherwise.
     * @since 0.1
     */
    public static boolean isVideo(Path osTargetFilePath) {
        try {
            String fileType = getFileType(osTargetFilePath);
            return Objects.equals(fileType, "video");

        } catch (FileTypeDetectionException e) {
            throw new ValidationException("Specified file is not a video");
        }
    }

    /**
     * Check if specified file is a text file.
     * @param osTargetFilePath Path to file.
     * @return True if file is a text file, false otherwise.
     * @since 0.1
     */
    public static boolean isText(Path osTargetFilePath) {
        try {
            String fileType = getFileType(osTargetFilePath);
            return Objects.equals(fileType, "text");
        } catch (FileTypeDetectionException e) {
            throw new ValidationException("Specified file is not a text");
        }
    }

    /**
     * Shortcut method, delete any specified file.
     * @param osTargetFilePath Path to file.
     * @throws FileDeletionException If file doesn't exist or unable to delete.
     * @since 0.1
     */
    public static void fileDispose(Path osTargetFilePath) throws FileDeletionException {
        try {
            if (!Files.exists(osTargetFilePath)) {
                throw new FileDeletionException("Specified file doesn't exist", osTargetFilePath);
            }
            File file = osTargetFilePath.toFile();
            boolean deleted = Files.deleteIfExists(file.toPath());
            if (!deleted) {
                throw new FileSystemException(osTargetFilePath.toString());
            }
        } catch (IOException e) {
            throw new FileDeletionException("failed to delete file", osTargetFilePath, e);
        }
    }

    /**
     * Returns the size, in bytes of the specified file.
     * @param osFilePath Path to file.
     * @return File length.
     * @since 0.1
     */
    public static long getSize(Path osFilePath) {
        File file = osFilePath.toFile();
        return file.length();
    }

    /**
     * Returns track length for audio and video files.
     * @param osTargetPath Path to file.
     * @return Audio/Video track length as Long
     * @since 0.1
     */
    public static long getTrackLength(Path osTargetPath) {
        try {
            File file = osTargetPath.toFile();
            MultimediaObject media = new MultimediaObject(file);
            MultimediaInfo mediaInfo = media.getInfo();
            return mediaInfo.getDuration();

        } catch (EncoderException e) {
            throw new ValidationException("Incorrect input format for track length");
        }
    }

    /**
     * Returns track length for audio and video files, nicely formatted.
     * @param duration Output of Utils.getTrackLength().
     * @return Audio/Video track length, nicely formatted
     * @since 0.1
     */
    public static String getTrackLengthFormatted(long duration) throws DurationFormattingException{
        if (duration <= -1 || duration == 0) {
            throw new DurationFormattingException("Invalid track length", ""+duration);
        }
        int durationSeconds = (int) duration/1000;
        int hours = (durationSeconds/3600);
        int minutes = (durationSeconds%3600/60);
        int seconds = (durationSeconds%60);
        try {
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        } catch (Exception e) {
            throw new DurationFormattingException("failed to return formatted duration",
                    "\""+hours+":"+minutes+":"+seconds+ "\"", e);
        }
    }

    /**
     * Returns audio/video codec for the specified file.
     * @param osTargetPath Path to file.
     * @param mediaType Detected file type.
     * @return Detected codec string.
     * @since 0.1
     */
    public static String getCodec(Path osTargetPath, String mediaType) throws CodecDetectionException {
        try {
            MultimediaObject media = new MultimediaObject(osTargetPath.toFile());
            MultimediaInfo mediaInfo = media.getInfo();
            String codec = "";
            if (mediaType.isEmpty()) {
                throw new CodecDetectionException("mediaType is empty", osTargetPath);
            }
            if (Objects.equals(mediaType, "audio")) {
                codec = mediaInfo.getAudio().getDecoder();
            } else if (Objects.equals(mediaType, "video")) {
                codec = mediaInfo.getVideo().getDecoder();
            }
            if (codec.contains(" ")) {
                codec = codec.split(" ")[0];
            }
            if ("vorbis".equals(codec)) {
                codec = "libvorbis";
            }
            if ("av1".equals(codec)) {
                codec = "vp8";
            }
            return codec;
        } catch (EncoderException e) {
            throw new CodecDetectionException("Failed to detect codec", osTargetPath, e);
        }
    }

    /**
     * Returns bitrate for specified audio/video file.
     * @param osTargetPath Path to input file.
     * @param mediaType Detected media type.
     * @param config Immutable configuration
     * @return Audio/Video bitrate
     * @throws EncoderException If failed to detect media.
     * @throws IOException If failed to calculate bitrate.
     * @since 0.1
     */
    public static int getBitrate(Path osTargetPath, String mediaType, DisarmConfig config) throws EncoderException, IOException {
        MultimediaObject input = new MultimediaObject(osTargetPath.toFile());
        int bitrate = -1;
        if (Objects.equals(mediaType, "audio")) {
            bitrate = input.getInfo().getAudio().getBitRate();
            if (bitrate <= 0) {
                bitrate = config.getAudioBitrateFallback();
            }
        } else if (Objects.equals(mediaType, "video")) {
            bitrate = input.getInfo().getVideo().getBitRate();
            if (bitrate <= 0) {
                bitrate = calcVideoBitRate(osTargetPath);
            }
        }
        return bitrate;
    }

    /**
     * Returns sampling rate for specified audio track.
     * @param osTargetPath Path to input file.
     * @return Sampling rate.
     * @throws EncoderException If failed to detect media.
     * @since 0.1
     */
    public static int getSamplingRate(Path osTargetPath) throws EncoderException {
        MultimediaObject input = new MultimediaObject(osTargetPath.toFile());
        return input.getInfo().getAudio().getSamplingRate();
    }

    /**
     * Calculates bitrate for video formats that don't specify bitrate in their headers.
     * @param osTargetPath Path to input file.
     * @return Calculated video bitrate.
     * @throws IOException If video is less than 1 second.
     * @throws EncoderException If failed to detect media.
     * @since 0.1
     */
    public static int calcVideoBitRate(Path osTargetPath) throws IOException, EncoderException {
        long fileSizeBytes = Files.size(osTargetPath);
        MultimediaObject source = new MultimediaObject(osTargetPath.toFile());
        long durationMs = source.getInfo().getDuration();
        if (durationMs < 1000) {
            throw new IOException("video is too short");
        }
        long durationSeconds = durationMs / 1000;
        return Math.toIntExact((fileSizeBytes * 8) / durationSeconds);
    }

    /**
     * Returns dimensions (width, height) of a specified video file.
     * @param osTargetPath Path to input file.
     * @return Jave's VideoSize object, containing width and height.
     * @throws EncoderException If failed to detect media.
     * @since 0.1
     */
    public static VideoSize getVideoDimensions(Path osTargetPath) throws EncoderException {
        MultimediaObject source = new MultimediaObject(osTargetPath.toFile());
        return source.getInfo().getVideo().getSize();
    }

    /**
     * Return the number of audio channels for specified media file.
     * @param osTargetPath Path to input file.
     * @return Number of detected audio channels.
     * @throws EncoderException If failed to detect media.
     * @since 0.1
     */
    public static int getAudioChannels(Path osTargetPath) throws EncoderException {
        MultimediaObject source = new MultimediaObject(osTargetPath.toFile());
        return source.getInfo().getAudio().getChannels();
    }

    /**
     * Returns frame rate of a specified video file.
     * @param osTargetPath Path to input file.
     * @return Video frame rate.
     * @throws EncoderException If failed to detect media.
     * @since 0.1
     */
    public static int getVideoFrameRate(Path osTargetPath) throws EncoderException {
        MultimediaObject source = new MultimediaObject(osTargetPath.toFile());
        return (int) source.getInfo().getVideo().getFrameRate();
    }
}

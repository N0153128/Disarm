package dev.n0153.app.plugins;

import dev.n0153.app.exceptions.CodecDetectionException;
import dev.n0153.app.exceptions.DurationFormattingException;
import dev.n0153.app.exceptions.ValidationException;
import ws.schild.jave.EncoderException;
import ws.schild.jave.MultimediaObject;
import ws.schild.jave.info.MultimediaInfo;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class MediaUtils {
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
     * Returns bitrate for specified audio/video file.
     * @param osTargetPath Path to input file.
     * @param mediaType Detected media type.
     * @return Audio/Video bitrate
     * @throws EncoderException If failed to detect media.
     * @throws IOException If failed to calculate bitrate.
     * @since 0.1
     */
    public static int getBitrate(Path osTargetPath, String mediaType) throws EncoderException, IOException {
        MultimediaObject input = new MultimediaObject(osTargetPath.toFile());
        int bitrate = -1;
        if (Objects.equals(mediaType, "audio")) {
            bitrate = input.getInfo().getAudio().getBitRate();
        } else if (Objects.equals(mediaType, "video")) {
            bitrate = input.getInfo().getVideo().getBitRate();
            if (bitrate <= 0) {
                bitrate = calcVideoBitRate(osTargetPath);
            }
        }
        return bitrate;
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
    public static String getTrackLengthFormatted(long duration) throws DurationFormattingException {
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
}

package dev.n0153.app.plugins.AudioPlugin;

import dev.n0153.app.core.exceptions.AudioTypeDetectionException;
import dev.n0153.app.core.exceptions.CodecDetectionException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ws.schild.jave.EncoderException;
import ws.schild.jave.MultimediaObject;
import ws.schild.jave.info.MultimediaInfo;

import javax.sound.sampled.AudioFileFormat;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AudioUtils {
    private static final Logger logger = LogManager.getLogger(AudioUtils.class);

    private static final Map<String, AudioFileFormat.Type> EXT_TO_TYPE = new HashMap<>();

    static {
        EXT_TO_TYPE.put("wav", AudioFileFormat.Type.WAVE);
        EXT_TO_TYPE.put("wave", AudioFileFormat.Type.WAVE);
        EXT_TO_TYPE.put("au", AudioFileFormat.Type.AU);
        EXT_TO_TYPE.put("snd", AudioFileFormat.Type.SND);
        EXT_TO_TYPE.put("aif", AudioFileFormat.Type.AIFF);
        EXT_TO_TYPE.put("aiff", AudioFileFormat.Type.AIFF);
        EXT_TO_TYPE.put("aifc", AudioFileFormat.Type.AIFC);
    }

    public static Map<String, AudioFileFormat.Type> getExtToType() {
        return Collections.unmodifiableMap(EXT_TO_TYPE);
    }

    /**
     * Returns an appropriate AudioFileFormat.Type for specified audio file.
     * @param osTargetFilePath Path to file.
     * @return Specific AudioFileFormat.Type
     * @throws AudioTypeDetectionException if specified file isn't an audio file.
     * @since 0.1
     */
    public static AudioFileFormat.Type getAudioType(Path osTargetFilePath, String format) throws AudioTypeDetectionException {
        if (format != null) {
            AudioFileFormat.Type type = getExtToType().get(format.toLowerCase());
            if (type != null) {
                return type;
            }
        } else {
            logger.error("non-audio MIME type detected");
        }
        // Fallback to WAVE
        logger.debug("Fallback to WAVE");
        return AudioFileFormat.Type.WAVE;
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
            throw new CodecDetectionException("Incorrect input format for track length", osTargetPath);
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
     * @return Audio/Video bitrate
     * @throws EncoderException If failed to detect media.
     * @throws IOException If failed to calculate bitrate.
     * @since 0.1
     */
    public static int getBitrate(Path osTargetPath, String mediaType) throws EncoderException, IOException {
        MultimediaObject input = new MultimediaObject(osTargetPath.toFile());
        int bitrate = -1;
        return input.getInfo().getAudio().getBitRate();
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


}

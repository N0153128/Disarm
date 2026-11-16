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

/**
 * Static audio helpers for the Audio plugin: reading media details through JAVE/FFmpeg
 * (duration, codec, bitrate, sampling rate, channels) and mapping formats to
 * {@code javax.sound.sampled} file types for the native fallback.
 * @since 0.2
 */
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

    /**
     * Returns the format-to-type map used by the native {@code javax.sound.sampled} fallback.
     * It covers {@code wav}, {@code wave}, {@code au}, {@code snd}, {@code aif}, {@code aiff}
     * and {@code aifc}.
     * @return an unmodifiable view of the format-to-{@link AudioFileFormat.Type} map
     * @since 0.2
     */
    public static Map<String, AudioFileFormat.Type> getExtToType() {
        return Collections.unmodifiableMap(EXT_TO_TYPE);
    }

    /**
     * Returns the {@link AudioFileFormat.Type} for a format, used to pick the output container
     * for native re-encoding. The lookup ignores case. If the format is {@code null} or
     * not in the map, it falls back to {@link AudioFileFormat.Type#WAVE}.
     * @param osTargetFilePath path to the input file
     * @param format the input file's format (e.g. {@code "wav"}, {@code "aiff"})
     * @return the matching file type, or {@code WAVE} if there is no match
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
     * Returns the duration of an audio or video file as reported by JAVE/FFmpeg.
     * @param osTargetPath path to the file
     * @return the track length in milliseconds
     * @throws CodecDetectionException if JAVE cannot read the file
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
     * Returns the decoder name of a file's audio or video stream, as reported by JAVE/FFmpeg,
     * in a form ready to pass back as an encoder name. Only the first word of the decoder
     * string is kept; {@code vorbis} becomes {@code libvorbis} and {@code av1} becomes {@code vp8}.
     * @param osTargetPath path to the file
     * @param mediaType {@code "audio"} or {@code "video"}, selecting which stream to read
     * @return the codec name, or an empty string if {@code mediaType} is neither {@code "audio"} nor {@code "video"}
     * @throws CodecDetectionException if {@code mediaType} is empty or JAVE cannot read the file
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
     * Returns the bitrate of a file's audio stream, as reported by JAVE/FFmpeg.
     * @param osTargetPath path to the input file
     * @param mediaType the detected media type
     * @return the audio bitrate in bits per second; JAVE returns {@code -1} if it cannot determine it
     * @throws EncoderException if JAVE cannot read the file
     * @throws IOException if an I/O error occurs
     * @since 0.1
     */
    public static int getBitrate(Path osTargetPath, String mediaType) throws EncoderException, IOException {
        MultimediaObject input = new MultimediaObject(osTargetPath.toFile());
        int bitrate = -1;
        return input.getInfo().getAudio().getBitRate();
    }

    /**
     * Returns the sampling rate of a file's audio stream, as reported by JAVE/FFmpeg.
     * @param osTargetPath path to the input file
     * @return the sampling rate in Hz
     * @throws EncoderException if JAVE cannot read the file
     * @since 0.1
     */
    public static int getSamplingRate(Path osTargetPath) throws EncoderException {
        MultimediaObject input = new MultimediaObject(osTargetPath.toFile());
        return input.getInfo().getAudio().getSamplingRate();
    }

    /**
     * Returns the number of channels in a file's audio stream, as reported by JAVE/FFmpeg.
     * @param osTargetPath path to the input file
     * @return the channel count
     * @throws EncoderException if JAVE cannot read the file
     * @since 0.1
     */
    public static int getAudioChannels(Path osTargetPath) throws EncoderException {
        MultimediaObject source = new MultimediaObject(osTargetPath.toFile());
        return source.getInfo().getAudio().getChannels();
    }


}

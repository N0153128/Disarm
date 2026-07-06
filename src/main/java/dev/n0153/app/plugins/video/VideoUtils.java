package dev.n0153.app.plugins.video;

import dev.n0153.app.exceptions.CodecDetectionException;
import ws.schild.jave.EncoderException;
import ws.schild.jave.MultimediaObject;
import ws.schild.jave.info.MultimediaInfo;
import ws.schild.jave.info.VideoSize;
import java.nio.file.Path;

public class VideoUtils {
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

    public static String getVideoCodec(Path osTargetPath, String swapAV1) throws CodecDetectionException {
        try {
            MultimediaObject media = new MultimediaObject(osTargetPath.toFile());
            MultimediaInfo mediaInfo = media.getInfo();
            String codec = "";
            codec = mediaInfo.getVideo().getDecoder();
            if (codec.contains(" ")) {
                codec = codec.split(" ")[0];
            }
            if ("av1".equals(codec)) {
                codec = swapAV1;
            }
            return codec;
        } catch (EncoderException e) {
            throw new CodecDetectionException("Failed to detect codec", osTargetPath, e);
        }
    }
}

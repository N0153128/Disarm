package dev.n0153.app.plugins.audio;

import dev.n0153.app.exceptions.AudioTypeDetectionException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sound.sampled.AudioFileFormat;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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
}

package dev.n0153.app;

import javax.sound.sampled.AudioFileFormat;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * This is the immutable configurations object. It holds all the configuration values, such as max image dimensions,
 * audio duration limits, size limits per format, etc. Once built, nothing can change it.
 * Every handler reads from it but nobody writes to it.
 * This object follows the Builder design pattern, where the Builder class is an external class.
 * <br /> <br />
 * Usage example: <br />
 * {@code
 * BuilderConfig builder = DisarmConfig.builder(); // create BuilderConfig object.
 * builder.setGeneralOutputPath("output/example/path"); // call a setter to change config value.
 * DisarmConfig config = builder.build(); // Locks the DisarmConfig with an altered field.
 * }
 * @since 0.1
 */
public final class DisarmConfig {

    private final int generalSizeLimit;
    private final int logoSizeLimit; //5MB
    private final boolean keepLogo;
    private final boolean keepImage;
    private final boolean keepInputs;
    private final boolean keepResult;
    private final boolean keepOriginal;
    private final int maxImgSize; // 5MB
    private final int maxTextSize; // 5MB
    private final double imgMaxWidth;
    private final double imgMaxHeight;
    private final int logoMaxWidth;
    private final int logoMaxHeight;
    private final int maxVideoFrameRate;
    private final int maxVideoWidth;
    private final int maxVideoHeight;
    private final int maxCodecLength;
    private final int videoMaxDuration;
    private final int audioMaxDuration; // 300k ms, 5 minutes
    private final int maxAudioChannels;
    private final int targetFileLength;
    private final int mimeLength;
    private final int titleLength;
    private final int generalFileSizeUpperBoundLimit;
    private final int audioBitrateFallback;
    private final int audioSamplingRateFallback;
    private final int videoBitrateFallback;
    private final Path generalOutputPath;

    /**
     * Config class constructor.
     * @param builder BuilderConfig instance to read values from.
     * @since 0.1
     */
    DisarmConfig(BuilderConfig builder) {
        this.maxAudioChannels = builder.getMaxAudioChannels();
        this.generalSizeLimit = builder.getGeneralSizeLimit();
        this.logoSizeLimit = builder.getLogoSizeLimit();
        this.keepLogo = builder.getKeepLogo();
        this.keepImage = builder.getKeepImage();
        this.keepInputs = builder.getKeepInputs();
        this.keepResult = builder.getKeepResult();
        this.keepOriginal = builder.isKeepOriginal();
        this.maxImgSize = builder.getMaxImageSize();
        this.maxTextSize = builder.getMaxTextSize();
        this.imgMaxWidth = builder.getImgMaxWidth();
        this.imgMaxHeight = builder.getImgMaxHeight();
        this.logoMaxWidth = builder.getLogoMaxWidth();
        this.logoMaxHeight = builder.getLogoMaxHeight();
        this.maxVideoFrameRate = builder.getMaxVideoFrameRate();
        this.maxVideoWidth = builder.getMaxVideoWidth();
        this.maxVideoHeight = builder.getMaxVideoHeight();
        this.maxCodecLength = builder.getMaxCodecLength();
        this.audioMaxDuration = builder.getAudioMaxDuration();
        this.videoMaxDuration = builder.getVideoMaxDuration();
        this.targetFileLength = builder.getTargetFileLength();
        this.mimeLength = builder.getMimeLength();
        this.titleLength = builder.getTitleLength();
        this.generalFileSizeUpperBoundLimit = builder.getGeneralFileSizeUpperBoundLimit();
        this.audioBitrateFallback = builder.getAudioBitrateFallback();
        this.audioSamplingRateFallback = builder.getAudioSamplingRateFallback();
        this.videoBitrateFallback = builder.getVideoBitrateFallback();
        this.generalOutputPath = builder.getGeneralOutputPath();
    }

    /**
     * Standard factory method, returns a new instance of BuilderConfig.
     * @return New instance of BuilderConfig.
     * @since 0.1
     */
    public static BuilderConfig builder() {
        return new BuilderConfig();
    }

    private static final Map<String, Integer> IMAGE_SIZE_LIMITS = new HashMap<>();

    static {
        IMAGE_SIZE_LIMITS.put("jpeg", 5_000_000);
        IMAGE_SIZE_LIMITS.put("png", 5_000_000);
        IMAGE_SIZE_LIMITS.put("webp", 5_000_000);
        IMAGE_SIZE_LIMITS.put("logo", 5_000_000);
    }

    public static Map<String, Integer> getImageSizeLimits() {
        return Collections.unmodifiableMap(IMAGE_SIZE_LIMITS);
    }

    private static final Map<String, Integer> TEXT_SIZE_LIMITS = new HashMap<>();

    static {
        TEXT_SIZE_LIMITS.put("text", 5_000_000);
        TEXT_SIZE_LIMITS.put("json", 5_000_000);
        TEXT_SIZE_LIMITS.put("log", 5_000_000);
        TEXT_SIZE_LIMITS.put("plain", 5_000_000);
    }

    public static Map<String, Integer> getTextSizeLimits() {
        return Collections.unmodifiableMap(TEXT_SIZE_LIMITS);
    }

    private static final Map<String, Integer> VIDEO_SIZE_LIMITS = new HashMap<>();

    static {
        VIDEO_SIZE_LIMITS.put("mp4", 5_000_000);
        VIDEO_SIZE_LIMITS.put("matroska", 5_000_000);
        VIDEO_SIZE_LIMITS.put("webm", 5_000_000);
        VIDEO_SIZE_LIMITS.put("mov", 5_000_000);

    }

    public static Map<String, Integer> getVideoSizeLimits() {
        return Collections.unmodifiableMap(VIDEO_SIZE_LIMITS);
    }

    private static final Map<String, Integer> AUDIO_SIZE_LIMITS = new HashMap<>();

    static {
        AUDIO_SIZE_LIMITS.put("mp3", 5_000_000); //5MB
        AUDIO_SIZE_LIMITS.put("ogg", 5_000_000); //5MB
        AUDIO_SIZE_LIMITS.put("flac", 5_000_000); //5MB
        AUDIO_SIZE_LIMITS.put("wav", 5_000_000); //5MB
        AUDIO_SIZE_LIMITS.put("au", 5_000_000); //5MB
        AUDIO_SIZE_LIMITS.put("aiff", 5_000_000); //5MB
    }

    public static Map<String, Integer> getAudioSizeLimits() {
        return Collections.unmodifiableMap(AUDIO_SIZE_LIMITS);
    }

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

    // getters
    public boolean isKeepOriginal() {
        return keepOriginal;
    }

    public int getGeneralFileSizeUpperBoundLimit() {
        return generalFileSizeUpperBoundLimit;
    }

    public int getTitleLength() {
        return titleLength;
    }

    public int getMimeLength() {
        return mimeLength;
    }

    public int getTargetFileLength() {
        return targetFileLength;
    }

    public int getAudioMaxDuration() {
        return audioMaxDuration;
    }

    public int getLogoMaxWidth() {
        return logoMaxWidth;
    }

    public int getLogoMaxHeight() {
        return logoMaxHeight;
    }

    public double getImgMaxWidth() {
        return imgMaxWidth;
    }

    public double getImgMaxHeight() {
        return imgMaxHeight;
    }

    public int getMaxTextSize() {
        return maxTextSize;
    }

    public int getMaxImageSize() {
        return maxImgSize;
    }

    public boolean getKeepResult() {
        return keepResult;
    }

    public boolean getKeepInputs() {
        return keepInputs;
    }

    public boolean getKeepImage() {
        return keepImage;
    }

    public boolean getKeepLogo() {
        return keepLogo;
    }

    public int getLogoSizeLimit() {
        return logoSizeLimit;
    }

    public int getGeneralSizeLimit() {
        return  generalSizeLimit;
    }

    public int getVideoMaxDuration() {
        return videoMaxDuration;
    }

    public int getMaxAudioChannels() {
        return maxAudioChannels;
    }

    public int getMaxVideoFrameRate() {
        return maxVideoFrameRate;
    }

    public int getMaxVideoWidth() {
        return maxVideoWidth;
    }

    public int getMaxVideoHeight() {
        return maxVideoHeight;
    }

    public int getMaxCodecLength() {
        return maxCodecLength;
    }

    public int getAudioBitrateFallback() {
        return audioBitrateFallback;
    }

    public int getAudioSamplingRateFallback() {
        return audioSamplingRateFallback;
    }

    public int getVideoBitrateFallback() {
        return videoBitrateFallback;
    }

    public Path getGeneralOutputPath() {
        return generalOutputPath;
    }
}

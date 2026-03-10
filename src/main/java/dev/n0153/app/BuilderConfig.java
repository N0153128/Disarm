package dev.n0153.app;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * This class is an external builder for DisarmConfig. It allows a user to customise values before
 * locking them in with .build() call. This class is pre-configured to utilise pre-set, sane values for
 * out-of-the-box usage.
 * @since 0.1
 */
public class BuilderConfig {
    private int generalSizeLimit = 0;
    private int logoSizeLimit = 5_000_000; //5MB
    private boolean keepLogo = true;
    private boolean keepImage = true;
    private boolean keepInputs = false;
    private boolean keepResult = true;
    private boolean keepOriginal = true;
    private int maxImgSize = 5_000_000; // 5MB
    private int maxTextSize = 5_000_000; // 5MB
    private double imgMaxWidth = 512;
    private double imgMaxHeight = 512;
    private int logoMaxWidth = 50;
    private int logoMaxHeight = 50;
    private int maxVideoFrameRate = 60;
    private int maxVideoWidth = 1920;
    private int maxVideoHeight = 1080;
    private int maxCodecLength = 15;
    private int videoMaxDuration = 300_000;
    private int audioMaxDuration = 300_000; // 300k ms, 5 minutes
    private int maxAudioChannels = 2;
    private int targetFileLength = 150;
    private int mimeLength = 10;
    private int titleLength = 50;
    private int generalFileSizeUpperBoundLimit = 10_000_000;
    private int audioBitrateFallback = 320000;
    private int audioSamplingRateFallback = 48000;
    private int videoBitrateFallback = 8_000_000;
    private Path generalOutputPath = Paths.get("resources/output");

    /**
     * Locks an instance with configured parameters.
     * @return Immutable DisarmConfig instance
     * @since 0.1
     */
    public DisarmConfig build() {
        return new DisarmConfig(this);
    }

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

    // setters

    public BuilderConfig setKeepOriginal(boolean keepOriginal) {
        this.keepOriginal = keepOriginal;
        return this;
    }

    public BuilderConfig setVideoMaxDuration(int videoMaxDuration) {
        if (videoMaxDuration < 0) {
            throw new IllegalArgumentException("Video duration cannot be less than zero");
        }
        if (videoMaxDuration == 0) {
            throw new IllegalArgumentException("Video duration cannot be zero");
        }
        this.videoMaxDuration = videoMaxDuration;
        return this;
    }

    private BuilderConfig setMaxAudioChannels(int maxAudioChannels) {
        if (maxAudioChannels < 0) {
            throw new IllegalArgumentException("Audio channels cannot be less than zero");
        }
        if (maxAudioChannels == 0) {
            throw new IllegalArgumentException("Audio channels cannot be zero");
        }
        if (maxAudioChannels > 2) {
            throw new IllegalArgumentException("Unsupported amount of audio channels");
        }
        this.maxAudioChannels = maxAudioChannels;
        return this;
    }

    private BuilderConfig setMaxVideoFrameRate(int maxVideoFrameRate) {
        if (maxVideoFrameRate < 0) {
            throw new IllegalArgumentException("Max video frame rate cannot be less than zero");
        }
        if (maxVideoFrameRate == 0) {
            throw new IllegalArgumentException("Max video frame rate cannot be zero");
        }
        this.maxVideoFrameRate = maxVideoFrameRate;
        return this;
    }

    private BuilderConfig setMaxVideoWidth(int maxVideoWidth) {
        if (maxVideoWidth < 0) {
            throw new IllegalArgumentException("Max video width cannot be less than zero");
        }
        if (maxVideoWidth == 0) {
            throw new IllegalArgumentException("Max video width cannot be zero");
        }
        this.maxVideoWidth = maxVideoWidth;
        return this;
    }

    private BuilderConfig setMaxVideoHeight(int maxVideoHeight) {
        if (maxVideoHeight < 0) {
            throw new IllegalArgumentException("Max video height cannot be less than zero");
        }
        if (maxVideoHeight == 0) {
            throw new IllegalArgumentException("Max video height cannot be zero");
        }
        this.maxVideoHeight = maxVideoHeight;
        return this;
    }

    private BuilderConfig setMaxCodecLength(int maxCodecLength) {
        if (maxCodecLength < 0) {
            throw new IllegalArgumentException("Max codec length cannot be less than zero");
        }
        if (maxCodecLength == 0) {
            throw new IllegalArgumentException("Max codec length cannot be zero");
        }
        this.maxCodecLength = maxCodecLength;
        return this;
    }

    private BuilderConfig setAudioBitrateFallback(int audioBitrateFallback) {
        if (audioBitrateFallback == 0) {
            throw new IllegalArgumentException("Audio bitrate fallback cannot be zero");
        }
        if (audioBitrateFallback < 0) {
            throw new IllegalArgumentException("Audio bitrate fallback cannot be less than zero");
        }
        this.audioBitrateFallback = audioBitrateFallback;
        return this;
    }

    private BuilderConfig setAudioSamplingRateFallback(int audioSamplingRateFallback) {
        if (audioSamplingRateFallback == 0) {
            throw new IllegalArgumentException("Audio sampling rate fallback cannot be zero");
        }
        if (audioSamplingRateFallback < 0) {
            throw new IllegalArgumentException("Audio sampling rate fallback cannot be less than zero");
        }
        this.audioSamplingRateFallback = audioSamplingRateFallback;
        return this;
    }

    private BuilderConfig setVideoBitrateFallback(int videoBitrateFallback) {
        if (videoBitrateFallback == 0) {
            throw new IllegalArgumentException("Video bitrate fallback cannot be zero");
        }
        if (videoBitrateFallback < 0) {
            throw new IllegalArgumentException("Video bitrate fallback cannot be less than zero");
        }
        this.videoBitrateFallback = videoBitrateFallback;
        return this;
    }

    public BuilderConfig setGeneralOutputPath(Path generalOutputPath) {
        if (generalOutputPath == null) {
            throw new IllegalArgumentException("General output path cannot be empty");
        }
        this.generalOutputPath = generalOutputPath;
        return this;
    }

    public BuilderConfig setGeneralSizeLimit(int newSizeLimit) {
        if (newSizeLimit < 0) {
            throw new IllegalArgumentException("Size limit cannot be less than zero");
        }
        if (newSizeLimit > getGeneralFileSizeUpperBoundLimit()) {
            throw new IllegalArgumentException("Size limit cannot exceed upper bound");
        }
        generalSizeLimit = newSizeLimit;
        return this;
    }

    public BuilderConfig setLogoSizeLimit(int newLogoSizeLimit) {
        if (newLogoSizeLimit < 0) {
            throw new IllegalArgumentException("Logo size limit cannot be less than zero");
        }
        if (newLogoSizeLimit > generalFileSizeUpperBoundLimit) {
            throw new IllegalArgumentException("Logo size limit cannot exceed upper bound");
        }
        logoSizeLimit = newLogoSizeLimit;
        return this;
    }

    public BuilderConfig setKeepLogo(boolean newKeepLogo) {
        keepLogo = newKeepLogo;
        return this;
    }

    public BuilderConfig setKeepImage(boolean newKeepImage) {
        keepImage = newKeepImage;
        return this;
    }

    public BuilderConfig setKeepInputs(boolean newKeepInputs) {
        keepInputs = newKeepInputs;
        return this;
    }

    public BuilderConfig setKeepResult(boolean newKeepResults) {
        keepResult = newKeepResults;
        return this;
    }

    public BuilderConfig setMaxImgSize(int newMaxImgSize) {
        if (newMaxImgSize < 0) {
            throw new IllegalArgumentException("Max image size cannot be less than zero");
        }
        if (newMaxImgSize > getGeneralFileSizeUpperBoundLimit()) {
            throw new IllegalArgumentException("Max image size limit cannot exceed upper bound");
        }
        maxImgSize = newMaxImgSize;
        return this;
    }

    public BuilderConfig setMaxTextSize(int newMaxTextSize) {
        if (newMaxTextSize < 0) {
            throw new IllegalArgumentException("Max text size cannot be less than zero");
        }
        if (newMaxTextSize > getGeneralFileSizeUpperBoundLimit()) {
            throw new IllegalArgumentException("Max text size limit cannot exceed upper bound");
        }
        maxTextSize = newMaxTextSize;
        return this;
    }

    public BuilderConfig setImgMaxWidth(double newImgMaxWidth) {
        if (newImgMaxWidth < 0) {
            throw new IllegalArgumentException("Image max width cannot be less than zero");
        }
        imgMaxWidth = newImgMaxWidth;
        return this;
    }

    public BuilderConfig setImgMaxHeight(double newImageMaxHeight) {
        if (newImageMaxHeight < 0) {
            throw new IllegalArgumentException("Image max height cannot be less than zero");
        }
        imgMaxHeight = newImageMaxHeight;
        return this;
    }

    public BuilderConfig setLogoMaxWidth(int newLogoMaxWidth) {
        if (newLogoMaxWidth < 0) {
            throw new IllegalArgumentException("Logo max width cannot be less than zero");
        }
        logoMaxWidth = newLogoMaxWidth;
        return this;
    }

    public BuilderConfig setLogoMaxHeight(int newLogoMaxHeight) {
        if (newLogoMaxHeight < 0) {
            throw new IllegalArgumentException("Logo max height cannot be less than zero");
        }
        logoMaxHeight = newLogoMaxHeight;
        return this;
    }

    public BuilderConfig setAudioMaxDuration(int newAudioMaxDuration) {
        if (newAudioMaxDuration < 0) {
            throw new IllegalArgumentException("Audio max duration cannot be less than zero");
        }
        audioMaxDuration = newAudioMaxDuration;
        return this;
    }

    private BuilderConfig setTargetFileLength(int newTargetFileLength) {
        if (newTargetFileLength < 0) {
            throw new IllegalArgumentException("Target file length cannot be less than zero");
        }
        targetFileLength = newTargetFileLength;
        return this;
    }

    private BuilderConfig setMimeLength(int newMimeLength) {
        if (newMimeLength < 0) {
            throw new IllegalArgumentException("Mime length cannot be less than zero");
        }
        mimeLength = newMimeLength;
        return this;
    }

    private BuilderConfig setTitleLength(int newTitleLength) {
        if (newTitleLength < 0) {
            throw new IllegalArgumentException("Mime length cannot be less than zero");
        }
        titleLength = newTitleLength;
        return this;
    }

    private BuilderConfig setUpperBoundLimit(int newUpperBoundLimit) {
        if (newUpperBoundLimit < 0) {
            throw new IllegalArgumentException("Upper bound limit cannot be less than zero");
        }
        generalFileSizeUpperBoundLimit = newUpperBoundLimit;
        return this;
    }
}

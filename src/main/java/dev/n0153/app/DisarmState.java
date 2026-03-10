package dev.n0153.app;

import org.opencv.core.Mat;
import ws.schild.jave.info.VideoSize;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

/**
 * This is a mutable runtime state object that holds all the data about the file currently being processed.
 * It gets populated during App.prepData() call and then passed around to media processors, so they all share the
 * same runtime context for the current file.
 * Data is stored in private fields, which can be accessed by public getters and setters.
 * Every piece of data coming in goes through a series of sanity checks.
 * @since 0.1
 */
public class DisarmState {

    private final DisarmConfig config;
    public DisarmState(DisarmConfig config) {
        this.config = config;
    }

    private Mat image;
    private Mat logo;
    private int x = 0;
    private int y = 0;
    private String mime;
    private Path osTargetFile;
    private String fileType;
    private Path osLogoTargetFile;
    private String logoTitle;
    private String generalFileTitle;
    private String textFile;
    private String audioCodec = "aac";
    private String videoCodec = "h264";
    private int videoBitrate = 128000;
    private int videoFrameRate = 60;
    private int audioBitrate = 128000;
    private int audioChannels = 2;
    private int audioSamplingRate = 44100;
    private double transparency = 0.5;
    private VideoSize videoSize = new VideoSize(1920, 1080);


    public boolean hasImage() {
        return image != null && !image.empty();
    }

    public boolean hasLogo() {
        return logo != null && !logo.empty();
    }

    // getters

    public double getTransparency() {
        return transparency;
    }

    public int getAudioBitrate() {
        return audioBitrate;
    }

    public int getAudioChannels() {
        return audioChannels;
    }

    public int getAudioSamplingRate() {
        return audioSamplingRate;
    }

    public String getAudioCodec() {
        return audioCodec;
    }

    public String getVideoCodec() {
        return videoCodec;
    }

    public int getVideoBitrate() {
        return videoBitrate;
    }

    public int getVideoFrameRate() {
        return videoFrameRate;
    }

    public VideoSize getVideoSize() {
        return videoSize;
    }

    public Mat getImage() {
        if (image == null) {
            throw new IllegalStateException("image object is null");
        }
        if (image.empty()) {
            throw new IllegalStateException("image object is empty");
        }
        return image;
    }

    public Mat getImageOrNull() {
        return (image != null && !image.empty()) ? image.clone() : null;
    }

    public Mat getLogo() {
        if (logo == null) {
            throw new IllegalStateException("logo object is null");
        }
        if (logo.empty()) {
            throw new IllegalStateException("logo object is empty");
        }
        return logo;
    }

    public Mat getLogoOrNull() {
        return (logo != null && !logo.empty()) ? logo.clone() : null;
    }

    public int getImgX() {
        if (x < 0) {
            throw new IllegalStateException("x is below zero"); /* many error messages lack proper context. this is done
            deliberately, so that the error messages won't aid potentially malicious users who might try to hunt for critical bugs.
            */
        }
        return x;
    }

    public int getImgY() {
        if (y < 0) {
            throw new IllegalStateException("y is below zero");
        }
        return y;
    }

    public String getMime() {
        if (mime == null) {
            throw new IllegalStateException("mime is null");
        }
        if (mime.isEmpty()) {
            throw new IllegalStateException("mime is empty");
        }
        return mime.toLowerCase().trim();
    }

    public Path getOsTargetFile() {
        if (osTargetFile == null) {
            throw new IllegalStateException("target file name is null");
        }
        if (osTargetFile.toString().trim().isEmpty()) {
            throw new IllegalStateException("target file name is empty");
        }
        return osTargetFile.normalize();
    }

    public String getFileType() {
        if (fileType == null) {
            throw new IllegalStateException("file type name is null");
        }
        if (fileType.isEmpty()) {
            throw new IllegalStateException("file type name is empty");
        }
        return fileType;
    }

    public Path getOsLogoTargetFile() {
        if (osLogoTargetFile == null) {
            throw new IllegalStateException("logo target file name is null");
        }
        if (osLogoTargetFile.toString().trim().isEmpty()) {
            throw new IllegalStateException("logo target file name is empty");
        }
        return osLogoTargetFile.normalize();
    }

    public String getGeneralFileTitle() {
        if (generalFileTitle == null) {
            throw new IllegalStateException("general file name is null");
        }
        if (generalFileTitle.isEmpty()) {
            throw new IllegalStateException("general file name is empty");
        }
        return generalFileTitle;
    }

    public String getTextFile() {
        if (textFile == null) {
            throw new IllegalStateException("text file is null");
        }
        if (textFile.isEmpty()) {
            throw new IllegalStateException("text file is empty");
        }
        return textFile;
    }

    public String getLogoTitle() {
        if (logoTitle == null) {
            throw new IllegalStateException("logo file name is null");
        }
        if (logoTitle.isEmpty()) {
            throw new IllegalStateException("logo file name is empty");
        }
        return logoTitle;
    }

    // setters

    public void setTransparency(double newTransparency) {
        if (newTransparency < 0) {
            throw new IllegalArgumentException("Transparency parameter cannot be less than zero");
        }
        if (newTransparency > 1) {
            throw new IllegalArgumentException("Transparency parameter cannot be greater than 1");
        }
        transparency = newTransparency;
    }

    public void setAudioBitrate(int audioBitrate) {
        if (audioBitrate == 0) {
            throw new IllegalArgumentException("Bitrate cannot be zero");
        }
        if (audioBitrate < 0) {
            this.audioBitrate = config.getAudioBitrateFallback();
            return;
        }
        this.audioBitrate = audioBitrate;
    }

    public void setAudioChannels(int audioChannels) {
        if (audioChannels == 0) {
            throw new IllegalArgumentException("Channels amount cannot be zero");
        }
        if (audioChannels < 0) {
            throw new IllegalArgumentException("Channels amount cannot be less than zero");
        }
        if (audioChannels > config.getMaxAudioChannels()) {
            throw new IllegalArgumentException("Channels amount cannot exceed channels limit");
        }
        this.audioChannels = audioChannels;
    }

    public void setAudioSamplingRate(int audioSamplingRate) {
        if (audioSamplingRate == 0) {
            throw new IllegalArgumentException("Audio sample rate cannot be zero");
        }
        if (audioSamplingRate < 0) {
            throw new IllegalArgumentException("Audio sample rate cannot be less than zero");
        }
        this.audioSamplingRate = audioSamplingRate;
    }

    public void setAudioCodec(String audioCodec) {
        if (audioCodec == null) {
            throw new IllegalArgumentException("Audio codec cannot be null");
        }
        if (audioCodec.isEmpty()) {
            throw new IllegalArgumentException("Audio codec cannot be empty");
        }
        if (audioCodec.length() > config.getMaxCodecLength()) {
            throw new IllegalArgumentException("Audio codec is too long");
        }
        this.audioCodec = audioCodec;
    }

    public void setVideoCodec(String videoCodec) {
        if (videoCodec == null) {
            throw new IllegalArgumentException("Video codec cannot be null");
        }
        if (videoCodec.isEmpty()) {
            throw new IllegalArgumentException("Video codec cannot be empty");
        }
        if (videoCodec.length() > config.getMaxCodecLength()) {
            throw new IllegalArgumentException("Video codec is too long");
        }
        this.videoCodec = videoCodec;
    }

    public void setVideoBitrate(int videoBitrate) {
        if (videoBitrate < 0) {
            throw new IllegalArgumentException("Video bitrate cannot be less than zero");
        }
        if (videoBitrate == 0) {
            throw new IllegalArgumentException("Video bitrate cannot be zero");
        }
        this.videoBitrate = videoBitrate;
    }

    public void setVideoFrameRate(int videoFrameRate) {
        if (videoFrameRate < 0) {
            throw new IllegalArgumentException("Video frame rate cannot be less than zero");
        }
        if (videoFrameRate == 0) {
            throw new IllegalArgumentException("Video frame rate cannot be zero");
        }
        if (videoFrameRate > config.getMaxVideoFrameRate()) {
            throw new IllegalArgumentException("Video frame rate cannot exceed the limit");
        }
        this.videoFrameRate = videoFrameRate;
    }

    public void setVideoSize(VideoSize videoSize) {
        if (videoSize == null) {
            throw new IllegalArgumentException("Video size cannot be null");
        }
        if (videoSize.getWidth() < 0) {
            throw new IllegalArgumentException("Video width cannot be less than zero");
        }
        if (videoSize.getHeight() < 0) {
            throw new IllegalArgumentException("Video height cannot be less than zero");
        }
        if (videoSize.getWidth() == 0) {
            throw new IllegalArgumentException("Video width cannot be zero");
        }
        if (videoSize.getHeight() == 0) {
            throw new IllegalArgumentException("Video height cannot be zero");
        }
        if (videoSize.getWidth() > config.getMaxVideoWidth()) {
            throw new IllegalArgumentException("Video width cannot exceed the limit");
        }
        if (videoSize.getHeight() > config.getMaxVideoHeight()) {
            throw new IllegalArgumentException("Video height cannot exceed the limit");
        }
        this.videoSize = videoSize;
    }

    public void setImage(Mat newImage) {
        if (newImage == null) {
            throw new IllegalArgumentException("Image cannot be null");
        }
        if (newImage.empty()) {
            throw new IllegalArgumentException("Empty image passed");
        }
        if (image != null) {
            image.release();
        }
        image = newImage.clone();
    }

    public void setLogo(Mat newLogo) {
        if (newLogo == null) {
            throw new IllegalArgumentException("Logo cannot be null");
        }
        if (newLogo.empty()) {
            throw new IllegalArgumentException("Empty logo passed");
        }
        if (logo != null) {
            logo.release();
        }
        logo = newLogo.clone();
    }

    public void setImgX(int newX) {
        if (newX < 0) {
            throw new IllegalArgumentException("X value cannot be lower than 0");
        }
        if (image != null && !image.empty() && newX >= image.width()) {
            throw new IllegalArgumentException("X coordinate exceeds image width");
        }
        x = newX;
    }

    public void setImgY(int newY) {
        if (newY < 0) {
            throw new IllegalArgumentException("Y value cannot be lower than 0");
        }
        if (image != null && !image.empty() && newY >= image.height()) {
            throw new IllegalArgumentException("Y coordinate exceeds image height");
        }
        y = newY;
    }

    public void setMime(String newMime) {
        if (newMime == null) {
            throw new IllegalArgumentException("Mime cannot be null");
        }
        if (newMime.isEmpty()) {
            throw new IllegalArgumentException("Mime type cannot be empty");
        }
        if (newMime.length() > config.getMimeLength()) { // this method only checks what comes after the "/", therefore the character count is restrictive.
            throw new IllegalArgumentException("Mime type cannot exceed mime length limit");
        }
        mime = newMime.toLowerCase().trim();
    }

    public void setOsTargetFile(Path newOsTargetFile) {
        if (newOsTargetFile == null) {
            throw new IllegalArgumentException("Target file name cannot be null");
        }
        if (newOsTargetFile.toString().trim().isEmpty()) {
            throw new IllegalArgumentException("File path cannot be empty");
        }
        if (newOsTargetFile.toString().length() > config.getTargetFileLength()) {
            throw new IllegalArgumentException("Target file name cannot exceed target file length limit");
        }
        if (!Validator.validatePath(newOsTargetFile)) {
            throw new IllegalArgumentException("path validation failed");
        }
        osTargetFile = Paths.get(newOsTargetFile.toString().trim()).normalize();
    }

    public void setFileType(String newFileType) {
        if (newFileType == null) {
            throw new IllegalArgumentException("File type cannot be null");
        }
        if (newFileType.isEmpty()) {
            throw new IllegalArgumentException("File type cannot be empty");
        }
        if (newFileType.length() > 10) {
            throw new IllegalArgumentException("File type is too long: "+newFileType);
        }
        try {
            FormatRegistry.AllowList.valueOf(newFileType.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown file type detected");
        }
        fileType = newFileType.toLowerCase().trim();
    }

    public void setOsLogoTargetFile(Path newOsLogoTargetFile) {
        if (newOsLogoTargetFile == null) {
            throw new IllegalArgumentException("Logo target file name cannot be null");
        }
        if (newOsLogoTargetFile.toString().trim().isEmpty()) {
            throw new IllegalArgumentException("Logo file path cannot be empty");
        }
        if (newOsLogoTargetFile.toString().length() > config.getTargetFileLength()) {
            throw new IllegalArgumentException("Logo target file name cannot exceed target file length limit");
        }
        if (!Validator.validatePath(newOsLogoTargetFile)) {
            throw new IllegalArgumentException("Logo path validation failed");
        }
        osLogoTargetFile = newOsLogoTargetFile.normalize();
    }

    public void setTextFile(String newTextFile) {
        if (newTextFile == null) {
            throw new IllegalArgumentException("Text file cannot be null");
        }
        if (newTextFile.isEmpty()) {
            throw new IllegalArgumentException("Text file cannot be empty");
        }
        int sizeInBytes = newTextFile.getBytes(StandardCharsets.UTF_8).length;
        int limit = DisarmConfig.getTextSizeLimits().getOrDefault("text", config.getGeneralSizeLimit());
        if (sizeInBytes > limit) {
            throw new IllegalArgumentException("Text file size cannot exceed the text size limit");
        }
        textFile = newTextFile;
    }

    public void setGeneralFileTitle(String newGeneralFileTitle) {
        if (newGeneralFileTitle == null) {
            throw new IllegalArgumentException("General file name cannot be null");
        }
        if (newGeneralFileTitle.isEmpty()) {
            throw new IllegalArgumentException("General file name cannot be empty");
        }
        if (newGeneralFileTitle.length() > config.getTitleLength()) {
            throw new IllegalArgumentException("General file name cannot exceed title length limit");
        }
        generalFileTitle = newGeneralFileTitle; // no ".trim()" normalization since the only source of "title" is trusted Utils.getTitle() method.
    }

    public void setLogoTitle(String newLogoTitle) {
        if (newLogoTitle == null) {
            throw new IllegalArgumentException("Logo file name cannot be null");
        }
        if (newLogoTitle.isEmpty()) {
            throw new IllegalArgumentException("Logo file name cannot be empty");
        }
        if (newLogoTitle.length() > config.getTitleLength()) {
            throw new IllegalArgumentException("Logo file name cannot exceed title length limit");
        }
        logoTitle = newLogoTitle; // no ".trim()" normalization since the only source of "title" is trusted Utils.getTitle() method.
    }
}

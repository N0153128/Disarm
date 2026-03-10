package dev.n0153.app;

import dev.n0153.app.exceptions.*;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ws.schild.jave.EncoderException;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.time.Duration;
import java.time.Instant;


/**
 * Top-level orchestrator class that combines validation, metadata extraction and sanitisation into
 * a single fileDisarm() call. This is the main entry point.
 * @since 0.1
 */
public class App {
    private final DisarmConfig config;
    private final DisarmState state;

    /**
     * Public constructor for App class.
     * @param state Mutable runtime state.
     * @param config Immutable configuration.
     * @since 0.1
     */
    public App(DisarmState state, DisarmConfig config) {
        this.state = state;
        this.config = config;
    }
    private static final Logger logger = LogManager.getLogger(App.class);

    /**
     * Text sanitisation handler.
     * @param osTextPath Path to input file.
     * @throws IOException If failed to read file.
     * @since 0.1
     */
    public void disarmText(Path osTextPath) throws IOException {
        logger.info("Disarming text at {}", osTextPath);
        Text textInstance = new Text(state, config);
        byte[] text = Files.readAllBytes(osTextPath);
        String stringText = new String(text, StandardCharsets.UTF_8);
        state.setTextFile(stringText);

        if (!Validator.validateEncoding(textInstance, state)) {
            throw new ValidationException("Failed to validate text encoding");
        }
        textInstance.escapeHTML();
        textInstance.normalizeUnicode();
        textInstance.stripPatterns();
        textInstance.saveTextData();
        logger.info("Text disarmed successfully");
    }

    /**
     * Logo sanitisation handler.
     * @param osLogoPath Path to input file.
     * @throws MimeTypeDetectionException If failed to detect mime type.
     * @since 0.1
     */
    public void disarmLogo(Path osLogoPath) throws MimeTypeDetectionException {
        logger.info("Disarming logo at {}", osLogoPath);
        try {
            state.setLogo(Imgcodecs.imread(osLogoPath.toString(), Imgcodecs.IMREAD_UNCHANGED));
            String logoMime = Utils.getMimeType(osLogoPath);
            state.setMime(logoMime);
            Utils.getTitle(state, true);
            scaleLogo();
        } catch (MimeTypeDetectionException e) {
            logger.error("Failed to disarm logo");
            throw new MimeTypeDetectionException("failed to detect mime type", osLogoPath, e);
        }
        logger.info("Logo disarmed successfully");
    }

    /**
     * Image sanitisation handler.
     * @param osTargetPath Path to input file.
     * @param logoRequired If true, the method will attempt to appy watermark after sanitisation.
     * @throws ImageProcessingException If failed to process an image file.
     * @since 0.1
     */
    public void disarmImage(Path osTargetPath, boolean logoRequired) throws ImageProcessingException {
        logger.info("Disarming image at {}", osTargetPath);
        if (!Validator.ensureSizeLimit(osTargetPath, config, state)) {
            throw new ValidationException("File size exceeded");
        }
        if (!Validator.checkMimeWhiteList(state.getFileType(), state.getMime())) {
            throw new WhiteListValidationException("White List check failed", osTargetPath);
        }
        Image image = new Image(state, config);
        state.setImage(Imgcodecs.imread(osTargetPath.toString()));
        image.scaleImageToScaleFactor(state.getImage());
        logger.debug("is logo present? {}", state.hasLogo());
        if (logoRequired) {
            logger.debug("Applying watermark");
            image.applyWatermarkAtRandomPosition();
        }
        logger.info("Image disarmed successfully");
    }

    /**
     * Video sanitisation handler.
     * @param osTargetPath Path to input file.
     * @since 0.1
     */
    public void disarmVideo(Path osTargetPath) {
        logger.info("Disarming video at {}", osTargetPath);
        if (!Validator.ensureSizeLimit(osTargetPath, config, state)) {
            throw new ValidationException("File size exceeded");
        }
        if (!Validator.checkMimeWhiteList(state.getFileType(), state.getMime())) {
            throw new WhiteListValidationException("White List check failed", osTargetPath);
        }
        if (!Validator.checkVideoBitrate(state.getMime(), state.getVideoBitrate())) {
            throw new ValidationException("Video bitrate limitations not met");
        }
        if (!Validator.checkAudioBitrateForVideo(state.getMime(), state.getAudioBitrate())) {
            throw new ValidationException("Audio bitrate for video limitations not met");
        }
        if (!Validator.validateVideoDuration(osTargetPath, config)) {
            throw new ValidationException("video track duration exceeded");
        }
        if (!Validator.checkAudioSampleRateForVideo(state.getMime(), state.getAudioSamplingRate())) {
            throw new ValidationException("Audio track sampling rate is invalid");
        }
        if (!Validator.checkVideoCodecWhiteList(state.getMime(), state.getVideoCodec())) {
            throw new WhiteListValidationException("Video codec white list check failed", osTargetPath);
        }
        if (!Validator.checkAudioCodecWhiteListForVideo(state.getMime(), state.getAudioCodec())) {
            throw new WhiteListValidationException("Audio codec for video white list check failed", osTargetPath);
        }
        Video video = new Video(state, config);
        try {
            video.reEncodeVideo(osTargetPath, state.getMime());
        } catch (VideoProcessingException e) {
            logger.info("failed to process video");
            throw new VideoProcessingException("Failed to process video", osTargetPath, e);
        }
    }

    /**
     * Audio sanitisation handler.
     * @param osTargetPath Path to input file.
     * @since 0.1
     */
    public void disarmAudio(Path osTargetPath) {
        logger.info("Disarming audio at {}", osTargetPath);
        if (!Validator.ensureSizeLimit(osTargetPath, config, state)) {
            throw new ValidationException("File size exceeded");
        }
        if (!Validator.checkMimeWhiteList(state.getFileType(), state.getMime())) {
            throw new WhiteListValidationException("White List check failed", osTargetPath);
        }
        if (!Validator.checkAudioBitrate(state.getMime(), state.getAudioBitrate())) {
            throw new ValidationException("Audio bitrate limitations not met");
        }
        if (!Validator.validateAudioDuration(osTargetPath, config)) {
            throw new ValidationException("Audio track duration exceeded");
        }
        if (!Validator.checkAudioSampleRate(state.getMime(), state.getAudioSamplingRate())) {
            throw new ValidationException("Audio track sampling rate is invalid");
        }
        if (!Validator.checkAudioCodecWhiteList(state.getMime(), state.getAudioCodec())) {
            throw new WhiteListValidationException("Audio codec white list check failed", osTargetPath);
        }
        Audio audio = new Audio(state, config);
        try {
            long trackLength = Utils.getTrackLength(osTargetPath);
            if (trackLength <= config.getAudioMaxDuration()) {
                audio.reEncode(osTargetPath, state.getMime());
            }
        } catch (AudioProcessingException e) {
            if (Validator.checkMimeWhiteList(state.getFileType(), state.getMime())) {
                logger.debug("An error occurred, attempting native re-encoding");
                disarmAudioNative(osTargetPath, audio);
            } else {
                logger.error("Failed to disarm audio");
                throw new AudioProcessingException("failed to disarm audio", osTargetPath, e);
            }
        }
        logger.info("Audio disarmed successfully");
    }

    /**
     * Audio sanitisation handler - native implementation.
     * @param osTargetPath Path to input file.
     * @param audio Audio processor class.
     * @since 0.1
     */
    public void disarmAudioNative(Path osTargetPath, Audio audio) {
        logger.info("Disarming audio - native approach");
        if (!Validator.ensureSizeLimit(osTargetPath, config, state)) {
            throw new ValidationException("File size exceeded");
        }
        if (!Validator.checkMimeWhiteList(state.getFileType(), state.getMime())) {
            throw new WhiteListValidationException("White List check failed", osTargetPath);
        }
        if (!Validator.checkAudioBitrate(state.getMime(), state.getAudioBitrate())) {
            throw new ValidationException("Audio bitrate limitations not met");
        }
        if (!Validator.validateAudioDuration(osTargetPath, config)) {
            throw new ValidationException("Audio track duration exceeded");
        }
        if (!Validator.checkAudioSampleRate(state.getMime(), state.getAudioSamplingRate())) {
            throw new ValidationException("Audio track sampling rate is invalid");
        }
        if (!Validator.checkAudioCodecWhiteList(state.getMime(), state.getAudioCodec())) {
            throw new WhiteListValidationException("Audio codec white list check failed", osTargetPath);
        }
        try {
            long trackLength = Utils.getTrackLength(osTargetPath);
            if ( trackLength <= config.getAudioMaxDuration()) {
                audio.reEncodeAudioNative(osTargetPath);
            }
        } catch (AudioProcessingException | UnsupportedAudioFileException e) {
            logger.error("Failed to disarm audio natively");
            throw new AudioProcessingException("failed to disarm audio natively", osTargetPath, e);
        }
        logger.info("Audio disarmed using native approach successfully");
    }

    /**
     * Scales logo from state.getLogo() to specified maximum width and height parameters.
     * NOTE! Maximum logo width and height can be altered using
     * BuilderConfig.setLogoMaxWidth() and BuilderConfig.setLogoMaxHeight().
     * @since 0.1
     */
    public void scaleLogo() {
        logger.info("Scaling logo to w{}:h{}", config.getLogoMaxWidth(), config.getLogoMaxHeight());
        Mat destination = new Mat();
        Size newsize = new Size(config.getLogoMaxWidth(), config.getLogoMaxHeight());
        Imgproc.resize(state.getLogo(), destination, newsize);
        state.setLogo(alterLogoTransparency(destination, state.getTransparency()));
        if (config.getKeepLogo()) {
            Imgcodecs.imwrite(config.getGeneralOutputPath().resolve(state.getLogoTitle()).toString(), state.getLogo());
        }
        destination.release();
        logger.info("Logo scaled successfully");
    }

    /**
     * Changes logo transparency in-place.
     * @param image OpenCV's image object, input logo image.
     * @param transparency New transparency value.
     * @return Mat object with altered alpha channel value.
     * @throws ImageProcessingException If fails to process logo image.
     * @since 0.1
     */
    public Mat alterLogoTransparency(Mat image, double transparency) throws ImageProcessingException {
        if (image == null) {
            throw new ValidationException("Empty image was provided");
        }

        if (image.getClass() != Mat.class) {
            throw new ValidationException("Invalid class was provided");
        }
        logger.info("Changing logo transparency to {}", transparency);
        List<Mat> channels = new ArrayList<>();
        try {
            Core.split(image, channels);
            if (channels.size() == 4) {
                Mat alpha = channels.get(3);
                alpha.convertTo(alpha, -1, transparency, 0);

                Core.merge(channels, image);
            }
            logger.info("Logo transparency changed successfully");
            return image;
        } finally {
            for (Mat channel : channels) {
                channel.release();
            }
        }
    }

    /**
     * Routes an input file to an appropriate handler.
     * @param osTargetPath Path to input file.
     * @since 0.1
     */
    public void disarmSwitch(Path osTargetPath) {
        try {
            switch (state.getFileType().toLowerCase()) {
                case "image" -> disarmImage(osTargetPath, false);
                case "text" -> disarmText(osTargetPath);
                case "logo" -> disarmLogo(osTargetPath);
                case "audio" -> disarmAudio(osTargetPath);
                case "video" -> disarmVideo(osTargetPath);
                default -> throw new DisarmException("something went wrong");
            }
        } catch (IOException e) {
            throw new TextProcessingException("failed to disarm text file", state.getOsTargetFile(), e);
        }
    }

    /**
     * Sanitises specified logo file first, then routes specified media file to the right handler.
     * If specified media file is an image, logo file will be passed along to apply as a watermark.
     * @param osTargetPath Path to input file.
     * @param osLogoPath Path to input logo file.
     * @since 0.1
     */
    public void disarmSwitch(Path osTargetPath, Path osLogoPath) {
        try {
            String targetMime = state.getMime();
            String targetFileType = state.getFileType();
            disarmLogo(osLogoPath);
            state.setMime(targetMime);
            state.setFileType(targetFileType);
            switch (state.getFileType().toLowerCase()) {
                case "image" -> disarmImage(osTargetPath,true);
                case "text" -> disarmText(osTargetPath);
                case "audio" -> disarmAudio(osTargetPath);
                case "video" -> disarmVideo(osTargetPath);
                default -> throw new DisarmException("something went wrong");
            }
        } catch (IOException e) {
            throw new TextProcessingException("failed to disarm text file", state.getOsTargetFile(), e);
        }
    }

    /**
     * Prepares media specific data and stores it in the runtime state object.
     * @param osTargetPath Path to input file.
     * @throws MimeTypeDetectionException If failed to detect media file's mime type.
     * @throws FileTypeDetectionException If failed to detect media files' file type.
     * @since 0.1
     */
    public void prepData(Path osTargetPath) throws MimeTypeDetectionException, FileTypeDetectionException {
        logger.debug("Prepping metadata for {}", osTargetPath);
        try {
            Validator.validatePath(config.getGeneralOutputPath());
            state.setMime(Utils.getFormatFromMime(Utils.getMimeType(osTargetPath)));
            state.setFileType(Utils.getFileType(osTargetPath));
            state.setOsTargetFile(osTargetPath);
            if (Objects.equals(state.getFileType(), "video")) {
                state.setAudioCodec(Utils.getCodec(osTargetPath, "audio"));
                state.setVideoCodec(Utils.getCodec(osTargetPath, "video"));
                state.setAudioSamplingRate(Utils.getSamplingRate(osTargetPath));
                state.setVideoBitrate(Utils.getBitrate(osTargetPath, "video", config));
                state.setVideoFrameRate(Utils.getVideoFrameRate(osTargetPath));
                state.setVideoSize(Utils.getVideoDimensions(osTargetPath));
                state.setAudioChannels(Utils.getAudioChannels(osTargetPath));
                state.setAudioBitrate(Utils.getBitrate(osTargetPath, "audio", config));
            }
            if (Objects.equals(state.getFileType(), "audio")) {
                state.setAudioCodec(Utils.getCodec(osTargetPath, "audio"));
                state.setAudioBitrate(Utils.getBitrate(osTargetPath, "audio", config));
                state.setAudioSamplingRate(Utils.getSamplingRate(osTargetPath));
                state.setAudioChannels(Utils.getAudioChannels(osTargetPath));
            }
        } catch (MimeTypeDetectionException e) {
            throw new MimeTypeDetectionException("failed to detect mime type", osTargetPath, e);
        } catch (FileTypeDetectionException e) {
            throw new FileTypeDetectionException("failed to detect file type", osTargetPath, e);
        } catch (EncoderException e) {
            throw new ValidationException("Failed to detect bitrate");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        logger.debug("Metadata prepped successfully");
    }

     // Main media file logic processor. Performs validation, metadata extraction, title generation, routing, cleanup.
    private void processFile(Path osTargetPath, Path osLogoPath) {
        if (!Validator.isInputReadable(osTargetPath)) {
            throw new ValidationException("Specified file is unreadable");
        }
        if (!Validator.isOutputPathWritable(config.getGeneralOutputPath())) {
            throw new ValidationException("Cannot write to the specified output path");
        }
        try {
            prepData(osTargetPath);
        } catch (MimeTypeDetectionException | FileTypeDetectionException e) {
            throw new DisarmException(e);
        }

        boolean isLogo = Objects.equals(state.getMime(), "logo");
        Utils.getTitle(state, isLogo);

        if (osLogoPath != null) {
            disarmSwitch(osTargetPath, osLogoPath);
        } else {
            disarmSwitch(osTargetPath);
        }

        if (!config.isKeepOriginal()) {
            try{
                Utils.fileDispose(osTargetPath);
            } catch (FileDeletionException e) {
                logger.info("Failed to delete specified file");
            }
        }
    }

    /**
     * Public endpoint, triggers file sanitisation.
     * @param osTargetPath Path to input file.
     * @return Time taken to sanitise the file in milliseconds.
     * @since 0.1
     */
    public long fileDisarm(Path osTargetPath) {
        Instant start = Instant.now();

        processFile(osTargetPath, null);

        long duration = Duration.between(start, Instant.now()).toMillis();
        logger.info("1 - Operation complete in {}ms", duration);
        return duration;
    }

    /**
     * Public endpoint, triggers file sanitisation. Allows for bulk sanitisation.
     * @param osTargetPath Paths to input files.
     * @return Time taken to sanitise the batch in milliseconds.
     * @since 0.1
     */
    public long fileDisarm(Path[] osTargetPath) {
        Instant start = Instant.now();

        for (Path path : osTargetPath) {
            processFile(path, null);
        }

        long duration = Duration.between(start, Instant.now()).toMillis();
        logger.info("2 - Operation complete in {}ms", duration);
        return duration;
    }

    /**
     * Public endpoint, triggers file sanitisation. Allows watermarking if both input files are image files.
     * @param osTargetPath Path to input file.
     * @param osLogoPath Path to input logo file.
     * @return Time taken to sanitise the file and watermark it, in milliseconds.
     * @since 0.1
     */
    public long fileDisarm(Path osTargetPath, Path osLogoPath) {
        Instant start = Instant.now();

        processFile(osTargetPath, osLogoPath);

        long duration = Duration.between(start, Instant.now()).toMillis();
        logger.info("3 - Operation complete in {}ms", duration);
        return duration;
    }

    /**
     * Public endpoint, triggers file sanitisation. Allows for bulk sanitisation and bulk watermarking for every
     * specified image file. Non-image files are still sanitised but not affected by watermarking.
     * @param osTargetPath Path to input file.
     * @param osLogoPath Paths to input logo files.
     * @return Time taken to sanitise the batch and watermark images in milliseconds.
     * @since 0.1
     */
    public long fileDisarm(Path[] osTargetPath, Path osLogoPath) {
        Instant start = Instant.now();

        for (Path path : osTargetPath) {
            processFile(path, osLogoPath);
        }

        long duration = Duration.between(start, Instant.now()).toMillis();
        logger.info("4 - Operation complete in {}ms", duration);
        return duration;
    }
}

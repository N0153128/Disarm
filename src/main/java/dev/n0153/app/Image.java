package dev.n0153.app;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.*;

/**
 * Handles image processing and sanitisation for image files.
 * Utilises OpenCV.
 * @since 0.1
 */
public class Image {
    private final DisarmConfig config;
    private final DisarmState state;
    /**
     * Public constructor for Image class.
     * @param state Mutable runtime state.
     * @param config Immutable configuration.
     * @since 0.1
     */
    public Image(DisarmState state, DisarmConfig config) {
        this.state = state;
        this.config = config;
    }
    private static final Logger logger = LogManager.getLogger(Image.class);


    /**
     * Scales given image Mat object to any specified dimensions.
     * @param source OpenCV's image object.
     * @param x Required width to scale to.
     * @param y Required height to scale to.
     * @since 0.1
     */
    public void scaleImageToFixedValue(Mat source, int x, int y) {
        Mat destination = new Mat();
        if (source.width() > x || source.height() > y) {
            if (source.width() > x) {
                Size newSize = new Size(x, source.height());
                Imgproc.resize(source, destination, newSize);
            }
            if (source.height() > y) {
                Size newSize = new Size(destination.width(), y);
                Imgproc.resize(source, destination, newSize);
            }
        } else {
            source.copyTo(destination);
        }
        if (config.getKeepImage()) {
            Imgcodecs.imwrite(config.getGeneralOutputPath().resolve(state.getGeneralFileTitle()).toString(), destination);
        }
        state.setImage(destination);
        destination.release();
    }

    /**
     * Scales image to specified size, preserves scale factor. Limited to image size dimensions from config.
     * Use fields of config.getImgMaxWidth() and config.getImgMaxHeight() to change image size
     * @param source OpenCV's image object.
     * @since 0.1
     */
    public void scaleImageToScaleFactor(Mat source) {
        Mat destination = new Mat();
        if (source.width() > config.getImgMaxWidth() || source.height() > config.getImgMaxHeight()) {
            double scaledWidth = config.getImgMaxWidth() / source.width();
            double scaledHeight = config.getImgMaxHeight() / source.height();
            double scale = Math.min(scaledWidth, scaledHeight);

            int newWidth = (int) (source.width() * scale);
            int newHeight = (int) (source.height() * scale);

            Size size = new Size(newWidth, newHeight);
            Imgproc.resize(source, destination, size);
            state.setImage(destination.clone());
        } else {
            state.setImage(source.clone());
        }
        if (config.getKeepImage()) {
            Imgcodecs.imwrite(config.getGeneralOutputPath().resolve(state.getGeneralFileTitle()).toString(), state.getImage());
        }
        destination.release();
    }

    /**
     * Low level method - applies any given logo to any given image, preserves the alpha channels.
     * Utilises state.getImgX() and state.getImgY() for coordinates.
     * @since 0.1
     */
    public void applyWatermark() {

        // check if an empty image is passed
        if (state.getLogo().empty() || state.getImage().empty()){
            logger.error("Empty file passed. Is Image empty - {}, is logo empty - {}",
                    state.getImage().empty(), state.getLogo().empty());
            throw new IllegalArgumentException("Empty file passed");
        }
//        check - if logo has 4 channels. if not - add alpha channel and set to 255, if yes - clone logo
        Mat logoBGRA = new Mat();
        if (state.getLogo().channels() == 3) {
            List<Mat> logoChannels = new ArrayList<>();
            Core.split(state.getLogo(), logoChannels);

            Mat alpha = Mat.ones(state.getLogo().size(), CvType.CV_8UC1);
            alpha.setTo(new Scalar(255)); // Full opacity
            logoChannels.add(alpha);

            Core.merge(logoChannels, logoBGRA);
        } else {
            logoBGRA = state.getLogo().clone();
        }


        int endX = Math.min(state.getImgX() + logoBGRA.cols(), state.getImage().cols());
        int endY = Math.min(state.getImgY() + logoBGRA.rows(), state.getImage().rows());
        int startX = Math.max(state.getImgX(), 0);
        int startY = Math.max(state.getImgY(), 0);

        if (startX >= endX || startY >= endY) {

            return;
        }

        Rect roi = new Rect(startX, startY, endX - startX, endY - startY);
        Mat imageROI = new Mat(state.getImage(), roi);


        int logoStartX = Math.max(0, -state.getImgX());
        int logoStartY = Math.max(0, -state.getImgY());

        Rect logoRect = new Rect(logoStartX, logoStartY,
                endX - startX, endY - startY);

        Mat logoROI = new Mat(logoBGRA, logoRect);
        List<Mat> logoChannels = new ArrayList<>();
        Core.split(logoROI, logoChannels);
        if (logoChannels.size() >= 4) {
            Mat alpha = logoChannels.get(3);

            Mat alphaMask = Mat.ones(state.getLogo().size(), CvType.CV_8UC1);
            alphaMask.setTo(new Scalar(128));
            Core.compare(alpha, new Scalar(0), alphaMask, Core.CMP_GT);

            Mat logoBGR = new Mat();
            List<Mat> bgrChannels = logoChannels.subList(0, 3);
            Core.merge(bgrChannels, logoBGR);
            logoBGR.copyTo(imageROI, alphaMask);

            if (config.getKeepResult()) {
                Imgcodecs.imwrite(config.getGeneralOutputPath().resolve(state.getGeneralFileTitle()).toString(), state.getImage());
            }
        }

        logoBGRA.release();
        imageROI.release();
        logoROI.release();
        for (Mat channel : logoChannels) {
            channel.release();
        }
    }

    /**
     * Shortcut method - applies watermark at the top left side of any given image
     * @since 0.1
     */
    public void applyWatermarkTopLeft() { //requires image as argument, must return image path
        logger.debug("Applying watermark at top left position");
        state.setImgX(0);
        state.setImgY(0);
        applyWatermark();
    }

    /**
     * Shortcut method - applies watermark at the top right side of any given image
     * @since 0.1
     */
    public void applyWatermarkTopRight() {
        logger.debug("Applying watermark at top right position");
        state.setImgX(state.getImage().width() - state.getLogo().width());
        state.setImgY(0);
        applyWatermark();
    }

    /**
     * Shortcut method - applies watermark at the bottom left side of any given image
     * @since 0.1
     */
    public void applyWatermarkBottomLeft() {
        logger.debug("Applying watermark at bottom left position");
        int imageY = state.getImage().height();
        int logoY = state.getLogo().height();
        state.setImgY(imageY - logoY);
        state.setImgX(0);
        applyWatermark();
    }

    /**
     * Shortcut method - applies watermark at the bottom right side of any given image
     * @since 0.1
     */
    public void applyWatermarkBottomRight() {
        logger.debug("Applying watermark at bottom right position");
        int imageX = state.getImage().width();
        int imageY = state.getImage().height();
        int logoX = state.getLogo().width();
        int logoY = state.getLogo().height();
        state.setImgX(imageX - logoX);
        state.setImgY(imageY - logoY);
        applyWatermark();
    }

    /**
     * Shortcut method - applies watermark at a random position of any given image.
     * Available presets: top-left, top-right, bottom-left, bottom-right
     * @since 0.1
     */
    public void applyWatermarkAtRandomPosition() {
        Map<Integer, Runnable> positionList = new HashMap<>();
        positionList.put(0, () -> applyWatermarkTopLeft());
        positionList.put(1, () -> applyWatermarkTopRight());
        positionList.put(2, () -> applyWatermarkBottomLeft());
        positionList.put(3, () -> applyWatermarkBottomRight());

        Random random = new Random();
        int rand = random.nextInt(4);
        positionList.get(rand).run();
   }
}

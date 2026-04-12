package dev.n0153.app.plugins.image;

import dev.n0153.app.*;
import dev.n0153.app.exceptions.DisarmException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.*;

public class ImagePlugin implements MediaProcessor<ImageConfig> {
    private ImageConfig config;
    private ImageContext context;
    private GlobalConfig globalConfig;
    private final ImageValidator validator = new ImageValidator();
    private static final Logger logger = LogManager.getLogger(ImagePlugin.class);

    public void createMeta(GlobalConfig globalConfig, ImageConfig config, ImageContext context) {
        this.config = config;
        this.context = context;
        this.globalConfig = globalConfig;
    }

    @Override
    public MediaValidator<ImageConfig> getValidator() {
        return validator;
    }

    @Override
    public ProcessingContext process(ProcessingContext context, ImageConfig config) {
        return null;
    }

    @Override
    public void process() throws DisarmException {

    }

    @Override
    public void register(PluginRegistry registry) throws DisarmException {
        registry.register(config.Supports(),
                new ImagePlugin(),
                new ImageCli());
    }

    /**
     * Scales given image Mat object to any specified dimensions.
     * @param source OpenCV's image object.
     * @param x Required width to scale to.
     * @param y Required height to scale to.
     * @since 0.1
     */
    private void scaleImageToFixedValue(Mat source, int x, int y) {
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
        if (config.isKeepImage()) {
            Imgcodecs.imwrite(globalConfig.getGeneralOutputPath().resolve(context.getImageTitle()).toString(), destination);
        }
        context.setImage(destination);
        destination.release();
    }

    /**
     * Scales image to specified size, preserves scale factor. Limited to image size dimensions from config.
     * Use fields of config.getImgMaxWidth() and config.getImgMaxHeight() to change image size
     * @param source OpenCV's image object.
     * @since 0.1
     */
    private void scaleImageToScaleFactor(Mat source) {
        Mat destination = new Mat();
        if (source.width() > config.getImgMaxWidth() || source.height() > config.getImgMaxHeight()) {
            double scaledWidth = config.getImgMaxWidth() / source.width();
            double scaledHeight = config.getImgMaxHeight() / source.height();
            double scale = Math.min(scaledWidth, scaledHeight);

            int newWidth = (int) (source.width() * scale);
            int newHeight = (int) (source.height() * scale);

            Size size = new Size(newWidth, newHeight);
            Imgproc.resize(source, destination, size);
            context.setImage(destination.clone());
        } else {
            context.setImage(source.clone());
        }
        if (config.isKeepImage()) {
            Imgcodecs.imwrite(globalConfig.getGeneralOutputPath().resolve(context.getImageTitle()).toString(), context.getImage());
        }
        destination.release();
    }

    /**
     * Low level method - applies any given logo to any given image, preserves the alpha channels.
     * Utilises state.getImgX() and state.getImgY() for coordinates.
     * @since 0.1
     */
    private void applyWatermark() {

        // check if an empty image is passed
        if (context.getLogo().empty() || context.getImage().empty()){
            logger.error("Empty file passed. Is Image empty - {}, is logo empty - {}",
                    context.getImage().empty(), context.getLogo().empty());
            throw new IllegalArgumentException("Empty file passed");
        }
//        check - if logo has 4 channels. if not - add alpha channel and set to 255, if yes - clone logo
        Mat logoBGRA = new Mat();
        if (context.getLogo().channels() == 3) {
            List<Mat> logoChannels = new ArrayList<>();
            Core.split(context.getLogo(), logoChannels);

            Mat alpha = Mat.ones(context.getLogo().size(), CvType.CV_8UC1);
            alpha.setTo(new Scalar(255)); // Full opacity
            logoChannels.add(alpha);

            Core.merge(logoChannels, logoBGRA);
        } else {
            logoBGRA = context.getLogo().clone();
        }


        int endX = Math.min(context.getImgX() + logoBGRA.cols(), context.getImage().cols());
        int endY = Math.min(context.getImgY() + logoBGRA.rows(), context.getImage().rows());
        int startX = Math.max(context.getImgX(), 0);
        int startY = Math.max(context.getImgY(), 0);

        if (startX >= endX || startY >= endY) {

            return;
        }

        Rect roi = new Rect(startX, startY, endX - startX, endY - startY);
        Mat imageROI = new Mat(context.getImage(), roi);


        int logoStartX = Math.max(0, -context.getImgX());
        int logoStartY = Math.max(0, -context.getImgY());

        Rect logoRect = new Rect(logoStartX, logoStartY,
                endX - startX, endY - startY);

        Mat logoROI = new Mat(logoBGRA, logoRect);
        List<Mat> logoChannels = new ArrayList<>();
        Core.split(logoROI, logoChannels);
        if (logoChannels.size() >= 4) {
            Mat alpha = logoChannels.get(3);

            Mat alphaMask = Mat.ones(context.getLogo().size(), CvType.CV_8UC1);
            alphaMask.setTo(new Scalar(128));
            Core.compare(alpha, new Scalar(0), alphaMask, Core.CMP_GT);

            Mat logoBGR = new Mat();
            List<Mat> bgrChannels = logoChannels.subList(0, 3);
            Core.merge(bgrChannels, logoBGR);
            logoBGR.copyTo(imageROI, alphaMask);

            if (globalConfig.getKeepResult()) {
                Imgcodecs.imwrite(globalConfig.getGeneralOutputPath().resolve(context.getImageTitle()).toString(), context.getImage());
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
    private void applyWatermarkTopLeft() { //requires image as argument, must return image path
        logger.debug("Applying watermark at top left position");
        context.setImgX(0);
        context.setImgY(0);
        applyWatermark();
    }

    /**
     * Shortcut method - applies watermark at the top right side of any given image
     * @since 0.1
     */
    private void applyWatermarkTopRight() {
        logger.debug("Applying watermark at top right position");
        context.setImgX(context.getImage().width() - context.getLogo().width());
        context.setImgY(0);
        applyWatermark();
    }

    /**
     * Shortcut method - applies watermark at the bottom left side of any given image
     * @since 0.1
     */
    private void applyWatermarkBottomLeft() {
        logger.debug("Applying watermark at bottom left position");
        int imageY = context.getImage().height();
        int logoY = context.getLogo().height();
        context.setImgY(imageY - logoY);
        context.setImgX(0);
        applyWatermark();
    }

    /**
     * Shortcut method - applies watermark at the bottom right side of any given image
     * @since 0.1
     */
    private void applyWatermarkBottomRight() {
        logger.debug("Applying watermark at bottom right position");
        int imageX = context.getImage().width();
        int imageY = context.getImage().height();
        int logoX = context.getLogo().width();
        int logoY = context.getLogo().height();
        context.setImgX(imageX - logoX);
        context.setImgY(imageY - logoY);
        applyWatermark();
    }

    /**
     * Shortcut method - applies watermark at a random position of any given image.
     * Available presets: top-left, top-right, bottom-left, bottom-right
     * @since 0.1
     */
    private void applyWatermarkAtRandomPosition() {
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

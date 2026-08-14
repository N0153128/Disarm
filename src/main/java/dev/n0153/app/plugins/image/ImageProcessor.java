package dev.n0153.app.plugins.image;

import dev.n0153.app.*;
import dev.n0153.app.exceptions.ImageProcessingException;
import dev.n0153.app.exceptions.MimeTypeDetectionException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import java.nio.file.Path;
import java.util.*;

public class ImageProcessor implements MediaProcessor<ImageConfig> {
    private final ImageConfig config;
    private final ImageContext context;
    private final GlobalConfig globalConfig;
    private static final Logger logger = LogManager.getLogger(ImageProcessor.class);

    public ImageProcessor (ImageConfig config, ImageContext context, GlobalConfig globalConfig) {
        this.config = config;
        this.context = context;
        this.globalConfig = globalConfig;
    }

    public boolean checkMeta() {
        return this.config != null && this.context != null;
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
        Imgproc.resize(context.getLogo(), destination, newsize);
        context.setLogo(alterLogoTransparency(destination, config.getTransparency()));
        if (config.isKeepLogo()) {
            Imgcodecs.imwrite(globalConfig.getGeneralOutputPath().resolve(context.getLogoTitle()).toString(), context.getLogo());
        }
//        destination.release();
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
    public Mat alterLogoTransparency(Mat image, double transparency) {
        if (image == null) {
            throw new ImageProcessingException("Empty image was provided");
        }

        if (image.getClass() != Mat.class) {
            throw new ImageProcessingException("Invalid class was provided");
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

    public void saveImage(Mat destination) {
        if (!config.getDontSaveImage()) {
            if (Objects.equals(config.getImageDefaultOutputTo(), "default")) {
                Imgcodecs.imwrite(globalConfig.getGeneralOutputPath().resolve(context.getImageTitle()).toString(), destination);
            } else {
                String baseTitle = context.getImageTitle().split("\\.")[0];
                context.setImageTitle(baseTitle+"."+config.getImageDefaultOutputTo());
                Imgcodecs.imwrite(globalConfig.getGeneralOutputPath().resolve(context.getImageTitle()).toString(), destination);
            }
        }
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
        saveImage(destination);
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
            double scaledWidth = (double) config.getImgMaxWidth() / source.width();
            double scaledHeight = (double) config.getImgMaxHeight() / source.height();
            double scale = Math.min(scaledWidth, scaledHeight);

            int newWidth = (int) (source.width() * scale);
            int newHeight = (int) (source.height() * scale);

            Size size = new Size(newWidth, newHeight);
            Imgproc.resize(source, destination, size);
            context.setImage(destination.clone());
        } else {
            context.setImage(source.clone());
        }
        saveImage(destination);
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
            throw new ImageProcessingException("Empty file passed");
        }
        logger.info("image channels: {}, logo channels: {}", context.getImage().channels(), context.getLogo().channels());
//        check - if logo has 4 channels. if not - add alpha channel and set to 255, if yes - clone logo
        Mat logoBGRA;
        if (context.getLogo().channels() == 3) {
            List<Mat> logoChannels = new ArrayList<>();
            Core.split(context.getLogo(), logoChannels);

            Mat alpha = Mat.ones(context.getLogo().size(), CvType.CV_8UC1);
            alpha.setTo(new Scalar(255)); // Full opacity
            logoChannels.add(alpha);

            logoBGRA = new Mat();
            Core.merge(logoChannels, logoBGRA);
            for (Mat channel : logoChannels) {
                channel.release();
            }
        } else {
            logoBGRA = context.getLogo().clone();
        }


        int endX = Math.min(context.getImgX() + logoBGRA.cols(), context.getImage().cols());
        int endY = Math.min(context.getImgY() + logoBGRA.rows(), context.getImage().rows());
        int startX = Math.max(context.getImgX(), 0);
        int startY = Math.max(context.getImgY(), 0);

        if (startX >= endX || startY >= endY) {
            logoBGRA.release();
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

            Mat alphaFloat = new Mat();
            alpha.convertTo(alphaFloat, CvType.CV_32F, 1.0 / 255.0);

            Mat invAlphaFloat = new Mat();
            Core.subtract(Mat.ones(alphaFloat.size(), alphaFloat.type()), alphaFloat, invAlphaFloat);

            Mat alpha3 = new Mat();
            Core.merge(Arrays.asList(alphaFloat, alphaFloat, alphaFloat), alpha3);
            Mat invAlpha3 = new Mat();
            Core.merge(Arrays.asList(invAlphaFloat, invAlphaFloat, invAlphaFloat), invAlpha3);

            Mat logoGBR = new Mat();
            List<Mat> bgrChannels = logoChannels.subList(0, 3);
            Core.merge(bgrChannels, logoGBR);

            Mat logoGBRFloat = new Mat();
            logoGBR.convertTo(logoGBRFloat, CvType.CV_32F);
            Mat roiFloat = new Mat();
            imageROI.convertTo(roiFloat, CvType.CV_32F);

            Mat blended = new Mat();
            Core.multiply(logoGBRFloat, alpha3, logoGBRFloat);
            Core.multiply(roiFloat, invAlpha3, roiFloat);
            Core.add(logoGBRFloat, roiFloat, blended);

            blended.convertTo(blended, imageROI.type());
            blended.copyTo(imageROI);
            saveImage(context.getImage());

            alphaFloat.release();
            invAlphaFloat.release();
            alpha3.release();
            invAlphaFloat.release();
            invAlpha3.release();
            roiFloat.release();
            blended.release();
            logoGBR.release();
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

    private void watermarkPositionHandler(String position) {
        switch (position) {
            case "random" -> applyWatermarkAtRandomPosition();
            case "top-left" -> applyWatermarkTopLeft();
            case "top-right" -> applyWatermarkTopRight();
            case "bottom-left" -> applyWatermarkBottomLeft();
            case "bottom-right" -> applyWatermarkBottomRight();
        }
    }

    @Override
    public void process(Path osTargetPath) {
        if (!checkMeta()) {
            throw new ImageProcessingException("Image Processor: meta is empty");
        }
        if (config.getFixedValueScaling().isEmpty()) {
            logger.info("current config width: {}, height: {}", config.getImgMaxWidth(), config.getImgMaxHeight());
        } else {
            logger.info("current config width and height overruled by fixed value scaling.") ;
        }
        try {
            context.setImage(Imgcodecs.imread(osTargetPath.toString(), Imgcodecs.IMREAD_UNCHANGED));
            context.setImageTitle(Utils.getTitle(osTargetPath, Utils.getMimeType(osTargetPath), false));
            if (!config.getFixedValueScaling().isEmpty()) {
                int fixedWidth = Integer.parseInt(config.getFixedValueScaling().split("x")[0]);
                int fixedHeight = Integer.parseInt(config.getFixedValueScaling().split("x")[1]);
                scaleImageToFixedValue(context.getImage(), fixedWidth, fixedHeight);
            } else {
                scaleImageToScaleFactor(context.getImage());
            }
            if (config.getPathToLogo() != null) {
                logger.info("logo present");
                ImageValidator validator = new ImageValidator();
                validator.createMeta(config, context);
                if (validator.validateLogo(config.getPathToLogo())) {
                    context.setLogo(Imgcodecs.imread(config.getPathToLogo().toString(), Imgcodecs.IMREAD_UNCHANGED));
                    context.setLogoTitle(Utils.getTitle(
                            config.getPathToLogo(),
                            Utils.getMimeFromSignature(config.getPathToLogo()),
                            true));
                    scaleLogo();
                    watermarkPositionHandler(config.getLogoPosition());
                } else {
                    throw new ImageProcessingException("Failed to process logo");
                }

            }
        } catch (MimeTypeDetectionException e) {
            throw new ImageProcessingException("Failed to process image", osTargetPath, e);
        }
    }

    @Override
    public MediaContext getContext() {
        return context;
    }
}

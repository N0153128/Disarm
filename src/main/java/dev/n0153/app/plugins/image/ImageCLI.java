package dev.n0153.app.plugins.image;

import dev.n0153.app.DisarmCLI;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command(name = "image", description = "Image Processing Plugin")
public class ImageCLI implements Runnable {
    private static final Logger logger = LogManager.getLogger(ImageCLI.class);

    private ImageConfig config;

    public void provideConfig(ImageConfig config) {
        this.config = config;
    }

    public ImageConfig getConfig() {
        if (this.config != null) {
            return this.config;
        } else {
            throw new IllegalStateException("Config is empty");
        }
    }

    @CommandLine.ParentCommand
    DisarmCLI inputPath;

    @CommandLine.Option(names = {"-lsz", "--logo-size-limit"}, description = "Change logo size limit")
    private int logoSizeLimit;

    @CommandLine.Option(names = {"-kl", "--keep-logo"}, description = "Save logo as a file after processing")
    private boolean keepLogo;

    @CommandLine.Option(names = {"-ki", "--keep-image"}, description = "Save image as a file after processing")
    private boolean keepImage;

    @CommandLine.Option(names = {"-imw", "--image-max-width"}, description = "Change maximum allowed image width")
    private int imageMaxWidth;

    @CommandLine.Option(names = {"-imh", "--image-max-height"}, description = "Change maximum allowed image height")
    private int imageMaxHeight;

    @CommandLine.Option(names = {"-lmw", "--logo-max-width"}, description = "Change maximum allowed logo width")
    private int logoMaxWidth;

    @CommandLine.Option(names = {"-lmh", "--logo-max-height"}, description = "Change maximum allowed logo height")
    private int logoMaxHeight;

    @Override
    public void run() {
        logger.info("Image CLI detected");
        if (logoSizeLimit > 0) {
            this.config.setLogoSizeLimit(logoSizeLimit);
        }
        if (keepLogo) {
            this.config.setKeepLogo(true);
        }
        if (keepImage) {
            this.config.setKeepImage(true);
        }
        if (imageMaxWidth > 0) {
            this.config.setImgMaxWidth(imageMaxWidth);
        }
        if (imageMaxHeight > 0) {
            this.config.setImgMaxHeight(imageMaxHeight);
        }
        if (logoMaxWidth > 0) {
            this.config.setLogoMaxWidth(logoMaxWidth);
        }
        if (logoMaxHeight > 0) {
            this.config.setLogoMaxHeight(logoMaxHeight);
        }
    }
}
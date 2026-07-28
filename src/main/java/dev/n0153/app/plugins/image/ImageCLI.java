package dev.n0153.app.plugins.image;

import dev.n0153.app.DisarmCLI;
import dev.n0153.app.PluginRegistry;
import dev.n0153.app.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opencv.imgcodecs.Imgcodecs;
import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.nio.file.Path;

@Command(name = "image", description = "Image Processing Plugin")
public class ImageCLI implements Runnable {
    private static final Logger logger = LogManager.getLogger(ImageCLI.class);

    private final ImageConfig config = new ImageConfig();
    private final PluginRegistry registry;
    private final ImageContext context;

    public ImageCLI (PluginRegistry registry, ImageContext context) {
        this.registry = registry;
        this.context = context;
        registry.updateConfig("image", config);
    }

    @CommandLine.ParentCommand
    DisarmCLI inputPath;

    @CommandLine.Option(names = {"-l", "--logo"}, description =
            "Apply watermark to specified image file. " +
                    "Accepts path to logo image. Example: resources/logos/logo.png")
    private Path logo;

    @CommandLine.Option(names = {"-lsz", "--logo-size-limit"}, description =
            "Changes file size limit for logo image file. " +
                    "This is a separate file size limit to the other image files. " +
                    "Accepts bytes as integers. Example for 10MB limit: -lsz 10000000")
    private int logoSizeLimit;

    @CommandLine.Option(names = {"-kl", "--keep-logo"}, description =
            "Saves logo as a separate, disarmed file after processing. " +
                    "Defaults to false. Example: -kl")
    private boolean keepLogo;

    @CommandLine.Option(names = {"-ki", "--keep-image"}, description =
            "Saves image as a file after processing")
    private boolean keepImage;

    @CommandLine.Option(names = {"-imw", "--image-max-width"}, description =
            "Change maximum allowed image width")
    private int imageMaxWidth;

    @CommandLine.Option(names = {"-imh", "--image-max-height"}, description =
            "Change maximum allowed image height")
    private int imageMaxHeight;

    @CommandLine.Option(names = {"-lmw", "--logo-max-width"}, description =
            "Change maximum allowed logo width")
    private int logoMaxWidth;

    @CommandLine.Option(names = {"-lmh", "--logo-max-height"}, description =
            "Change maximum allowed logo height")
    private int logoMaxHeight;

    @CommandLine.Option(names = {"-fvs", "--fixed-value-scaling"}, description =
            "Specify fixed value scaling parameters in the following format: WidthxHeight. Example: 1920x1080")
    private String fixedValueScaling;

    @Override
    public void run() {
        if (logoSizeLimit > 0) {
            this.config.setLogoSizeLimit(logoSizeLimit);
        }
        if (keepLogo) {
            this.config.setKeepLogo(true);
        }
        if (keepImage) {
            this.config.setDeleteImageResult(true);
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
        if (logo != null) {
            this.context.setLogo(Imgcodecs.imread(logo.toString(), Imgcodecs.IMREAD_UNCHANGED));
            this.context.setLogoTitle(Utils.getTitle(logo, "png", true)); //REFACTOR THIS: logo img may not always be png
        }
        if (fixedValueScaling != null) {
            config.setFixedValueScaling(fixedValueScaling);
        }
        registry.updateConfig("image", config);
    }
}
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

@Command(name = "image", description = "Image Processing Plugin.", mixinStandardHelpOptions = true)
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
                    "This is a separate file size limit from the other image files. " +
                    "Accepts bytes as integers. Example for 10MB limit: -lsz 10000000")
    private int logoSizeLimit;

    @CommandLine.Option(names = {"-kl", "--keep-logo"}, description =
            "Saves logo as a separate, disarmed file after processing. " +
                    "Defaults to false. Example: -kl")
    private boolean keepLogo;

    @CommandLine.Option(names = {"-dsi", "--dont-save-image"}, description =
            "Executes an entire image disarming cycle for benchmarking purposes." +
                    " Resulting file will not be saved to disk. " +
                    "Similar to -dr, but image-specific and skips the write entirely. Example: -dsi")
    private boolean dontSaveImage;

    @CommandLine.Option(names = {"-imw", "--image-max-width"}, description =
            "Changes maximum allowed image width. " +
                    "Accepts pixels as integers, defaults to 512. Example: -imw 1920")
    private int imageMaxWidth;

    @CommandLine.Option(names = {"-imh", "--image-max-height"}, description =
            "Changes maximum allowed image height. " +
                    "Accepts pixels as integers, defaults to 512. Example: -imh 1080")
    private int imageMaxHeight;

    @CommandLine.Option(names = {"-lmw", "--logo-max-width"}, description =
            "Changes maximum allowed logo width. " +
                    "Accepts pixels as integers, defaults to 50. Example: -lmw 100")
    private int logoMaxWidth;

    @CommandLine.Option(names = {"-lmh", "--logo-max-height"}, description =
            "Changes maximum allowed logo height. " +
                    "Accepts pixels as integers, defaults to 50. Example: -lmh 100")
    private int logoMaxHeight;

    @CommandLine.Option(names = {"-fvs", "--fixed-value-scaling"}, description =
            "Specify fixed value scaling parameters in the following format: WidthxHeight." +
                    " Example: 1920x1080")
    private String fixedValueScaling;

    @Override
    public void run() {
        if (logoSizeLimit > 0) {
            this.config.setLogoSizeLimit(logoSizeLimit);
        }
        if (keepLogo) {
            this.config.setKeepLogo(true);
        }
        if (dontSaveImage) {
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
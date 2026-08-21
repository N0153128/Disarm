package dev.n0153.app.plugins.image;

import dev.n0153.app.MediaConfig;

import javax.swing.plaf.PanelUI;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class ImageConfig implements MediaConfig {

    private final int logoSizeLimit = 5_000_000; //5MB
    private final boolean keepLogo = false;
    private final boolean dontSaveImage = false;
    private final int imgMaxWidth = 512;
    private final int imgMaxHeight = 512;
    private final int logoMaxWidth = 50;
    private final int logoMaxHeight = 50;
    private final double transparency = 0.5;
    private final String fixedValueScaling = "";
    private final String imageDefaultOutputTo = "default";
    private final Path pathToLogo = null;
    private final String logoPosition = "random";
    private final int pngSizeLimit = 5_000_000; //5MB;
    private final int jpgSizeLimit = 5_000_000; //5MB;
    private final int webpSizeLimit = 5_000_000; //5MB;

    private final String KEY_LOGO_SIZE_LIMIT = "logoSizeLimit";
    private final String KEY_KEEP_LOGO = "keepLogo";
    private final String KEY_DONT_SAVE_IMAGE = "dontSaveImage";
    private final String KEY_IMG_MAX_WIDTH = "imgMaxWidth";
    private final String KEY_IMG_MAX_HEIGHT = "imgMaxHeight";
    private final String KEY_LOGO_MAX_WIDTH = "logoMaxWidth";
    private final String KEY_LOGO_MAX_HEIGHT = "logoMaxHeight";
    private final String KEY_TRANSPARENCY = "transparency";
    private final String KEY_FIXED_VALUE_SCALING = "fixedValueScaling";
    private final String KEY_IMAGE_DEFAULT_OUTPUT_TO = "imageDefaultOutputTo";
    private final String KEY_PATH_TO_LOGO = "pathToLogo";
    private final String KEY_LOGO_POSITION = "logoPosition";
    private final String KEY_PNG_SIZE_LIMIT = "pngSizeLimit";
    private final String KEY_JPG_SIZE_LIMIT = "jpgSizeLimit";
    private final String KEY_WEBP_SIZE_LIMIT = "webpSizeLimit";

    private final Map<String, Object> configStorage = new HashMap<>() {{
        put(KEY_LOGO_SIZE_LIMIT, logoSizeLimit);
        put(KEY_KEEP_LOGO, keepLogo);
        put(KEY_DONT_SAVE_IMAGE, dontSaveImage);
        put(KEY_IMG_MAX_WIDTH, imgMaxWidth);
        put(KEY_IMG_MAX_HEIGHT, imgMaxHeight);
        put(KEY_LOGO_MAX_WIDTH, logoMaxWidth);
        put(KEY_LOGO_MAX_HEIGHT, logoMaxHeight);
        put(KEY_TRANSPARENCY, transparency);
        put(KEY_FIXED_VALUE_SCALING, fixedValueScaling);
        put(KEY_IMAGE_DEFAULT_OUTPUT_TO, imageDefaultOutputTo);
        put(KEY_PATH_TO_LOGO, pathToLogo);
        put(KEY_LOGO_POSITION, logoPosition);
        put(KEY_PNG_SIZE_LIMIT, pngSizeLimit);
        put(KEY_JPG_SIZE_LIMIT, jpgSizeLimit);
        put(KEY_WEBP_SIZE_LIMIT, webpSizeLimit);
    }};

    @Override
    public void put(String key, Object value) {
        if (!configStorage.containsKey(key)) {
            throw new IllegalArgumentException("Specified key doesn't exist");
        }
        configStorage.replace(key, value);
    }

    @Override
    public <$ValueType> $ValueType get(String key, Class<$ValueType> type) {
        return type.cast(configStorage.get(key));
    }

    @Override
    public void release() {
        configStorage.clear();
    }

    @Override
    public String getName() {
        return "Image";
    }

    @Override
    public String toDebugString() {
        StringBuilder output = new StringBuilder("\n\n=== IMAGE PLUGIN CONFIG === \n");
        for (Map.Entry<String, Object> entry : configStorage.entrySet()) {
            output.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
        output.append("\n=== END OF SNAPSHOT ===\n");
        return output.toString();
    }

    @Override
    public double getVersion() {
        return 1;
    }

    @Override
    public Set<String> supports() {
        return Set.of("png", "jpeg", "jpg", "webp");
    }

    @Override
    public int maxFileSizeInBytes(String mime) {
        return switch (mime) {
            case "png" -> getPngSizeLimit();
            case "jpeg", "jpg" -> getJpgSizeLimit();
            case "webp" -> getWebpSizeLimit();
            default -> throw new IllegalStateException("Unsupported value: " + mime);
        };
    }

    //getters

    public int getPngSizeLimit() {
        return Objects.requireNonNullElse(
                get(KEY_PNG_SIZE_LIMIT, Integer.class),
                pngSizeLimit
        );
    }

    public int getJpgSizeLimit() {
        return Objects.requireNonNullElse(
                get(KEY_JPG_SIZE_LIMIT, Integer.class),
                jpgSizeLimit
        );
    }

    public int getWebpSizeLimit() {
        return Objects.requireNonNullElse(
                get(KEY_WEBP_SIZE_LIMIT, Integer.class),
                webpSizeLimit
        );
    }

    public String getLogoPosition() {
        return Objects.requireNonNullElse(
                get(KEY_LOGO_POSITION, String.class),
                logoPosition
        );
    }

    public Path getPathToLogo() {
        return get(KEY_PATH_TO_LOGO, Path.class);
    }

    public int getLogoSizeLimit() {
        return Objects.requireNonNullElse(
                get(KEY_LOGO_SIZE_LIMIT, Integer.class),
                logoSizeLimit);
    }

    public String getImageDefaultOutputTo() {
        return Objects.requireNonNullElse(
                get(KEY_IMAGE_DEFAULT_OUTPUT_TO, String.class),
                imageDefaultOutputTo);
    }

    public String getFixedValueScaling() {
        return Objects.requireNonNullElse(
                get(KEY_FIXED_VALUE_SCALING, String.class),
                fixedValueScaling);
    }

    public boolean isKeepLogo() {
        return Objects.requireNonNullElse(
                get(KEY_KEEP_LOGO, Boolean.class),
                keepLogo);
    }

    public boolean getDontSaveImage() {
        return Objects.requireNonNullElse(
                get(KEY_DONT_SAVE_IMAGE, Boolean.class),
                dontSaveImage);
    }

    public int getImgMaxWidth() {
        return Objects.requireNonNullElse(
                get(KEY_IMG_MAX_WIDTH, Integer.class),
                imgMaxWidth);
    }

    public int getImgMaxHeight() {
        return Objects.requireNonNullElse(
                get(KEY_IMG_MAX_HEIGHT, Integer.class),
                imgMaxHeight);
    }

    public int getLogoMaxWidth() {
        return Objects.requireNonNullElse(
                get(KEY_LOGO_MAX_WIDTH, Integer.class),
                logoMaxWidth);
    }

    public int getLogoMaxHeight() {
        return Objects.requireNonNullElse(
                get(KEY_LOGO_MAX_HEIGHT, Integer.class),
                logoMaxHeight);
    }

    public double getTransparency() {
        return Objects.requireNonNullElse(
                get(KEY_TRANSPARENCY, Double.class),
                transparency);
    }

    //setters

    public void setPngSizeLimit(int newPngSizeLimit) {
        if (newPngSizeLimit == 0) {
            throw new IllegalArgumentException("Png size limit cannot be zero");
        }
        if (newPngSizeLimit < 0) {
            throw new IllegalArgumentException("Png size limit cannot be less than zero");
        }
        put(KEY_PNG_SIZE_LIMIT, newPngSizeLimit);
    }

    public void setJpgSizeLimit(int newJpgSizeLimit) {
        if (newJpgSizeLimit == 0) {
            throw new IllegalArgumentException("Jpg size limit cannot be zero");
        }
        if (newJpgSizeLimit < 0) {
            throw new IllegalArgumentException("Jpg size limit cannot be less than zero");
        }
        put(KEY_PNG_SIZE_LIMIT, newJpgSizeLimit);
    }

    public void setWebpSizeLimit(int newWebpSizeLimit) {
        if (newWebpSizeLimit == 0) {
            throw new IllegalArgumentException("Webp size limit cannot be zero");
        }
        if (newWebpSizeLimit < 0) {
            throw new IllegalArgumentException("Webp size limit cannot be less than zero");
        }
        put(KEY_PNG_SIZE_LIMIT, newWebpSizeLimit);
    }

    public void setLogoPosition(String newLogoPosition) {
        if (newLogoPosition == null) {
            throw new IllegalArgumentException("Logo position cannot be null");
        }
        if (newLogoPosition.isEmpty()) {
            throw new IllegalArgumentException("Logo position cannot be empty");
        }
        Set<String> validPositions = Set.of("top-right", "top-left", "bottom-right", "bottom-left");
        if (!validPositions.contains(newLogoPosition.toLowerCase())) {
            throw new IllegalArgumentException("Invalid logo position was provided");
        }
        put(KEY_LOGO_POSITION, newLogoPosition);
    }

    public void setPathToLogo(Path newPathToLogo) {
        if (newPathToLogo == null) {
            throw new IllegalArgumentException("Path to logo cannot be null");
        }
        put(KEY_PATH_TO_LOGO, newPathToLogo);
    }

    public void setTransparency(double newTransparency) {
        if (newTransparency < 0.0) {
            throw new IllegalArgumentException("Transparency cannot be less than zero");
        }
        if (newTransparency > 1.0) {
            throw new IllegalArgumentException("Transparency cannot be greater than one");
        }
        put(KEY_TRANSPARENCY, newTransparency);
    }

    public void setImageOutputTo(String newImageOutputTo) {
        if (newImageOutputTo == null) {
            throw new IllegalArgumentException("Image output format cannot be null");
        }
        if (newImageOutputTo.isEmpty()) {
            throw new IllegalArgumentException("Image output format cannot be empty");
        }
        if (!supports().contains(newImageOutputTo)) {
            throw new IllegalArgumentException("Provided image output format is not supported");
        }
        put(KEY_IMAGE_DEFAULT_OUTPUT_TO, newImageOutputTo);
    }

    public void setLogoSizeLimit(int newLogoSizeLimit) {
        if (newLogoSizeLimit < 0) {
            throw new IllegalArgumentException("Logo size limit cannot be less than zero");
        }
        put(KEY_LOGO_SIZE_LIMIT, newLogoSizeLimit);
    }

    public void setFixedValueScaling(String newFixedValueScaling) {
        if (newFixedValueScaling == null) {
            throw new IllegalArgumentException("Fixed value scaling cannot be null");
        }
        int width = Integer.parseInt(newFixedValueScaling.split("x")[0]);
        int height = Integer.parseInt(newFixedValueScaling.split("x")[1]);
        if (width < 0) {
            throw new IllegalArgumentException("Width cannot be less than zero");
        }
        if (height < 0) {
            throw new IllegalArgumentException("Height cannot be less than zero");
        }
        if (width > getImgMaxWidth()) {
            throw new IllegalArgumentException("Width cannot exceed maximum image width value");
        }
        if (height > getImgMaxHeight()) {
            throw new IllegalArgumentException("Height cannot exceed maximum image height value");
        }
        put(KEY_FIXED_VALUE_SCALING, newFixedValueScaling);
    }

    public void setKeepLogo(boolean keepLogo) {
        put(KEY_KEEP_LOGO, keepLogo);
    }

    public void setDontSaveImage(boolean newDontSaveImage) {
        put(KEY_DONT_SAVE_IMAGE, newDontSaveImage);
    }

    public void setImgMaxWidth(int newImgMaxWidth) {
        if (newImgMaxWidth < 0) {
            throw new IllegalArgumentException("Image max width cannot be less than zero");
        }
        put(KEY_IMG_MAX_WIDTH, newImgMaxWidth);
    }

    public void setImgMaxHeight(int newImageMaxHeight) {
        if (newImageMaxHeight < 0) {
            throw new IllegalArgumentException("Image max height cannot be less than zero");
        }
        put(KEY_IMG_MAX_HEIGHT, newImageMaxHeight);
    }

    public void setLogoMaxWidth(int newLogoMaxWidth) {
        if (newLogoMaxWidth < 0) {
            throw new IllegalArgumentException("Logo max width cannot be less than zero");
        }
        put(KEY_LOGO_MAX_WIDTH, newLogoMaxWidth);
    }

    public void setLogoMaxHeight(int newLogoMaxHeight) {
        if (newLogoMaxHeight < 0) {
            throw new IllegalArgumentException("Logo max height cannot be less than zero");
        }
        put(KEY_LOGO_MAX_HEIGHT, newLogoMaxHeight);
    }
}

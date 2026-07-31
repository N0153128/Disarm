package dev.n0153.app.plugins.image;

import dev.n0153.app.MediaConfig;

import javax.swing.plaf.PanelUI;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class ImageConfig implements MediaConfig {

    private final int logoSizeLimit = 5_000_000; //5MB
    private final boolean keepLogo = true;
    private final boolean dontSaveImage = false;
    private final int imgMaxWidth = 512;
    private final int imgMaxHeight = 512;
    private final int logoMaxWidth = 50;
    private final int logoMaxHeight = 50;
    private final double transparency = 0.5;
    private final String fixedValueScaling = "";
    private final String imageDefaultOutputTo = "default";

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
        return 5_000_000;
    }

    //getters
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

    public void setImageOutputTo(String newImageOutputTo) {
        if (newImageOutputTo == null) {
            throw new IllegalArgumentException("Image output container cannot be null");
        }
        if (newImageOutputTo.isEmpty()) {
            throw new IllegalArgumentException("Image output container cannot be empty");
        }
        if (supports().contains(newImageOutputTo)) {
            throw new IllegalArgumentException("Provided image output container is not supported");
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

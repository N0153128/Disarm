package dev.n0153.app.plugins.image;

import dev.n0153.app.MediaConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class ImageConfig implements MediaConfig {

    private final int logoSizeLimit = 5_000_000; //5MB
    private final boolean keepLogo = true;
    private final boolean keepImage = true;
    private final int imgMaxWidth = 512;
    private final int imgMaxHeight = 512;
    private final int logoMaxWidth = 50;
    private final int logoMaxHeight = 50;

    private final String KEY_LOGO_SIZE_LIMIT = "logoSizeLimit";
    private final String KEY_KEEP_LOGO = "keepLogo";
    private final String KEY_KEEP_IMAGE = "keepImage";
    private final String KEY_IMG_MAX_WIDTH = "imgMaxWidth";
    private final String KEY_IMG_MAX_HEIGHT = "imgMaxHeight";
    private final String KEY_LOGO_MAX_WIDTH = "logoMaxWidth";
    private final String KEY_LOGO_MAX_HEIGHT = "logoMaxHeight";


    private final Map<String, Object> configStorage = new HashMap<>() {{
        put(KEY_LOGO_SIZE_LIMIT, logoSizeLimit);
        put(KEY_KEEP_LOGO, keepLogo);
        put(KEY_KEEP_IMAGE, keepImage);
        put(KEY_IMG_MAX_WIDTH, imgMaxWidth);
        put(KEY_IMG_MAX_HEIGHT, imgMaxHeight);
        put(KEY_LOGO_MAX_WIDTH, logoMaxWidth);
        put(KEY_LOGO_MAX_HEIGHT, logoMaxHeight);
    }};

    @Override
    public void put(String key, Object value) {
        if (!configStorage.containsKey(key)) {
            throw new IllegalArgumentException("Specified key doesn't exist");
        }
        configStorage.replace(key, value);
    }

    @Override
    public <ValueType> ValueType get(String key, Class<ValueType> type) {
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

    @Override
    public String getLogLevel() {
        return "info";
    }

    //getters
    public int getLogoSizeLimit() {
        return Objects.requireNonNullElse(
                get(KEY_LOGO_SIZE_LIMIT, int.class),
                logoSizeLimit);
    }

    public boolean isKeepLogo() {
        return Objects.requireNonNullElse(
                get(KEY_KEEP_LOGO, boolean.class),
                keepLogo);
    }

    public boolean isKeepImage() {
        return Objects.requireNonNullElse(
                get(KEY_KEEP_IMAGE, boolean.class),
                keepImage);
    }

    public int getImgMaxWidth() {
        return Objects.requireNonNullElse(
                get(KEY_IMG_MAX_WIDTH, int.class),
                imgMaxWidth);
    }

    public int getImgMaxHeight() {
        return Objects.requireNonNullElse(
                get(KEY_IMG_MAX_HEIGHT, int.class),
                imgMaxHeight);
    }

    public int getLogoMaxWidth() {
        return Objects.requireNonNullElse(
                get(KEY_LOGO_MAX_WIDTH, int.class),
                logoMaxWidth);
    }

    public int getLogoMaxHeight() {
        return Objects.requireNonNullElse(
                get(KEY_LOGO_MAX_HEIGHT, int.class),
                logoMaxHeight);
    }

    //setters
    public void setLogoSizeLimit(int newLogoSizeLimit) {
        if (newLogoSizeLimit < 0) {
            throw new IllegalArgumentException("Logo size limit cannot be less than zero");
        }
        put(KEY_LOGO_SIZE_LIMIT, newLogoSizeLimit);
    }

    public void setKeepLogo(boolean keepLogo) {
        put(KEY_KEEP_LOGO, keepLogo);
    }

    public void setKeepImage(boolean keepImage) {
        put(KEY_KEEP_IMAGE, keepImage);
    }

    public void setImgMaxWidth(double newImgMaxWidth) {
        if (newImgMaxWidth < 0) {
            throw new IllegalArgumentException("Image max width cannot be less than zero");
        }
        put(KEY_IMG_MAX_WIDTH, newImgMaxWidth);
    }

    public void setImgMaxHeight(double newImageMaxHeight) {
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

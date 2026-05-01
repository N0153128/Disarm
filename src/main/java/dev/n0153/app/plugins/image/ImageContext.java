package dev.n0153.app.plugins.image;

import dev.n0153.app.MediaContext;
import org.opencv.core.Mat;

import java.util.HashMap;
import java.util.Map;

public class ImageContext implements MediaContext {
    private final Map<String, Object> contextStorage = new HashMap<>() {{
        put(KEY_IMAGE, null);
        put(KEY_LOGO, null);
        put(KEY_X, null);
        put(KEY_Y, null);
        put(KEY_LOGO_TITLE, null);
        put(KEY_IMAGE_TITLE, null);
    }};

    @Override
    public void put(String key, Object value) {
        contextStorage.put(key, value);
    }

    @Override
    public <ValueType> ValueType get(String key, Class<ValueType> type) {
        return type.cast(contextStorage.get(key));
    }

    @Override
    public void release() {
        contextStorage.clear();
    }

    @Override
    public void close() throws Exception {}

    private final String KEY_IMAGE = "image";
    private final String KEY_LOGO = "logo";
    private final String KEY_X = "x";
    private final String KEY_Y = "y";
    private final String KEY_LOGO_TITLE = "logoTitle";
    private final String KEY_IMAGE_TITLE = "imageTitle";


    public Mat getImage() {
        return get(KEY_IMAGE, Mat.class);
    }

    public Mat getLogo() {
        return get(KEY_LOGO, Mat.class);
    }

    public int getImgX(){
        return get(KEY_X, int.class);
    }

    public int getImgY(){
        return get(KEY_Y, int.class);
    }

    public String getLogoTitle(){
        return get(KEY_LOGO_TITLE, String.class);
    }

    public String getImageTitle(){
        return get(KEY_IMAGE_TITLE, String.class);
    }

    public void setImage(Mat newImage) {
        if (newImage == null) {
            throw new IllegalArgumentException("Image cannot be null");
        }
        if (newImage.empty()) {
            throw new IllegalArgumentException("Empty image passed");
        }
        if (get(KEY_IMAGE, Mat.class) != null) {
            get(KEY_IMAGE, Mat.class).release();
        }
        put(KEY_IMAGE, newImage.clone());
    }

    public void setLogo(Mat newLogo) {
        if (newLogo == null) {
            throw new IllegalArgumentException("Logo cannot be null");
        }
        if (newLogo.empty()) {
            throw new IllegalArgumentException("Empty logo passed");
        }
        if (get(KEY_LOGO, Mat.class) != null) {
            get(KEY_LOGO, Mat.class).release();
        }
        put(KEY_LOGO, newLogo.clone());
    }

    public void setImgX(int newX) {
        if (newX < 0) {
            throw new IllegalArgumentException("X value cannot be lower than 0");
        }
        if (get(KEY_IMAGE, Mat.class) != null && !get(KEY_IMAGE, Mat.class).empty() && newX >= get(KEY_IMAGE, Mat.class).width()) {
            throw new IllegalArgumentException("X coordinate exceeds image width");
        }
        put(KEY_X, newX);
    }

    public void setImgY(int newY) {
        if (newY < 0) {
            throw new IllegalArgumentException("Y value cannot be lower than 0");
        }
        if (get(KEY_IMAGE, Mat.class) != null && !get(KEY_IMAGE, Mat.class).empty() && newY >= get(KEY_IMAGE, Mat.class).height()) {
            throw new IllegalArgumentException("Y coordinate exceeds image height");
        }
        put(KEY_Y, newY);
    }

    public void setLogoTitle(String newLogoTitle) {
        if (newLogoTitle == null) {
            throw new IllegalArgumentException("Logo file name cannot be null");
        }
        if (newLogoTitle.isEmpty()) {
            throw new IllegalArgumentException("Logo file name cannot be empty");
        }
        put(KEY_LOGO_TITLE, newLogoTitle);
    }

    public void setImageTitle(String newImageTitle) {
        if (newImageTitle == null) {
            throw new IllegalArgumentException("Logo file name cannot be null");
        }
        if (newImageTitle.isEmpty()) {
            throw new IllegalArgumentException("Logo file name cannot be empty");
        }
        put(KEY_IMAGE_TITLE, newImageTitle);
    }
}

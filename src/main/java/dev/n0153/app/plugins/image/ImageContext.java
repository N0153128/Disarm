package dev.n0153.app.plugins.image;

import dev.n0153.app.MediaContext;
import org.opencv.core.Mat;

import java.util.HashMap;
import java.util.Map;

public class ImageContext implements MediaContext {
    private final Map<String, Object> contextStorage = new HashMap<>();

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

    private Mat image;
    private Mat logo;
    private int x = 0;
    private int y = 0;
    private String logoTitle;
    private String imageTitle;

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

    public int getX(){
        return get(KEY_X, int.class);
    }

    public int getY(){
        return get(KEY_Y, int.class);
    }

    public String getLogoTitle(){
        return get(KEY_LOGO_TITLE, String.class);
    }

    public String getImageTitle(){
        return get(KEY_IMAGE_TITLE, String.class);
    }

    public void setImage() {
        put(KEY_IMAGE, image);
    }

    public void setLogo() {
        put(KEY_LOGO, logo);
    }

    public void setX() {
        put(KEY_X, x);
    }

    public void setY() {
        put(KEY_Y, y);
    }

    public void setLogoTitle() {
        put(KEY_LOGO_TITLE, logoTitle);
    }

    public void setImageTitle() {
        put(KEY_IMAGE_TITLE, imageTitle);
    }
}

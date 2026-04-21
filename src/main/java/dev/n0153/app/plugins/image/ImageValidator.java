package dev.n0153.app.plugins.image;

import dev.n0153.app.*;
import dev.n0153.app.exceptions.UnsupportedFileTypeException;
import org.opencv.core.Mat;

public class ImageValidator implements MediaValidator {
    private ImageConfig config;
    private ImageContext context;

    public void createMeta(ImageConfig config, ImageContext context) {
        this.config = config;
        this.context = context;
    }

    @Override
    public ValidationResult validate() {
        return null;
    }

    public boolean checkMeta() {
        return this.config != null || this.context != null;
    }

    public static boolean checkEmpty(Mat image) {
        return !image.empty();
    }

    public static boolean checkMimeWhiteList(String fileType, String mimeType) throws UnsupportedFileTypeException {
        try {
            FormatRegistry.AllowList list = FormatRegistry.AllowList.valueOf(fileType.toUpperCase());
            return list.isValidFormat(mimeType.toLowerCase());
        } catch(IllegalArgumentException e) {
            throw new UnsupportedFileTypeException("failed to detect file type", fileType, e);
        }
    }
}

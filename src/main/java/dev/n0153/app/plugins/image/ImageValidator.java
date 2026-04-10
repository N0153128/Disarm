package dev.n0153.app.plugins.image;

import dev.n0153.app.*;
import dev.n0153.app.exceptions.UnsupportedFileTypeException;

public class ImageValidator implements MediaValidator<ImageConfig> {

    @Override
    public ValidationResult validate(ProcessingContext context, ImageConfig config) {
        return null;
    }
    public static boolean checkMimeWhiteList(String fileType, String mimeType) throws UnsupportedFileTypeException {
        if (fileType == null || mimeType == null) {
            return false;
        }
        try {
            FormatRegistry.AllowList list = FormatRegistry.AllowList.valueOf(fileType.toUpperCase());
            return list.isValidFormat(mimeType.toLowerCase());
        } catch(IllegalArgumentException e) {
            throw new UnsupportedFileTypeException("failed to detect file type", fileType, e);
        }
    }

}

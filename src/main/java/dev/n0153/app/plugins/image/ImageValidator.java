package dev.n0153.app.plugins.image;

import dev.n0153.app.*;
import dev.n0153.app.exceptions.ValidationException;

import java.nio.file.Files;
import java.nio.file.Path;

public class ImageValidator implements MediaValidator {
    private ImageConfig config;
    private ImageContext context;

    public void createMeta(ImageConfig config, ImageContext context) {
        this.config = config;
        this.context = context;
    }

    @Override
    public boolean validate(Path osTargetPath) {
        if (!checkMeta()) {
            throw new ValidationException("Plugin meta data is empty");
        }
        return true;
    }

    public boolean checkMeta() {
        return this.config != null && this.context != null;
    }

    public boolean validateLogo(Path osTargetPath) {
        if (!checkMeta()) {
            throw new ValidationException("Plugin meta data is empty");
        }
        if (!checkLogoSizeLimit(osTargetPath)) {
            throw new ValidationException("Logo file size exceeds logo file size limit");
        }
        return true;
    }

    public boolean checkLogoSizeLimit(Path osTargetPath) {
        long size = Utils.getSize(osTargetPath);
        return size <= config.getLogoSizeLimit();
    }
}

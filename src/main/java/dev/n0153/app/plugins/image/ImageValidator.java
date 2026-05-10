package dev.n0153.app.plugins.image;

import dev.n0153.app.*;
import dev.n0153.app.exceptions.InvalidPathException;
import dev.n0153.app.exceptions.ValidationException;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.IOException;
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
        if (!checkEmpty(osTargetPath)) {
            throw new InvalidPathException("Path is not readable", osTargetPath);
        }
        return true;
    }

    public boolean checkMeta() {
        return this.config != null || this.context != null;
    }

    public static boolean checkEmpty(Path osTargetPath) {
        try {
            return Files.size(osTargetPath) == 0;
        } catch (IOException e) {
            throw new ValidationException("Failed to check file size");
        }
    }
}

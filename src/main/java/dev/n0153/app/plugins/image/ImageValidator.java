package dev.n0153.app.plugins.image;

import dev.n0153.app.MediaValidator;
import dev.n0153.app.ProcessingContext;
import dev.n0153.app.ProcessorConfig;
import dev.n0153.app.ValidationResult;

public class ImageValidator implements MediaValidator<ImageConfig> {

    @Override
    public ValidationResult validate(ProcessingContext context, ImageConfig config) {
        return null;
    }
}

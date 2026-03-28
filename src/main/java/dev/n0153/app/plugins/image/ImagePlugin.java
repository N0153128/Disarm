package dev.n0153.app.plugins.image;

import dev.n0153.app.MediaProcessor;
import dev.n0153.app.ProcessingContext;
import dev.n0153.app.Validator;
import dev.n0153.app.exceptions.DisarmException;

public class ImagePlugin implements MediaProcessor<ImageConfig> {
    private final ImageValidator validator = new ImageValidator();

    @Override
    public Validator getValidator() {
        return null;
    }

    @Override
    public ProcessingContext process(ProcessingContext context, ImageConfig config) {
        return null;
    }

    @Override
    public void process() throws DisarmException {

    }
}

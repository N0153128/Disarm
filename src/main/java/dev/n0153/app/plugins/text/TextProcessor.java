package dev.n0153.app.plugins.text;

import dev.n0153.app.MediaContext;
import dev.n0153.app.MediaProcessor;
import dev.n0153.app.exceptions.DisarmException;

import java.nio.file.Path;

public class TextProcessor implements MediaProcessor<TextConfig> {
    @Override
    public void process(Path osTargetPath) throws DisarmException {

    }

    @Override
    public MediaContext getContext() {
        return null;
    }
}

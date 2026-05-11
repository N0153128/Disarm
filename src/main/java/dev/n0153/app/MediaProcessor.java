package dev.n0153.app;

import dev.n0153.app.exceptions.DisarmException;

import java.nio.file.Path;

/**
 * This interface holds processing logic.
 */
public interface MediaProcessor<Config extends MediaConfig> {

    ProcessingContext process(ProcessingContext context, Config config);

    /**
     * Default processing logic interface.
     * @throws DisarmException if processing fails
     */
    void process(Path osTargetPath) throws DisarmException;
    MediaContext getContext();
    void createMeta(Object... args);
}
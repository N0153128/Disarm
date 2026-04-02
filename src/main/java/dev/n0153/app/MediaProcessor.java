package dev.n0153.app;

import dev.n0153.app.exceptions.DisarmException;

/**
 * This interface holds processing logic.
 */
public interface MediaProcessor<Config extends ProcessorConfig> {

    MediaValidator<Config> getValidator();
    ProcessingContext process(ProcessingContext context, Config config);

    /**
     * Default processing logic interface.
     * @throws DisarmException if processing fails
     */
    void process() throws DisarmException;

}

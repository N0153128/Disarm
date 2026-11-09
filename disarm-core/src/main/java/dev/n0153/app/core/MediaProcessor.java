package dev.n0153.app.core;

import dev.n0153.app.core.exceptions.DisarmException;

import java.nio.file.Path;

/**
 * Contract for a plugin's media-processing logic: takes a validated input
 * file and performs the actual disarm/re-encode operation, using the
 * processor's associated context for runtime state.
 * @param <Config> the {@link MediaConfig} type this processor is configured by
 * @since 0.2
 */
public interface MediaProcessor<Config extends MediaConfig> {

    /**
     * Processes the given input file, producing a sanitized output.
     * @param osTargetPath path to the input file to process
     * @throws DisarmException if processing fails
     * @since 0.2
     */
    void process(Path osTargetPath) throws DisarmException;

    /**
     * @return the runtime context tracking this processor's per-file state
     * @since 0.2
     */
    MediaContext getContext();
}
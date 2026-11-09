package dev.n0153.app.core;

import java.nio.file.Path;

/**
 * Contract for a plugin's media-specific validation logic, run after
 * {@link GlobalValidator}'s checks have passed, before the file is handed
 * to the plugin's {@link MediaProcessor}.
 * @since 0.2
 */
public interface MediaValidator {

    /**
     * Checks the input file against this plugin's configured size limit.
     * @param osTargetPath path to the input file
     * @return true if the file's size is within the plugin's configured limit
     * @since 0.2
     */
    boolean validateFileSize(Path osTargetPath);

    /**
     * Runs the full set of plugin-specific validation checks on the input file
     * (e.g. codec, bitrate, dimensions, duration, encoding, as applicable to the media type).
     * @param osTargetPath path to the input file
     * @return true if all checks pass
     * @since 0.2
     */
    boolean validate(Path osTargetPath);
}

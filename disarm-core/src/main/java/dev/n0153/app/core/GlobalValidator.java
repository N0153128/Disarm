package dev.n0153.app.core;

import dev.n0153.app.core.exceptions.InvalidPathException;
import dev.n0153.app.core.exceptions.UnsafePathException;
import dev.n0153.app.core.exceptions.ValidationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Performs global, cross-plugin validation checks on input files and output
 * paths before a file is handed off to a plugin-specific validator.
 * @since 0.2
 */
public class GlobalValidator {
    private static final Logger logger = LogManager.getLogger(GlobalValidator.class);

    /**
     * Path validation method, ensures that the user cannot get out of bounds and reach sensitive files.
     * NOTE!: Absolute path checking is disabled for release 0.1 due to local nature of the project.
     * This check will be re-enabled once networking features are implemented.
     * @param osTargetPath Path to input file.
     * @return True if path is valid, throws UnsafePathException if validation checks fail.
     * @since 0.1
     */
    public static boolean validatePath(Path osTargetPath, GlobalConfig config) {
        if (config.getRestrictInputFromRoot()) {
            Path allowedRoot = config.getInputRootPath().toAbsolutePath();
            Path resolved = osTargetPath.toAbsolutePath().normalize();
            if (!resolved.startsWith(allowedRoot)) {
                return false;
            }
        }
        if (Files.isSymbolicLink(osTargetPath)) {
            return false;
        }
        return Files.exists(osTargetPath);
    }

    /**
     * Checks if provided file is readable.
     * @param osTargetPath Path to input file.
     * @return True if file is readable.
     * @since 0.1
     */
    public static boolean isInputReadable(Path osTargetPath) {
        return Files.isReadable(osTargetPath);
    }

    /**
     * Checks whether the given path exists on disk.
     * @param osTargetPath path to check
     * @return true if the path exists
     * @since 0.2
     */
    public static boolean outputExist(Path osTargetPath) {
        return Files.exists(osTargetPath);
    }

    /**
     * Checks if configured output path is writeable.
     * @param osTargetPath Path to output directory.
     * @return True if path is writeable.
     * @since 0.1
     */
    public static boolean isOutputPathWritable(Path osTargetPath) {
        return Files.isWritable(osTargetPath);
    }

    /**
     * Checks that the input file does not exceed the configured global size ceiling.
     * @param osTargetPath path to the input file
     * @param config global config supplying the size limit
     * @return true if the file's size is within {@link GlobalConfig#getGeneralSizeLimit()}
     * @since 0.2
     */
    public static boolean ensureGlobalSizeLimit(Path osTargetPath, GlobalConfig config) {
        long size = Utils.getSize(osTargetPath);
        return size <= config.getGeneralSizeLimit();
    }

    /**
     * Checks that the input file is not empty.
     * @param osTargetPath path to the input file
     * @return true if the file's size is greater than zero
     * @throws ValidationException if the file size cannot be determined
     * @since 0.2
     */
    public static boolean checkEmpty(Path osTargetPath) {
        try {
            return Files.size(osTargetPath) > 0;
        } catch (IOException e) {
            throw new ValidationException("Failed to check file size");
        }
    }

    /**
     * Runs the full global validation sequence on an input file: path safety,
     * readability, output directory existence/creation, output writability,
     * non-empty check, and global size ceiling. The output directory is
     * created automatically if missing.
     * @param osTargetPath path to the input file
     * @param config global config supplying paths and limits
     * @return true if all checks pass
     * @throws UnsafePathException if the path fails the path-safety check
     * @throws InvalidPathException if the input file is not readable
     * @throws java.nio.file.InvalidPathException if the output path is not writable
     * @throws ValidationException if the input file is empty or exceeds the global size limit
     * @since 0.2
     */
    public static boolean validate(Path osTargetPath, GlobalConfig config) {
        if (!validatePath(osTargetPath, config)) {
            throw new UnsafePathException("Potentially unsafe path", osTargetPath);
        }
        if (!isInputReadable(osTargetPath)) {
            throw new InvalidPathException("Path is not readable", osTargetPath);
        }
        if (!outputExist(config.getGeneralOutputPath())) {
            logger.warn("Output directory doesn't exist, creating one...");
            Utils.createDirectory(config.getGeneralOutputPath());
        }
        if (!isOutputPathWritable(config.getGeneralOutputPath())) {
            throw new java.nio.file.InvalidPathException("Output path is not writeable: " + config.getGeneralOutputPath(),
                    config.getGeneralOutputPath().toString());
        }
        if (!checkEmpty(osTargetPath)) {
            throw new ValidationException("Input file is empty");
        }
        if (!ensureGlobalSizeLimit(osTargetPath, config)) {
            throw new ValidationException("Input path exceeds global size limit");
        }
        return true;
    }

}

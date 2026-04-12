package dev.n0153.app;

import dev.n0153.app.exceptions.UnsafePathException;

import java.nio.file.Files;
import java.nio.file.Path;

public class GlobalValidator {

    /**
     * Path validation method, ensures that the user cannot get out of bounds and reach sensitive files.
     * NOTE!: Absolute path checking is disabled for release 0.1 due to local nature of the project.
     * This check will be re-enabled once networking features are implemented.
     * @param osTargetPath Path to input file.
     * @return True if path is valid, throws UnsafePathException if validation checks fail.
     * @since 0.1
     */
    public static boolean validatePath(Path osTargetPath) {
        boolean singleDot = osTargetPath.toString().contains("./");
        boolean doubleDot = osTargetPath.toString().contains("../");
//        boolean absolute = osTargetPath.toString().startsWith("/");
        if (singleDot) {
            throw new UnsafePathException("potentially dangerous pathing, \"./\" ", osTargetPath);
        }
        if (doubleDot) {
            throw new UnsafePathException("potentially dangerous pathing, \"../\"", osTargetPath);
        }
        // disabled absolute path checking util networking features implemented
//        if (absolute) {
//            throw new UnsafePathException("potentially dangerous absolute pathing", osTargetPath);
//        }
        if (Files.isSymbolicLink(osTargetPath)) {
            throw new UnsafePathException("potentially dangerous symlink pathing", osTargetPath);
        }
        if (!Files.exists(osTargetPath)) {
            throw new UnsafePathException("Path doesnt exist", osTargetPath);
        }
        return true;
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
     * Checks if configured output path is writeable.
     * @param osTargetPath Path to output directory.
     * @return True if path is writeable.
     * @since 0.1
     */
    public static boolean isOutputPathWritable(Path osTargetPath) {
        return Files.isWritable(osTargetPath);
    }

    public static boolean ensureGlobalSizeLimit(Path osTargetPath, GlobalConfig config) {
        long size = Utils.getSize(osTargetPath);
        return size <= config.getGeneralSizeLimit();
    }

}

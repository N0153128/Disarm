package dev.n0153.app;

import dev.n0153.app.exceptions.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.FileSystemException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * General utilities class, immutable.
 * @since 0.1
 */
public class Utils {
    private static final Logger logger = LogManager.getLogger(Utils.class);

    record MimeSignature(byte[] byteSignature, boolean[] mask) {}
    private static final Map<String, MimeSignature> mimeSignatures = new HashMap<>() {{
        put("jpeg", new MimeSignature(
                new byte[]{ (byte)0xFF, (byte)0xD8, (byte)0xFF},
                new boolean[]{true, true, true}
        ));
        put("png", new MimeSignature(
                new byte[]{ (byte)0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A},
                new boolean[]{true, true, true, true, true, true, true, true}
        ));
        put("webp", new MimeSignature(
                new byte[]{0x52, 0x49, 0x46, 0x46, 0x00, 0x00, 0x00, 0x00, 0x57, 0x45, 0x42, 0x50},
                new boolean[]{true, true, true, true, false, false, false, false, true, true, true, true}
        ));
        put("mp3-id3", new MimeSignature(
                new byte[]{0x49, 0x44, 0x33},
                new boolean[]{true, true, true}
        ));
        put("mp3-sync", new MimeSignature(
                new byte[]{(byte)0xFF, (byte)0xFB},
                new boolean[]{true, true}
        ));
        put("mp3-sync-v2", new MimeSignature(
                new byte[]{ (byte)0xFF, (byte)0xF3},
                new boolean[]{true, true}
        ));
        put("mp3-sync-v3", new MimeSignature(
                new byte[]{(byte)0xFF, (byte)0xF2},
                new boolean[]{true, true}
        ));
        put("wav", new MimeSignature(
                new byte[]{ 0x52, 0x49, 0x46, 0x46, 0x00, 0x00, 0x00, 0x00, 0x57, 0x41, 0x56, 0x45},
                new boolean[]{true, true, true, true, false, false, false, false, true, true, true, true}
        ));
        put("ogg", new MimeSignature(
                new byte[]{ 0x4F, 0x67, 0x67, 0x53},
                new boolean[]{true, true, true, true}
        ));
        put("flac", new MimeSignature(
                new byte[]{0x66, 0x4C, 0x61, 0x43},
                new boolean[]{true, true, true, true}
        ));
        put("aiff", new MimeSignature(
                new byte[]{0x46, 0x4F, 0x52, 0x4D, 0x00, 0x00, 0x00, 0x00, 0x41, 0x49, 0x46, 0x46},
                new boolean[]{true, true, true, true, false, false, false, false, true, true, true, true}
        ));
        put("au", new MimeSignature(
                new byte[]{0x2E, 0x73, 0x6E, 0x64},
                new boolean[]{true, true, true, true}
        ));
        put("mp4", new MimeSignature(
                new byte[]{0x00, 0x00, 0x00, 0x00, 0x66, 0x74, 0x79, 0x70, 0x69, 0x73, 0x6F, 0x6D},
                new boolean[]{false, false, false, false, true, true, true, true, true, true, true, true}
        ));
        put("mov", new MimeSignature(
                new byte[]{0x00, 0x00, 0x00, 0x00, 0x66, 0x74, 0x79, 0x70, 0x71, 0x74, 0x20, 0x20},
                new boolean[]{false, false, false, false, true, true, true, true, true, true, true, true}
        ));
        put("webm", new MimeSignature(
                new byte[]{0x1A, 0x45, (byte)0xDF, (byte)0xA3},
                new boolean[]{true, true, true, true}
        ));
        put("x-matroska", new MimeSignature(
                new byte[]{0x1A, 0x45, (byte)0xDF, (byte)0xA3},
                new boolean[]{true, true, true, true}
        ));
        put("zip", new MimeSignature(
                new byte[]{0x50, 0x4B, 0x03, 0x04},
                new boolean[]{true, true, true, true}
        ));
        put("rar", new MimeSignature(
                new byte[]{0x52, 0x61, 0x72, 0x21, 0x1A, 0x07},
                new boolean[]{true, true, true, true, true, true}
        ));
        put("gz", new MimeSignature(
                new byte[]{0x1F, (byte)0x8B},
                new boolean[]{true, true}
        ));
        put("pdf", new MimeSignature(
                new byte[]{0x25, 0x50, 0x44, 0x46},
                new boolean[]{true, true, true, true}
        ));
        put("doc", new MimeSignature(
                new byte[]{(byte)0xD0, (byte)0xCF, 0x11, (byte)0xE0, (byte)0xA1, (byte)0xB1, 0x1A, (byte)0xE1},
                new boolean[]{true, true, true, true, true, true, true, true}
        ));
        put("class", new MimeSignature(
                new byte[]{(byte)0xCA, (byte)0xFE, (byte)0xBA, (byte)0xBE},
                new boolean[]{true, true, true, true}
        ));
        put("elf", new MimeSignature(
                new byte[]{ 0x7F, 0x45, 0x4C, 0x46},
                new boolean[]{true, true, true, true}
        ));
        put("sqlite", new MimeSignature(
                new byte[]{0x53, 0x51, 0x4C, 0x69, 0x74, 0x65, 0x20, 0x66, 0x6F, 0x72, 0x6D, 0x61, 0x74, 0x20, 0x33, 0x00},
                new boolean[]{true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true}
        ));
    }};

    public static String getMimeFromSignature(Path osTargetPath) {
        byte[] header = new byte[16];
        String result = "";
        try (FileInputStream stream = new FileInputStream(osTargetPath.toFile())) {
            stream.read(header);
        } catch (IOException e) {
            throw new DisarmException("Failed to read magic bytes");
        }
        for(Map.Entry<String, MimeSignature> entry : mimeSignatures.entrySet()) {
            byte[] signature = entry.getValue().byteSignature();
            boolean[] mask = entry.getValue().mask();
            boolean match = true;
            for (int i = 0; i < signature.length; i++) {
                if (!mask[i]) continue;
                if (header[i] != signature[i]) {
                    match = false;
                    break;
                }
            }
            if (match) result = entry.getKey();
        }
        return result;
    }

    /**
     * Shortcut method, generates title for a file that is currently in processing.
     * The title is then saved to state and can be accessed with state.getGeneralFileTitle().
     * @param logo determines if file in processing is a logo or media file
     * @since 0.1
     */
    public static String getTitle(Path osTargetPath, String mime, boolean logo) {
        LocalDateTime now = LocalDateTime.now();
        String day = String.valueOf(now.getDayOfMonth());
        String month = String.valueOf(now.getMonthValue());
        String year = String.valueOf(now.getYear());
        String hour = String.valueOf(now.getHour());
        String minute = String.valueOf(now.getMinute());
        String second = String.valueOf(now.getSecond());
        String nanoSecond = String.valueOf(now.getNano());
        String milliSecond = String.valueOf(now.getNano() / 1_000_000);
        String type;
        if (logo) {
            type = "logo";
        } else {
            try {
                type = Utils.getFileType(osTargetPath);
            } catch (FileTypeDetectionException e) {
                throw new RuntimeException(e);
            }
        }
        String objectName = null;
        objectName = type+"_"+year+"_"+month+"_"+day+
                "_"+hour+"_"+minute+"_"+second+
                "_"+nanoSecond+"_"+milliSecond+"."+ mime;
        return objectName;
    }

    /**
     * Shortcut method, generates title for a file that is currently in processing.
     * The title is then saved to state and can be accessed with state.getGeneralFileTitle().
     * @since 0.1
     */
    public static String getTitle(String forcedTag, String mime) {
        LocalDateTime now = LocalDateTime.now();
        String day = String.valueOf(now.getDayOfMonth());
        String month = String.valueOf(now.getMonthValue());
        String year = String.valueOf(now.getYear());
        String hour = String.valueOf(now.getHour());
        String minute = String.valueOf(now.getMinute());
        String second = String.valueOf(now.getSecond());
        String nanoSecond = String.valueOf(now.getNano());
        String milliSecond = String.valueOf(now.getNano() / 1_000_000);
        return forcedTag+"_"+year+"_"+month+"_"+day+
                "_"+hour+"_"+minute+"_"+second+
                "_"+nanoSecond+"_"+milliSecond+"."+ mime;
    }

    @SuppressWarnings("unchecked")
    public static <$T> $T requireArgs(Object[] args, int index, Class<$T> type) {
        if (!type.isInstance(args[index])) {
            throw new ValidationException("Argument " + (index+1) + " must be an instance of " +
                    type.getSimpleName());
        }
        return ($T) args[index];
    }

    /**
     * Renames specified file.
     * @param osTargetFile Path to file.
     * @param newTitle New title.
     * @throws FileRenameException If file isn't supported.
     * @since 0.1
     */
    public static void renameFile(Path osTargetFile, String newTitle) throws FileRenameException {
        try {
            Files.move(osTargetFile, osTargetFile.getParent().resolve(newTitle));
        } catch (IOException e) {
            throw new FileRenameException("failed to rename file",  osTargetFile, e);
        }
    }

    /**
     * Returns file type of given file.
     * @param osTargetFile Path to file
     * @return File type.
     * @throws FileTypeDetectionException If fails to detect file type.
     * @since 0.1
     */
    public static String getFileType(Path osTargetFile) throws FileTypeDetectionException {
        try {
            if (!Files.exists(osTargetFile)) {
                throw new IOException("File doesn't exist");
            }; // skip non-existent paths
            if (!Files.isRegularFile(osTargetFile)) {
                throw new IOException("Not a file");
            }
            String fileType = Files.probeContentType(osTargetFile);
            if (fileType == null) {
                throw new FileTypeDetectionException("failed to detect file type, object is null: ", osTargetFile);
            }
            return fileType.split("/")[0];

        } catch (IOException e) {
            throw new FileTypeDetectionException("failed to detect file type", osTargetFile, e);
        }
    }

    /**
     * Shortcut method, get mime type of given file.
     * NOTE! This method relies on Files.probeContentType(), which uses platform-specific implementations, which
     * don't provide any content validation whatsoever.
     * Proper magic bytes check coming in version 0.2.
     * @param osTargetFilePath Path to file
     * @return Subtype of a specified file's mime type.
     * @throws MimeTypeDetectionException if fails to detect mime type.
     * @since 0.1
     */
    public static String getMimeType(Path osTargetFilePath) throws MimeTypeDetectionException {
        try {
            String mime = Files.probeContentType(osTargetFilePath);
            return mime.split("/")[1];
        } catch (IOException e) {
            throw new MimeTypeDetectionException("failed to detect mime type", osTargetFilePath, e);
        } catch (NullPointerException e) {
            throw new MimeTypeDetectionException("specified mime type isn't whitelisted", osTargetFilePath, e);
        }
    }

    /**
     * Check if specified file is an image file.
     * @param osTargetFilePath Path to file.
     * @return True if file is an image file, false otherwise.
     * @since 0.1
     */
    public static boolean isImage(Path osTargetFilePath) {
        try {
            String fileType = getFileType(osTargetFilePath);
            return Objects.equals(fileType, "image");

        } catch (FileTypeDetectionException e) {
            throw new ValidationException("Specified file is not an image");
        }
    }

    /**
     * Check if specified file is an audio file.
     * @param osTargetFilePath Path to file.
     * @return True if file is an audio file, false otherwise.
     * @since 0.1
     */
    public static boolean isAudio(Path osTargetFilePath) {
        try {
            String fileType = getFileType(osTargetFilePath);
            return Objects.equals(fileType, "audio");
        } catch (FileTypeDetectionException e) {
            throw new ValidationException("Specified file is not an audio");
        }
    }

    /**
     * Check if specified file is a video file.
     * @param osTargetFilePath Path to file.
     * @return True if file is a video file, false otherwise.
     * @since 0.1
     */
    public static boolean isVideo(Path osTargetFilePath) {
        try {
            String fileType = getFileType(osTargetFilePath);
            return Objects.equals(fileType, "video");

        } catch (FileTypeDetectionException e) {
            throw new ValidationException("Specified file is not a video");
        }
    }

    /**
     * Check if specified file is a text file.
     * @param osTargetFilePath Path to file.
     * @return True if file is a text file, false otherwise.
     * @since 0.1
     */
    public static boolean isText(Path osTargetFilePath) {
        try {
            String fileType = getFileType(osTargetFilePath);
            return Objects.equals(fileType, "text");
        } catch (FileTypeDetectionException e) {
            throw new ValidationException("Specified file is not a text");
        }
    }

    /**
     * Shortcut method, delete any specified file.
     * @param osTargetFilePath Path to file.
     * @throws FileDeletionException If file doesn't exist or unable to delete.
     * @since 0.1
     */
    public static void fileDispose(Path osTargetFilePath) throws FileDeletionException {
        try {
            if (!Files.exists(osTargetFilePath)) {
                throw new FileDeletionException("Specified file doesn't exist", osTargetFilePath);
            }
            File file = osTargetFilePath.toFile();
            boolean deleted = Files.deleteIfExists(file.toPath());
            if (!deleted) {
                throw new FileSystemException(osTargetFilePath.toString());
            }
        } catch (IOException e) {
            throw new FileDeletionException("failed to delete file", osTargetFilePath, e);
        }
    }

    public static void createDirectory(Path dirToCreate) {
        try {
            Files.createDirectories(dirToCreate);
            logger.info("Directory created successfully: {}", dirToCreate);
        } catch (IOException e) {
            throw new DisarmException("Failed to create missing directory: ", e);
        }
    }

    /**
     * Returns the size, in bytes of the specified file.
     * @param osFilePath Path to file.
     * @return File length.
     * @since 0.1
     */
    public static long getSize(Path osFilePath) {
        File file = osFilePath.toFile();
        return file.length();
    }
}

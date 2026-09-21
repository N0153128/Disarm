package dev.n0153.app;

import com.sun.jdi.CharType;
import dev.n0153.app.exceptions.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.NullConfiguration;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.*;
import java.security.SecureRandom;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.FileSystemException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
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
        put("mp3/id3", new MimeSignature(
                new byte[]{0x49, 0x44, 0x33},
                new boolean[]{true, true, true}
        ));
        put("mp3/sync", new MimeSignature(
                new byte[]{(byte)0xFF, (byte)0xFB},
                new boolean[]{true, true}
        ));
        put("mp3/sync-v2", new MimeSignature(
                new byte[]{ (byte)0xFF, (byte)0xF3},
                new boolean[]{true, true}
        ));
        put("mp3/sync-v3", new MimeSignature(
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
        put("mov/m4v", new MimeSignature(
                new byte[]{0x00, 0x00, 0x00, 0x00, 0x66, 0x74, 0x79, 0x70, 0x4D, 0x34, 0x56, 0x20},
                new boolean[]{false, false, false, false, true, true, true, true, true, true, true, true}
        ));
        put("mov/mp41", new MimeSignature(
                new byte[]{0x00, 0x00, 0x00, 0x00, 0x66, 0x74, 0x79, 0x70, 0x6D, 0x70, 0x34, 0x31},
                new boolean[]{false, false, false, false, true, true, true, true, true, true, true, true}
        ));
        put("mov/mp42", new MimeSignature(
                new byte[]{0x00, 0x00, 0x00, 0x00, 0x66, 0x74, 0x79, 0x70, 0x6D, 0x70, 0x34, 0x32},
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
        put("gif/87a", new MimeSignature(
                new byte[]{0x47, 0x49, 0x46, 0x38, 0x37, 0x61},
                new boolean[]{true, true, true, true, true, true}
        ));
        put("gif/89a", new MimeSignature(
                new byte[]{0x47, 0x49, 0x46, 0x38, 0x39, 0x61},
                new boolean[]{true, true, true, true, true, true}
        ));
        put("tiff/le", new MimeSignature(
                new byte[]{0x49, 0x49, 0x2A, 0x00},
                new boolean[]{true, true, true, true}
        ));
        put("tiff/be", new MimeSignature(
                new byte[]{0x4D, 0x4D, 0x00, 0x2A},
                new boolean[]{true, true, true, true}
        ));
    }};

    private static final List<String> textFormats = List.of("txt", "log", "json");

    public static final int SAMPLE_SIZE_IN_BYTES = 8192;
    private static final double MAX_CONTROL_CHARACTER_RATIO = 0.02;
    private static final double MIN_UTF16_NUL_RATIO = 0.3;

    private static int toUnsignedValue(byte signedByte) {
        return signedByte & 0xFF;
    }

    private static boolean canBeDecoded(
        Charset charset,
        byte[] bytes,
        int length,
        boolean dataWasTruncated
    ) {
        CharsetDecoder decoder = charset.newDecoder();
        decoder.onMalformedInput(CodingErrorAction.REPORT);
        decoder.onUnmappableCharacter(CodingErrorAction.REPORT);

        ByteBuffer inputBuffer = ByteBuffer.wrap(bytes, 0, length);
        CharBuffer outputBuffer = CharBuffer.allocate(4096);
        boolean isEndOfInput = !dataWasTruncated;

        while (true) {
            CoderResult decodeResult = decoder.decode(inputBuffer, outputBuffer, isEndOfInput);
            if (decodeResult.isError()) {
                return false;
            }
            if (decodeResult.isUnderflow()) {
                return true;
            }
            outputBuffer.clear();
        }
    }

    private static Charset detectCharsetFromByteOrderMark(byte[] bytes, int length) {
        if (length >= 3) {
            int firstByte = toUnsignedValue(bytes[0]);
            int secondByte = toUnsignedValue(bytes[1]);
            int thirdByte = toUnsignedValue(bytes[2]);

            if (firstByte == 0xEF && secondByte == 0xBB && thirdByte == 0xBF) {
                return StandardCharsets.UTF_8;
            }
        }
        if (length >= 2) {
            int firstByte = toUnsignedValue(bytes[0]);
            int secondByte = toUnsignedValue(bytes[1]);

            if (firstByte == 0xFF && secondByte == 0xFE) {
                return StandardCharsets.UTF_16LE;
            }
        }
        return null;
    }

    private static Charset guessUtf16WithoutByteOrderMark(byte[] bytes, int length) {
        if (length < 4) {
            return null;
        }

        int nulCountAtEvenPositions = 0;
        int nulCountAtOddPositions = 0;

        for (int position = 0; position < length; position++) {
            if (bytes[position] == 0) {
                boolean positionIsEven = (position % 2 == 0);
                if (positionIsEven) {
                    nulCountAtEvenPositions ++;
                } else {
                    nulCountAtOddPositions ++;
                }
            }
        }

        int expectedNulCount = Math.max(nulCountAtEvenPositions, nulCountAtOddPositions);
        int strayNulCount = Math.min(nulCountAtEvenPositions, nulCountAtOddPositions);

        int characterCount = length / 2;
        double minimumExpectedNulCount = characterCount * MIN_UTF16_NUL_RATIO;
        double maximumStrayNulCount = expectedNulCount * MAX_CONTROL_CHARACTER_RATIO;

        if (expectedNulCount <= minimumExpectedNulCount) {
            return null;
        }

        if (strayNulCount > maximumStrayNulCount) {
            return null;
        }

        if (nulCountAtEvenPositions > nulCountAtOddPositions) {
            return StandardCharsets.UTF_16BE;
        }

        return StandardCharsets.UTF_16LE;
    }

    private static boolean containsNulByte(byte[] bytes, int length) {
        for (int position = 0; position < length; position++) {
            if (bytes[position] == 0) {
                return true;
            }
        }
        return false;
    }

    private static boolean isSuspiciousControlCharacter(int value) {
        boolean isControlCharacter = (value < 0x20) || (value == 0x7F);

        boolean isAllowedInText = (value == '\t')
                || (value == '\n')
                || (value == '\r')
                || (value == '\f')
                || (value == 0x1B);
        return isControlCharacter && !isAllowedInText;
    }

    private static boolean hasTooManyControlCharacters(byte[] bytes, int length) {
        int suspiciousCount = 0;
        for (int position = 0; position < length; position++) {
            int value = toUnsignedValue(bytes[position]);
            if (isSuspiciousControlCharacter(value)) {
                suspiciousCount++;
            }
        }
        double maximumAllowedCount = length * MAX_CONTROL_CHARACTER_RATIO;
        return suspiciousCount > maximumAllowedCount;
    }

    private static boolean hasTooManyControlCharactersAfterDecoding(
            Charset charset, byte[] bytes, int length
    ) {
        String decodedText = new String(bytes, 0, length, charset);
        int suspiciousCount = 0;
        for (int position = 0; position < decodedText.length(); position++) {
            char character = decodedText.charAt(position);
            if (isSuspiciousControlCharacter(character)) {
                suspiciousCount++;
            }
        }
        double maximumAllowedCount = decodedText.length() * MAX_CONTROL_CHARACTER_RATIO;
        return suspiciousCount > maximumAllowedCount;
    }

    private static boolean isPureAscii(byte[] bytes, int length) {
        for (int position = 0; position < length; position++) {
            int value = toUnsignedValue(bytes[position]);
            if (value > 127) {
                return false;
            }
        }
        return true;
    }

    public static Charset detectTextCharset(byte[] bytes, int length, boolean dataWasTruncated) {
        if (length == 0) {
            return StandardCharsets.UTF_8;
        }

        Charset charsetFromBom = detectCharsetFromByteOrderMark(bytes, length);
        if (charsetFromBom != null) {
            if (canBeDecoded(charsetFromBom, bytes, length, dataWasTruncated)) {
                return charsetFromBom;
            }
            return null;
        }

        Charset guessedUtf16 = guessUtf16WithoutByteOrderMark(bytes, length);
        if (guessedUtf16 != null) {
            boolean decodesCleanly = canBeDecoded(guessedUtf16, bytes, length, dataWasTruncated);
            boolean looksLikeText =
                    !hasTooManyControlCharactersAfterDecoding(guessedUtf16, bytes, length);
            if (decodesCleanly && looksLikeText) {
                return guessedUtf16;
            }
        }

        if (containsNulByte(bytes, length)) {
            return null;
        }
        if (hasTooManyControlCharacters(bytes, length)) {
            return null;
        }
        if (isPureAscii(bytes, length)) {
            return StandardCharsets.US_ASCII;
        }
        if (canBeDecoded(StandardCharsets.UTF_8, bytes, length, dataWasTruncated)) {
            return StandardCharsets.UTF_8;
        }
        return StandardCharsets.ISO_8859_1;
    }

    public static Charset detectTextCharset(Path osTargetPath) throws IOException {
        try (InputStream fileStream = Files.newInputStream(osTargetPath)) {
            byte[] sampleBytes = fileStream.readNBytes(SAMPLE_SIZE_IN_BYTES);
            boolean fileHasMoreData = fileStream.read() != -1;
            return detectTextCharset(sampleBytes, sampleBytes.length, fileHasMoreData);
        }
    }

    public static String checkWebmOrMkvOverDocType(Path osTargetPath) {
        String result = null;
        byte[] docType = new byte[64];
        try (FileInputStream stream = new FileInputStream(osTargetPath.toFile())) {
            stream.read(docType);
        } catch (IOException e) {
            throw new MimeTypeDetectionException("Failed to read magic bytes", osTargetPath);
        }
        String bufferString = new String(docType, StandardCharsets.US_ASCII);
        logger.info("buffer: {}", bufferString);
        if (bufferString.contains("webm")) result = "webm";
        if (bufferString.contains("matroska")) result = "x-matroska";
        return result;
    }

    public static String getFileExtension(Path osTargetPath) {
        String filename = osTargetPath.getFileName().toString();
        int dotIndex = filename.lastIndexOf('.');
        return dotIndex >= 0 ? filename.substring(dotIndex + 1) : "";
    }

    public static String getMimeFromSignature(Path osTargetPath) {
        byte[] header = new byte[16];
        try (FileInputStream stream = new FileInputStream(osTargetPath.toFile())) {
            stream.read(header);
        } catch (IOException e) {
            throw new MimeTypeDetectionException("Failed to read magic bytes", osTargetPath);
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
            if (match) {
                if (Objects.equals(entry.getKey(), "webm") || Objects.equals(entry.getKey(), "x-matroska")) {
                    return checkWebmOrMkvOverDocType(osTargetPath);
                }
                if (entry.getKey().contains("/")) {
                    return entry.getKey().split("/")[0];
                } else {
                    return entry.getKey();
                }
            }
        }
        String extension = getFileExtension(osTargetPath);
        if (textFormats.contains(extension)) {
            return extension;
        } else {
            throw new MimeTypeDetectionException(
                    "Provided file's mime type cannot be verified or is not supported",
                    osTargetPath
            );
        }
    }

    public static void setJaveLogging(Level level) {
        Configurator.setLevel("ws.schild.jave", level);
    }

    public static void setDisarmLogging(Level level) {
        Configurator.setLevel("dev.n0153.app", level);
    }

    /**
     * Shortcut method, generates title for a file that is currently in processing.
     * The title is then saved to state and can be accessed with state.getGeneralFileTitle().
     * @since 0.1
     */
    public static String getTitle(Path osTargetPath, String mime, boolean randomCharactersTitle) {
        if (randomCharactersTitle) {
            final String CHARACTERS =
                    "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
            final SecureRandom RANDOM = new SecureRandom();
            StringBuilder objectName = new StringBuilder(40);
            for (int i = 0; i < 40; i++) {
                int index = RANDOM.nextInt(CHARACTERS.length());
                objectName.append(CHARACTERS.charAt(index));
            }
            return objectName.toString()+"."+mime;
        } else {
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
            type = Utils.getFileType(osTargetPath);
            String objectName = null;
            objectName = type+"_"+year+"_"+month+"_"+day+
                    "_"+hour+"_"+minute+"_"+second+
                    "_"+nanoSecond+"_"+milliSecond+"."+ mime;
            return objectName;
        }
    }

    /**
     * Shortcut method, generates title for a file that is currently in processing.
     * The title is then saved to state and can be accessed with state.getGeneralFileTitle().
     * @since 0.1
     */
    public static String getTitleForcedTagTimeStamp(String forcedTag, String mime) {
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

    /**
     * Renames specified file.
     * @param osTargetFile Path to file.
     * @param newTitle New title.
     * @throws IOException If file isn't supported.
     * @since 0.1
     */
    public static void renameFile(Path osTargetFile, String newTitle) throws IOException {
        Files.move(osTargetFile, osTargetFile.getParent().resolve(newTitle));
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
                String mime = getMimeType(osTargetFile);
                if (mime.equals("log")) {
                    return "text";
                }
                throw new FileTypeDetectionException("failed to detect file type, object is null: ", osTargetFile);
            }
            // edge case: some text formats report "application" as a filetype.
            String mime = getMimeType(osTargetFile);
            if (mime.equals("json") || mime.equals("log") || mime.equals("html") ) {
                return "text";
            } else {
                return fileType.split("/")[0];
            }

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
            return getMimeFromSignature(osTargetFilePath);
        } catch (NullPointerException e) {
            throw new MimeTypeDetectionException("specified mime type isn't whitelisted", osTargetFilePath, e);
        }
    }

    /**
     * Shortcut method, delete any specified file.
     * @param osTargetFilePath Path to file.
     * @throws IOException If file doesn't exist or unable to delete.
     * @since 0.1
     */
    public static void fileDispose(Path osTargetFilePath) throws IOException {
        if (!Files.exists(osTargetFilePath)) {
            throw new NoSuchFileException("Specified file doesn't exist");
        }
        File file = osTargetFilePath.toFile();
        boolean deleted = Files.deleteIfExists(file.toPath());
        if (!deleted) {
            throw new FileSystemException(osTargetFilePath.toString());
        }
    }

    public static void createDirectory(Path dirToCreate) {
        try {
            Files.createDirectories(dirToCreate);
            logger.info("Directory created successfully: {}", dirToCreate);
        } catch (IOException e) {
            throw new InvalidPathException("Failed to create missing directory: ", dirToCreate);
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

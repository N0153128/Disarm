package dev.n0153.app.core;

import dev.n0153.app.core.exceptions.FileTypeDetectionException;
import dev.n0153.app.core.exceptions.MimeTypeDetectionException;

import dev.n0153.app.core.exceptions.InvalidPathException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.Level;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.*;
import java.nio.file.*;
import java.security.SecureRandom;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Static utilities shared by the core and plugins: magic-byte MIME detection,
 * text charset detection, file type detection, output file title generation,
 * file operations (rename, move, delete, directory creation, size), and
 * logging level control.
 * <p>
 * {@link #init(GlobalConfig)} must be called before any charset detection
 * method, as the detection thresholds are read from {@link GlobalConfig}.
 * @since 0.1
 */
public class Utils {
    private static final Logger logger = LogManager.getLogger(Utils.class);
    private static GlobalConfig globalConfig;

    /**
     * Sets the global config used for charset detection thresholds and sample size.
     * Must be called once at startup, before any charset detection runs.
     * @param globalConfig the global config instance
     * @since 0.2
     */
    public static void init(GlobalConfig globalConfig) {
        Utils.globalConfig = globalConfig;
    }

    /**
     * A file signature used for magic-byte MIME detection.
     * @param byteSignature the expected bytes, matched from offset 0 of the file
     * @param mask per-byte match flags; {@code false} marks a wildcard byte that is not compared
     *             (e.g. the RIFF chunk size in WAV/WEBP, or the box size in MP4/MOV)
     * @since 0.2
     */
    record MimeSignature(byte[] byteSignature, boolean[] mask) {}

    /**
     * Known file signatures keyed by format. Keys of the form {@code "format/variant"}
     * (e.g. {@code "mp3/id3"}, {@code "html/div_lower"}) group several signatures under
     * one format; only the part before the {@code /} is returned on a match.
     * @since 0.2
     */
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
        put("html/doctype", new MimeSignature(
                new byte[]{0x3C, 0x21, 0x44, 0x4F, 0x43, 0x54, 0x59, 0x50, 0x45, 0x20, 0x48, 0x54, 0x4D, 0x4C, 0x00},
                new boolean[]{true, true, true, true, true, true, true, true, true, true, true, true, true, true, false}
        ));
        put("html/html", new MimeSignature(
                new byte[]{0x3C, 0x48, 0x54, 0x4D, 0x4C, 0x00},
                new boolean[]{true, true, true, true, true, false}
        ));
        put("html/head", new MimeSignature(
                new byte[]{0x3C, 0x48, 0x45, 0x41, 0x44, 0x00},
                new boolean[]{true, true, true, true, true, false}
        ));
        put("html/script", new MimeSignature(
                new byte[]{0x3C, 0x53, 0x43, 0x52, 0x49, 0x50, 0x54, 0x00},
                new boolean[]{true, true, true, true, true, true, true, false}
        ));
        put("html/iframe", new MimeSignature(
                new byte[]{0x3C, 0x49, 0x46, 0x52, 0x41, 0x4D, 0x45, 0x00},
                new boolean[]{true, true, true, true, true, true, true, false}
        ));
        put("html/h1", new MimeSignature(
                new byte[]{0x3C, 0x48, 0x31, 0x00},
                new boolean[]{true, true, true, false}
        ));
        put("html/div", new MimeSignature(
                new byte[]{0x3C, 0x44, 0x49, 0x56, 0x00},
                new boolean[]{true, true, true, true, false}
        ));
        put("html/font", new MimeSignature(
                new byte[]{0x3C, 0x46, 0x4F, 0x4E, 0x54, 0x00},
                new boolean[]{true, true, true, true, true, false}
        ));
        put("html/table", new MimeSignature(
                new byte[]{0x3C, 0x54, 0x41, 0x42, 0x4C, 0x45, 0x00},
                new boolean[]{true, true, true, true, true, true, false}
        ));
        put("html/a", new MimeSignature(
                new byte[]{0x3C, 0x41, 0x00},
                new boolean[]{true, true, false}
        ));
        put("html/style", new MimeSignature(
                new byte[]{0x3C, 0x53, 0x54, 0x59, 0x4C, 0x45, 0x00},
                new boolean[]{true, true, true, true, true, true, false}
        ));
        put("html/title", new MimeSignature(
                new byte[]{0x3C, 0x54, 0x49, 0x54, 0x4C, 0x45, 0x00},
                new boolean[]{true, true, true, true, true, true, false}
        ));
        put("html/b", new MimeSignature(
                new byte[]{0x3C, 0x42, 0x00},
                new boolean[]{true, true, false}
        ));
        put("html/body", new MimeSignature(
                new byte[]{0x3C, 0x42, 0x4F, 0x44, 0x59, 0x00},
                new boolean[]{true, true, true, true, true, false}
        ));
        put("html/br", new MimeSignature(
                new byte[]{0x3C, 0x42, 0x52, 0x00},
                new boolean[]{true, true, true, false}
        ));
        put("html/p", new MimeSignature(
                new byte[]{0x3C, 0x50, 0x00},
                new boolean[]{true, true, false}
        ));
        put("html/comment", new MimeSignature(
                new byte[]{0x3C, 0x21, 0x2D, 0x2D, 0x00},
                new boolean[]{true, true, true, true, false}
        ));
        put("html/doctype_mixed", new MimeSignature(
                new byte[]{0x3C, 0x21, 0x44, 0x4F, 0x43, 0x54, 0x59, 0x50, 0x45, 0x20, 0x68, 0x74, 0x6D, 0x6C, 0x00},
                new boolean[]{true, true, true, true, true, true, true, true, true, true, true, true, true, true, false}
        ));
        put("html/doctype_lower", new MimeSignature(
                new byte[]{0x3C, 0x21, 0x64, 0x6F, 0x63, 0x74, 0x79, 0x70, 0x65, 0x20, 0x68, 0x74, 0x6D, 0x6C, 0x00},
                new boolean[]{true, true, true, true, true, true, true, true, true, true, true, true, true, true, false}
        ));
        put("html/html_lower", new MimeSignature(
                new byte[]{0x3C, 0x68, 0x74, 0x6D, 0x6C, 0x00},
                new boolean[]{true, true, true, true, true, false}
        ));
        put("html/head_lower", new MimeSignature(
                new byte[]{0x3C, 0x68, 0x65, 0x61, 0x64, 0x00},
                new boolean[]{true, true, true, true, true, false}
        ));
        put("html/script_lower", new MimeSignature(
                new byte[]{0x3C, 0x73, 0x63, 0x72, 0x69, 0x70, 0x74, 0x00},
                new boolean[]{true, true, true, true, true, true, true, false}
        ));
        put("html/iframe_lower", new MimeSignature(
                new byte[]{0x3C, 0x69, 0x66, 0x72, 0x61, 0x6D, 0x65, 0x00},
                new boolean[]{true, true, true, true, true, true, true, false}
        ));
        put("html/h1_lower", new MimeSignature(
                new byte[]{0x3C, 0x68, 0x31, 0x00},
                new boolean[]{true, true, true, false}
        ));
        put("html/div_lower", new MimeSignature(
                new byte[]{0x3C, 0x64, 0x69, 0x76, 0x00},
                new boolean[]{true, true, true, true, false}
        ));
        put("html/font_lower", new MimeSignature(
                new byte[]{0x3C, 0x66, 0x6F, 0x6E, 0x74, 0x00},
                new boolean[]{true, true, true, true, true, false}
        ));
        put("html/table_lower", new MimeSignature(
                new byte[]{0x3C, 0x74, 0x61, 0x62, 0x6C, 0x65, 0x00},
                new boolean[]{true, true, true, true, true, true, false}
        ));
        put("html/a_lower", new MimeSignature(
                new byte[]{0x3C, 0x61, 0x00},
                new boolean[]{true, true, false}
        ));
        put("html/style_lower", new MimeSignature(
                new byte[]{0x3C, 0x73, 0x74, 0x79, 0x6C, 0x65, 0x00},
                new boolean[]{true, true, true, true, true, true, false}
        ));
        put("html/title_lower", new MimeSignature(
                new byte[]{0x3C, 0x74, 0x69, 0x74, 0x6C, 0x65, 0x00},
                new boolean[]{true, true, true, true, true, true, false}
        ));
        put("html/b_lower", new MimeSignature(
                new byte[]{0x3C, 0x62, 0x00},
                new boolean[]{true, true, false}
        ));
        put("html/body_lower", new MimeSignature(
                new byte[]{0x3C, 0x62, 0x6F, 0x64, 0x79, 0x00},
                new boolean[]{true, true, true, true, true, false}
        ));
        put("html/br_lower", new MimeSignature(
                new byte[]{0x3C, 0x62, 0x72, 0x00},
                new boolean[]{true, true, true, false}
        ));
        put("html/p_lower", new MimeSignature(
                new byte[]{0x3C, 0x70, 0x00},
                new boolean[]{true, true, false}
        ));
    }};

    /**
     * Text formats with no fixed magic bytes. These are identified by file
     * extension, and the content must also pass charset detection.
     * @since 0.2
     */
    private static final List<String> textFormats = List.of("txt", "log", "json");

    /**
     * @param signedByte a raw byte
     * @return the byte's unsigned value, 0–255
     * @since 0.2
     */
    private static int toUnsignedValue(byte signedByte) {
        return signedByte & 0xFF;
    }

    /**
     * Checks whether the bytes decode under the given charset with no malformed or
     * unmappable input.
     * @param charset the charset to test
     * @param bytes the sample bytes
     * @param length number of bytes in {@code bytes} to decode
     * @param dataWasTruncated {@code true} if the sample is a prefix of a larger file;
     *                         an incomplete multibyte sequence at the end is then tolerated
     * @return {@code true} if the sample decodes cleanly
     * @since 0.2
     */
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

    /**
     * Detects a charset from a leading byte order mark. Checks for UTF-32 BE
     * ({@code 00 00 FE FF}), UTF-8 ({@code EF BB BF}) and UTF-16 LE ({@code FF FE}).
     * @param bytes the sample bytes
     * @param length number of valid bytes in {@code bytes}
     * @return the charset indicated by the BOM, or {@code null} if no recognised BOM is present
     * @since 0.2
     */
    private static Charset detectCharsetFromByteOrderMark(byte[] bytes, int length) {
        if (length >= 4) {
            int firstByte = toUnsignedValue(bytes[0]);
            int secondByte = toUnsignedValue(bytes[1]);
            int thirdByte = toUnsignedValue(bytes[2]);
            int fourthByte = toUnsignedValue(bytes[3]);

            if (firstByte == 0X00 && secondByte == 0x00
                    && thirdByte == 0xFE && fourthByte == 0xFF) {
                return Charset.forName("UTF-32BE");
            }
        }

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

    /**
     * Heuristically detects BOM-less UTF-32 by looking at NUL byte positions within
     * each complete 4-byte group. BE is chosen when every group starts with NUL, not every
     * group ends with NUL, and at least {@link GlobalConfig#getMinUtf32BasicPlaneRatio()}
     * of groups have NUL in the second byte. LE is the same check mirrored, using the
     * last byte and the third byte.
     * @param bytes the sample bytes
     * @param length number of valid bytes in {@code bytes}; fewer than 8 always returns {@code null}
     * @return {@code UTF-32BE}, {@code UTF-32LE}, or {@code null} if neither pattern fits
     * @since 0.2
     */
    private static Charset guessUtf32WithoutByteOrderMark(byte[] bytes, int length) {
        if (length < 8) {
            return null;
        }

        int completeCharacterCount = length / 4;
        int groupsWithNulAtStart = 0;
        int groupsWithNulAtSecondByte = 0;
        int groupsWithNulAtThirdByte = 0;
        int groupsWithNulAtEnd = 0;

        for (int characterIndex = 0; characterIndex < completeCharacterCount; characterIndex ++) {
            int groupStart = characterIndex * 4;
            if (bytes[groupStart] == 0) {
                groupsWithNulAtStart ++;
            }
            if (bytes[groupStart + 1] == 0) {
                groupsWithNulAtSecondByte ++;
            }
            if (bytes[groupStart + 2] == 0) {
                groupsWithNulAtThirdByte ++;
            }
            if (bytes[groupStart + 3] == 0) {
                groupsWithNulAtEnd ++;
            }
        }

        boolean everyGroupStartsWithNul = (groupsWithNulAtStart == completeCharacterCount);
        boolean everyGroupEndsWithNul = (groupsWithNulAtEnd == completeCharacterCount);
        double minimumBasicPlaneCount = completeCharacterCount * globalConfig.getMinUtf32BasicPlaneRatio();

        if (everyGroupStartsWithNul && !everyGroupEndsWithNul) {
            if (groupsWithNulAtSecondByte >= minimumBasicPlaneCount) {
                return Charset.forName("UTF-32BE");
            }
            return null;
        }
        if (everyGroupEndsWithNul && !everyGroupStartsWithNul) {
            if (groupsWithNulAtThirdByte >= minimumBasicPlaneCount) {
                return Charset.forName("UTF-32LE");
            }
            return null;
        }
        return null;
    }

    /**
     * Confirms a guessed charset: the sample must decode cleanly and, once decoded,
     * must not exceed the suspicious control character ratio.
     * @param charset the guessed charset
     * @param bytes the sample bytes
     * @param length number of valid bytes in {@code bytes}
     * @param dataWasTruncated {@code true} if the sample is a prefix of a larger file
     * @return {@code true} if the sample is accepted as text in {@code charset}
     * @since 0.2
     */
    private static boolean isConfirmedText(
            Charset charset, byte[] bytes, int length, boolean dataWasTruncated
    ) {
        boolean decodesCleanly = canBeDecoded(charset, bytes, length, dataWasTruncated);
        if (!decodesCleanly) {
            return false;
        }
        return  !hasTooManyControlCharactersAfterDecoding(charset, bytes, length);
    }

    /**
     * Heuristically detects BOM-less UTF-16 by counting NUL bytes at even and odd
     * positions. The more common position must hold more than
     * {@link GlobalConfig#getMinUtf16NulRatio()} of the character count, and NULs at
     * the other position must not exceed {@link GlobalConfig#getMaxControlCharacterRatio()}
     * of that count. More NULs at even positions means BE, otherwise LE.
     * @param bytes the sample bytes
     * @param length number of valid bytes in {@code bytes}; fewer than 4 always returns {@code null}
     * @return {@code UTF-16BE}, {@code UTF-16LE}, or {@code null} if the NUL pattern doesn't fit
     * @since 0.2
     */
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
        double minimumExpectedNulCount = characterCount * globalConfig.getMinUtf16NulRatio();
        double maximumStrayNulCount = expectedNulCount * globalConfig.getMaxControlCharacterRatio();

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

    /**
     * @param bytes the sample bytes
     * @param length number of valid bytes in {@code bytes}
     * @return {@code true} if any byte in range is {@code 0x00}
     * @since 0.2
     */
    private static boolean containsNulByte(byte[] bytes, int length) {
        for (int position = 0; position < length; position++) {
            if (bytes[position] == 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks whether a value is a C0 control character or DEL that is not normally
     * found in text. Tab, LF, CR, form feed and ESC are allowed.
     * @param value a byte or char value
     * @return {@code true} if the value is a suspicious control character
     * @since 0.2
     */
    private static boolean isSuspiciousControlCharacter(int value) {
        boolean isControlCharacter = (value < 0x20) || (value == 0x7F);

        boolean isAllowedInText = (value == '\t')
                || (value == '\n')
                || (value == '\r')
                || (value == '\f')
                || (value == 0x1B);
        return isControlCharacter && !isAllowedInText;
    }

    /**
     * Checks the raw byte sample for a suspicious control character ratio above
     * {@link GlobalConfig#getMaxControlCharacterRatio()}.
     * @param bytes the sample bytes
     * @param length number of valid bytes in {@code bytes}
     * @return {@code true} if the ratio is exceeded
     * @since 0.2
     */
    private static boolean hasTooManyControlCharacters(byte[] bytes, int length) {
        int suspiciousCount = 0;
        for (int position = 0; position < length; position++) {
            int value = toUnsignedValue(bytes[position]);
            if (isSuspiciousControlCharacter(value)) {
                suspiciousCount++;
            }
        }
        double maximumAllowedCount = length * globalConfig.getMaxControlCharacterRatio();
        return suspiciousCount > maximumAllowedCount;
    }

    /**
     * Decodes the sample with the given charset and checks the decoded characters for a
     * suspicious control character ratio above {@link GlobalConfig#getMaxControlCharacterRatio()}.
     * @param charset the charset to decode with
     * @param bytes the sample bytes
     * @param length number of valid bytes in {@code bytes}
     * @return {@code true} if the ratio is exceeded
     * @since 0.2
     */
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
        double maximumAllowedCount = decodedText.length() * globalConfig.getMaxControlCharacterRatio();
        return suspiciousCount > maximumAllowedCount;
    }

    /**
     * @param bytes the sample bytes
     * @param length number of valid bytes in {@code bytes}
     * @return {@code true} if every byte in range is 7-bit ASCII
     * @since 0.2
     */
    private static boolean isPureAscii(byte[] bytes, int length) {
        for (int position = 0; position < length; position++) {
            int value = toUnsignedValue(bytes[position]);
            if (value > 127) {
                return false;
            }
        }
        return true;
    }

    /**
     * Detects the charset of a text sample, or rejects it as non-text. Checks run in order:
     * <ol>
     *   <li>Empty sample: UTF-8.</li>
     *   <li>Byte order mark: the BOM's charset if the sample decodes cleanly, otherwise {@code null}.</li>
     *   <li>BOM-less UTF-32, then BOM-less UTF-16 heuristics, each confirmed by decoding.</li>
     *   <li>Any NUL byte, or too many suspicious control characters: {@code null}.</li>
     *   <li>Pure ASCII: US-ASCII.</li>
     *   <li>Clean UTF-8 decode: UTF-8.</li>
     *   <li>Otherwise: ISO-8859-1.</li>
     * </ol>
     * Requires {@link #init(GlobalConfig)}.
     * @param bytes the sample bytes
     * @param length number of valid bytes in {@code bytes}
     * @param dataWasTruncated {@code true} if the sample is a prefix of a larger file
     * @return the detected charset, or {@code null} if the sample is not considered text
     * @since 0.2
     */
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

        Charset guessedUtf32 = guessUtf32WithoutByteOrderMark(bytes, length);
        if (guessedUtf32 != null) {
            if (isConfirmedText(guessedUtf32, bytes, length, dataWasTruncated)) {
                return guessedUtf32;
            }
        }

        Charset guessedUtf16 = guessUtf16WithoutByteOrderMark(bytes, length);
        if (guessedUtf16 != null) {
            if (isConfirmedText(guessedUtf16, bytes, length, dataWasTruncated)) {
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

    /**
     * Reads up to {@link GlobalConfig#getSampleSizeInBytes()} bytes from the file and
     * detects its charset using {@link #detectTextCharset(byte[], int, boolean)}.
     * Requires {@link #init(GlobalConfig)}.
     * @param osTargetPath path to the file
     * @return the detected charset, or {@code null} if the file is not considered text
     * @throws IOException if the file cannot be read
     * @since 0.2
     */
    public static Charset detectTextCharset(Path osTargetPath) throws IOException {
        try (InputStream fileStream = Files.newInputStream(osTargetPath)) {
            byte[] sampleBytes = fileStream.readNBytes(globalConfig.getSampleSizeInBytes());
            boolean fileHasMoreData = fileStream.read() != -1;
            return detectTextCharset(sampleBytes, sampleBytes.length, fileHasMoreData);
        }
    }

    /**
     * Tells WEBM and Matroska apart, since both share the same EBML magic bytes. Reads
     * the first 64 bytes and searches them as ASCII for the EBML DocType string.
     * @param osTargetPath path to the file
     * @return {@code "webm"}, {@code "x-matroska"} (if both strings are present), or
     *         {@code null} if neither DocType is found
     * @throws MimeTypeDetectionException if the file cannot be read
     * @since 0.2
     */
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

    /**
     * @param osTargetPath path to the file
     * @return the text after the last {@code .} in the file name, or an empty string if there is none
     * @since 0.2
     */
    public static String getFileExtension(Path osTargetPath) {
        String filename = osTargetPath.getFileName().toString();
        int dotIndex = filename.lastIndexOf('.');
        return dotIndex >= 0 ? filename.substring(dotIndex + 1) : "";
    }

    /**
     * Identifies a file's format from its first 16 bytes against the known signature table.
     * On a match, returns the signature's format key; for grouped keys such as
     * {@code "mp3/id3"} or {@code "html/div"}, only the part before the {@code /} is returned.
     * WEBM/Matroska matches are resolved via {@link #checkWebmOrMkvOverDocType(Path)}.
     * <p>
     * If no signature matches, files with a {@code txt}, {@code log} or {@code json}
     * extension are accepted as that extension, provided their content passes
     * {@link #detectTextCharset(Path)}.
     * @param osTargetPath path to the file
     * @return the detected format (e.g. {@code "png"}, {@code "mp3"}, {@code "html"}, {@code "txt"})
     * @throws MimeTypeDetectionException if the file cannot be read, its charset cannot be
     *         detected, or its format is unrecognised or unsupported
     * @since 0.2
     */
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
            try {
                Charset detectedCharset = detectTextCharset(osTargetPath);
                if (detectedCharset == null) {
                    throw new MimeTypeDetectionException("Failed to detect charset", osTargetPath);
                } else {
                    return extension;
                }
            } catch (IOException e) {
                throw new MimeTypeDetectionException("Failed to read charset", osTargetPath);
            }
        } else {
            throw new MimeTypeDetectionException(
                    "Provided file's mime type cannot be verified or is not supported",
                    osTargetPath
            );
        }
    }

    /**
     * Sets the log level for the JAVE library ({@code ws.schild.jave}).
     * @param level the log level to apply
     * @since 0.2
     */
    public static void setJaveLogging(Level level) {
        Configurator.setLevel("ws.schild.jave", level);
    }

    /**
     * Sets the log level for all Disarm loggers ({@code dev.n0153.app}).
     * @param level the log level to apply
     * @since 0.2
     */
    public static void setDisarmLogging(Level level) {
        Configurator.setLevel("dev.n0153.app", level);
    }

    /**
     * Generates an output file name for a file being processed. The name is either
     * 40 random alphanumeric characters, or a timestamp prefixed with the file's type:
     * {@code {fileType}_{year}_{month}_{day}_{hour}_{minute}_{second}_{nanosecond}_{millisecond}.{mime}}.
     * Timestamp fields are not zero-padded.
     * @param osTargetPath path to the file; used to detect the file type for timestamp names
     * @param mime the format used as the file extension (e.g. {@code "png"})
     * @param randomCharactersTitle {@code true} for a random name, {@code false} for a timestamped name
     * @return the generated file name, including extension
     * @throws FileTypeDetectionException if a timestamped name is requested and the file type cannot be detected
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
     * Generates an output file name made of a caller-supplied tag and 40 random
     * alphanumeric characters: {@code {forcedTag}_{random}.{mime}}.
     * @param forcedTag the prefix to use in place of the detected file type
     * @param mime the format used as the file extension
     * @return the generated file name, including extension
     * @since 0.2
     */
    public static String getTitleForcedTagRandomCharacters(String forcedTag, String mime) {
        final String CHARACTERS =
                "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        final SecureRandom RANDOM = new SecureRandom();
        StringBuilder objectName = new StringBuilder(40);
        for (int i = 0; i < 40; i++) {
            int index = RANDOM.nextInt(CHARACTERS.length());
            objectName.append(CHARACTERS.charAt(index));
        }
        return forcedTag+"_"+objectName+"."+mime;
    }

    /**
     * Generates a timestamped output file name with a caller-supplied tag in place of the
     * detected file type:
     * {@code {forcedTag}_{year}_{month}_{day}_{hour}_{minute}_{second}_{nanosecond}_{millisecond}.{mime}}.
     * Timestamp fields are not zero-padded.
     * @param forcedTag the prefix to use in place of the detected file type
     * @param mime the format used as the file extension
     * @return the generated file name, including extension
     * @since 0.2
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
     * Renames a file in place, within its current directory.
     * @param osTargetFile path to the file
     * @param newTitle the new file name
     * @throws IOException if the move fails, including when a file with the new name already exists
     * @since 0.1
     */
    public static void renameFile(Path osTargetFile, String newTitle) throws IOException {
        Files.move(osTargetFile, osTargetFile.getParent().resolve(newTitle));
    }

    /**
     * Returns a file's top-level type (e.g. {@code "image"}, {@code "audio"}, {@code "video"},
     * {@code "text"}), used to pick the matching plugin and CLI mixin.
     * The base type comes from {@link Files#probeContentType(Path)}; files detected by
     * {@link #getMimeType(Path)} as {@code json}, {@code log} or {@code html} are always
     * reported as {@code "text"}.
     * @param osTargetFile path to the file
     * @return the file's top-level type
     * @throws FileTypeDetectionException if the file doesn't exist, isn't a regular file,
     *         or its type cannot be determined
     * @throws MimeTypeDetectionException if signature-based MIME detection fails
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
     * Returns a file's format via magic-byte detection; see {@link #getMimeFromSignature(Path)}.
     * @param osTargetFilePath path to the file
     * @return the detected format (e.g. {@code "png"}, {@code "mp3"}, {@code "txt"})
     * @throws MimeTypeDetectionException if the format cannot be detected or isn't supported
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
     * Deletes the specified file.
     * @param osTargetFilePath path to the file
     * @throws NoSuchFileException if the file doesn't exist
     * @throws FileSystemException if the file could not be deleted
     * @throws IOException if an I/O error occurs
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

    /**
     * Moves a file into the destination directory, keeping its file name. The destination
     * directory, including any missing parents, is created if it doesn't exist.
     * @param osTargetPath path to the file to move
     * @param destination the target directory
     * @throws IOException if the move fails, including when a file with the same name already exists there
     * @throws InvalidPathException if the destination directory cannot be created
     * @since 0.2
     */
    public static void fileMoveTo(Path osTargetPath, Path destination) throws IOException {
        if (!Files.isDirectory(destination)) {
            logger.error("Specified directory doesn't exist. Creating new directory at: {}", destination);
            createDirectory(destination);
        }
        Files.move(osTargetPath, destination.resolve(osTargetPath.getFileName()));
    }

    /**
     * Creates a directory, including any missing parent directories. Does nothing if it already exists.
     * @param dirToCreate the directory to create
     * @throws InvalidPathException if the directory cannot be created
     * @since 0.2
     */
    public static void createDirectory(Path dirToCreate) {
        try {
            Files.createDirectories(dirToCreate);
            logger.info("Directory created successfully: {}", dirToCreate);
        } catch (IOException e) {
            throw new InvalidPathException("Failed to create missing directory: ", dirToCreate);
        }
    }

    /**
     * Returns a file's size in bytes, read from file system metadata without loading the file.
     * @param osFilePath path to the file
     * @return the file size in bytes, or {@code 0} if the file doesn't exist
     * @since 0.1
     */
    public static long getSize(Path osFilePath) {
        File file = osFilePath.toFile();
        return file.length();
    }
}

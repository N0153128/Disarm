package dev.n0153.app;

import nu.pattern.OpenCV;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

class TextTest {

    @TempDir
    Path tempDir;
    DisarmConfig config;
    DisarmState state;

    @BeforeEach
    void setUp() {
        OpenCV.loadLocally();
        config = new BuilderConfig()
                .setGeneralOutputPath(tempDir)
                .build();
        state = new DisarmState(config);
    }

    private static final Map<String, byte[]> ENCODING_TO_BYTES = new HashMap<>();
    static {
        // BOMs
        ENCODING_TO_BYTES.put("utf8bom", new byte[] {(byte)0xEF, (byte)0xBB, (byte)0xBF, 0x48,
                                                        0x65, 0x6C, 0x6C, 0x6F}); // BOM + "Hello"
        ENCODING_TO_BYTES.put("utf16be", new byte[] {(byte)0xFE, (byte)0xFF, 0x00, 0x48,
                                                      0x00, 0x69}); // BOM + "Hi"
        ENCODING_TO_BYTES.put("utf16le", new byte[] {(byte)0xFF, (byte)0xFE, 0x48, 0x00,
                                                        0x69, 0x00}); // BOM + "Hi"
        ENCODING_TO_BYTES.put("utf32be", new byte[] {0x00, 0x00, (byte)0xFE, (byte)0xFF,
                                                     0x00, 0x00, 0x00, 0x48}); // BOM + "H"
        ENCODING_TO_BYTES.put("nobom",   new byte[] {0x48, 0x65, 0x6C, 0x6C,
                                                     0x6F}); // "Hello"

        // UTF-8 valid multibyte (é = C3 A9)
        ENCODING_TO_BYTES.put("utf8valid",  new byte[] {0x48, 0x65, 0x6C, 0x6C,
                                                        0x6F, (byte)0xC3, (byte)0xA9}); // "Helloe with accent"
        // Invalid UTF-8 (lone continuation byte)
        ENCODING_TO_BYTES.put("utf8bad",    new byte[] {(byte)0x80, (byte)0x81, 0x48, 0x65,
                                                        0x6C, 0x6C, 0x6F});
        // Pure ASCII
        ENCODING_TO_BYTES.put("ascii",      new byte[] {0x48, 0x65, 0x6C, 0x6C,
                                                        0x6F, 0x20, 0x57, 0x6F,
                                                        0x72, 0x6C, 0x64}); // "Hello World"

        // Non-ASCII (has byte > 0x7F, fails ASCII check)
        ENCODING_TO_BYTES.put("nonascii",   new byte[] {0x48, 0x65, 0x6C, 0x6C,
                                                        0x6F, (byte)0xC3, (byte)0xA9});
    }

    private final Path testStripJson = Paths.get("resources/test-text/test-strip.json");
    private final Path testStripLog = Paths.get("resources/test-text/test-strip.log");
    private final Path testStripClean = Paths.get("resources/test-text/test-strip-clean.txt");
    private final Path testStripDirty = Paths.get("resources/test-text/test-strip-dirty.txt");
    private final Path testUnicodeClean = Paths.get("resources/test-text/test-unicode-clean.txt");
    private final Path testUnicodeDirty = Paths.get("resources/test-text/test-unicode-dirty.txt");
    // encoding and bom testing
    private final Path testAscii = Paths.get("resources/test-text/test-ascii.txt");
    private final Path testBomUtf8 = Paths.get("resources/test-text/test-bom-utf8.txt");
    private final Path testBomUtf16be = Paths.get("resources/test-text/test-bom-utf16be.txt");
    private final Path testBomUtf16le = Paths.get("resources/test-text/test-bom-utf16le.txt");
    private final Path testNoBom = Paths.get("resources/test-text/test-nobom.txt");
    private final Path testUtf8 = Paths.get("resources/test-text/test-utf8.txt");
    private final Path testBomUtf32be = Paths.get("resources/test-text/test-bom-utf32be.txt");
    private final Path testBomUtf32le = Paths.get("resources/test-text/test-bom-utf32le.txt");
    private final Path testEmpty = Paths.get("resources/test-text/test-empty.txt");
    private final Path testInvalidUtf8 = Paths.get("resources/test-text/test-invalid-utf8.txt");

    private void encodingHandler(Path osTargetPath) {
        Text text = new Text(state, config);
        try {
            byte[] bytes = Files.readAllBytes(osTargetPath);
            if (text.isBom(bytes)) {
                if (bytes[0] == 0x00 && bytes[1] == 0x00) {
                    state.setTextFile(new String(bytes, Charset.forName("UTF-32BE")));
                } else if (bytes[0] == (byte) 0xEF) {
                    state.setTextFile(new String(bytes, StandardCharsets.UTF_8));
                } else {
                    state.setTextFile(new String(bytes, StandardCharsets.UTF_16));
                }
            }
            else if (text.isASCII(bytes)) {
                state.setTextFile(new String(bytes, StandardCharsets.US_ASCII));
            }
            else if (text.isUTF8(bytes)) {
                state.setTextFile(new String(bytes, StandardCharsets.UTF_8));
            }
            else {
                state.setTextFile(new String(bytes, StandardCharsets.UTF_8));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void prepData(Path osTargetPath) {
        try {
            Validator.validatePath(config.getGeneralOutputPath());
            state.setMime(Utils.getFormatFromMime(Utils.getMimeType(osTargetPath)));
            state.setFileType(Utils.getFileType(osTargetPath));
            state.setOsTargetFile(osTargetPath);
            Utils.getTitle(state, false);
            encodingHandler(osTargetPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] getBytes(String key) {
        return ENCODING_TO_BYTES.get(key);
    }

    private void ensureActionedString() {
        assertFalse(state.getTextFile().contains("<script"));
        assertFalse(state.getTextFile().contains("vbscript:"));
        assertFalse(state.getTextFile().contains("javascript:"));
        assertFalse(state.getTextFile().contains("data:"));
    }

    private void ensureActionedFile(Path osTargetPath) {
        try {
            assertFalse(Files.readString(osTargetPath).contains("<script"));
            assertFalse(Files.readString(osTargetPath).contains("vbscript:"));
            assertFalse(Files.readString(osTargetPath).contains("javascript:"));
            assertFalse(Files.readString(osTargetPath).contains("data:"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void stripPatterns_fromText() {
        Text text = new Text(state, config);
        prepData(testStripDirty);
        assertDoesNotThrow(text::stripPatterns);
        ensureActionedString();
    }

//    @Test
    // should pass, although v0.1 doesn't support json, but "Clean" is a standard text file.
//    void stripPatterns_fromJson() {
//        Text text = new Text(state, config);
//        prepData(testStripJson);
//        assertDoesNotThrow(text::stripPatterns);
//    }

    @Test
    void stripPatterns_fromLog() {
        Text text = new Text(state, config);
        assertThrows(RuntimeException.class, () -> prepData(testStripLog));
        assertThrows(IllegalStateException.class, text::stripPatterns);
    }

    @Test
    void stripPatterns_fromPng() {
        Text text = new Text(state, config);
        assertThrows(RuntimeException.class, () -> prepData(DebugPaths.imageTestInputPng));
        assertThrows(IllegalStateException.class, text::stripPatterns);
    }

    @Test
    void stripPatterns_nonExistent() {
        Text text = new Text(state, config);
        assertThrows(RuntimeException.class, () -> prepData(tempDir.resolve("none.test")));
        assertThrows(IllegalStateException.class, text::stripPatterns);
    }

    @Test
    void isBom_bytesUtf8Bom() {
        Text text = new Text(state, config);
        assertTrue(text.isBom(getBytes("utf8bom")));
    }

    @Test
    void isBom_bytesUtf16be() {
        Text text = new Text(state, config);
        assertTrue(text.isBom(getBytes("utf16be")));
    }

    @Test
    void isBom_bytesUtf16le() {
        Text text = new Text(state, config);
        assertTrue(text.isBom(getBytes("utf16le")));
    }

    @Test
    void isBom_bytesUtf32be() {
        Text text = new Text(state, config);
        assertTrue(text.isBom(getBytes("utf32be")));
    }

    @Test
    void isBom_bytesNoBom() {
        Text text = new Text(state, config);
        assertFalse(text.isBom(getBytes("nobom")));
    }

    @Test
    void isBom_bytesEmpty() {
        Text text = new Text(state, config);
        assertFalse(text.isBom(new byte[] {}));
    }

    @Test
    void isBom_bytesShort() {
        Text text = new Text(state, config);
        assertFalse(text.isBom(new byte[] {0x01}));
    }

    @Test
    void isBom_fileBomUtf8() {
        Text text = new Text(state, config);
        try {
            assertTrue(text.isBom(Files.readAllBytes(testBomUtf8)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void isBom_fileBomUtf16be() {
        Text text = new Text(state, config);
        try {
            assertTrue(text.isBom(Files.readAllBytes(testBomUtf16be)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void isBom_fileBomUtf16le() {
        Text text = new Text(state, config);
        try {
            assertTrue(text.isBom(Files.readAllBytes(testBomUtf16le)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void isBom_fileBomUtf32be() {
        Text text = new Text(state, config);
        try {
            assertTrue(text.isBom(Files.readAllBytes(testBomUtf32be)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void isBom_fileNoBom() {
        Text text = new Text(state, config);
        try {
            assertFalse(text.isBom(Files.readAllBytes(testNoBom)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void isBom_fileEmpty() {
        Text text = new Text(state, config);
        try {
            assertFalse(text.isBom(Files.readAllBytes(testEmpty)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void isBom_nonExistent() {
        Text text = new Text(state, config);
        assertThrows(IOException.class, () ->
                text.isBom(getBytes(Files.readString(Paths.get("none.test")))));
    }

    @Test
    void isUTF8_bytesAscii() {
        Text text = new Text(state, config);
        assertTrue(text.isUTF8(getBytes("ascii")));
    }

    @Test
    void isUTF8_bytesUtf8() {
        Text text = new Text(state, config);
        assertTrue(text.isUTF8(getBytes("utf8valid")));
    }

    @Test
    void isUTF8_bytesUtf8Bad() {
        Text text = new Text(state, config);
        assertFalse(text.isUTF8(getBytes("utf8bad")));
    }

    @Test
    void isUTF8_filesAscii() {
        Text text = new Text(state, config);
        try {
            assertTrue(text.isUTF8(Files.readAllBytes(testAscii)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void isUTF8_filesUtf8() {
        Text text = new Text(state, config);
        try {
            assertTrue(text.isUTF8(Files.readAllBytes(testUtf8)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void isUTF8_filesUtf8Bad() {
        Text text = new Text(state, config);
        try {
            assertFalse(text.isUTF8(Files.readAllBytes(testInvalidUtf8)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void isUTF8_nonExistent() {
        Text text = new Text(state, config);
        assertThrows(IOException.class, () ->
                text.isUTF8(getBytes(Files.readString(Paths.get("none.test")))));
    }

    @Test
    void isUTF8_bytesEmpty() {
        Text text = new Text(state, config);
        assertTrue(text.isUTF8(new byte[] {}));
    }

    @Test
    void isASCII_bytesAscii() {
        Text text = new Text(state, config);
        assertTrue(text.isASCII(getBytes("ascii")));
    }

    @Test
    void isASCII_bytesNoAscii() {
        Text text = new Text(state, config);
        assertFalse(text.isASCII(getBytes("nonascii")));
    }

    @Test
    void isASCII_bytesUtf8() {
        Text text = new Text(state, config);
        assertFalse(text.isASCII(getBytes("utf8valid")));
    }

    @Test
    void isASCII_filesAscii() {
        Text text = new Text(state, config);
        try {
            assertTrue(text.isASCII(Files.readAllBytes(testAscii)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void isASCII_filesUtf8() {
        Text text = new Text(state, config);
        try {
            assertFalse(text.isASCII(Files.readAllBytes(testUtf8)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void isASCII_filesBadUtf8() {
        Text text = new Text(state, config);
        try {
            assertFalse(text.isASCII(Files.readAllBytes(testInvalidUtf8)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void isASCII_nonExistent() {
        Text text = new Text(state, config);
        assertThrows(IOException.class, () ->
                text.isASCII(getBytes(Files.readString(Paths.get("none.test")))));
    }

    @Test
    void isASCII_bytesEmpty() {
        Text text = new Text(state, config);
        assertTrue(text.isASCII(new byte[] {}));
    }

    private void ensureNormalizedUnicode(String content) {
        Pattern pattern = Pattern.compile("[\\u200B-\\u200D\\uFEFF]");
        assertFalse(pattern.matcher(content).find());
    }

    @Test
    void normalizeUnicodeDirty() {
        Text text = new Text(state, config);
        try {
            state.setTextFile(Files.readString(Paths.get("resources/test-text/test-unicode-dirty.txt")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        assertDoesNotThrow(text::normalizeUnicode);
        ensureNormalizedUnicode(state.getTextFile());
    }

    @Test
    void normalizeUnicodeClean() {
        Text text = new Text(state, config);
        try {
            state.setTextFile(Files.readString(Paths.get("resources/test-text/test-unicode-clean.txt")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        assertDoesNotThrow(text::normalizeUnicode);
        ensureNormalizedUnicode(state.getTextFile());
    }

    @Test
    void normalizeUnicodeNonExistent() {
        Text text = new Text(state, config);
           assertThrows(IOException.class, () ->
                   state.setTextFile(Files.readString(Paths.get("none.test"))));
        assertThrows(IllegalStateException.class, text::normalizeUnicode);
    }

    private void ensureHtmlEscapes(String original, String escaped) {
        if (original.contains("&")) assertTrue(escaped.contains("&amp;"));
        if (original.contains("<")) assertTrue(escaped.contains("&lt;"));
        if (original.contains(">")) assertTrue(escaped.contains("&gt;"));
        if (original.contains("\"")) assertTrue(escaped.contains("&quot;"));
        if (original.contains("'")) assertTrue(escaped.contains("&#39;"));
    }

    @Test
    void escapeHTMLDirty() {
        Text text = new Text(state, config);
        Path original = Paths.get("resources/test-text/test-strip-dirty.txt");
        try {
            state.setTextFile(Files.readString(original));
            assertDoesNotThrow(text::escapeHTML);
            ensureHtmlEscapes(Files.readString(original), state.getTextFile());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void escapeHTMLClean() {
        Text text = new Text(state, config);
        Path original = Paths.get("resources/test-text/test-strip-clean.txt");
        try {
            state.setTextFile(Files.readString(original));
            assertDoesNotThrow(text::escapeHTML);
            ensureHtmlEscapes(Files.readString(original), state.getTextFile());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void escapeHTMLNonExistent() {
        Text text = new Text(state, config);
        assertThrows(IOException.class, () -> state.setTextFile(Files.readString(Paths.get("none.test"))));
        assertThrows(IllegalStateException.class, text::escapeHTML);
    }

    private void saveTextDataTest(Path osTargetPath, Function<byte[], Boolean> func, boolean check) {
        try {
            Text text = new Text(state, config);
            prepData(osTargetPath);
            assertDoesNotThrow(text::saveTextData);
            assertTrue(Files.exists(config.getGeneralOutputPath().resolve(state.getGeneralFileTitle()+".txt")));
            byte[] data = Files.readAllBytes(config.getGeneralOutputPath().
                    resolve(state.getGeneralFileTitle()+".txt"));
            if (check) assertTrue(func.apply(data));
            if (!check) assertFalse(func.apply(data));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Test
    void saveTextData_ascii() {
        Text text = new Text(state, config);
        saveTextDataTest(testAscii, text::isASCII, true);
    }

    @Test
    void saveTextData_utf8() {
        Text text = new Text(state, config);
        saveTextDataTest(testUtf8, text::isUTF8, true);
    }

    @Test
    void saveTextData_utf8Bom() {
        Text text = new Text(state, config);
        saveTextDataTest(testBomUtf8, text::isBom, true);
    }

    @Test
    void saveTextData_utf16Be() {
        Text text = new Text(state, config);
        saveTextDataTest(testBomUtf16be, text::isBom, false);
    }

    @Test
    void saveTextData_utf816Le() {
        Text text = new Text(state, config);
        saveTextDataTest(testBomUtf16le, text::isBom, false);
    }

    @Test
    void saveTextData_utf32Be() {
        Text text = new Text(state, config);
        saveTextDataTest(testBomUtf32be, text::isBom, false);
    }

    //unsupported for v0.1
//    @Test
//    void saveTextData_utf32Le() {
//        Text text = new Text(state, config);
//        saveTextDataTest(testBomUtf32le, text::isBom, false);
//    }

    @Test
    void saveTextData_invalidUtf8() {
        Text text = new Text(state, config);
        saveTextDataTest(testInvalidUtf8, text::isBom, false);
    }

    @Test
    void saveTextData_empty() {
            Text text = new Text(state, config);
            assertThrows(IllegalArgumentException.class, () -> prepData(testEmpty));
            assertThrows(IllegalStateException.class,  text::saveTextData);
    }

    @Test
    void saveTextData_nonExistent() {
        Text text = new Text(state, config);
        assertThrows(RuntimeException.class, () -> prepData(tempDir.resolve("none.test")));
        assertThrows(IllegalStateException.class,  text::saveTextData);
    }
}
package dev.n0153.app;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.Normalizer;

/**
 * Handles text sanitisation for text files.
 * @since 0.1
 */
public class Text {
    private final DisarmConfig config;
    private final DisarmState state;

    /**
     * Public constructor for Text class.
     * @param state Mutable runtime state.
     * @param config Immutable configuration.
     * @since 0.1
     */
    public Text(DisarmState state, DisarmConfig config) {
        this.state = state;
        this.config = config;
    }

    /**
     * Removes control characters, strips dangerous patterns: Script tags, control characters and dangerous URL schemes.
     * @since 0.1
     */
    public void stripPatterns() {
        String text = state.getTextFile().replaceAll("<script.*?>.*?</script>", "")
                .replaceAll("[\\u0000-\\u001F\\u007F-\\u009F]", "")
                .replaceAll("(javascript:|data:|vbscript:)", "");
        state.setTextFile(text);
    }

    /**
     * Checks if specified text data contains a BOM.
     * @param data Input text data in bytes.
     * @return True if BOM is detected.
     * @since 0.1
     */
    public Boolean isBom(byte[] data) {
        if (data.length < 2) return false;

        // UTF-8 BOM: EF BB BF
        if (data.length >= 3 &&
                data[0] == (byte) 0xEF &&
                data[1] == (byte) 0xBB &&
                data[2] == (byte) 0xBF) {
            return true;
        }

        // UTF-16 BE BOM: FE FF
        if (data[0] == (byte) 0xFE && data[1] == (byte) 0xFF) {
            return true;
        }

        // UTF-16 LE BOM: FF FE
        if (data[0] == (byte) 0xFF && data[1] == (byte) 0xFE) {
            return true;
        }

        // UTF-32 BE BOM: 00 00 FE FF
        if (data.length >= 4 &&
                data[0] == 0x00 && data[1] == 0x00 &&
                data[2] == (byte) 0xFE && data[3] == (byte) 0xFF) {
            Charset.forName("UTF-32BE");
            return true;
        }
        // needs valid UTF-32 LE BOM: FF FE 00 00 check
        return false;
    }

    /**
     * Checks if specified text is in UTF-8 encoding.
     * @param data Input text data in bytes.
     * @return True if specified file's encoding is UTF-8.
     * @since 0.1
     */
    public Boolean isUTF8(byte[] data) {
        int i = 0;
        while (i < data.length) {
            byte b = data[i];

            // ASCII (0xxxxxxx)
            if ((b & 0x80) == 0) {
                i++;
                continue;
            }

            // 2-byte UTF-8 (110xxxxx 10xxxxxx)
            if ((b & 0xE0) == 0xC0) {
                if (i + 1 >= data.length) return false;
                if ((data[i + 1] & 0xC0) != 0x80) return false;
                i += 2;
                continue;
            }

            // 3-byte UTF-8 (1110xxxx 10xxxxxx 10xxxxxx)
            if ((b & 0xF0) == 0xE0) {
                if (i + 2 >= data.length) return false;
                if ((data[i + 1] & 0xC0) != 0x80) return false;
                if ((data[i + 2] & 0xC0) != 0x80) return false;
                i += 3;
                continue;
            }

            // 4-byte UTF-8 (11110xxx 10xxxxxx 10xxxxxx 10xxxxxx)
            if ((b & 0xF8) == 0xF0) {
                if (i + 3 >= data.length) return false;
                if ((data[i + 1] & 0xC0) != 0x80) return false;
                if ((data[i + 2] & 0xC0) != 0x80) return false;
                if ((data[i + 3] & 0xC0) != 0x80) return false;
                i += 4;
                continue;
            }

            // Invalid UTF-8 sequence
            return false;
        }
        return true;
    }

    /**
     * Checks if specified text is in ASCII encoding.
     * @param data Input text data in bytes.
     * @return True if specified file's encoding is ASCII.
     * @since 0.1
     */
    public Boolean isASCII(byte[] data) {
        for (byte b : data) {
            if ((b & 0x80) != 0) {
                return false; // Non-ASCII byte
            }
        }
        return true;
    }

    /**
     * Normalises to NFKC form to prevent lookalike character attacks.
     * @since 0.1
     */
    public void normalizeUnicode() {
        String text = Normalizer.normalize(state.getTextFile(), Normalizer.Form.NFKC)
                .replaceAll("[\\u200B-\\u200D\\uFEFF]", "");
        state.setTextFile(text);
    }

    /**
     * Escapes HTML special characters, replacing them with safe character entity references.
     * @since 0.1
     */
    public void escapeHTML() {
        String text = state.getTextFile()
                .replaceAll("&", "&amp;")
                .replaceAll("<", "&lt;")
                .replaceAll(">", "&gt;")
                .replaceAll("\"", "&quot;")
                .replaceAll("'", "&#39;");
        state.setTextFile(text);
    }

    /**
     * Saves the sanitised text from runtime state object as a text file.
     * @throws IOException If fails to write file.
     * @since 0.1
     */
    public void saveTextData() throws IOException {
        Path toFile = config.getGeneralOutputPath().resolve(state.getGeneralFileTitle()+".txt");
        Files.writeString(toFile, state.getTextFile(), StandardCharsets.UTF_8);
    }
}

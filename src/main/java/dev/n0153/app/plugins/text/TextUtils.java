package dev.n0153.app.plugins.text;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class TextUtils {
    /**
     * Checks if specified text data contains a BOM.
     * @return True if BOM is detected.
     * @since 0.1
     */
    public static Charset detectEncoding(byte[] text) {
        if (text.length < 2) {
            throw new IllegalArgumentException("Provided byte array is too short");
        }

        // UTF-8 BOM: EF BB BF
        if (text.length >= 3 &&
                text[0] == (byte) 0xEF &&
                text[1] == (byte) 0xBB &&
                text[2] == (byte) 0xBF) {
            return StandardCharsets.UTF_8;
        }

        // UTF-16 BE BOM: FE FF
        if (text[0] == (byte) 0xFE && text[1] == (byte) 0xFF) {
            return StandardCharsets.UTF_16BE;
        }

        // UTF-16 LE BOM: FF FE
        if (text[0] == (byte) 0xFF && text[1] == (byte) 0xFE) {
            return StandardCharsets.UTF_16LE;
        }

        // UTF-32 BE BOM: 00 00 FE FF
        if (text.length >= 4 &&
                text[0] == 0x00 && text[1] == 0x00 &&
                text[2] == (byte) 0xFE && text[3] == (byte) 0xFF) {
            return StandardCharsets.UTF_32BE;
        }
        // needs valid UTF-32 LE BOM: FF FE 00 00 check
        throw new IllegalArgumentException("Failed to detect encoding");
    }
}

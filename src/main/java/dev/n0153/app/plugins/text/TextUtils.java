package dev.n0153.app.plugins.text;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class TextUtils {

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

        // UTF-32 LE BOM FF FE 00 00 (comes before UTF-16 LE - shares the same first 2 bytes)

        if (text.length >= 4 &&
                text[0] == (byte) 0xFF &&
                text[1] == (byte) 0xFE &&
                text[2] == 0x00 &&
                text[3] == 0x00) {
            return StandardCharsets.UTF_32LE;
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
        throw new IllegalArgumentException("Failed to detect encoding");
    }
}

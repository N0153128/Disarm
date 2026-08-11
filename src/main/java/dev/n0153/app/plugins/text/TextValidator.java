package dev.n0153.app.plugins.text;

import dev.n0153.app.MediaValidator;
import dev.n0153.app.exceptions.ValidationException;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class TextValidator implements MediaValidator {
    private TextConfig config;
    private TextContext context;

    public void createMeta(TextConfig config, TextContext context) {
        this.config = config;
        this.context = context;
    }

    /**
     * Checks if specified text data contains a BOM.
     * @return True if BOM is detected.
     * @since 0.1
     */
    public boolean isBom() {
        byte[] text = context.getRawBytes();
        try {
            TextUtils.detectEncoding(text);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }


    /**
     * Checks if specified text is in ASCII encoding.
     * @return True if specified file's encoding is ASCII.
     * @since 0.1
     */
    public boolean isASCII() {
        byte[] text = context.getRawBytes();
        for (byte b : text) {
            if ((b & 0x80) != 0) {
                return false; // Non-ASCII byte
            }
        }
        return true;
    }

    /**
     * Checks if specified text is in UTF-8 encoding.
     * @return True if specified file's encoding is UTF-8.
     * @since 0.1
     */
    public boolean isUTF8() {
        byte[] text = context.getRawBytes();
        int i = 0;
        while (i < text.length) {
            byte b = text[i];

            // ASCII (0xxxxxxx)
            if ((b & 0x80) == 0) {
                i++;
                continue;
            }

            // 2-byte UTF-8 (110xxxxx 10xxxxxx)
            if ((b & 0xE0) == 0xC0) {
                if (i + 1 >= text.length) return false;
                if ((text[i + 1] & 0xC0) != 0x80) return false;
                i += 2;
                continue;
            }

            // 3-byte UTF-8 (1110xxxx 10xxxxxx 10xxxxxx)
            if ((b & 0xF0) == 0xE0) {
                if (i + 2 >= text.length) return false;
                if ((text[i + 1] & 0xC0) != 0x80) return false;
                if ((text[i + 2] & 0xC0) != 0x80) return false;
                i += 3;
                continue;
            }

            // 4-byte UTF-8 (11110xxx 10xxxxxx 10xxxxxx 10xxxxxx)
            if ((b & 0xF8) == 0xF0) {
                if (i + 3 >= text.length) return false;
                if ((text[i + 1] & 0xC0) != 0x80) return false;
                if ((text[i + 2] & 0xC0) != 0x80) return false;
                if ((text[i + 3] & 0xC0) != 0x80) return false;
                i += 4;
                continue;
            }

            // Invalid UTF-8 sequence
            return false;
        }
        return true;
    }

    /**
     * Ensures that specified text object utilises supported encoding.
     * @return True if encoding is appropriate.
     * @since 0.1
     */
    public boolean validateEncoding() {
        try {
            Charset detected = TextUtils.detectEncoding(context.getRawBytes());
            context.setBom(true);
            context.setDetectedEncoding(detected.name());
            return true;
        } catch (IllegalArgumentException e) {
            if (isUTF8()) {
                context.setDetectedEncoding("UTF-8");
                return true;
            } else if (isASCII()) {
                context.setDetectedEncoding("ASCII");
                return true;
            } else {
                return false;
            }
        }
    }

    public boolean checkSizeLimit(Path osTargetPath, TextConfig config) {
        try {
            return Files.size(osTargetPath) <= config.getMaxTextSize();
        } catch (IOException e) {
            throw new ValidationException("Failed to check size limit");
        }
    }


    public boolean checkMeta() {
        return this.config != null && this.context != null;
    }

    @Override
    public boolean validate(Path osTargetPath) {
        try {
            if (!checkMeta()) {
                throw new ValidationException("Text Validator: meta is empty");
            }
            byte[] text = Files.readAllBytes(osTargetPath);
            context.setRawBytes(text);
            if (!checkSizeLimit(osTargetPath, config)) {
                throw new ValidationException("Text File size limit exceeded");
            }
            return validateEncoding();
        } catch (IOException e) {
            throw new ValidationException("Failed to read bytes");
        }

    }
}

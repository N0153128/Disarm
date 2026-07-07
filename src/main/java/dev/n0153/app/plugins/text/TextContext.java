package dev.n0153.app.plugins.text;

import dev.n0153.app.MediaContext;

import java.util.HashMap;
import java.util.Map;

public class TextContext implements MediaContext {

    private final String KEY_TEXT_CONTENT = "textContent";
    private final String KEY_TEXT_TITLE = "textTitle";
    private final String KEY_DETECTED_ENCODING = "detectedEncoding";
    private final String KEY_HAS_BOM = "hasBom";
    private final String KEY_RAW_BYTES = "rawBytes";

    private final Map<String, Object> contextStorage = new HashMap<>() {{
        put(KEY_TEXT_CONTENT, null);
        put(KEY_TEXT_TITLE, null);
        put(KEY_DETECTED_ENCODING, null);
        put(KEY_HAS_BOM, null);
        put(KEY_RAW_BYTES, null);
    }};


    @Override
    public void put(String key, Object value) {
        contextStorage.replace(key, value);
    }

    @Override
    public <ValueType> ValueType get(String key, Class<ValueType> type) {
        return type.cast(contextStorage.get(key));
    }

    @Override
    public void release() {
        contextStorage.clear();
    }

    @Override
    public void close() throws Exception {}

    public String getTextContent() {
        return get(KEY_TEXT_CONTENT, String.class);
    }

    public String getTextTitle() {
        return get(KEY_TEXT_TITLE, String.class);
    }

    public String getDetectedEncoding() {
        return get(KEY_DETECTED_ENCODING, String.class);
    }

    public boolean isBom() {
        Boolean bom = get(KEY_HAS_BOM, Boolean.class);
        if (bom == null) {
            return false;
        } else {
            return get(KEY_HAS_BOM, Boolean.class);
        }
    }

    public byte[] getRawBytes() {
        return get(KEY_RAW_BYTES, byte[].class);
    }

    public void setTextContent(String newTextContent) {
        if (newTextContent == null) {
            throw new IllegalArgumentException("Text content cannot be null");
        }
        put(KEY_TEXT_CONTENT, newTextContent);
    }

    public void setTextTitle(String newTextTitle) {
        if (newTextTitle == null) {
            throw new IllegalArgumentException("Text title cannot be null");
        }
        if (newTextTitle.isEmpty()) {
            throw new IllegalArgumentException("Text title cannot be empty");
        }
        put(KEY_TEXT_TITLE, newTextTitle);
    }

    public void setDetectedEncoding(String newDetectedEncoding) {
        if (newDetectedEncoding == null) {
            throw new IllegalArgumentException("Text encoding cannot be null");
        }
        if (newDetectedEncoding.isEmpty()) {
            throw new IllegalArgumentException("Text encoding cannot be empty");
        }
        put(KEY_DETECTED_ENCODING, newDetectedEncoding);
    }

    public void setBom(boolean newBom) {
        put(KEY_HAS_BOM, newBom);
    }

    public void setRawBytes(byte[] newRawBytes) {
        if (newRawBytes == null) {
            throw new IllegalArgumentException("Text raw bytes cannot be null");
        }
        if (newRawBytes.length == 0) {
            throw new IllegalArgumentException("Text raw bytes cannot be empty");
        }
        put(KEY_RAW_BYTES, newRawBytes);
    }
}

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
        return get(KEY_HAS_BOM, Boolean.class);
    }

    public byte[] getRawBytes() {
        return get(KEY_RAW_BYTES, byte[].class);
    }

    public void setTextContent(String newTextContent) {
        put(KEY_TEXT_CONTENT, newTextContent);
    }

    public void setTextTitle(String newTextTitle) {
        put(KEY_TEXT_TITLE, newTextTitle);
    }

    public void setDetectedEncoding(String newDetectedEncoding) {
        put(KEY_DETECTED_ENCODING, newDetectedEncoding);
    }

    public void setBom(boolean newBom) {
        put(KEY_HAS_BOM, newBom);
    }

    public void setRawBytes(byte[] newRawBytes) {
        put(KEY_RAW_BYTES, newRawBytes);
    }
}

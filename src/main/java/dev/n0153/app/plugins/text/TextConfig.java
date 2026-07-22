package dev.n0153.app.plugins.text;

import dev.n0153.app.MediaConfig;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.*;

public class TextConfig implements MediaConfig {

    private final int maxTextSize = 5_000_000; //5MB
    private final String[] urlSchemes = {"javascript:", "data:", "vbscript:"};
    private final Set<Character> zeroLengthChars = new HashSet<>(Set.of(
            '\u200B', '\u200D', '\uFEFF'
    ));
    private final Normalizer.Form normalizeForm = Normalizer.Form.NFKC;
    private final Charset outputEncoding = StandardCharsets.UTF_8;

    private final String KEY_MAX_TEXT_SIZE = "maxTextSize";
    private final String KEY_URL_SCHEMES = "urlSchemes";
    private final String KEY_ZERO_LENGTH_CHARS = "zeroLengthChars";
    private final String KEY_NORMALIZE_FORM = "normalizeForm";
    private final String KEY_OUTPUT_ENCODING = "outputEncoding";

    private final Map<String, Object> configStorage = new HashMap<>() {{
        put(KEY_MAX_TEXT_SIZE, maxTextSize);
        put(KEY_URL_SCHEMES, urlSchemes);
        put(KEY_ZERO_LENGTH_CHARS, zeroLengthChars);
        put(KEY_NORMALIZE_FORM, normalizeForm);
        put(KEY_OUTPUT_ENCODING, outputEncoding);
    }};


    @Override
    public void put(String key, Object value) {
        if (!configStorage.containsKey(key)) {
            throw new IllegalArgumentException("Specified key doesn't exist");
        }
        configStorage.replace(key, value);
    }

    @Override
    public <$ValueType> $ValueType get(String key, Class<$ValueType> type) {
        return type.cast(configStorage.get(key));
    }

    @Override
    public void release() {
        configStorage.clear();
    }

    @Override
    public String getName() {
        return "Text";
    }

    @Override
    public String toDebugString() {
        StringBuilder output = new StringBuilder("\n\n=== TEXT PLUGIN CONFIG === \n");
        for (Map.Entry<String, Object> entry : configStorage.entrySet()) {
            output.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
        output.append("\n=== END OF SNAPSHOT ===\n");
        return output.toString();
    }

    @Override
    public double getVersion() {
        return 1;
    }

    @Override
    public Set<String> supports() {
        return Set.of("text", "plain", "log", "json");
    }

    @Override
    public int maxFileSizeInBytes(String mime) {
        return 5_000_000;
    }

    public int getMaxTextSize() {
        return Objects.requireNonNullElse(
                get(KEY_MAX_TEXT_SIZE, Integer.class),
                maxTextSize
        );
    }

    public String[] getUrlSchemes() {
        return Objects.requireNonNullElse(
                get(KEY_URL_SCHEMES, String[].class),
                urlSchemes
        );
    }

    @SuppressWarnings("unchecked")
    public Set<Character> getZeroLengthChars() {
        return Objects.requireNonNullElse(
                get(KEY_ZERO_LENGTH_CHARS, Set.class),
                zeroLengthChars
        );
    }

    public Normalizer.Form getNormalizeForm() {
        return Objects.requireNonNullElse(
                get(KEY_NORMALIZE_FORM, Normalizer.Form.class),
                normalizeForm
        );
    }

    public Charset getOutputEncoding() {
        return Objects.requireNonNullElse(
                get(KEY_OUTPUT_ENCODING, Charset.class),
                outputEncoding
        );
    }

    public void setMaxTextSize(int newMaxTextSize) {
        if (newMaxTextSize < 0) {
            throw new IllegalArgumentException("Max text size cannot be less than zero");
        }
        if (newMaxTextSize == 0) {
            throw new IllegalArgumentException("Max text size cannot be zero");
        }
        put(KEY_MAX_TEXT_SIZE, newMaxTextSize);
    }

    public void setUrlSchemes(String[] newUrlSchemes) {
        if (newUrlSchemes == null) {
            throw new IllegalArgumentException("URL schemes array cannot be null");
        }
        if (newUrlSchemes.length == 0) {
            throw new IllegalArgumentException("URL schemes array cannot be empty");
        }
        put(KEY_URL_SCHEMES, newUrlSchemes);
    }

    public void setZeroLengthChars(Set<Character> newZeroLengthChars) {
        if (newZeroLengthChars == null) {
            throw new IllegalArgumentException("URL schemes array cannot be null");
        }
        if (newZeroLengthChars.isEmpty()) {
            throw new IllegalArgumentException("URL schemes array cannot be empty");
        }
        put(KEY_ZERO_LENGTH_CHARS, newZeroLengthChars);
    }

    public void setNormalizeForm(Normalizer.Form newNormalizeForm) {
        if (newNormalizeForm == null) {
            throw new IllegalArgumentException("Normalization form cannot be null");
        }
        put(KEY_NORMALIZE_FORM, newNormalizeForm);
    }

    public void setOutputEncoding(Charset newOutputEncoding) {
        if (newOutputEncoding == null) {
            throw new IllegalArgumentException("Output encoding cannot be null");
        }
        put(KEY_OUTPUT_ENCODING, newOutputEncoding);
    }
}

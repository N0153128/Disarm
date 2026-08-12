package dev.n0153.app.plugins.text;

import dev.n0153.app.MediaConfig;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.*;

public class TextConfig implements MediaConfig {

    public record ControlCharactersRange(int rangeStart, int rangeEnd) {
        public ControlCharactersRange {
            if (rangeStart < 0) {
                throw new IllegalArgumentException("Control character range start cannot be less than zero");
            }
            if (rangeEnd < 0) {
                throw new IllegalArgumentException("Control character end start cannot be less than zero");
            }
            if (rangeStart > rangeEnd) {
                throw new IllegalArgumentException("Control character range start cannot be greater than range end");
            }
            if (rangeEnd > 0xFFFF) {
                throw new IllegalArgumentException("Control character range start cannot exceed 0xFFFF");
            }
        }
    }

    private final int maxTextSize = 5_000_000; //5MB
    private final String[] urlSchemes = {"javascript:", "data:", "vbscript:"};
    private final Set<Character> zeroLengthChars = new HashSet<>(Set.of(
            '\u200B', '\u200D', '\uFEFF'
    ));
    private final Normalizer.Form normalizeForm = Normalizer.Form.NFKC;
    private final Charset outputEncoding = StandardCharsets.UTF_8;
    private final boolean dontSaveText = false;
    private final String textDefaultOutputTo = "default";
    private final List<ControlCharactersRange> controlCharactersRanges = List.of(
            new ControlCharactersRange(0x0000, 0x001F),
            new ControlCharactersRange(0x007F, 0x009F)
    );
    private final String skipScriptStripFor = "default";

    private final String KEY_MAX_TEXT_SIZE = "maxTextSize";
    private final String KEY_URL_SCHEMES = "urlSchemes";
    private final String KEY_ZERO_LENGTH_CHARS = "zeroLengthChars";
    private final String KEY_NORMALIZE_FORM = "normalizeForm";
    private final String KEY_OUTPUT_ENCODING = "outputEncoding";
    private final String KEY_DONT_SAVE_TEXT = "dontSaveText";
    private final String KEY_TEXT_DEFAULT_OUTPUT_TO = "textDefaultOutputTo";
    private final String KEY_CONTROL_CHARACTERS_RANGES = "controlCharactersRanges";
    private final String KEY_SKIP_SCRIPT_STRIP_FOR = "skipScriptStripFor";

    private final Map<String, Object> configStorage = new HashMap<>() {{
        put(KEY_MAX_TEXT_SIZE, maxTextSize);
        put(KEY_URL_SCHEMES, urlSchemes);
        put(KEY_ZERO_LENGTH_CHARS, zeroLengthChars);
        put(KEY_NORMALIZE_FORM, normalizeForm);
        put(KEY_OUTPUT_ENCODING, outputEncoding);
        put(KEY_TEXT_DEFAULT_OUTPUT_TO, textDefaultOutputTo);
        put(KEY_CONTROL_CHARACTERS_RANGES, controlCharactersRanges);
        put(KEY_SKIP_SCRIPT_STRIP_FOR, skipScriptStripFor);
    }};

    private boolean[] buildControlCharactersStripTable(List<ControlCharactersRange> ranges) {
        boolean[] stripTable = new boolean[65536];
        for (ControlCharactersRange range : ranges) {
            for (int codePoint = range.rangeStart(); codePoint <= range.rangeEnd(); codePoint++) {
                stripTable[codePoint] = true;
            }
        }
        return stripTable;
    }

    private boolean[] controlCharactersStripTable = buildControlCharactersStripTable(controlCharactersRanges);

    public boolean shouldStripControlCharacter(char character) {
        return controlCharactersStripTable[character];
    }

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
        return Set.of("text", "plain", "log", "json", "txt");
    }

    @Override
    public int maxFileSizeInBytes(String mime) {
        return 5_000_000;
    }

    @SuppressWarnings("unchecked")
    public List<ControlCharactersRange> getControlCharactersRanges() {
        return Objects.requireNonNullElse(
                get(KEY_CONTROL_CHARACTERS_RANGES, List.class),
                controlCharactersRanges
        );
    }

    public String getSkipScriptStripFor() {
        return Objects.requireNonNullElse(
                get(KEY_SKIP_SCRIPT_STRIP_FOR, String.class),
                skipScriptStripFor
        );
    }

    public String getTextDefaultOutputTo() {
        return Objects.requireNonNullElse(
                get(KEY_TEXT_DEFAULT_OUTPUT_TO, String.class),
                textDefaultOutputTo
        );
    }

    public boolean getDontSaveText() {
        return Objects.requireNonNullElse(
                get(KEY_DONT_SAVE_TEXT, Boolean.class),
                dontSaveText
        );
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

    public void setSkipScriptStripFor(String newSkipScriptStripFor) {
        if (newSkipScriptStripFor == null) {
            throw new IllegalArgumentException("Skip script strip for format cannot be null");
        }
        if (newSkipScriptStripFor.isEmpty()) {
            throw new IllegalArgumentException("Skip script strip for format cannot be empty");
        }
        if (newSkipScriptStripFor.equals("default")) {
            throw new IllegalArgumentException("Skip script strip for format cannot be default");
        }
        if (!supports().contains(newSkipScriptStripFor)) {
            throw new IllegalArgumentException("Unsupported format specified");
        }
        put(KEY_SKIP_SCRIPT_STRIP_FOR, newSkipScriptStripFor);
    }

    public void setControlCharactersRanges(List<ControlCharactersRange> newControlCharactersRanges) {
        if (newControlCharactersRanges == null) {
            throw new IllegalArgumentException("Control character ranges list cannot be null");
        }
        if (newControlCharactersRanges.isEmpty()) {
            throw new IllegalArgumentException("Control character ranges list cannot be empty");
        }
        controlCharactersStripTable = buildControlCharactersStripTable(newControlCharactersRanges);
        put(KEY_CONTROL_CHARACTERS_RANGES, newControlCharactersRanges);
    }

    public void setDontSaveText(boolean newDontSaveText) {
        put(KEY_DONT_SAVE_TEXT, newDontSaveText);
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

    public void setTextDefaultOutputTo(String newTextDefaultOutputTo) {
        if (newTextDefaultOutputTo == null) {
            throw new IllegalArgumentException("Text output format cannot be null");
        }
        if (newTextDefaultOutputTo.isEmpty()) {
            throw new IllegalArgumentException("Text output format cannot be empty");

        }
        if (!supports().contains(newTextDefaultOutputTo)) {
            throw new IllegalArgumentException("Provided text output format is not supported");
        }
        put(KEY_TEXT_DEFAULT_OUTPUT_TO, newTextDefaultOutputTo);
    }
}

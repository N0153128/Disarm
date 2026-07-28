package dev.n0153.app.plugins.audio;

import dev.n0153.app.MediaConfig;
import java.util.*;

public class AudioConfig implements MediaConfig {
    private final int maxAudioDuration = 300_000;
    private final Map<String, Integer> maxBitrates = new HashMap<>() {{
        put("mp3", 320_000);
        put("flac", 1_500_000);
        put("ogg", 500_000);
        put("wav", 4_608_000);
        put("au", 1_536_000);
        put("aif", 4_608_000);
        put("aiff", 4_608_000);
        put("aifc", 4_608_000);
    }};
    private final Map<String, Integer> maxSampleRates = new HashMap<>() {{
        put("mp3", 48_000);
        put("flac", 192_000);
        put("ogg", 48_000);
        put("wav", 96_000);
        put("au", 48_000);
        put("aif", 96_000);
        put("aiff", 96_000);
        put("aifc", 96_000);
    }};

    private static final List<String> MP3_CODECS = List.of("mp3");
    private static final List<String> OGG_CODECS = List.of("vorbis", "opus", "flac",
            "speex", "libvorbis");
    private static final List<String> FLAC_CODECS = List.of("flac");
    private static final List<String> WAV_CODECS = List.of("pcm", "adpcm", "mp3",
            "a-law", "μ-law", "gsm",
            "u-law", "pcm_u8");
    private static final List<String> AU_CODECS = List.of("pcm", "μ-law", "u-law", "pcm_s16be");
    private static final List<String> AIF_CODECS = List.of("pcm", "μ-law", "a-law",
            "ima adpcm", "u-law", "pcm_s16be");

    private final Map<String, List<String>> allowedAudioCodecs = new HashMap<>() {{
        put("mp3", MP3_CODECS);
        put("mpeg", MP3_CODECS);
        put("ogg", OGG_CODECS);
        put("flac", FLAC_CODECS);
        put("wav", WAV_CODECS);
        put("wave", WAV_CODECS);
        put("x-wav", WAV_CODECS);
        put("au", AU_CODECS);
        put("basic", AU_CODECS);
        put("aif", AIF_CODECS);
        put("aiff", AIF_CODECS);
        put("aifc", AIF_CODECS);
    }};

    private static final Map<String, String> mimeToFormat = new HashMap<>() {{
        //mp3
        put("mp3", "mp3");
        put("mpeg", "mp3");
        put("mpeg-3", "mp3");

        //ogg
        put("ogg", "ogg");
        put("vorbis", "ogg");
        put("x-ogg", "ogg");

        //flac
        put("flac", "flac");
        put("x-flac", "flac");

        //wav
        put("wav", "wav");
        put("x-wav", "wav");
        put("wave", "wav");
        put("vnd.wave", "wav");

        //au
        put("au", "au");
        put("basic", "au");

        //aif
        put("aif", "aiff");
        put("aiff", "aiff");
        put("x-aiff", "aiff");
        put("aifc", "aiff");

    }};
    private final int maxChannels = 2;
    private final int bitrateFallback = 320_000;
    private final int samplingRateFallback = 48_000;
    private final int outputBitrate = 128_000;
    private final int outputSampleRate = 44_100;
    private final int outputChannels = 2;
    private final int maxFileSize = 5_000_000; //5MB
    private final String defaultOutputTo = "default";
    private final boolean dontSaveAudio = false;

    private final String KEY_MAX_AUDIO_DURATION = "maxAudioDuration";
    private final String KEY_MAX_BITRATES = "maxBitrates";
    private final String KEY_MAX_SAMPLE_RATES = "maxSampleRates";
    private final String KEY_MAX_CHANNELS = "maxChannels";
    private final String KEY_BITRATE_FALLBACK = "bitrateFallback";
    private final String KEY_SAMPLING_RATE_FALLBACK = "samplingRateFallback";
    private final String KEY_OUTPUT_BITRATE = "outputBitrate";
    private final String KEY_OUTPUT_SAMPLE_RATE = "outputSampleRate";
    private final String KEY_OUTPUT_CHANNELS = "outputChannels";
    private final String KEY_MIME_TO_FORMAT = "mimeToFormat";
    private final String KEY_ALLOWED_AUDIO_CODECS = "allowedAudioCodecs";
    private final String KEY_MAX_FILE_SIZE = "maxFileSize";
    private final String KEY_DEFAULT_OUTPUT_TO = "defaultOutputTo";
    private final String KEY_DONT_SAVE_AUDIO = "dontSaveAudio";


    @Override
    public void put(String key, Object value) {
        if (!configStorage.containsKey(key)) {
            throw new IllegalArgumentException("Specified key doesn't exist");
        }
        configStorage.replace(key, value);
    }

    private final Map<String, Object> configStorage = new HashMap<>() {{
        put(KEY_MAX_AUDIO_DURATION, maxAudioDuration);
        put(KEY_MAX_BITRATES, maxBitrates);
        put(KEY_MAX_SAMPLE_RATES, maxSampleRates);
        put(KEY_MAX_CHANNELS, maxChannels);
        put(KEY_BITRATE_FALLBACK, bitrateFallback);
        put(KEY_SAMPLING_RATE_FALLBACK, samplingRateFallback);
        put(KEY_OUTPUT_BITRATE, outputBitrate);
        put(KEY_OUTPUT_SAMPLE_RATE, outputSampleRate);
        put(KEY_OUTPUT_CHANNELS, outputChannels);
        put(KEY_MAX_FILE_SIZE, maxFileSize);
        put(KEY_DONT_SAVE_AUDIO, dontSaveAudio);
    }};

    @Override
    public <$ValueType> $ValueType get(String key, Class<$ValueType> type) {
        return type.cast(configStorage.get(key));
    }

    @Override
    public void release() {
        configStorage.clear();
    }

    @Override
    public String toDebugString() {
        StringBuilder output = new StringBuilder("\n\n=== AUDIO PLUGIN CONFIG === \n");
        for (Map.Entry<String, Object> entry : configStorage.entrySet()) {
            output.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
        output.append("\n=== END OF SNAPSHOT ===\n");
        return output.toString();
    }

    @Override
    public String getName() {
        return "audio";
    }

    @Override
    public double getVersion() {
        return 1;
    }

    @Override
    public Set<String> supports() {
        return mimeToFormat.keySet();
    }

    @Override
    public int maxFileSizeInBytes(String mime) {
        return getMaxFileSize(); //5MB
    }

    //getters

    public boolean getDontSaveAudio() {
        return Objects.requireNonNullElse(
                get(KEY_DONT_SAVE_AUDIO, Boolean.class),
                dontSaveAudio
        );
    }

    public String getDefaultOutputTo() {
        return get(KEY_DEFAULT_OUTPUT_TO, String.class);
    }

    public int getMaxFileSize() {
        return get(KEY_MAX_FILE_SIZE, Integer.class);
    }

    public boolean isCodecAllowed(String format, String codec) {
        List<String> codecs = allowedAudioCodecs.getOrDefault(format, List.of());
        return codecs.contains(codec);
    }

    public String getFormatFromMime(String mime) {
        return mimeToFormat.get(mime);
    }

    public int getMaxAudioDuration() {
        return Objects.requireNonNullElse(
                get(KEY_MAX_AUDIO_DURATION, Integer.class),
                maxAudioDuration
        );
    }
    @SuppressWarnings("unchecked")
    public int getMaxBitrates(String format) {
        Map<String, Integer> rates = get(KEY_MAX_BITRATES, Map.class);
        if (rates == null) {
            rates = maxBitrates;
        }
        Integer rate = rates.get(format);
        return rate != null ? rate : maxBitrates.getOrDefault(format, 0);
    }

    @SuppressWarnings("unchecked")
    public int getMaxSampleRates(String format) {
        Map<String, Integer> rates = get(KEY_MAX_SAMPLE_RATES, Map.class);
        if (rates == null) {
            rates = maxSampleRates;
        }
        Integer rate = rates.get(format);
        return rate != null ? rate : maxSampleRates.getOrDefault(format, 0);
    }

    public int getMaxChannels() {
        return Objects.requireNonNullElse(
                get(KEY_MAX_CHANNELS, Integer.class),
                maxChannels
        );
    }

    public int getBitrateFallback() {
        return Objects.requireNonNullElse(
                get(KEY_BITRATE_FALLBACK, Integer.class),
                bitrateFallback
        );
    }

    public int getSamplingRateFallback() {
        return Objects.requireNonNullElse(
                get(KEY_SAMPLING_RATE_FALLBACK, Integer.class),
                samplingRateFallback
        );
    }

    public int getOutputBitrate() {
        return Objects.requireNonNullElse(
                get(KEY_OUTPUT_BITRATE, Integer.class),
                outputBitrate
        );
    }

    public int getOutputSampleRate() {
        return Objects.requireNonNullElse(
                get(KEY_OUTPUT_SAMPLE_RATE, Integer.class),
                outputSampleRate
        );
    }
    public int getOutputChannels() {
        return Objects.requireNonNullElse(
                get(KEY_OUTPUT_CHANNELS, Integer.class),
                outputChannels
        );
    }
    //setters

    public void setDontSaveAudio(boolean newDontSaveAudio) {
        put(KEY_DONT_SAVE_AUDIO, newDontSaveAudio);
    }

    public void setDefaultOutputTo(String newDefaultOutputTo) {
        List<String> allowedValues = List.of(
                "default", "mp3", "ogg",
                "flac", "wav", "au",
                "aif");
        if (!allowedValues.contains(newDefaultOutputTo)) {
            throw new IllegalArgumentException("Audio Config: unsupported format");
        } else {
            put(KEY_DEFAULT_OUTPUT_TO, newDefaultOutputTo);
        }
    }

    public void setMaxFileSize(int newMaxFileSize) {
        if (newMaxFileSize < 0) {
            throw new IllegalArgumentException("Max file size cannot be less than zero");
        }
        if (newMaxFileSize == 0) {
            throw new IllegalArgumentException("Max file size cannot be zero");
        }
        put(KEY_MAX_FILE_SIZE, newMaxFileSize);
    }

    public void setMaxAudioDuration(int newMaxAudioDuration) {
        if (newMaxAudioDuration < 0) {
            throw new IllegalArgumentException("Max audio duration cannot be less than zero");
        }
        if (newMaxAudioDuration == 0) {
            throw new IllegalArgumentException("Max audio duration cannot be zero");
        }
        put(KEY_MAX_AUDIO_DURATION, newMaxAudioDuration);
    }

    @SuppressWarnings("unchecked")
    public void setMaxBitrates(String format, int newMaxBitrates) {
        if (newMaxBitrates < 0) {
            throw new IllegalArgumentException("Max audio bitrate cannot be less than zero");
        }
        if (newMaxBitrates == 0) {
            throw new IllegalArgumentException("Max audio bitrate cannot be zero");
        }
        if (format == null) {
            throw new IllegalArgumentException("Format cannot be null");
        }
        if (format.isEmpty()) {
            throw new IllegalArgumentException("Format cannot be empty");
        }
        Map<String, Integer> rates = get(KEY_MAX_BITRATES, Map.class);
        if (rates == null) {
            rates = new HashMap<>(maxBitrates);
        }
        rates.replace(format, newMaxBitrates);
        put(KEY_MAX_BITRATES, rates);
    }

    @SuppressWarnings("unchecked")
    public void setMaxSampleRates(String format, int newMaxSampleRates) {
        if (newMaxSampleRates < 0) {
            throw new IllegalArgumentException("Max audio sampling rate cannot be less than zero");
        }
        if (newMaxSampleRates == 0) {
            throw new IllegalArgumentException("Max audio sampling rate cannot be zero");
        }
        if (format == null) {
            throw new IllegalArgumentException("Format cannot be null");
        }
        if (format.isEmpty()) {
            throw new IllegalArgumentException("Format cannot be empty");
        }
        Map<String, Integer> rates = get(KEY_MAX_SAMPLE_RATES, Map.class);
        if (rates == null) {
            rates = new HashMap<>(maxSampleRates);
        }
        rates.replace(format, newMaxSampleRates);
        put(KEY_MAX_SAMPLE_RATES, rates);
    }

    public void setMaxChannels(int newMaxChannels) {
        if (newMaxChannels < 0) {
            throw new IllegalArgumentException("Max audio channels cannot be less than zero");
        }
        if (newMaxChannels == 0) {
            throw new IllegalArgumentException("Max audio channels cannot be zero");
        }
        put(KEY_MAX_CHANNELS, newMaxChannels);
    }

    public void setBitrateFallback(int newBitrateFallback) {
        if (newBitrateFallback < 0) {
            throw new IllegalArgumentException("Max audio channels cannot be less than zero");
        }
        if (newBitrateFallback == 0) {
            throw new IllegalArgumentException("Max audio channels cannot be zero");
        }
        put(KEY_BITRATE_FALLBACK, newBitrateFallback);
    }

    public void setSamplingRateFallback(int newSamplingRateFallback) {
        if (newSamplingRateFallback < 0) {
            throw new IllegalArgumentException("Sampling rate fallback cannot be less than zero");
        }
        if (newSamplingRateFallback == 0) {
            throw new IllegalArgumentException("Sampling rate fallback cannot be zero");
        }
        put(KEY_SAMPLING_RATE_FALLBACK, newSamplingRateFallback);
    }

    public void setOutputBitrate(int newOutputBitrate) {
        if (newOutputBitrate < 0) {
            throw new IllegalArgumentException("Output bitrate cannot be less than zero");
        }
        if (newOutputBitrate == 0) {
            throw new IllegalArgumentException("Output bitrate cannot be zero");
        }
        put(KEY_OUTPUT_BITRATE, newOutputBitrate);
    }

    public void setOutputSampleRate(int newOutputSampleRate) {
        if (newOutputSampleRate < 0) {
            throw new IllegalArgumentException("Output sampling rate cannot be less than zero");
        }
        if (newOutputSampleRate == 0) {
            throw new IllegalArgumentException("Output sampling rate cannot be zero");
        }
        put(KEY_OUTPUT_SAMPLE_RATE, newOutputSampleRate);
    }
    public void setOutputChannels(int newOutputChannels) {
        if (newOutputChannels < 0) {
            throw new IllegalArgumentException("Output channels cannot be less than zero");
        }
        if (newOutputChannels == 0) {
            throw new IllegalArgumentException("Output channels cannot be zero");
        }
        put(KEY_OUTPUT_CHANNELS, newOutputChannels);
    }
}

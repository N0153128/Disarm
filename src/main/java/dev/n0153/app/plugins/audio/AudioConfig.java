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
    private final int maxChannels = 2;
    private final int bitrateFallback = 320_000;
    private final int samplingRateFallback = 48_000;
    private final int outputBitrate = 128_000;
    private final int outputSampleRate = 44_100;
    private final int outputChannels = 2;

    private final String KEY_MAX_AUDIO_DURATION = "maxAudioDuration";
    private final String KEY_MAX_BITRATES = "maxBitrates";
    private final String KEY_MAX_SAMPLE_RATES = "maxSampleRates";
    private final String KEY_MAX_CHANNELS = "maxChannels";
    private final String KEY_BITRATE_FALLBACK = "bitrateFallback";
    private final String KEY_SAMPLING_RATE_FALLBACK = "samplingRateFallback";
    private final String KEY_OUTPUT_BITRATE = "outputBitrate";
    private final String KEY_OUTPUT_SAMPLE_RATE = "outputSampleRate";
    private final String KEY_OUTPUT_CHANNELS = "outputChannels";



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
    }};

    @Override
    public <ValueType> ValueType get(String key, Class<ValueType> type) {
        return type.cast(configStorage.get(key));
    }

    @Override
    public void release() {
        configStorage.clear();
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
        return Set.of("mp3", "ogg", "flac",
                "wav", "wave", "au",
                "aif", "aiff", "aifc",
                "x-wav", "mpeg", "x-aiff",
                "basic");
    }

    @Override
    public int maxFileSizeInBytes(String mime) {
        return 5_000_000; //5MB
    }

    //getters
    public int getMaxAudioDuration() {
        return Objects.requireNonNullElse(
                get(KEY_MAX_AUDIO_DURATION, Integer.class),
                maxAudioDuration
        );
    }
    @SuppressWarnings("unchecked")
    public int getMaxBitrates(String format) { //TODO
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

    public void setMaxAudioDuration(int newMaxAudioDuration) {
        put(KEY_MAX_AUDIO_DURATION, newMaxAudioDuration);
    }

    @SuppressWarnings("unchecked")
    public void setMaxBitrates(String format, int newMaxBitrates) {
        Map<String, Integer> rates = get(KEY_MAX_BITRATES, Map.class);
        if (rates == null) {
            rates = new HashMap<>(maxBitrates);
        }
        rates.replace(format, newMaxBitrates);
        put(KEY_MAX_BITRATES, rates);
    }

    @SuppressWarnings("unchecked")
    public void setMaxSampleRates(String format, int newMaxSampleRates) {
        Map<String, Integer> rates = get(KEY_MAX_SAMPLE_RATES, Map.class);
        if (rates == null) {
            rates = new HashMap<>(maxSampleRates);
        }
        rates.replace(format, newMaxSampleRates);
        put(KEY_MAX_SAMPLE_RATES, rates);
    }

    public void setMaxChannels(int newMaxChannels) {
        put(KEY_MAX_CHANNELS, newMaxChannels);
    }

    public void setBitrateFallback(int newBitrateFallback) {
        put(KEY_BITRATE_FALLBACK, newBitrateFallback);
    }

    public void setSamplingRateFallback(int newSamplingRateFallback) {
        put(KEY_SAMPLING_RATE_FALLBACK, newSamplingRateFallback);
    }

    public void setOutputBitrate(int newOutputBitrate) {
        put(KEY_OUTPUT_BITRATE, newOutputBitrate);
    }

    public void setOutputSampleRate(int newOutputSampleRate) {
        put(KEY_OUTPUT_SAMPLE_RATE, newOutputSampleRate);
    }
    public void setOutputChannels(int newOutputChannels) {
        put(KEY_OUTPUT_CHANNELS, newOutputChannels);
    }
}

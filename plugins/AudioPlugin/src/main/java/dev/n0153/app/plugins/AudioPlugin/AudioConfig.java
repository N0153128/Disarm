package dev.n0153.app.plugins.AudioPlugin;

import dev.n0153.app.core.MediaConfig;
import java.util.*;

/**
 * Configuration for the Audio plugin. Every setting is stored in an internal
 * key/value map. Typed getters fall back to a hardcoded default when nothing has
 * been stored, and typed setters validate input before storing it.
 * <p>
 * The config has two groups of settings:
 * <ul>
 *   <li><b>Validation ceilings</b> used by {@link AudioValidator}: duration (300,000 ms),
 *       file size overall and per format (5 MB each), bitrate and sampling rate per
 *       format, channels (2), and an allowlist of codecs per format.</li>
 *   <li><b>Output settings</b> used by {@link AudioProcessor}: output bitrate, sample
 *       rate and channels ({@code 0} keeps the input's value), output format
 *       ({@code "default"} keeps the input's format), fallback bitrate (320,000) and
 *       sampling rate (48,000) for input whose values are unknown, turning off the native
 *       fallback, and a no-save mode for benchmarking.</li>
 * </ul>
 * The MIME-to-format map and the codec allowlist are fixed and have no setters.
 * <p>
 * Supported formats: MP3, OGG, FLAC, WAV, AU and AIFF/AIFC.
 * @since 0.2
 */
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
    private final int outputBitrate = 0;
    private final int outputSampleRate = 0;
    private final int outputChannels = 0;
    private final int maxFileSize = 5_000_000; //5MB
    private final String defaultOutputTo = "default";
    private final boolean dontSaveAudio = false;
    private final boolean disableNativeProcessingFallback = false;
    private final int mp3SizeLimit = 5_000_000; //5MB
    private final int oggSizeLimit = 5_000_000; //5MB
    private final int flacSizeLimit = 5_000_000; //5MB
    private final int wavSizeLimit = 5_000_000; //5MB
    private final int auSizeLimit = 5_000_000; //5MB
    private final int aifSizeLimit = 5_000_000; //5MB
    private final boolean audioDummyDebugTriggered = false;

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
    private final String KEY_DISABLE_NATIVE_PROCESSING_FALLBACK = "disableNativeProcessingFallback";
    private final String KEY_MP3_SIZE_LIMIT = "mp3SizeLimit";
    private final String KEY_OGG_SIZE_LIMIT = "oggSizeLimit";
    private final String KEY_FLAC_SIZE_LIMIT = "flacSizeLimit";
    private final String KEY_WAV_SIZE_LIMIT = "wavSizeLimit";
    private final String KEY_AU_SIZE_LIMIT = "auSizeLimit";
    private final String KEY_AIF_SIZE_LIMIT = "aifSizeLimit";
    private final String KEY_AUDIO_DUMMY_DEBUG_TRIGGERED = "audioDummyDebugTriggered";

    /**
     * {@inheritDoc}
     * Only the predefined config keys are accepted; this method skips the typed setters' validation.
     * @throws IllegalArgumentException if the key is not a predefined config key
     * @since 0.2
     */
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
        put(KEY_DEFAULT_OUTPUT_TO, defaultOutputTo);
        put(KEY_ALLOWED_AUDIO_CODECS, allowedAudioCodecs);
        put(KEY_MIME_TO_FORMAT, mimeToFormat);
        put(KEY_DISABLE_NATIVE_PROCESSING_FALLBACK, disableNativeProcessingFallback);
        put(KEY_MP3_SIZE_LIMIT, mp3SizeLimit);
        put(KEY_OGG_SIZE_LIMIT, oggSizeLimit);
        put(KEY_FLAC_SIZE_LIMIT, flacSizeLimit);
        put(KEY_WAV_SIZE_LIMIT, wavSizeLimit);
        put(KEY_AU_SIZE_LIMIT, auSizeLimit);
        put(KEY_AIF_SIZE_LIMIT, aifSizeLimit);
        put(KEY_AUDIO_DUMMY_DEBUG_TRIGGERED, audioDummyDebugTriggered);
    }};

    /**
     * {@inheritDoc}
     * @return the stored value cast to {@code type}, or {@code null} if the key is unset or unknown
     * @throws ClassCastException if the stored value is not an instance of {@code type}
     * @since 0.2
     */
    @Override
    public <$ValueType> $ValueType get(String key, Class<$ValueType> type) {
        return type.cast(configStorage.get(key));
    }

    /**
     * {@inheritDoc}
     * After this call, the typed getters return their hardcoded defaults and
     * {@link #put(String, Object)} rejects every key.
     * @since 0.2
     */
    @Override
    public void release() {
        configStorage.clear();
    }

    /**
     * {@inheritDoc}
     * Lists every stored key and value under an {@code AUDIO PLUGIN CONFIG} header.
     * @since 0.2
     */
    @Override
    public String toDebugString() {
        StringBuilder output = new StringBuilder("\n\n=== AUDIO PLUGIN CONFIG === \n");
        for (Map.Entry<String, Object> entry : configStorage.entrySet()) {
            output.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
        output.append("\n=== END OF SNAPSHOT ===\n");
        return output.toString();
    }

    /**
     * {@inheritDoc}
     * @return {@code "Audio"}
     * @since 0.2
     */
    @Override
    public String getName() {
        return "Audio";
    }

    /**
     * {@inheritDoc}
     * @return {@code 1}
     * @since 0.2
     */
    @Override
    public double getVersion() {
        return 1;
    }

    /**
     * {@inheritDoc}
     * @return every MIME subtype or format alias this plugin accepts (e.g. {@code "mp3"},
     *         {@code "mpeg"}, {@code "x-wav"}, {@code "basic"}), i.e. the keys of the MIME-to-format map
     * @since 0.2
     */
    @Override
    public Set<String> supports() {
        return mimeToFormat.keySet();
    }

    /**
     * {@inheritDoc}
     * MIME aliases resolve to the size limit of their format (e.g. {@code "mpeg"} uses the MP3 limit).
     * @param mime a supported MIME subtype or format alias
     * @throws IllegalArgumentException if {@code mime} is not a supported audio type
     * @since 0.2
     */
    @Override
    public int maxFileSizeInBytes(String mime) {
        return switch (mime) {
            case "mp3", "mpeg", "mpeg-3" -> getMp3SizeLimit();
            case "ogg", "vorbis", "x-ogg" -> getOggSizeLimit();
            case "flac", "x-flac" -> getFlacSizeLimit();
            case "wav", "x-wav", "wave", "vnd.wave" -> getWavSizeLimit();
            case "au", "basic" -> getAuSizeLimit();
            case "aif", "aiff", "aifc", "x-aiff" -> getAifSizeLimit();
            default -> throw new IllegalArgumentException("Unsupported mime: " + mime);
        };
    }

    //getters

    /**
     * @return {@code true} if the debug dummy flag was triggered; defaults to {@code false}
     * @since 0.2
     */
    public boolean getAudioDummyDebugTriggered() {
        return Objects.requireNonNullElse(
                get(KEY_AUDIO_DUMMY_DEBUG_TRIGGERED, Boolean.class),
                audioDummyDebugTriggered
        );
    }

    /**
     * @return the MP3 file size ceiling in bytes; defaults to 5,000,000
     * @since 0.2
     */
    public int getMp3SizeLimit() {
        return Objects.requireNonNullElse(
                get(KEY_MP3_SIZE_LIMIT, Integer.class),
                mp3SizeLimit);
    }

    /**
     * @return the OGG file size ceiling in bytes; defaults to 5,000,000
     * @since 0.2
     */
    public int getOggSizeLimit() {
        return Objects.requireNonNullElse(
                get(KEY_OGG_SIZE_LIMIT, Integer.class),
                oggSizeLimit);
    }

    /**
     * @return the FLAC file size ceiling in bytes; defaults to 5,000,000
     * @since 0.2
     */
    public int getFlacSizeLimit() {
        return Objects.requireNonNullElse(
                get(KEY_FLAC_SIZE_LIMIT, Integer.class),
                flacSizeLimit);
    }

    /**
     * @return the WAV file size ceiling in bytes; defaults to 5,000,000
     * @since 0.2
     */
    public int getWavSizeLimit() {
        return Objects.requireNonNullElse(
                get(KEY_WAV_SIZE_LIMIT, Integer.class),
                wavSizeLimit);
    }

    /**
     * @return the AU file size ceiling in bytes; defaults to 5,000,000
     * @since 0.2
     */
    public int getAuSizeLimit() {
        return Objects.requireNonNullElse(
                get(KEY_AU_SIZE_LIMIT, Integer.class),
                auSizeLimit);
    }

    /**
     * @return the AIF/AIFF/AIFC file size ceiling in bytes; defaults to 5,000,000
     * @since 0.2
     */
    public int getAifSizeLimit() {
        return Objects.requireNonNullElse(
                get(KEY_AIF_SIZE_LIMIT, Integer.class),
                aifSizeLimit);
    }

    /**
     * @return {@code true} if the native {@code javax.sound.sampled} fallback is turned off
     *         when JAVE/FFmpeg re-encoding fails; defaults to {@code false}
     * @since 0.2
     */
    public boolean getDisableNativeProcessingFallback() {
        return Objects.requireNonNullElse(
                get(KEY_DISABLE_NATIVE_PROCESSING_FALLBACK, Boolean.class),
                disableNativeProcessingFallback);
    }

    /**
     * @return {@code true} if the processed file is deleted right after processing
     *         (benchmarking mode); defaults to {@code false}
     * @since 0.2
     */
    public boolean getDontSaveAudio() {
        return Objects.requireNonNullElse(
                get(KEY_DONT_SAVE_AUDIO, Boolean.class),
                dontSaveAudio
        );
    }

    /**
     * @return the output format, or {@code "default"} to keep the input file's format; defaults to {@code "default"}
     * @since 0.2
     */
    public String getDefaultOutputTo() {
        return Objects.requireNonNullElse(
                get(KEY_DEFAULT_OUTPUT_TO, String.class),
                defaultOutputTo
        );
    }

    /**
     * @return the overall audio file size ceiling in bytes; defaults to 5,000,000
     * @since 0.2
     */
    public int getMaxFileSize() {
        return Objects.requireNonNullElse(
                get(KEY_MAX_FILE_SIZE, Integer.class),
                maxFileSize
        );
    }

    /**
     * Checks a codec against the codec allowlist for a format.
     * @param format the format or MIME alias (e.g. {@code "mp3"}, {@code "x-wav"})
     * @param codec the detected codec name
     * @return {@code true} if the codec is allowed for that format; {@code false} if it isn't,
     *         or if the format is unknown
     * @since 0.2
     */
    public boolean isCodecAllowed(String format, String codec) {
        List<String> codecs = allowedAudioCodecs.getOrDefault(format, List.of());
        return codecs.contains(codec);
    }

    /**
     * Converts a MIME subtype or alias to the format it represents
     * (e.g. {@code "mpeg"} to {@code "mp3"}, {@code "x-wav"} to {@code "wav"}, {@code "aifc"} to {@code "aiff"}).
     * @param mime the MIME subtype or alias
     * @return the format, or {@code null} if the MIME is not supported
     * @since 0.2
     */
    public String getFormatFromMime(String mime) {
        return mimeToFormat.get(mime);
    }

    /**
     * @return the maximum audio duration in milliseconds; defaults to 300,000 (5 minutes)
     * @since 0.2
     */
    public int getMaxAudioDuration() {
        return Objects.requireNonNullElse(
                get(KEY_MAX_AUDIO_DURATION, Integer.class),
                maxAudioDuration
        );
    }

    /**
     * Returns the bitrate ceiling for a format, falling back to the hardcoded default table
     * when no value has been configured.
     * @param format the format (e.g. {@code "mp3"}, {@code "flac"})
     * @return the bitrate ceiling in bits per second, or {@code 0} if the format is unknown
     * @since 0.2
     */
    @SuppressWarnings("unchecked")
    public int getMaxBitrates(String format) {
        Map<String, Integer> rates = get(KEY_MAX_BITRATES, Map.class);
        if (rates == null) {
            rates = maxBitrates;
        }
        Integer rate = rates.get(format);
        return rate != null ? rate : maxBitrates.getOrDefault(format, 0);
    }

    /**
     * Returns the sampling rate ceiling for a format, falling back to the hardcoded default
     * table when no value has been configured.
     * @param format the format (e.g. {@code "mp3"}, {@code "flac"})
     * @return the sampling rate ceiling in Hz, or {@code 0} if the format is unknown
     * @since 0.2
     */
    @SuppressWarnings("unchecked")
    public int getMaxSampleRates(String format) {
        Map<String, Integer> rates = get(KEY_MAX_SAMPLE_RATES, Map.class);
        if (rates == null) {
            rates = maxSampleRates;
        }
        Integer rate = rates.get(format);
        return rate != null ? rate : maxSampleRates.getOrDefault(format, 0);
    }

    /**
     * @return the maximum number of audio channels allowed in input; defaults to 2
     * @since 0.2
     */
    public int getMaxChannels() {
        return Objects.requireNonNullElse(
                get(KEY_MAX_CHANNELS, Integer.class),
                maxChannels
        );
    }

    /**
     * @return the output bitrate used when the input's bitrate is unknown or invalid
     *         and no output bitrate is configured; defaults to 320,000
     * @since 0.2
     */
    public int getBitrateFallback() {
        return Objects.requireNonNullElse(
                get(KEY_BITRATE_FALLBACK, Integer.class),
                bitrateFallback
        );
    }

    /**
     * @return the output sampling rate used when the input's sampling rate is unknown or
     *         invalid and no output sample rate is configured; defaults to 48,000
     * @since 0.2
     */
    public int getSamplingRateFallback() {
        return Objects.requireNonNullElse(
                get(KEY_SAMPLING_RATE_FALLBACK, Integer.class),
                samplingRateFallback
        );
    }

    /**
     * @return the configured output bitrate, or {@code 0} to keep the input's bitrate;
     *         not applied to FLAC output; defaults to {@code 0}
     * @since 0.2
     */
    public int getOutputBitrate() {
        return Objects.requireNonNullElse(
                get(KEY_OUTPUT_BITRATE, Integer.class),
                outputBitrate
        );
    }

    /**
     * @return the configured output sample rate, or {@code 0} to keep the input's sample rate; defaults to {@code 0}
     * @since 0.2
     */
    public int getOutputSampleRate() {
        return Objects.requireNonNullElse(
                get(KEY_OUTPUT_SAMPLE_RATE, Integer.class),
                outputSampleRate
        );
    }

    /**
     * @return the configured number of output channels, or {@code 0} to keep the input's channel count; defaults to {@code 0}
     * @since 0.2
     */
    public int getOutputChannels() {
        return Objects.requireNonNullElse(
                get(KEY_OUTPUT_CHANNELS, Integer.class),
                outputChannels
        );
    }
    //setters

    /**
     * Sets the debug dummy flag. Used only to check that plugin CLI options are applied.
     * @param newAudioDummyDebugTriggered the flag value
     * @since 0.2
     */
    public void setAudioDummyDebugTriggered(boolean newAudioDummyDebugTriggered) {
        put(KEY_AUDIO_DUMMY_DEBUG_TRIGGERED, newAudioDummyDebugTriggered);
    }

    /**
     * Sets the MP3 file size ceiling.
     * @param newMp3SizeLimit the ceiling in bytes
     * @throws IllegalArgumentException if {@code newMp3SizeLimit} is zero or negative
     * @since 0.2
     */
    public void setMp3SizeLimit(int newMp3SizeLimit) {
        if (newMp3SizeLimit == 0) {
            throw new IllegalArgumentException("Mp3 size limit cannot be zero");
        }
        if (newMp3SizeLimit < 0) {
            throw new IllegalArgumentException("Mp3 size limit cannot be less than zero");
        }
        put(KEY_MP3_SIZE_LIMIT, newMp3SizeLimit);
    }

    /**
     * Sets the OGG file size ceiling.
     * @param newOggSizeLimit the ceiling in bytes
     * @throws IllegalArgumentException if {@code newOggSizeLimit} is zero or negative
     * @since 0.2
     */
    public void setOggSizeLimit(int newOggSizeLimit) {
        if (newOggSizeLimit == 0) {
            throw new IllegalArgumentException("Ogg size limit cannot be zero");
        }
        if (newOggSizeLimit < 0) {
            throw new IllegalArgumentException("Ogg size limit cannot be less than zero");
        }
        put(KEY_OGG_SIZE_LIMIT, newOggSizeLimit);
    }

    /**
     * Sets the FLAC file size ceiling.
     * @param newFlacSizeLimit the ceiling in bytes
     * @throws IllegalArgumentException if {@code newFlacSizeLimit} is zero or negative
     * @since 0.2
     */
    public void setFlacSizeLimit(int newFlacSizeLimit) {
        if (newFlacSizeLimit == 0) {
            throw new IllegalArgumentException("Flac size limit cannot be zero");
        }
        if (newFlacSizeLimit < 0) {
            throw new IllegalArgumentException("Flac size limit cannot be less than zero");
        }
        put(KEY_FLAC_SIZE_LIMIT, newFlacSizeLimit);
    }

    /**
     * Sets the WAV file size ceiling.
     * @param newWavSizeLimit the ceiling in bytes
     * @throws IllegalArgumentException if {@code newWavSizeLimit} is zero or negative
     * @since 0.2
     */
    public void setWavSizeLimit(int newWavSizeLimit) {
        if (newWavSizeLimit == 0) {
            throw new IllegalArgumentException("Wav size limit cannot be zero");
        }
        if (newWavSizeLimit < 0) {
            throw new IllegalArgumentException("Wav size limit cannot be less than zero");
        }
        put(KEY_WAV_SIZE_LIMIT, newWavSizeLimit);
    }

    /**
     * Sets the AU file size ceiling.
     * @param newAuSizeLimit the ceiling in bytes
     * @throws IllegalArgumentException if {@code newAuSizeLimit} is zero or negative
     * @since 0.2
     */
    public void setAuSizeLimit(int newAuSizeLimit) {
        if (newAuSizeLimit == 0) {
            throw new IllegalArgumentException("Au size limit cannot be zero");
        }
        if (newAuSizeLimit < 0) {
            throw new IllegalArgumentException("Au size limit cannot be less than zero");
        }
        put(KEY_AU_SIZE_LIMIT, newAuSizeLimit);
    }

    /**
     * Sets the AIF/AIFF/AIFC file size ceiling.
     * @param newAifSizeLimit the ceiling in bytes
     * @throws IllegalArgumentException if {@code newAifSizeLimit} is zero or negative
     * @since 0.2
     */
    public void setAifSizeLimit(int newAifSizeLimit) {
        if (newAifSizeLimit == 0) {
            throw new IllegalArgumentException("Aif size limit cannot be zero");
        }
        if (newAifSizeLimit < 0) {
            throw new IllegalArgumentException("Aif size limit cannot be less than zero");
        }
        put(KEY_AIF_SIZE_LIMIT, newAifSizeLimit);
    }

    /**
     * Turns the native {@code javax.sound.sampled} fallback on or off for when JAVE/FFmpeg
     * re-encoding fails.
     * @param newDisableNativeProcessingFallback {@code true} to turn the fallback off
     * @since 0.2
     */
    public void setDisableNativeProcessingFallback(boolean newDisableNativeProcessingFallback) {
        put(KEY_DISABLE_NATIVE_PROCESSING_FALLBACK, newDisableNativeProcessingFallback);
    }

    /**
     * Turns no-save mode on or off. In this mode the full processing cycle runs and
     * the output file is deleted right after, for benchmarking.
     * @param newDontSaveAudio {@code true} to discard output
     * @since 0.2
     */
    public void setDontSaveAudio(boolean newDontSaveAudio) {
        put(KEY_DONT_SAVE_AUDIO, newDontSaveAudio);
    }

    /**
     * Sets the output format.
     * @param newDefaultOutputTo one of {@code "default"} (keep the input format), {@code "mp3"},
     *                           {@code "ogg"}, {@code "flac"}, {@code "wav"}, {@code "au"}, {@code "aif"}
     * @throws IllegalArgumentException if the format is not one of the allowed values
     * @since 0.2
     */
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

    /**
     * Sets the overall audio file size ceiling.
     * @param newMaxFileSize the ceiling in bytes
     * @throws IllegalArgumentException if {@code newMaxFileSize} is zero or negative
     * @since 0.2
     */
    public void setMaxFileSize(int newMaxFileSize) {
        if (newMaxFileSize < 0) {
            throw new IllegalArgumentException("Max file size cannot be less than zero");
        }
        if (newMaxFileSize == 0) {
            throw new IllegalArgumentException("Max file size cannot be zero");
        }
        put(KEY_MAX_FILE_SIZE, newMaxFileSize);
    }

    /**
     * Sets the maximum audio duration.
     * @param newMaxAudioDuration the limit in milliseconds
     * @throws IllegalArgumentException if {@code newMaxAudioDuration} is zero or negative
     * @since 0.2
     */
    public void setMaxAudioDuration(int newMaxAudioDuration) {
        if (newMaxAudioDuration < 0) {
            throw new IllegalArgumentException("Max audio duration cannot be less than zero");
        }
        if (newMaxAudioDuration == 0) {
            throw new IllegalArgumentException("Max audio duration cannot be zero");
        }
        put(KEY_MAX_AUDIO_DURATION, newMaxAudioDuration);
    }

    /**
     * Sets the bitrate ceiling for a single format; other formats keep their current values.
     * Only formats already in the bitrate table can be updated; any other format is ignored.
     * @param format the format to update (e.g. {@code "mp3"})
     * @param newMaxBitrates the ceiling in bits per second
     * @throws IllegalArgumentException if the bitrate is zero or negative, or {@code format} is {@code null} or empty
     * @since 0.2
     */
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

    /**
     * Sets the sampling rate ceiling for a single format; other formats keep their current values.
     * Only formats already in the sampling rate table can be updated; any other format is ignored.
     * @param format the format to update (e.g. {@code "mp3"})
     * @param newMaxSampleRates the ceiling in Hz
     * @throws IllegalArgumentException if the sampling rate is zero or negative, or {@code format} is {@code null} or empty
     * @since 0.2
     */
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

    /**
     * Sets the maximum number of audio channels allowed in input.
     * @param newMaxChannels the channel ceiling
     * @throws IllegalArgumentException if {@code newMaxChannels} is zero or negative
     * @since 0.2
     */
    public void setMaxChannels(int newMaxChannels) {
        if (newMaxChannels < 0) {
            throw new IllegalArgumentException("Max audio channels cannot be less than zero");
        }
        if (newMaxChannels == 0) {
            throw new IllegalArgumentException("Max audio channels cannot be zero");
        }
        put(KEY_MAX_CHANNELS, newMaxChannels);
    }

    /**
     * Sets the fallback output bitrate, used when the input's bitrate is unknown or invalid
     * and no output bitrate is configured.
     * @param newBitrateFallback the fallback bitrate in bits per second
     * @throws IllegalArgumentException if {@code newBitrateFallback} is zero or negative
     * @since 0.2
     */
    public void setBitrateFallback(int newBitrateFallback) {
        if (newBitrateFallback < 0) {
            throw new IllegalArgumentException("Max audio channels cannot be less than zero");
        }
        if (newBitrateFallback == 0) {
            throw new IllegalArgumentException("Max audio channels cannot be zero");
        }
        put(KEY_BITRATE_FALLBACK, newBitrateFallback);
    }

    /**
     * Sets the fallback output sampling rate, used when the input's sampling rate is unknown
     * or invalid and no output sample rate is configured.
     * @param newSamplingRateFallback the fallback sampling rate in Hz
     * @throws IllegalArgumentException if {@code newSamplingRateFallback} is zero or negative
     * @since 0.2
     */
    public void setSamplingRateFallback(int newSamplingRateFallback) {
        if (newSamplingRateFallback < 0) {
            throw new IllegalArgumentException("Sampling rate fallback cannot be less than zero");
        }
        if (newSamplingRateFallback == 0) {
            throw new IllegalArgumentException("Sampling rate fallback cannot be zero");
        }
        put(KEY_SAMPLING_RATE_FALLBACK, newSamplingRateFallback);
    }

    /**
     * Sets the output bitrate. When set, it overrides the input's bitrate. Not applied to FLAC output.
     * @param newOutputBitrate the bitrate in bits per second
     * @throws IllegalArgumentException if {@code newOutputBitrate} is zero or negative
     * @since 0.2
     */
    public void setOutputBitrate(int newOutputBitrate) {
        if (newOutputBitrate < 0) {
            throw new IllegalArgumentException("Output bitrate cannot be less than zero");
        }
        if (newOutputBitrate == 0) {
            throw new IllegalArgumentException("Output bitrate cannot be zero");
        }
        put(KEY_OUTPUT_BITRATE, newOutputBitrate);
    }

    /**
     * Sets the output sample rate. When set, it overrides the input's sample rate.
     * @param newOutputSampleRate the sample rate in Hz
     * @throws IllegalArgumentException if {@code newOutputSampleRate} is zero or negative
     * @since 0.2
     */
    public void setOutputSampleRate(int newOutputSampleRate) {
        if (newOutputSampleRate < 0) {
            throw new IllegalArgumentException("Output sampling rate cannot be less than zero");
        }
        if (newOutputSampleRate == 0) {
            throw new IllegalArgumentException("Output sampling rate cannot be zero");
        }
        put(KEY_OUTPUT_SAMPLE_RATE, newOutputSampleRate);
    }

    /**
     * Sets the number of output channels. When set, it overrides the input's channel count.
     * @param newOutputChannels the channel count
     * @throws IllegalArgumentException if {@code newOutputChannels} is zero or negative
     * @since 0.2
     */
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

package dev.n0153.app.plugins.AudioPlugin;

import dev.n0153.app.core.MediaContext;

import java.util.HashMap;
import java.util.Map;

/**
 * Per-file runtime state for the Audio plugin: the output title, and the codec,
 * bitrate, sampling rate, channel count and MIME type detected from the input file.
 * {@link AudioProcessor} fills these in before re-encoding and reads them back when
 * building the output encoding attributes.
 * <p>
 * Values are stored in a fixed-key map, and every value starts as {@code null}.
 * Typed setters validate input before storing it.
 * <p>
 * Not thread-safe. Intended for one file processed sequentially.
 * @since 0.2
 */
public class AudioContext implements MediaContext {
    private final String KEY_AUDIO_TITLE = "audioTitle";
    private final String KEY_AUDIO_CODEC = "audioCodec";
    private final String KEY_AUDIO_BITRATE = "audioBitrate";
    private final String KEY_AUDIO_SAMPLING_RATE = "audioSamplingRate";
    private final String KEY_AUDIO_CHANNELS = "audioChannels";
    private final String KEY_DETECTED_MIME = "detectedMime";

    private final Map<String, Object> contextStorage = new HashMap<>() {{
        put(KEY_AUDIO_TITLE, null);
        put(KEY_AUDIO_CODEC, null);
        put(KEY_AUDIO_BITRATE, null);
        put(KEY_AUDIO_SAMPLING_RATE, null);
        put(KEY_AUDIO_CHANNELS, null);
        put(KEY_DETECTED_MIME, null);
    }};

    /**
     * {@inheritDoc}
     * Only the predefined context keys are written; a value for any other key is ignored.
     * This method skips the typed setters' validation.
     * @since 0.2
     */
    @Override
    public void put(String key, Object value) {
        contextStorage.replace(key, value);
    }

    /**
     * {@inheritDoc}
     * @return the stored value cast to {@code type}, or {@code null} if the key is unset or unknown
     * @throws ClassCastException if the stored value is not an instance of {@code type}
     * @since 0.2
     */
    @Override
    public <ValueType> ValueType get(String key, Class<ValueType> type) {
        return type.cast(contextStorage.get(key));
    }

    /**
     * {@inheritDoc}
     * Clears all stored keys and values. After this call, {@link #put(String, Object)}
     * no longer stores anything, so the context cannot be reused.
     * @since 0.2
     */
    @Override
    public void release() {
        contextStorage.clear();
    }

    /**
     * Releases this context; same as {@link #release()}. Lets the context be used in a
     * try-with-resources block.
     * @since 0.2
     */
    @Override
    public void close() throws Exception {
        release();
    }

    /**
     * {@inheritDoc}
     * @return the generated audio output title; same as {@link #getAudioTitle()}
     * @since 0.2
     */
    @Override
    public String getOutputTitle() {
        return getAudioTitle();
    }

    //getters

    /**
     * @return the generated output file name, including extension, or {@code null} if not set
     * @since 0.2
     */
    public String getAudioTitle() {
        return get(KEY_AUDIO_TITLE, String.class);
    }

    /**
     * @return the codec detected in the input file, or {@code null} if not set
     * @since 0.2
     */
    public String getAudioCodec() {
        return get(KEY_AUDIO_CODEC, String.class);
    }

    /**
     * Must be set before it is read.
     * @return the input file's detected bitrate in bits per second
     * @throws NullPointerException if the bitrate has not been set
     * @since 0.2
     */
    public int getAudioBitrate() {
        return get(KEY_AUDIO_BITRATE, Integer.class);
    }

    /**
     * Must be set before it is read.
     * @return the input file's detected sampling rate in Hz
     * @throws NullPointerException if the sampling rate has not been set
     * @since 0.2
     */
    public int getSamplingRate() {
        return get(KEY_AUDIO_SAMPLING_RATE, Integer.class);
    }

    /**
     * Must be set before it is read.
     * @return the input file's detected channel count
     * @throws NullPointerException if the channel count has not been set
     * @since 0.2
     */
    public int getAudioChannels() {
        return get(KEY_AUDIO_CHANNELS, Integer.class);
    }

    /**
     * @return the MIME subtype detected from the input file's signature (e.g. {@code "mp3"},
     *         {@code "wav"}), or {@code null} if not set
     * @since 0.2
     */
    public String getDetectedMime() {
        return get(KEY_DETECTED_MIME, String.class);
    }

    //setters

    /**
     * Sets the generated output file name.
     * @param newAudioTitle the file name, including extension
     * @throws IllegalArgumentException if {@code newAudioTitle} is {@code null} or empty
     * @since 0.2
     */
    public void setAudioTitle(String newAudioTitle) {
        if (newAudioTitle == null) {
            throw new IllegalArgumentException("Audio title cannot be null");
        }
        if (newAudioTitle.isEmpty()) {
            throw new IllegalArgumentException("Audio title cannot be empty");
        }
        put(KEY_AUDIO_TITLE, newAudioTitle);
    }

    /**
     * Sets the codec detected in the input file.
     * @param newAudioCodec the codec name
     * @throws IllegalArgumentException if {@code newAudioCodec} is {@code null} or empty
     * @since 0.2
     */
    public void setAudioCodec(String newAudioCodec) {
        if (newAudioCodec == null) {
            throw new IllegalArgumentException("Audio codec cannot be null");
        }
        if (newAudioCodec.isEmpty()) {
            throw new IllegalArgumentException("Audio codec cannot be empty");
        }
        put(KEY_AUDIO_CODEC, newAudioCodec);
    }

    /**
     * Sets the input file's detected bitrate.
     * @param newAudioBitrate the bitrate in bits per second
     * @throws IllegalArgumentException if {@code newAudioBitrate} is zero or negative
     * @since 0.2
     */
    public void setAudioBitrate(int newAudioBitrate) {
        if (newAudioBitrate < 0) {
            throw new IllegalArgumentException("Audio bitrate cannot be less than zero");
        }
        if (newAudioBitrate == 0) {
            throw new IllegalArgumentException("Audio bitrate cannot be zero");
        }
        put(KEY_AUDIO_BITRATE, newAudioBitrate);
    }

    /**
     * Sets the input file's detected sampling rate.
     * @param newAudioSamplingRate the sampling rate in Hz
     * @throws IllegalArgumentException if {@code newAudioSamplingRate} is zero or negative
     * @since 0.2
     */
    public void setAudioSamplingRate(int newAudioSamplingRate) {
        if (newAudioSamplingRate < 0) {
            throw new IllegalArgumentException("Audio sampling rate cannot be less than zero");
        }
        if (newAudioSamplingRate == 0) {
            throw new IllegalArgumentException("Audio sampling rate cannot be zero");
        }
        put(KEY_AUDIO_SAMPLING_RATE, newAudioSamplingRate);
    }

    /**
     * Sets the input file's detected channel count.
     * @param newAudioChannels the channel count
     * @throws IllegalArgumentException if {@code newAudioChannels} is zero or negative
     * @since 0.2
     */
    public void setAudioChannels(int newAudioChannels) {
        if (newAudioChannels < 0) {
            throw new IllegalArgumentException("Audio channels cannot be less than zero");
        }
        if (newAudioChannels == 0) {
            throw new IllegalArgumentException("Audio channels cannot be zero");
        }
        put(KEY_AUDIO_CHANNELS, newAudioChannels);
    }

    /**
     * Sets the MIME subtype detected from the input file's signature.
     * @param newDetectedMime the MIME subtype (e.g. {@code "mp3"})
     * @throws IllegalArgumentException if {@code newDetectedMime} is {@code null} or empty
     * @since 0.2
     */
    public void setDetectedMime(String newDetectedMime) {
        if (newDetectedMime == null) {
            throw new IllegalArgumentException("Mime type cannot be null");
        }
        if (newDetectedMime.isEmpty()) {
            throw new IllegalArgumentException("Mime type cannot be empty");
        }
        put(KEY_DETECTED_MIME, newDetectedMime);
    }
}

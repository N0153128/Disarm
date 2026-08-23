package dev.n0153.app.plugins.audio;

import dev.n0153.app.MediaContext;

import java.util.HashMap;
import java.util.Map;

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
    public void close() throws Exception {
        release();
    }

    @Override
    public String getOutputTitle() {
        return getAudioTitle();
    }

    //getters
    public String getAudioTitle() {
        return get(KEY_AUDIO_TITLE, String.class);
    }

    public String getAudioCodec() {
        return get(KEY_AUDIO_CODEC, String.class);
    }

    public int getAudioBitrate() {
        return get(KEY_AUDIO_BITRATE, Integer.class);
    }

    public int getSamplingRate() {
        return get(KEY_AUDIO_SAMPLING_RATE, Integer.class);
    }

    public int getAudioChannels() {
        return get(KEY_AUDIO_CHANNELS, Integer.class);
    }

    public String getDetectedMime() {
        return get(KEY_DETECTED_MIME, String.class);
    }

    //setters
    public void setAudioTitle(String newAudioTitle) {
        if (newAudioTitle == null) {
            throw new IllegalArgumentException("Audio title cannot be null");
        }
        if (newAudioTitle.isEmpty()) {
            throw new IllegalArgumentException("Audio title cannot be empty");
        }
        put(KEY_AUDIO_TITLE, newAudioTitle);
    }

    public void setAudioCodec(String newAudioCodec) {
        if (newAudioCodec == null) {
            throw new IllegalArgumentException("Audio codec cannot be null");
        }
        if (newAudioCodec.isEmpty()) {
            throw new IllegalArgumentException("Audio codec cannot be empty");
        }
        put(KEY_AUDIO_CODEC, newAudioCodec);
    }

    public void setAudioBitrate(int newAudioBitrate) {
        if (newAudioBitrate < 0) {
            throw new IllegalArgumentException("Audio bitrate cannot be less than zero");
        }
        if (newAudioBitrate == 0) {
            throw new IllegalArgumentException("Audio bitrate cannot be zero");
        }
        put(KEY_AUDIO_BITRATE, newAudioBitrate);
    }

    public void setAudioSamplingRate(int newAudioSamplingRate) {
        if (newAudioSamplingRate < 0) {
            throw new IllegalArgumentException("Audio sampling rate cannot be less than zero");
        }
        if (newAudioSamplingRate == 0) {
            throw new IllegalArgumentException("Audio sampling rate cannot be zero");
        }
        put(KEY_AUDIO_SAMPLING_RATE, newAudioSamplingRate);
    }

    public void setAudioChannels(int newAudioChannels) {
        if (newAudioChannels < 0) {
            throw new IllegalArgumentException("Audio channels cannot be less than zero");
        }
        if (newAudioChannels == 0) {
            throw new IllegalArgumentException("Audio channels cannot be zero");
        }
        put(KEY_AUDIO_CHANNELS, newAudioChannels);
    }

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

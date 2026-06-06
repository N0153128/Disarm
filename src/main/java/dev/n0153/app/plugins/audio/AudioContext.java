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
    private final String KEY_TRACK_LENGTH = "trackLength";
    private final String KEY_USE_NATIVE_FALLBACK = "useNativeFallback";

    private final Map<String, Object> contextStorage = new HashMap<>() {{
        put(KEY_AUDIO_TITLE, null);
        put(KEY_AUDIO_CODEC, null);
        put(KEY_AUDIO_BITRATE, null);
        put(KEY_AUDIO_SAMPLING_RATE, null);
        put(KEY_AUDIO_CHANNELS, null);
        put(KEY_DETECTED_MIME, null);
        put(KEY_TRACK_LENGTH, null);
        put(KEY_USE_NATIVE_FALLBACK, null);
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

    public int getTrackLength() {
        return get(KEY_TRACK_LENGTH, Integer.class);
    }

    public boolean getUseNativeFallback() {
        return get(KEY_USE_NATIVE_FALLBACK, Boolean.class);
    }

    //setters
    public void setAudioTitle(String newAudioTitle) {
        put(KEY_AUDIO_TITLE, newAudioTitle);
    }

    public void setAudioCodec(String newAudioCodec) {
        put(KEY_AUDIO_CODEC, newAudioCodec);
    }

    public void setAudioBitrate(int newAudioBitrate) {
        put(KEY_AUDIO_BITRATE, newAudioBitrate);
    }

    public void setAudioSamplingRate(int newAudioSamplingRate) {
        put(KEY_AUDIO_SAMPLING_RATE, newAudioSamplingRate);
    }

    public void setAudioChannels(int newAudioChannels) {
        put(KEY_AUDIO_CHANNELS, newAudioChannels);
    }

    public void setDetectedMime(String newDetectedMime) {
        put(KEY_DETECTED_MIME, newDetectedMime);
    }

    public void setTrackLength(int newTrackLength) {
        put(KEY_TRACK_LENGTH, newTrackLength);
    }

    public void setUseNativeFallback(boolean newUseNativeFallback) {
        put(KEY_USE_NATIVE_FALLBACK, newUseNativeFallback);
    }
}

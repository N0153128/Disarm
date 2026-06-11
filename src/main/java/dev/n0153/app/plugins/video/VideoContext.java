package dev.n0153.app.plugins.video;

import dev.n0153.app.MediaContext;
import ws.schild.jave.info.VideoSize;

import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;

public class VideoContext implements MediaContext {
    private final String KEY_VIDEO_TITLE = "videoTitle";
    private final String KEY_VIDEO_CODEC = "videoCodec";
    private final String KEY_AUDIO_CODEC = "audioCodec";
    private final String KEY_VIDEO_BITRATE = "videoBitrate";
    private final String KEY_AUDIO_BITRATE = "audioBitrate";
    private final String KEY_VIDEO_FRAME_RATE = "videoFrameRate";
    private final String KEY_AUDIO_SAMPLING_RATE = "audioSamplingRate";
    private final String KEY_AUDIO_CHANNELS = "audioChannels";
    private final String KEY_VIDEO_SIZE = "videoSize";
    private final String KEY_DETECTED_MIME = "detectedMime";
    private final String KEY_TRACK_LENGTH = "trackLength";

    private final Map<String, Object> contextStorage = new HashMap<>() {{
        put(KEY_VIDEO_TITLE, null);
        put(KEY_VIDEO_CODEC, null);
        put(KEY_AUDIO_CODEC, null);
        put(KEY_VIDEO_BITRATE, null);
        put(KEY_AUDIO_BITRATE, null);
        put(KEY_VIDEO_FRAME_RATE, null);
        put(KEY_AUDIO_SAMPLING_RATE, null);
        put(KEY_AUDIO_CHANNELS, null);
        put(KEY_VIDEO_SIZE, null);
        put(KEY_DETECTED_MIME, null);
        put(KEY_TRACK_LENGTH, null);
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
    public String getVideoTitle() {
        return get(KEY_VIDEO_TITLE, String.class);
    }

    public String getVideoCodec() {
        return get(KEY_VIDEO_CODEC, String.class);
    }

    public String getAudioCodec() {
        return get(KEY_AUDIO_CODEC, String.class);
    }

    public int getVideoBitrate() {
        return get(KEY_VIDEO_BITRATE, Integer.class);
    }

    public int getAudioBitrate() {
        return get(KEY_AUDIO_BITRATE, Integer.class);
    }

    public int getVideoFrameRate() {
        return get(KEY_VIDEO_FRAME_RATE, Integer.class);
    }

    public int getAudioSamplingRate() {
        return get(KEY_AUDIO_SAMPLING_RATE, Integer.class);
    }

    public int getAudioChannels() {
        return get(KEY_AUDIO_CHANNELS, Integer.class);
    }

    public VideoSize getVideoSize() {
        return get(KEY_VIDEO_SIZE, VideoSize.class);
    }

    public String getDetectedMime() {
        return get(KEY_DETECTED_MIME, String.class);
    }

    public int getTrackLength() {
        return get(KEY_TRACK_LENGTH, Integer.class);
    }

    //setters
    public void setVideoTitle(String newVideoTitle) {
        put(KEY_VIDEO_TITLE, newVideoTitle);
    }

    public void setVideoCodec(String newVideoCodec) {
        put(KEY_VIDEO_CODEC, newVideoCodec);
    }

    public void setAudioCodec(String newAudioCodec) {
        put(KEY_AUDIO_CODEC, newAudioCodec);
    }

    public void setVideoBitrate(int newVideoBitrate) {
        put(KEY_VIDEO_BITRATE, newVideoBitrate);
    }

    public void setAudioBitrate(int newAudioBitrate) {
        put(KEY_AUDIO_BITRATE, newAudioBitrate);
    }

    public void setVideoFrameRate(int newVideoFrameRate) {
        put(KEY_VIDEO_FRAME_RATE, newVideoFrameRate);
    }

    public void setAudioSamplingRate(int newAudioSamplingRate) {
        put(KEY_AUDIO_SAMPLING_RATE, newAudioSamplingRate);
    }

    public void setAudioChannels(int newAudioChannels) {
        put(KEY_AUDIO_CHANNELS, newAudioChannels);
    }

    public void setVideoSize(VideoSize newVideoSize) {
        put(KEY_VIDEO_SIZE, newVideoSize);
    }

    public void setDetectedMime(String newDetectedMime) {
        put(KEY_DETECTED_MIME, newDetectedMime);
    }

    public void setTrackLength(int newTrackLength) {
        put(KEY_TRACK_LENGTH, newTrackLength);
    }
}

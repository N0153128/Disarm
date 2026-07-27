package dev.n0153.app.plugins.video;

import dev.n0153.app.MediaContext;
import ws.schild.jave.info.VideoSize;

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

    @Override
    public String getOutputTitle() {
        return getVideoTitle();
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
        if (newVideoTitle == null) {
            throw new IllegalArgumentException("Video title cannot be null");
        }
        if (newVideoTitle.isEmpty()) {
            throw new IllegalArgumentException("Video title cannot be empty");
        }
        put(KEY_VIDEO_TITLE, newVideoTitle);
    }

    public void setVideoCodec(String newVideoCodec) {
        if (newVideoCodec == null) {
            throw new IllegalArgumentException("Video codec cannot be null");
        }
        if (newVideoCodec.isEmpty()) {
            throw new IllegalArgumentException("Video codec cannot be empty");
        }
        put(KEY_VIDEO_CODEC, newVideoCodec);
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

    public void setVideoBitrate(int newVideoBitrate) {
        if (newVideoBitrate < 0) {
            throw new IllegalArgumentException("Video bitrate cannot be less than zero");
        }
        if (newVideoBitrate == 0) {
            throw new IllegalArgumentException("Video bitrate cannot be zero");
        }
        put(KEY_VIDEO_BITRATE, newVideoBitrate);
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

    public void setVideoFrameRate(int newVideoFrameRate) {
        if (newVideoFrameRate < 0) {
            throw new IllegalArgumentException("Video frame rate cannot be less than zero");
        }
        if (newVideoFrameRate == 0) {
            throw new IllegalArgumentException("Video frame rate cannot be zero");
        }
        put(KEY_VIDEO_FRAME_RATE, newVideoFrameRate);
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

    public void setVideoSize(VideoSize newVideoSize) {
        if (newVideoSize == null) {
            throw new IllegalArgumentException("Video size cannot be null");
        }
        if (newVideoSize.getHeight() == 0) {
            throw new IllegalArgumentException("Video height cannot be zero");
        }
        if (newVideoSize.getHeight() < 0) {
            throw new IllegalArgumentException("Video height cannot be less than zero");
        }
        if (newVideoSize.getWidth() == 0) {
            throw new IllegalArgumentException("Video width cannot be zero");
        }
        if (newVideoSize.getWidth() < 0) {
            throw new IllegalArgumentException("Video width cannot be less than zero");
        }
        put(KEY_VIDEO_SIZE, newVideoSize);
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

    public void setTrackLength(int newTrackLength) {
        if (newTrackLength < 0) {
            throw new IllegalArgumentException("Track length cannot be less than zero");
        }
        if (newTrackLength == 0) {
            throw new IllegalArgumentException("Track length cannot be zero");
        }
        put(KEY_TRACK_LENGTH, newTrackLength);
    }
}

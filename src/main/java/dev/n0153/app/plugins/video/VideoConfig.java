package dev.n0153.app.plugins.video;

import dev.n0153.app.MediaConfig;

import java.util.*;

public class VideoConfig implements MediaConfig {

    private final int maxVideoDuration = 300_000;
    private final Map<String, Integer> maxFileSize = new HashMap<>() {{
        put("mp4", 5_000_000);
        put("matroska", 5_000_000);
        put("webm", 5_000_000);
        put("mov", 5_000_000);

    }};
    private final Map<String, Integer> maxVideoBitrates = new HashMap<>() {{
        put("mp4", 15_000_000);
        put("matroska", 20_000_000);
        put("webm", 12_000_000);
        put("mov", 15_000_000);
    }};
    private final Map<String, Integer> maxAudioBitrates = new HashMap<>() {{
        put("mp4", 512_000);
        put("matroska", 1_536_000);
        put("webm", 512_000);
        put("mov", 1_536_000);
    }};
    private final Map<String, Integer> maxSampleRates = new HashMap<>() {{
        put("mp4", 48_000);
        put("matroska", 48_000);
        put("webm", 48_000);
        put("mov", 48_000);
    }};

    private static final List<String> MP4_CODECS = List.of(
            "h.264", "hevc", "av1",
            "mpeg-4", "vp9", "h.265",
            "h264");
    private static final List<String> MATROSKA_CODECS = List.of(
            "h.264", "hevc", "vp8",
            "vp9", "av1", "mpeg-4",
            "theora", "ffv1", "mpeg-2",
            "vc-1", "h.265", "h264");
    private static final List<String> WEBM_CODECS = List.of(
            "vp8", "vp9", "av1"
    );
    private static final List<String> MOV_CODECS = List.of(
            "h.264", "hevc", "prores",
            "av1", "mjpeg", "mpeg-4",
            "dnxhd", "h.265", "h264"
    );

    private final Map<String, List<String>> allowedVideoCodecs = new HashMap<>() {{
        put("mp4", MP4_CODECS);
        put("matroska", MATROSKA_CODECS);
        put("webm", WEBM_CODECS);
        put("mov", MOV_CODECS);
    }};

    private static final List<String> MP4_AUDIO_CODECS = List.of(
            "aac", "mp3", "ac3",
            "e-ac3", "flac", "opus",
            "alac"
    );
    private static final List<String> MATROSKA_AUDIO_CODECS = List.of(
            "aac", "mp3", "flac",
            "opus", "vorbis", "ac3",
            "dts", "pcm", "truehd",
            "eac3"
    );
    private static final List<String> WEBM_AUDIO_CODECS = List.of(
            "vorbis", "opus", "libvorbis"
    );
    private static final List<String> MOV_AUDIO_CODECS = List.of(
            "aac", "mp3", "pcm",
            "alac", "ac3"
    );

    private final Map<String, List<String>> allowedAudioCodecs = new HashMap<>() {{
        put("mp4", MP4_AUDIO_CODECS);
        put("matroska", MATROSKA_AUDIO_CODECS);
        put("webm", WEBM_AUDIO_CODECS);
        put("mov", MOV_AUDIO_CODECS);
    }};
    private final Map<String, String> mimeToFormat = new HashMap<>() {{
        //mp4
        put("mp4", "mp4");
        put("mpeg-4", "mp4");
        put("x-m4v", "mp4");
        //matroska
        put("x-matroska", "matroska");
        put("mkv", "matroska");
        put("matroska", "matroska");
        //webm
        put("webm", "webm");
        //mov
        put("quicktime", "mov");
        put("mov", "mov");
    }};
    private final int maxVideoWidth = 1920;
    private final int maxVideoHeight = 1080;
    private final int maxVideoFrameRate = 60;
    private final String outputVideoCodec = "default";
    private final String outputAudioCodec = "default";
    private final int outputVideoBitrate = 0;
    private final int outputAudioBitrate = 0;
    private final int outputSampleRate = 0;
    private final int outputChannels = 0;
    private final int outputFrameRate = 0;
    private final String defaultOutputTo = "default";
    private final String swapAV1 = "vp8";
    private final Boolean dontSaveVideo = false;
    private final int mp4SizeLimit = 5_000_000; //5MB
    private final int matroskaSizeLimit = 5_000_000; //5MB
    private final int webmSizeLimit = 5_000_000; //5MB
    private final int movSizeLimit = 5_000_000; //5MB

    private final String KEY_MAX_VIDEO_DURATION = "maxVideoDuration";
    private final String KEY_MAX_FILE_SIZE = "maxFileSize";
    private final String KEY_MAX_VIDEO_BITRATE = "maxVideoBitrates";
    private final String KEY_MAX_AUDIO_BITRATE = "maxAudioBitrates";
    private final String KEY_MAX_SAMPLE_RATE = "maxSampleRates";
    private final String KEY_ALLOWED_VIDEO_CODECS = "allowedVideoCodecs";
    private final String KEY_ALLOWED_AUDIO_CODECS = "allowedAudioCodecs";
    private final String KEY_MIME_TO_FORMAT = "mimeToFormat";
    private final String KEY_MAX_VIDEO_WIDTH = "maxVideoWidth";
    private final String KEY_MAX_VIDEO_HEIGHT = "maxVideoHeight";
    private final String KEY_MAX_VIDEO_FRAME_RATE = "maxVideoFrameRate";
    private final String KEY_OUTPUT_VIDEO_CODEC = "outputVideoCodec";
    private final String KEY_OUTPUT_AUDIO_CODEC = "outputAudioCodec";
    private final String KEY_OUTPUT_VIDEO_BITRATE = "outputVideoBitrate";
    private final String KEY_OUTPUT_AUDIO_BITRATE = "outputAudioBitrate";
    private final String KEY_OUTPUT_SAMPLING_RATE = "outputSampleRate";
    private final String KEY_OUTPUT_CHANNELS = "outputChannels";
    private final String KEY_OUTPUT_FRAME_RATE = "outputFrameRate";
    private final String KEY_DEFAULT_OUTPUT_TO = "defaultOutputTo";
    private final String KEY_SWAP_AV1 = "swapAV1";
    private final String KEY_DONT_SAVE_VIDEO= "dontSaveVideo";
    private final String KEY_MP4_SIZE_LIMIT = "mp4SizeLimit";
    private final String KEY_MATROSKA_SIZE_LIMIT = "matroskaSizeLimit";
    private final String KEY_WEBM_SIZE_LIMIT = "webmSizeLimit";
    private final String KEY_MOV_SIZE_LIMIT = "movSizeLimit";

    private final Map<String, Object> configStorage = new HashMap<>() {{
        put(KEY_MAX_VIDEO_DURATION, maxVideoDuration);
        put(KEY_MAX_FILE_SIZE, maxFileSize);
        put(KEY_MAX_VIDEO_BITRATE, maxVideoBitrates);
        put(KEY_MAX_AUDIO_BITRATE, maxAudioBitrates);
        put(KEY_MAX_SAMPLE_RATE, maxSampleRates);
        put(KEY_ALLOWED_VIDEO_CODECS, allowedVideoCodecs);
        put(KEY_ALLOWED_AUDIO_CODECS, allowedAudioCodecs);
        put(KEY_MIME_TO_FORMAT, mimeToFormat);
        put(KEY_MAX_VIDEO_WIDTH, maxVideoWidth);
        put(KEY_MAX_VIDEO_HEIGHT, maxVideoHeight);
        put(KEY_MAX_VIDEO_FRAME_RATE, maxVideoFrameRate);
        put(KEY_OUTPUT_VIDEO_CODEC, outputVideoCodec);
        put(KEY_OUTPUT_AUDIO_CODEC, outputAudioCodec);
        put(KEY_OUTPUT_VIDEO_BITRATE, outputVideoBitrate);
        put(KEY_OUTPUT_AUDIO_BITRATE, outputAudioBitrate);
        put(KEY_OUTPUT_SAMPLING_RATE, outputSampleRate);
        put(KEY_OUTPUT_CHANNELS, outputChannels);
        put(KEY_OUTPUT_FRAME_RATE, outputFrameRate);
        put(KEY_DEFAULT_OUTPUT_TO, defaultOutputTo);
        put(KEY_SWAP_AV1, swapAV1);
        put(KEY_DONT_SAVE_VIDEO, dontSaveVideo);
        put(KEY_MP4_SIZE_LIMIT, mp4SizeLimit);
        put(KEY_MATROSKA_SIZE_LIMIT, matroskaSizeLimit);
        put(KEY_WEBM_SIZE_LIMIT, webmSizeLimit);
        put(KEY_MOV_SIZE_LIMIT, movSizeLimit);
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
        return "video";
    }

    @Override
    public String toDebugString() {
        StringBuilder output = new StringBuilder("\n\n=== VIDEO PLUGIN CONFIG === \n");
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
        return mimeToFormat.keySet();
    }

    @Override
    public int maxFileSizeInBytes(String mime) {
        return switch(mime) {
            case "mp4", "mpeg-4", "x-m4v" -> getMp4SizeLimit();
            case "matroska", "x-matroska", "mkv" -> getMatroskaSizeLimit();
            case "webm" -> getWebmSizeLimit();
            case "mov" -> getMovSizeLimit();
            default -> throw new IllegalArgumentException("Unsupported mime: " + mime);
        };
    }

    // getters

    public int getMp4SizeLimit() {
        return Objects.requireNonNullElse(
                get(KEY_MP4_SIZE_LIMIT, Integer.class),
                mp4SizeLimit);
    }

    public int getMatroskaSizeLimit() {
        return Objects.requireNonNullElse(
                get(KEY_MATROSKA_SIZE_LIMIT, Integer.class),
                matroskaSizeLimit);
    }

    public int getWebmSizeLimit() {
        return Objects.requireNonNullElse(
                get(KEY_WEBM_SIZE_LIMIT, Integer.class),
                webmSizeLimit);
    }

    public int getMovSizeLimit() {
        return Objects.requireNonNullElse(
                get(KEY_MOV_SIZE_LIMIT, Integer.class),
                movSizeLimit);
    }

    public boolean getDontSaveVideo() {
        return Objects.requireNonNullElse(
                get(KEY_DONT_SAVE_VIDEO, Boolean.class),
                dontSaveVideo);
    }

    public String getSwapAV1() {
        return Objects.requireNonNullElse(
                get(KEY_SWAP_AV1, String.class),
                swapAV1);
    }

    public String getDefaultOutputTo() {
        return Objects.requireNonNullElse(
                get(KEY_DEFAULT_OUTPUT_TO, String.class),
                defaultOutputTo);
    }

    public int getMaxVideoDuration() {
        return Objects.requireNonNullElse(
                get(KEY_MAX_VIDEO_DURATION, Integer.class),
                maxVideoDuration);
    }

    @SuppressWarnings("unchecked")
    public int getMaxFileSize(String format) {
        Map<String, Integer> rates = get(KEY_MAX_FILE_SIZE, Map.class);
        if (rates == null) {
            rates = maxFileSize;
        }
        Integer rate = rates.get(format);
        return rate != null ? rate : maxFileSize.getOrDefault(format, maxFileSizeInBytes(""));
    }

    @SuppressWarnings("unchecked")
    public int getMaxVideoBitrate(String format) {
        Map<String, Integer> rates = get(KEY_MAX_VIDEO_BITRATE, Map.class);
        if (rates == null) {
            rates = maxVideoBitrates;
        }
        Integer rate = rates.get(format);
        return rate != null ? rate : maxVideoBitrates.getOrDefault(format, 0);
    }

    @SuppressWarnings("unchecked")
    public int getMaxAudioBitrate(String format) {
        Map<String, Integer> rates = get(KEY_MAX_AUDIO_BITRATE, Map.class);
        if (rates == null) {
            rates = maxAudioBitrates;
        }
        Integer rate = rates.get(format);
        return rate != null ? rate : maxAudioBitrates.getOrDefault(format, 0);
    }

    @SuppressWarnings("unchecked")
    public int getMaxSampleRate(String format) {
        Map<String, Integer> rates = get(KEY_MAX_SAMPLE_RATE, Map.class);
        if (rates == null) {
            rates = maxSampleRates;
        }
        Integer rate = rates.get(format);
        return rate != null ? rate : maxSampleRates.getOrDefault(format, 0);
    }

    public boolean isVideoCodecAllowedFor(String format, String codec) {
        List<String> codecs = allowedVideoCodecs.getOrDefault(format, List.of());
        return codecs.contains(codec);
    }

    public boolean isAudioCodecAllowedFor(String format, String codec) {
        List<String> codecs = allowedAudioCodecs.getOrDefault(format, List.of());
        return codecs.contains(codec);
    }

    public String getFormatFromMime(String mime) {
        return mimeToFormat.get(mime);
    }

    public int getMaxVideoWidth() {
        return Objects.requireNonNullElse(
                get(KEY_MAX_VIDEO_WIDTH, Integer.class),
                maxVideoWidth);
    }

    public int getMaxVideoHeight() {
        return Objects.requireNonNullElse(
                get(KEY_MAX_VIDEO_HEIGHT, Integer.class),
                maxVideoHeight);
    }

    public int getMaxVideoFrameRate() {
        return Objects.requireNonNullElse(
                get(KEY_MAX_VIDEO_FRAME_RATE, Integer.class),
                maxVideoFrameRate);
    }

    public String getOutputVideoCodec() {
        return Objects.requireNonNullElse(
                get(KEY_OUTPUT_VIDEO_CODEC, String.class),
                outputVideoCodec);
    }

    public String getOutputAudioCodec() {
        return Objects.requireNonNullElse(
                get(KEY_OUTPUT_AUDIO_CODEC, String.class),
                outputAudioCodec);
    }

    public int getOutputVideoBitrate() {
        return Objects.requireNonNullElse(
                get(KEY_OUTPUT_VIDEO_BITRATE, Integer.class),
                outputVideoBitrate);
    }

    public int getOutputAudioBitrate() {
        return Objects.requireNonNullElse(
                get(KEY_OUTPUT_AUDIO_BITRATE, Integer.class),
                outputAudioBitrate);
    }

    public int getOutputSampleRate() {
        return Objects.requireNonNullElse(
                get(KEY_OUTPUT_SAMPLING_RATE, Integer.class),
                outputSampleRate);
    }

    public int getOutputChannels() {
        return Objects.requireNonNullElse(
                get(KEY_OUTPUT_CHANNELS, Integer.class),
                outputChannels);
    }

    public int getOutputFrameRate() {
        return Objects.requireNonNullElse(
                get(KEY_OUTPUT_FRAME_RATE, Integer.class),
                outputFrameRate);
    }

    //setters

    public void setMp4SizeLimit(int newMp4SizeLimit) {
        if (newMp4SizeLimit == 0) {
            throw new IllegalArgumentException("Mp4size limit cannot be zero");
        }
        if (newMp4SizeLimit < 0) {
            throw new IllegalArgumentException("Mp4 size limit cannot be less than zero");
        }
        put(KEY_MP4_SIZE_LIMIT, newMp4SizeLimit);
    }

    public void setMatroskaSizeLimit(int newMatroskaSizeLimit) {
        if (newMatroskaSizeLimit == 0) {
            throw new IllegalArgumentException("Matroska size limit cannot be zero");
        }
        if (newMatroskaSizeLimit < 0) {
            throw new IllegalArgumentException("Matroska size limit cannot be less than zero");
        }
        put(KEY_MATROSKA_SIZE_LIMIT, newMatroskaSizeLimit);
    }

    public void setWebmSizeLimit(int newWebmSizeLimit) {
        if (newWebmSizeLimit == 0) {
            throw new IllegalArgumentException("Webm size limit cannot be zero");
        }
        if (newWebmSizeLimit < 0) {
            throw new IllegalArgumentException("Webm size limit cannot be less than zero");
        }
        put(KEY_WEBM_SIZE_LIMIT, newWebmSizeLimit);
    }

    public void setMovSizeLimit(int newMovSizeLimit) {
        if (newMovSizeLimit == 0) {
            throw new IllegalArgumentException("Mov size limit cannot be zero");
        }
        if (newMovSizeLimit < 0) {
            throw new IllegalArgumentException("Mov size limit cannot be less than zero");
        }
        put(KEY_MOV_SIZE_LIMIT, newMovSizeLimit);
    }

    public void setDontSaveVideo(boolean newDontSaveVideo) {
        put(KEY_DONT_SAVE_VIDEO, newDontSaveVideo);
    }

    public void setSwapAV1(String newSwapAV1) {
        List<String> allowedCodecs = List.of("av1", "vp8", "vp9");
        if (allowedCodecs.contains(newSwapAV1)) {
            put(KEY_SWAP_AV1, newSwapAV1);
        } else {
            throw new IllegalArgumentException("Unsupported codec provided:" + newSwapAV1);
        }
    }

    public void setDefaultOutputTo(String newDefaultOutputTo) {
        List<String> allowedValues = List.of(
                "default", "mp4", "webm", "matroska", "mov");
        if (!allowedValues.contains(newDefaultOutputTo)) {
            throw new IllegalArgumentException("Video Config: unsupported format");
        } else {
            put(KEY_DEFAULT_OUTPUT_TO, newDefaultOutputTo);
        }
    }

    public void setMaxVideoDuration(int newMaxVideoDuration) {
        if (newMaxVideoDuration < 0) {
            throw new IllegalArgumentException("Max video duration cannot be less than zero");
        }
        if (newMaxVideoDuration == 0) {
            throw new IllegalArgumentException("Max video duration cannot be zero");
        }
        put(KEY_MAX_VIDEO_DURATION, newMaxVideoDuration);
    }

    @SuppressWarnings("unchecked")
    public void setMaxFileSize(String format, int newMaxFileSize) {
        if (newMaxFileSize < 0) {
            throw new IllegalArgumentException("Max file size cannot be less than zero");
        }
        if (newMaxFileSize == 0) {
            throw new IllegalArgumentException("Max file size cannot be zero");
        }
        if (format == null) {
            throw new IllegalArgumentException("Format cannot be null");
        }
        if (format.isEmpty()) {
            throw new IllegalArgumentException("Format cannot be empty");
        }
        Map<String, Integer> rates = get(KEY_MAX_FILE_SIZE, Map.class);
        if (rates == null) {
            rates = new HashMap<>(maxFileSize);
        }
        rates.replace(format, newMaxFileSize);
        put(KEY_MAX_FILE_SIZE, rates);
    }

    public void setMaxVideoBitrate(String format, int newMaxVideoBitrate) {
        if (newMaxVideoBitrate < 0) {
            throw new IllegalArgumentException("Max video bitrate cannot be less than zero");
        }
        if (newMaxVideoBitrate == 0) {
            throw new IllegalArgumentException("Max video bitrate cannot be zero");
        }
        if (format == null) {
            throw new IllegalArgumentException("Format cannot be null");
        }
        if (format.isEmpty()) {
            throw new IllegalArgumentException("Format cannot be empty");
        }
        Map<String, Integer> rates = get(KEY_MAX_VIDEO_BITRATE, Map.class);
        if (rates == null) {
            rates = new HashMap<>(maxVideoBitrates);
        }
        rates.replace(format, newMaxVideoBitrate);
        put(KEY_MAX_VIDEO_BITRATE, rates);
    }

    public void setMaxAudioBitrate(String format, int newMaxAudioBitrate) {
        if (newMaxAudioBitrate < 0) {
            throw new IllegalArgumentException("Max audio bitrate cannot be less than zero");
        }
        if (newMaxAudioBitrate == 0) {
            throw new IllegalArgumentException("Max audio bitrate cannot be zero");
        }
        if (format == null) {
            throw new IllegalArgumentException("Format cannot be null");
        }
        if (format.isEmpty()) {
            throw new IllegalArgumentException("Format cannot be empty");
        }
        Map<String, Integer> rates = get(KEY_MAX_AUDIO_BITRATE, Map.class);
        if (rates == null) {
            rates = new HashMap<>(maxAudioBitrates);
        }
        rates.replace(format, newMaxAudioBitrate);
        put(KEY_MAX_AUDIO_BITRATE, rates);
    }

    public void setMaxSampleRate(String format, int newMaxSampleRate) {
        if (newMaxSampleRate < 0) {
            throw new IllegalArgumentException("Max audio sampling rate cannot be less than zero");
        }
        if (newMaxSampleRate == 0) {
            throw new IllegalArgumentException("Max audio sampling rate cannot be zero");
        }
        if (format == null) {
            throw new IllegalArgumentException("Format cannot be null");
        }
        if (format.isEmpty()) {
            throw new IllegalArgumentException("Format cannot be empty");
        }
        Map<String, Integer> rates = get(KEY_MAX_SAMPLE_RATE, Map.class);
        if (rates == null) {
            rates = new HashMap<>(maxSampleRates);
        }
        rates.replace(format, newMaxSampleRate);
        put(KEY_MAX_SAMPLE_RATE, rates);
    }

    public void setMaxVideoWidth(int newMaxVideoWidth) {
        if (newMaxVideoWidth < 0) {
            throw new IllegalArgumentException("Max video width cannot be less than zero");
        }
        if (newMaxVideoWidth == 0) {
            throw new IllegalArgumentException("Max video width cannot be zero");
        }
        put(KEY_MAX_VIDEO_WIDTH, newMaxVideoWidth);
    }

    public void setMaxVideoHeight(int newMaxVideoHeight) {
        if (newMaxVideoHeight < 0) {
            throw new IllegalArgumentException("Max video height cannot be less than zero");
        }
        if (newMaxVideoHeight == 0) {
            throw new IllegalArgumentException("Max video height cannot be zero");
        }
        put(KEY_MAX_VIDEO_HEIGHT, newMaxVideoHeight);
    }

    public void setMaxVideoFrameRate(int newMaxVideoFrameRate) {
        if (newMaxVideoFrameRate < 0) {
            throw new IllegalArgumentException("Max video frame rate cannot be less than zero");
        }
        if (newMaxVideoFrameRate == 0) {
            throw new IllegalArgumentException("Max video frame rate cannot be zero");
        }
        put(KEY_MAX_VIDEO_FRAME_RATE, newMaxVideoFrameRate);
    }

    public void setOutputVideoCodec(String newOutputVideoCodec) {
        if (newOutputVideoCodec == null) {
            throw new IllegalArgumentException("Output video codec cannot be null");
        }
        if (newOutputVideoCodec.isEmpty()) {
            throw new IllegalArgumentException("Output video codec cannot be empty");
        }
        put(KEY_OUTPUT_VIDEO_CODEC, newOutputVideoCodec);
    }

    public void setOutputAudioCodec(String newOutputAudioCodec) {
        if (newOutputAudioCodec == null) {
            throw new IllegalArgumentException("Output audio codec cannot be null");
        }
        if (newOutputAudioCodec.isEmpty()) {
            throw new IllegalArgumentException("Output audio codec cannot be empty");
        }
        put(KEY_OUTPUT_AUDIO_CODEC, newOutputAudioCodec);
    }

    public void setOutputVideoBitrate(int newOutputVideoBitrate) {
        if (newOutputVideoBitrate < 0) {
            throw new IllegalArgumentException("Output video bitrate cannot be less than zero");
        }
        if (newOutputVideoBitrate == 0) {
            throw new IllegalArgumentException("Output video bitrate cannot be zero");
        }
        put(KEY_OUTPUT_VIDEO_BITRATE, newOutputVideoBitrate);
    }

    public void setOutputAudioBitrate(int newOutputAudioBitrate) {
        if (newOutputAudioBitrate < 0) {
            throw new IllegalArgumentException("Output audio bitrate cannot be less than zero");
        }
        if (newOutputAudioBitrate == 0) {
            throw new IllegalArgumentException("Output audio bitrate cannot be zero");
        }
        put(KEY_OUTPUT_AUDIO_BITRATE, newOutputAudioBitrate);
    }

    public void setOutputSampleRate(int newOutputSampleRate) {
        if (newOutputSampleRate < 0) {
            throw new IllegalArgumentException("Output audio sampling rate cannot be less than zero");
        }
        if (newOutputSampleRate == 0) {
            throw new IllegalArgumentException("Output audio sampling rate cannot be zero");
        }
        put(KEY_OUTPUT_SAMPLING_RATE, newOutputSampleRate);
    }

    public void setOutputChannels(int newOutputChannels) {
        if (newOutputChannels < 0) {
            throw new IllegalArgumentException("Output audio channels cannot be less than zero");
        }
        if (newOutputChannels == 0) {
            throw new IllegalArgumentException("Output audio channels cannot be zero");
        }
        put(KEY_OUTPUT_CHANNELS, newOutputChannels);
    }

    public void setOutputFrameRate(int newOutputFrameRate) {
        if (newOutputFrameRate < 0) {
            throw new IllegalArgumentException("Output video frame rate cannot be less than zero");
        }
        if (newOutputFrameRate == 0) {
            throw new IllegalArgumentException("Output video frame rate cannot be zero");
        }
        put(KEY_OUTPUT_FRAME_RATE, newOutputFrameRate);
    }

}

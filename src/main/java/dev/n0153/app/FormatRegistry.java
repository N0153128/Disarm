package dev.n0153.app;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Immutable class containing whitelists and size limits.
 * This class contains crucial data, therefore it lacks any setters.
 * If the user wants to alter the values, they'd have to re-compile the sources.
 * @since 0.1
 */
public final class FormatRegistry {

    /**
     * Whitelist for filetypes and formats.
     * @since 0.1
     */
    public enum AllowList {
        IMAGE(Set.of("png", "jpeg", "jpg", "webp")),
        TEXT(Set.of("text", "log", "plain")),
        VIDEO(Set.of("mp4", "mkv", "webm", "mov",
                "matroska", "x-matroska", "quicktime")),
        AUDIO(Set.of(
                "mp3", "ogg", "flac",
                "wav", "wave", "au",
                "aif", "aiff", "aifc",
                "x-wav", "mpeg", "x-aiff",
                "basic"
        )),
        LOGO(Set.of("png"));

        private final Set<String> allowedFormats;

        AllowList(Set<String> formats) {
            this.allowedFormats = formats;
        }

        public boolean isValidFormat(String format) {
            return allowedFormats.contains(format.toLowerCase());
        }
    }

    /**
     * An enum containing maximum audio bitrate and sample rate for supported audio formats.
     * @since 0.1
     */
    public enum AudioFormatLimits {
        MP3(Map.of("maxAudioBitrate", 320000,
                "maxSampleRate", 48000),
                "mp3", "mpeg-3", "mpeg"),
        FLAC(Map.of("maxAudioBitrate", 1500000,
                "maxSampleRate", 192000),
                "flac"),
        OGG(Map.of("maxAudioBitrate", 500000,
                "maxSampleRate", 48000),
                "ogg", "vorbis"),
        WAV(Map.of("maxAudioBitrate", 4608000,
                "maxSampleRate", 96000),
                "wav", "wave", "x-wav"),
        AU(Map.of("maxAudioBitrate", 1536000,
                "maxSampleRate", 48000),
                "au", "basic"),
        AIF(Map.of("maxAudioBitrate", 4608000,
                "maxSampleRate", 96000),
                "aif", "aiff", "aifc", "x-aiff");
        private final Map<String, Integer> limits;
        private final Set<String> aliases;
        private static final Map<String, AudioFormatLimits> LOOKUP_MAP;

        AudioFormatLimits(Map<String, Integer> limits, String... aliases) {
            this.limits = limits;
            this.aliases = Set.of(aliases);
        }

        static {
            Map<String, AudioFormatLimits> map = new HashMap<>();
            for (AudioFormatLimits fmt : values()) {
                for (String alias : fmt.aliases) {
                    map.put(alias.toLowerCase(), fmt);
                }
            }
            LOOKUP_MAP = Collections.unmodifiableMap(map);
        }

        public static AudioFormatLimits fromFormat(String format) {
            return LOOKUP_MAP.get(format.toLowerCase());
        }

        public int getMaxBitrate() {
            return limits.get("maxAudioBitrate");
        }

        public int getMaxSampleRate() {
            return limits.get("maxSampleRate");
        }

        public boolean isValidBitrate(int bitrate) {
            return bitrate > 0 && bitrate <= getMaxBitrate();
        }

        public boolean isValidSampleRate(int sampleRate) {
            return sampleRate > 0 && sampleRate <= getMaxSampleRate();
        }

        public static boolean isValidBitrateFor(String format, int bitrate) {
            AudioFormatLimits fmt = fromFormat(format);
            return fmt != null && fmt.isValidBitrate(bitrate);
        }

        public static boolean isValidSampleRateFor(String format, int sampleRate) {
            AudioFormatLimits fmt = fromFormat(format);
            return fmt != null && fmt.isValidSampleRate(sampleRate);
        }

    }

    /**
     * An enum containing maximum audio bitrate, maximum sample rate and maximum video bitrate for supported
     * video formats.
     * The enum contains default values for a hypothetical 5-minute-long Full HD file.
     * @since 0.1
     */
    public enum VideoFormatLimits {
        // the following are the max rates for 5 minutes of Full HD video - the default values.
        MP4(Map.of("maxAudioBitrate",512_000,
                "maxSampleRate", 48000,
                "maxVideoBitrate", 15000000),
                "mp4", "mpeg-4"),
        MOV(Map.of("maxAudioBitrate", 1_536_000,
                "maxSampleRate", 48000,
                "maxVideoBitrate", 15000000),
                "mov", "quicktime"),
        MATROSKA(Map.of("maxAudioBitrate", 1_536_000,
                "maxSampleRate", 48000,
                "maxVideoBitrate", 20000000),
                "matroska", "x-matroska", "mkv"),
        WEBM(Map.of("maxAudioBitrate", 512_000,
                "maxSampleRate", 48000,
                "maxVideoBitrate", 12000000),
                "webm");
        private final Map<String, Integer> limits;
        private final Set<String> aliases;
        private static final Map<String, VideoFormatLimits> LOOKUP_MAP;

        VideoFormatLimits(Map<String, Integer> limits, String... aliases) {
            this.limits = limits;
            this.aliases = Set.of(aliases);
        }

        static {
            Map<String, VideoFormatLimits> map = new HashMap<>();
            for (VideoFormatLimits fmt : values()) {
                for (String alias : fmt.aliases) {
                    map.put(alias.toLowerCase(), fmt);
                }
            }
            LOOKUP_MAP = Collections.unmodifiableMap(map);
        }

        public static VideoFormatLimits fromFormat(String format) {
            return LOOKUP_MAP.get(format.toLowerCase());
        }

        public int getMaxAudioBitrate() {
            return limits.get("maxAudioBitrate");
        }

        public int getMaxSampleRate() {
            return limits.get("maxSampleRate");
        }

        public int getMaxVideoBitrate() {
            return limits.get("maxVideoBitrate");
        }

        public boolean isValidAudioBitrate(int bitrate) {
            return bitrate > 0 && bitrate <= getMaxAudioBitrate();
        }

        public boolean isValidSampleRate(int sampleRate) {
            return sampleRate > 0 && sampleRate <= getMaxSampleRate();
        }

        public boolean isValidVideoBitrate(int bitrate) {
            return bitrate > 0 && bitrate <= getMaxVideoBitrate();
        }

        public static boolean isValidAudioBitrateFor(String format, int bitrate) {
            VideoFormatLimits fmt = fromFormat(format);
            return fmt != null && fmt.isValidAudioBitrate(bitrate);
        }

        public static boolean isValidSampleRateFor(String format, int sampleRate) {
            VideoFormatLimits fmt = fromFormat(format);
            return fmt != null && fmt.isValidSampleRate(sampleRate);
        }

        public static boolean isValidVideoBitrateFor(String format, int bitrate) {
            VideoFormatLimits fmt = fromFormat(format);
            return fmt != null && fmt.isValidVideoBitrate(bitrate);
        }

    }

    /**
     * Whitelist of allowed video codecs.
     * @since 0.1
     */
    public enum AllowedVideoCodecs {
        MP4(Set.of("h.264", "hevc", "av1",
                "mpeg-4", "vp9", "h.265",
                "h264"), "mp4", "mpeg-4"),
        MATROSKA(Set.of("h.264", "hevc",
                "vp8", "vp9", "av1",
                "mpeg-4", "theora", "ffv1",
                "mpeg-2", "vc-1", "h.265",
                "h264"), "matroska", "x-matroska", "mkv"),
        WEBM(Set.of("vp8", "vp9", "av1"), "webm"),
        MOV(Set.of("h.264", "hevc", "prores",
                "av1", "mjpeg", "mpeg-4",
                "dnxhd", "h.265", "h264"),
                "quicktime", "mov");

        private final Set<String> allowedCodecs;
        private final Set<String> aliases;
        private static final Map<String, AllowedVideoCodecs> LOOKUP_MAP;

        AllowedVideoCodecs(Set<String> codecs, String... aliases) {
            this.allowedCodecs = codecs;
            this.aliases = Set.of(aliases);
        }

        static {
            Map<String, AllowedVideoCodecs> map = new HashMap<>();
            for (AllowedVideoCodecs codec : values()) {
                for (String alias : codec.aliases) {
                    map.put(alias.toLowerCase(), codec);
                }
            }
            LOOKUP_MAP = Collections.unmodifiableMap(map);
        }

        public static boolean isValidCodecForContainer(String containerFormat, String codec) {
            AllowedVideoCodecs container = LOOKUP_MAP.get(containerFormat.toLowerCase());
            return container != null && container.isValidFormat(codec);
        }

        public static AllowedVideoCodecs fromFormat(String format) {
            return LOOKUP_MAP.get(format.toLowerCase());
        }

        public boolean isValidFormat(String format) {
            return allowedCodecs.contains(format.toLowerCase());
        }
    }

    /**
     * Whitelist of audio codecs for video files.
     * @since 0.1
     */
    public enum AllowedAudioCodecsVideo {
        MP4(Set.of("aac", "mp3", "ac3",
                "e-ac3", "flac", "opus",
                "alac"), "mp4"),
        MATROSKA(Set.of("aac", "mp3", "flac",
                "opus", "vorbis", "ac3",
                "dts", "pcm", "truehd",
                "eac3"), "mkv", "matroska",
                "x-matroska"),
        WEBM(Set.of("vorbis", "opus", "libvorbis"), "webm"),
        MOV(Set.of("aac", "mp3", "pcm",
                "alac", "ac3"), "mov", "quicktime");

        private final Set<String> allowedCodecs;
        private final Set<String> aliases;
        private static final Map<String, AllowedAudioCodecsVideo> LOOKUP_MAP;

        AllowedAudioCodecsVideo(Set<String> codecs, String... aliases) {
            this.allowedCodecs = codecs;
            this.aliases = Set.of(aliases);
        }

        static {
            Map<String, AllowedAudioCodecsVideo> map = new HashMap<>();
            for (AllowedAudioCodecsVideo codec : values()) {
                for (String alias : codec.aliases) {
                    map.put(alias.toLowerCase(), codec);
                }
            }
            LOOKUP_MAP = Collections.unmodifiableMap(map);
        }

        public static boolean isValidCodecForContainer(String containerFormat, String codec) {
            AllowedAudioCodecsVideo container = LOOKUP_MAP.get(containerFormat.toLowerCase());
            return container != null && container.isValidFormat(codec);
        }

        public static AllowedAudioCodecsVideo fromFormat(String format) {
            return LOOKUP_MAP.get(format.toLowerCase());
        }

        public boolean isValidFormat(String format) {
            return allowedCodecs.contains(format.toLowerCase());
        }
    }

    /**
     * Mime-to-format resolver.
     * @since 0.1
     */
    public enum MimeToFormat {
        PNG(Set.of("png")),
        JPEG(Set.of("jpeg", "jpg")),
        WEBP(Set.of("webp")),
        TEXT(Set.of("plain", "txt", "x-plain", "text")),
//        JSON(Set.of("json")),
        LOG(Set.of("log", "x-log")),
        MP4(Set.of("mp4", "x-m4v", "mpeg-4")),
        MATROSKA(Set.of("x-matroska", "mkv", "matroska")),
        WEBM(Set.of("webm")),
        MOV(Set.of("quicktime", "mov")),
        MP3(Set.of("mpeg", "mp3", "mpeg-3")),
        OGG(Set.of("ogg", "vorbis", "x-ogg")),
        FLAC(Set.of("flac", "x-flac")),
        WAV(Set.of("wav", "x-wav", "wave", "vnd.wave")),
        AU(Set.of("basic", "au")),
        AIFF(Set.of("aiff", "x-aiff", "aif", "aifc")),;


        private final Set<String> mimeAliases;
        private static final Map<String, MimeToFormat> LOOKUP_MAP;

            MimeToFormat(Set<String> formats) {
            this.mimeAliases = formats;
        }

        static {
                Map<String, MimeToFormat> map = new HashMap<>();
                for (MimeToFormat fmt : values()) {
                    for (String alias : fmt.mimeAliases) {
                        map.put(alias.toLowerCase(), fmt);
                    }
                }
                LOOKUP_MAP = Collections.unmodifiableMap(map);
        }

        public static MimeToFormat fromMime(String mime) {
                return LOOKUP_MAP.get(mime.toLowerCase());
        }
    }

    /**
     * Whitelist of audio codecs for audio files.
     * @since 0.1
     */
    public enum AllowedAudioCodecs {
        MP3(Set.of("mp3"), "mp3", "mpeg"),
        OGG(Set.of("vorbis", "opus", "flac",
                "speex", "libvorbis"), "ogg"),
        FLAC(Set.of("flac"), "flac"),
        WAV(Set.of("pcm", "adpcm", "mp3",
                "a-law", "μ-law", "gsm",
                "u-law", "pcm_u8"), "wav", "wave",
                "x-wav"),
        AU(Set.of("pcm", "μ-law", "u-law", "pcm_s16be"),
                "au", "basic"),
        AIF(Set.of("pcm", "μ-law", "a-law",
                "ima adpcm", "u-law", "pcm_s16be"), "aif",
                "aiff", "aifc", "x-aiff");

        private final Set<String> allowedCodecs;
        private final Set<String> aliases;
        private static final Map<String, AllowedAudioCodecs> LOOKUP_MAP;

        AllowedAudioCodecs(Set<String> codecs, String... aliases) {
            this.allowedCodecs = codecs;
            this.aliases = Set.of(aliases);
        }

        static {
            Map<String, AllowedAudioCodecs> map = new HashMap<>();
            for (AllowedAudioCodecs codec : values()) {
                for (String alias : codec.aliases) {
                    map.put(alias.toLowerCase(), codec);
                }
            }
            LOOKUP_MAP = Collections.unmodifiableMap(map);
        }

        public static boolean isValidCodecForContainer(String containerFormat, String codec) {
            AllowedAudioCodecs container = LOOKUP_MAP.get(containerFormat.toLowerCase());
            return container != null && container.isValidFormat(codec);
        }

        public static AllowedAudioCodecs fromFormat(String format) {
            return LOOKUP_MAP.get(format.toLowerCase());
        }

        public boolean isValidFormat(String format) {
            return allowedCodecs.contains(format.toLowerCase());
        }
    }
}

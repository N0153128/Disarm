package dev.n0153.app;

import dev.n0153.app.exceptions.CodecDetectionException;
import dev.n0153.app.exceptions.DurationFormattingException;
import dev.n0153.app.exceptions.ValidationException;
import dev.n0153.app.plugins.MediaUtils;
import dev.n0153.app.plugins.audio.AudioUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import ws.schild.jave.EncoderException;

import javax.sound.sampled.AudioFileFormat;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class AudioUtilsTest {

    GlobalConfig globalConfig = new GlobalConfig();
    ProcessingContext context = new ProcessingContext(globalConfig);
    @TempDir
    Path tempDir;
    private static final Map<String, byte[]> MIME_TO_BYTE = new HashMap<>();
    static {
        MIME_TO_BYTE.put("png", new byte[] {(byte)0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A});
        MIME_TO_BYTE.put("jpeg", new byte[] {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF});
        MIME_TO_BYTE.put("webp", new byte[] {(byte) 0x52, 0x49, 0x46, 0x46,
                0x00, 0x00, 0x00, 0x00,
                0x57, 0x45, 0x42, 0x50});
        MIME_TO_BYTE.put("text", new byte[] {(byte) 0x68, 0x65, 0x6C, 0x6F});
        MIME_TO_BYTE.put("json", new byte[] {(byte) 0x7B});
        MIME_TO_BYTE.put("log", new byte[] {(byte) 0x58});
        MIME_TO_BYTE.put("mp4", new byte[] {(byte) 0x00, 0x00, 0x00, 0x08,
                0x66, 0x74, 0x79, 0x70});
        MIME_TO_BYTE.put("matroska", new byte[] {(byte) 0x1A, 0x45, (byte) 0xDF, (byte) 0xA3});
        MIME_TO_BYTE.put("webm", new byte[] {(byte) 0x1A, 0x45, (byte) 0xDF, (byte) 0xA3});
        MIME_TO_BYTE.put("mov", new byte[] {(byte) 0x00, 0x00, 0x00, 0x08,
                0x66, 0x74, 0x79, 0x70,
                0x71, 0x74, 0x20, 0x20});
        MIME_TO_BYTE.put("mp3", new byte[] {(byte) 0x49, 0x44, 0x33});
        MIME_TO_BYTE.put("ogg", new byte[] {(byte) 0x4F, 0x67, 0x67, 0x53});
        MIME_TO_BYTE.put("flac", new byte[] {(byte) 0x66, 0x4C, 0x61, 0x43});
        MIME_TO_BYTE.put("wav", new byte[] {(byte) 0x52, 0x49, 0x46, 0x46,
                0x00, 0x00, 0x00, 0x00,
                0x57, 0x41, 0x56, 0x45});
        MIME_TO_BYTE.put("au", new byte[] {(byte) 0x2E, 0x73, 0x6E, 0x64});
        MIME_TO_BYTE.put("aiff", new byte[] {(byte) 0x46, 0x4F, 0x52, 0x4D,
                0x00, 0x00, 0x00, 0x00,
                0x41, 0x49, 0x46, 0x46});
        MIME_TO_BYTE.put("pdf", new byte[] {(byte) 0x25, 0x50, 0x44, 0x46}); // unsupported on 0.1
        MIME_TO_BYTE.put("zip", new byte[] {(byte) 0x50, 0x48, 0x03, 0x04}); // unsupported on 0.1
        MIME_TO_BYTE.put("docx", new byte[] {(byte) 0x50, 0x4B, 0x03, 0x04}); // unsupported on 0.1
        MIME_TO_BYTE.put("snd", new byte[] {(byte) 0x2E, 0x73, 0x6E, 0x64}); // unsupported on 0.1
        MIME_TO_BYTE.put("gif", new byte[] {(byte) 0x47, 0x49, 0x46, 0x38}); // unsupported on 0.1
        MIME_TO_BYTE.put("html", new byte[] {(byte) 0x3C, 0x21, 0x44, 0x4F,
                0x43, 0x54, 0x59, 0x50,
                0x45});
        MIME_TO_BYTE.put("php", new byte[] {(byte) 0x3C, 0x3F, 0x70, 0x68,
                0x70});
        MIME_TO_BYTE.put("exe", new byte[] {(byte) 0x4D, 0x5A});
    }

    private Path createDummyFile(String mime) {
        try {
            Path tempFile = tempDir.resolve("test."+mime);
            if (mime.equals("mkv") || mime.equals("x-matroska")) {
                Files.write(tempFile, MIME_TO_BYTE.get("matroska"));
            } else {
                Files.write(tempFile, MIME_TO_BYTE.get(mime));
            }
            return tempFile;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void getAudioTypeTest(String mime, AudioFileFormat.Type audioFormat) {
        try {
            Path dummyFile = createDummyFile(mime);
            AudioFileFormat.Type audioType = AudioUtils.getAudioType(dummyFile, Utils.getFileType(dummyFile));
            assertNotNull(audioType);
            assertEquals(audioFormat, audioType);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void getAudioTypeWav() {
        getAudioTypeTest("wav", AudioFileFormat.Type.WAVE);
    }

    @Test
    void getAudioTypeAiff() {
        getAudioTypeTest("aiff", AudioFileFormat.Type.AIFF);
    }

    @Test
    void getAudioTypeAu() {
        getAudioTypeTest("au", AudioFileFormat.Type.AU);
    }

    @Test
    void getAudioType_unsupportedSnd() {
        try {
            Path tempFileSnd = tempDir.resolve("test.snd");
            Files.write(tempFileSnd, MIME_TO_BYTE.get("snd"));
            String fileType = Utils.getFileType(tempFileSnd);
            AudioFileFormat.Type audioType = AudioUtils.getAudioType(tempFileSnd, fileType);
            assertNotNull(audioType);
            assertNotEquals(AudioFileFormat.Type.SND, audioType);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void getAudioTypeAifc_conversionToAiff() {
        try {
            Path tempFileAifc = tempDir.resolve("test.aifc");
            Files.write(tempFileAifc, MIME_TO_BYTE.get("aiff"));
            String fileType = Utils.getFileType(tempFileAifc);
            AudioFileFormat.Type audioType = AudioUtils.getAudioType(tempFileAifc, fileType);
            assertNotNull(audioType);
            assertEquals(AudioFileFormat.Type.AIFF, audioType);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void getCodec_forAudio() {
        String codec = assertDoesNotThrow(() -> MediaUtils.getCodec(DebugPaths.audioTestInputMp3, "audio"));
        assertNotNull(codec);
        assertFalse(codec.isEmpty());
    }

    @Test
    void getCodec_withEmptyMedia() {
        assertThrows(CodecDetectionException.class,
                () -> MediaUtils.getCodec(DebugPaths.audioTestInputMp3, ""));
    }

    @Test
    void getCodec_nonExistent() {
        assertThrows(CodecDetectionException.class,
                () -> MediaUtils.getCodec(tempDir.resolve("none.mp3"), "audio"));
    }

    @Test
    void getCodec_withImage() {
        assertThrows(NullPointerException.class,
                () -> MediaUtils.getCodec(DebugPaths.imageTestInputPng, "audio"));
    }


    @Test
    void getBitrate_forAudio() {
        int bitrate = assertDoesNotThrow(() -> MediaUtils.getBitrate(
                DebugPaths.audioTestInputMp3, "audio"));
        assertNotEquals(0, bitrate);
    }

    @Test
    void getBitrate_noneExistent() {
        assertThrows(EncoderException.class, () -> MediaUtils.getBitrate(
                tempDir.resolve("none.mp3"), "audio"));
    }

    @Test
    void getBitrate_withEmptyMedia() {
        try {
            assertEquals(-1, MediaUtils.getBitrate(
                    DebugPaths.audioTestInputMp3, ""));
        } catch (EncoderException | IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Test
    void getSamplingRate_withAudio() {
        int sampling = assertDoesNotThrow(() ->
                MediaUtils.getSamplingRate(DebugPaths.audioTestInputMp3));
        assertTrue(sampling > 0);
    }

    @Test
    void getSamplingRate_nonExistent() {
        assertThrows(EncoderException.class, () ->
                MediaUtils.getSamplingRate(tempDir.resolve("none.test")));
    }

    @Test
    void getSamplingRate_withImage() {
        assertThrows(NullPointerException.class, () ->
                MediaUtils.getSamplingRate(DebugPaths.imageTestInputPng));
    }


    @Test
    void getAudioChannels_withMp3() {
        int channels = assertDoesNotThrow(() ->
                MediaUtils.getAudioChannels(DebugPaths.audioTestInputMp3));
        assertTrue(channels > 0);
    }

    @Test
    void getAudioChannels_withAif() {
        int channels = assertDoesNotThrow(() ->
                MediaUtils.getAudioChannels(DebugPaths.audioTestInputAif));
        assertTrue(channels > 0);
    }

    @Test
    void getAudioChannels_withAifc() {
        int channels = assertDoesNotThrow(() ->
                MediaUtils.getAudioChannels(DebugPaths.audioTestInputAifc));
        assertTrue(channels > 0);
    }

    @Test
    void getAudioChannels_withAu() {
        int channels = assertDoesNotThrow(() ->
                MediaUtils.getAudioChannels(DebugPaths.audioTestInputAu));
        assertTrue(channels > 0);
    }

    @Test
    void getAudioChannels_withAiff() {
        int channels = assertDoesNotThrow(() ->
                MediaUtils.getAudioChannels(DebugPaths.audioTestInputAiff));
        assertTrue(channels > 0);
    }

    @Test
    void getAudioChannels_withFlac() {
        int channels = assertDoesNotThrow(() ->
                MediaUtils.getAudioChannels(DebugPaths.audioTestInputFlac));
        assertTrue(channels > 0);
    }

    @Test
    void getAudioChannels_withOgg() {
        int channels = assertDoesNotThrow(() ->
                MediaUtils.getAudioChannels(DebugPaths.audioTestInputOgg));
        assertTrue(channels > 0);
    }

    @Test
    void getAudioChannels_withWav() {
        int channels = assertDoesNotThrow(() ->
                MediaUtils.getAudioChannels(DebugPaths.audioTestInputWav));
        assertTrue(channels > 0);
    }

    @Test
    void getAudioChannels_withVideo() {
        assertThrows(EncoderException.class, () ->
                MediaUtils.getAudioChannels(DebugPaths.videoTestOutputMp4));
    }

    @Test
    void getAudioChannels_nonExistent() {
        assertThrows(EncoderException.class, () ->
                MediaUtils.getAudioChannels(tempDir.resolve("none.test")));
    }


    @Test
    void getTrackLength() {
        assertDoesNotThrow(() -> MediaUtils.getTrackLength(DebugPaths.audioTestInputMp3));
        long trackLength = MediaUtils.getTrackLength(DebugPaths.audioTestInputMp3);
        assertNotEquals(0, trackLength);
    }

    @Test
    void getTrackLength_nonExistent() {
        assertThrows(ValidationException.class, () -> MediaUtils.getTrackLength(tempDir.resolve("none.test")));
    }

    @Test
    void getTrackLengthFormatted() {
        assertTrue(MediaUtils.getTrackLengthFormatted(MediaUtils.getTrackLength(DebugPaths.audioTestInputMp3)).matches(
                "\\d{2}:\\d{2}:\\d{2}"
        ));
    }

    @Test
    void getTrackLengthFormatted_negativeParameter() {
        assertThrows(DurationFormattingException.class, () ->MediaUtils.getTrackLengthFormatted(-1).matches(
                "\\d{2}:\\d{2}:\\d{2}"
        ));
    }
}

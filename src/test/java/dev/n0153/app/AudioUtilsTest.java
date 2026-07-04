package dev.n0153.app;

import dev.n0153.app.exceptions.CodecDetectionException;
import dev.n0153.app.exceptions.DurationFormattingException;
import dev.n0153.app.exceptions.ValidationException;
import org.junit.jupiter.api.Test;
import ws.schild.jave.EncoderException;

import javax.sound.sampled.AudioFileFormat;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class AudioUtilsTest {
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
            AudioFileFormat.Type audioType = Utils.getAudioType(tempFileSnd, fileType);
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
            AudioFileFormat.Type audioType = Utils.getAudioType(tempFileAifc, fileType);
            assertNotNull(audioType);
            assertEquals(AudioFileFormat.Type.AIFF, audioType);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void getCodec_forAudio() {
        String codec = assertDoesNotThrow(() -> Utils.getCodec(DebugPaths.audioTestInputMp3, "audio"));
        assertNotNull(codec);
        assertFalse(codec.isEmpty());
    }

    @Test
    void getCodec_withEmptyMedia() {
        assertThrows(CodecDetectionException.class,
                () -> Utils.getCodec(DebugPaths.audioTestInputMp3, ""));
    }

    @Test
    void getCodec_nonExistent() {
        assertThrows(CodecDetectionException.class,
                () -> Utils.getCodec(tempDir.resolve("none.mp3"), "audio"));
    }

    @Test
    void getCodec_withImage() {
        assertThrows(NullPointerException.class,
                () -> Utils.getCodec(DebugPaths.imageTestInputPng, "audio"));
    }


    @Test
    void getBitrate_forAudio() {
        int bitrate = assertDoesNotThrow(() -> Utils.getBitrate(
                DebugPaths.audioTestInputMp3, "audio"));
        assertNotEquals(0, bitrate);
    }

    @Test
    void getBitrate_noneExistent() {
        assertThrows(EncoderException.class, () -> Utils.getBitrate(
                tempDir.resolve("none.mp3"), "audio"));
    }

    @Test
    void getBitrate_withEmptyMedia() {
        try {
            assertEquals(-1, Utils.getBitrate(
                    DebugPaths.audioTestInputMp3, ""));
        } catch (EncoderException | IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Test
    void getSamplingRate_withAudio() {
        int sampling = assertDoesNotThrow(() ->
                Utils.getSamplingRate(DebugPaths.audioTestInputMp3));
        assertTrue(sampling > 0);
    }

    @Test
    void getSamplingRate_nonExistent() {
        assertThrows(EncoderException.class, () ->
                Utils.getSamplingRate(tempDir.resolve("none.test")));
    }

    @Test
    void getSamplingRate_withImage() {
        assertThrows(NullPointerException.class, () ->
                Utils.getSamplingRate(DebugPaths.imageTestInputPng));
    }


    @Test
    void getAudioChannels_withMp3() {
        int channels = assertDoesNotThrow(() ->
                Utils.getAudioChannels(DebugPaths.audioTestInputMp3));
        assertTrue(channels > 0);
    }

    @Test
    void getAudioChannels_withAif() {
        int channels = assertDoesNotThrow(() ->
                Utils.getAudioChannels(DebugPaths.audioTestInputAif));
        assertTrue(channels > 0);
    }

    @Test
    void getAudioChannels_withAifc() {
        int channels = assertDoesNotThrow(() ->
                Utils.getAudioChannels(DebugPaths.audioTestInputAifc));
        assertTrue(channels > 0);
    }

    @Test
    void getAudioChannels_withAu() {
        int channels = assertDoesNotThrow(() ->
                Utils.getAudioChannels(DebugPaths.audioTestInputAu));
        assertTrue(channels > 0);
    }

    @Test
    void getAudioChannels_withAiff() {
        int channels = assertDoesNotThrow(() ->
                Utils.getAudioChannels(DebugPaths.audioTestInputAiff));
        assertTrue(channels > 0);
    }

    @Test
    void getAudioChannels_withFlac() {
        int channels = assertDoesNotThrow(() ->
                Utils.getAudioChannels(DebugPaths.audioTestInputFlac));
        assertTrue(channels > 0);
    }

    @Test
    void getAudioChannels_withOgg() {
        int channels = assertDoesNotThrow(() ->
                Utils.getAudioChannels(DebugPaths.audioTestInputOgg));
        assertTrue(channels > 0);
    }

    @Test
    void getAudioChannels_withWav() {
        int channels = assertDoesNotThrow(() ->
                Utils.getAudioChannels(DebugPaths.audioTestInputWav));
        assertTrue(channels > 0);
    }

    @Test
    void getAudioChannels_withVideo() {
        assertThrows(EncoderException.class, () ->
                Utils.getAudioChannels(DebugPaths.videoTestOutputMp4));
    }

    @Test
    void getAudioChannels_nonExistent() {
        assertThrows(EncoderException.class, () ->
                Utils.getAudioChannels(tempDir.resolve("none.test")));
    }


    @Test
    void getTrackLength() {
        assertDoesNotThrow(() -> Utils.getTrackLength(DebugPaths.audioTestInputMp3));
        long trackLength = Utils.getTrackLength(DebugPaths.audioTestInputMp3);
        assertNotEquals(0, trackLength);
    }

    @Test
    void getTrackLength_nonExistent() {
        assertThrows(ValidationException.class, () -> Utils.getTrackLength(tempDir.resolve("none.test")));
    }

    @Test
    void getTrackLengthFormatted() {
        assertTrue(Utils.getTrackLengthFormatted(Utils.getTrackLength(DebugPaths.audioTestInputMp3)).matches(
                "\\d{2}:\\d{2}:\\d{2}"
        ));
    }

    @Test
    void getTrackLengthFormatted_negativeParameter() {
        assertThrows(DurationFormattingException.class, () ->Utils.getTrackLengthFormatted(-1).matches(
                "\\d{2}:\\d{2}:\\d{2}"
        ));
    }
}

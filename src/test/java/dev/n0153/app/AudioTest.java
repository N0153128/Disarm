package dev.n0153.app;

import dev.n0153.app.exceptions.CodecDetectionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import ws.schild.jave.EncoderException;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class AudioTest {
    @TempDir
    Path tempDir;
    DisarmConfig config;
    DisarmState state;

    @BeforeEach
    void setUp() {
        config = new BuilderConfig().setGeneralOutputPath(tempDir).build();
        state = new DisarmState(config);
    }

    private void prepData(Path osTargetPath) {
        try {
            state.setFileType(Utils.getFileType(osTargetPath));
            state.setMime(Utils.getFormatFromMime(Utils.getMimeType(osTargetPath)));
            state.setAudioCodec(Utils.getCodec(osTargetPath, "audio"));
            state.setAudioBitrate(Utils.getBitrate(osTargetPath, "audio", config));
            state.setAudioSamplingRate(Utils.getSamplingRate(osTargetPath));
            state.setAudioChannels(Utils.getAudioChannels(osTargetPath));
            Utils.getTitle(state, false);
        } catch (EncoderException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void isSane(String title) {
        Path newFile = config.getGeneralOutputPath().resolve(title);
        assertTrue(Files.exists(newFile));
        try {
            assertEquals("audio", Utils.getFileType(newFile));
            assertTrue(Files.size(newFile) > 0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // WAV, AIFF, AU only
    @Test
    @Tag("audio")
    void reEncodeAudioNative_withWav() {
        Audio audio = new Audio(state, config);
        prepData(DebugPaths.audioTestInputWav);
        assertDoesNotThrow(() ->
                audio.reEncodeAudioNative(DebugPaths.audioTestInputWav));
        isSane(state.getGeneralFileTitle());
    }

    @Test
    @Tag("audio")
    void reEncodeAudioNative_withAiff() {
        Audio audio = new Audio(state, config);
        prepData(DebugPaths.audioTestInputAiff);
        assertDoesNotThrow(() ->
                audio.reEncodeAudioNative(DebugPaths.audioTestInputAiff));
        isSane(state.getGeneralFileTitle());
    }

    @Test
    @Tag("audio")
    void reEncodeAudioNative_withAu() {
        Audio audio = new Audio(state, config);
        prepData(DebugPaths.audioTestInputAu);
        assertDoesNotThrow(() ->
                audio.reEncodeAudioNative(DebugPaths.audioTestInputAu));
        isSane(state.getGeneralFileTitle());
    }

    @Test
    @Tag("audio")
    void reEncodeAudio_withMp3() {
        Audio audio = new Audio(state, config);
        prepData(DebugPaths.audioTestInputMp3);
        assertDoesNotThrow(() ->
                audio.reEncode(DebugPaths.audioTestInputMp3, "mp3"));
        isSane(state.getGeneralFileTitle());
    }

    @Test
    @Tag("audio")
    void reEncodeAudio_withAiff() {
        Audio audio = new Audio(state, config);
        prepData(DebugPaths.audioTestInputAiff);
        assertDoesNotThrow(() ->
                audio.reEncode(DebugPaths.audioTestInputAiff, "aiff"));
        isSane(state.getGeneralFileTitle());
    }

    @Test
    @Tag("audio")
    void reEncodeAudio_withFlac() {
        Audio audio = new Audio(state, config);
        prepData(DebugPaths.audioTestInputFlac);
        assertDoesNotThrow(() ->
                audio.reEncode(DebugPaths.audioTestInputFlac, "flac"));
        isSane(state.getGeneralFileTitle());
    }

    @Test
    @Tag("audio")
    void reEncodeAudio_withOgg() {
        Audio audio = new Audio(state, config);
        prepData(DebugPaths.audioTestInputOgg);
        assertDoesNotThrow(() ->
                audio.reEncode(DebugPaths.audioTestInputOgg, "ogg"));
        isSane(state.getGeneralFileTitle());
    }

    @Test
    @Tag("audio")
    void reEncodeAudio_withWav() {
        Audio audio = new Audio(state, config);
        prepData(DebugPaths.audioTestInputWav);
        assertDoesNotThrow(() ->
                audio.reEncode(DebugPaths.audioTestInputWav, "wav"));
        isSane(state.getGeneralFileTitle());
    }

    @Test
    @Tag("audio")
    void reEncodeAudio_withAu() {
        Audio audio = new Audio(state, config);
        prepData(DebugPaths.audioTestInputAu);
        assertDoesNotThrow(() ->
                audio.reEncode(DebugPaths.audioTestInputAu, "au"));
        isSane(state.getGeneralFileTitle());
    }

    @Test
    @Tag("audio")
    void reEncodeAudio_withAifc() {
        Audio audio = new Audio(state, config);
        prepData(DebugPaths.audioTestInputAifc);
        assertDoesNotThrow(() ->
                audio.reEncode(DebugPaths.audioTestInputAifc, "aiff"));
        isSane(state.getGeneralFileTitle());
    }

    @Test
    @Tag("audio")
    void reEncodeAudioNative_withMp3() {
        Audio audio = new Audio(state, config);
        prepData(DebugPaths.audioTestInputMp3);
        assertThrows(UnsupportedAudioFileException.class, () ->
                audio.reEncodeAudioNative(DebugPaths.audioTestInputMp3));
    }

    @Test
    void reEncodeAudioNative_nonExistent() {
        Audio audio = new Audio(state, config);
        assertThrows(NullPointerException.class, () ->
                prepData(tempDir.resolve("none.test")));
        assertThrows(IllegalStateException.class, () ->
                audio.reEncodeAudioNative(tempDir.resolve("none.test")));
    }

    @Test
    void reEncodeAudioNative_withPng() {
        Audio audio = new Audio(state, config);
        assertThrows(CodecDetectionException.class, () ->
                prepData(tempDir.resolve(DebugPaths.imageTestInputPng)));
        assertThrows(IllegalStateException.class, () ->
                audio.reEncodeAudioNative(DebugPaths.imageTestInputPng));
    }
}
package dev.n0153.app;

import org.junit.jupiter.api.Test;
import ws.schild.jave.EncoderException;
import ws.schild.jave.info.VideoSize;

import java.nio.file.NoSuchFileException;

import static org.junit.jupiter.api.Assertions.*;

public class VideoUtilsTest {

    @Test
    void getCodec_forVideo() {
        String codec = assertDoesNotThrow(() -> Utils.getCodec(DebugPaths.videoTestInputMp4, "video"));
        assertNotNull(codec);
        assertFalse(codec.isEmpty());
    }

    @Test
    void getBitrate_forVideo() {
        int bitrate = assertDoesNotThrow(() -> Utils.getBitrate(
                DebugPaths.videoTestInputMp4, "video"));
        assertNotEquals(0, bitrate);
    }

    @Test
    void getSamplingRate_withVideo() {
        int sampling = assertDoesNotThrow(() ->
                Utils.getSamplingRate(DebugPaths.videoTestInputMp4));
        assertTrue(sampling > 0);
    }

    @Test
    void calcVideoBitRate_withWebm() {
        int bitrate = assertDoesNotThrow(() ->
                Utils.calcVideoBitRate(DebugPaths.videoTestInputWebm));
        assertTrue(bitrate > 0);
    }

    @Test
    void calcVideoBitRate_withMkv() {
        int bitrate = assertDoesNotThrow(() ->
                Utils.calcVideoBitRate(DebugPaths.videoTestInputMkv));
        assertTrue(bitrate > 0);
    }

    @Test
    void calcVideoBitRate_withMp4() {
        int bitrate = assertDoesNotThrow(() ->
                Utils.calcVideoBitRate(DebugPaths.videoTestInputMp4));
        assertTrue(bitrate > 0);
    }

    @Test
    void calcVideoBitRate_withMov() {
        int bitrate = assertDoesNotThrow(() ->
                Utils.calcVideoBitRate(DebugPaths.videoTestInputMov));
        assertTrue(bitrate > 0);
    }

    @Test
    void calcVideoBitRate_withAudio() {
        int bitrate = assertDoesNotThrow(() ->
                Utils.calcVideoBitRate(DebugPaths.audioTestInputMp3));
        assertTrue(bitrate > 0);
    }

    @Test
    void calcVideoBitRate_nonExistent() {
        assertThrows(NoSuchFileException.class,
                () -> Utils.calcVideoBitRate(tempDir.resolve("none.test")));
    }

    @Test
    void getVideoDimensions_withWebm() {
        VideoSize size = assertDoesNotThrow(() ->
                Utils.getVideoDimensions(DebugPaths.videoTestInputWebm));
        assertTrue(size.getHeight() > 0);
        assertTrue(size.getWidth() > 0);
    }

    @Test
    void getVideoDimensions_withMp4() {
        VideoSize size = assertDoesNotThrow(() ->
                Utils.getVideoDimensions(DebugPaths.videoTestInputMp4));
        assertTrue(size.getHeight() > 0);
        assertTrue(size.getWidth() > 0);
    }

    @Test
    void getVideoDimensions_withMov() {
        VideoSize size = assertDoesNotThrow(() ->
                Utils.getVideoDimensions(DebugPaths.videoTestInputMov));
        assertTrue(size.getHeight() > 0);
        assertTrue(size.getWidth() > 0);
    }

    @Test
    void getVideoDimensions_withMkv() {
        VideoSize size = assertDoesNotThrow(() ->
                Utils.getVideoDimensions(DebugPaths.videoTestInputMkv));
        assertTrue(size.getHeight() > 0);
        assertTrue(size.getWidth() > 0);
    }

    @Test
    void getVideoDimensions_withAudio() {
        assertThrows(NullPointerException.class, () ->
                Utils.getVideoDimensions(DebugPaths.audioTestInputMp3));
    }

    @Test
    void getVideoDimensions_nonExistent() {
        assertThrows(EncoderException.class, () ->
                Utils.getVideoDimensions(tempDir.resolve("none.test")));
    }

    @Test
    void getVideoFrameRate_withWebm() {
        int frames = assertDoesNotThrow(() ->
                Utils.getVideoFrameRate(DebugPaths.videoTestInputWebm));
        assertTrue(frames > 0);
    };

    @Test
    void getVideoFrameRate_withMp4() {
        int frames = assertDoesNotThrow(() ->
                Utils.getVideoFrameRate(DebugPaths.videoTestInputMp4));
        assertTrue(frames > 0);
    };

    @Test
    void getVideoFrameRate_withMov() {
        int frames = assertDoesNotThrow(() ->
                Utils.getVideoFrameRate(DebugPaths.videoTestInputMov));
        assertTrue(frames > 0);
    };

    @Test
    void getVideoFrameRate_withMkv() {
        int frames = assertDoesNotThrow(() ->
                Utils.getVideoFrameRate(DebugPaths.videoTestInputMkv));
        assertTrue(frames > 0);
    };

    @Test
    void getVideoFrameRate_withAudio() {
        assertThrows(NullPointerException.class, () ->
                Utils.getVideoFrameRate(DebugPaths.audioTestInputMp3));
    };

    @Test
    void getVideoFrameRate_nonExistent() {
        assertThrows(EncoderException.class, () ->
                Utils.getVideoFrameRate(tempDir.resolve("none.test")));
    }
}

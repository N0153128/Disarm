package dev.n0153.app;

import dev.n0153.app.plugins.MediaUtils;
import dev.n0153.app.plugins.video.VideoUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import ws.schild.jave.EncoderException;
import ws.schild.jave.info.VideoSize;

import java.nio.file.NoSuchFileException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class VideoUtilsTest {

    @TempDir
    Path tempDir;

    @Test
    void getCodec_forVideo() {
        String codec = assertDoesNotThrow(() -> MediaUtils.getCodec(DebugPaths.videoTestInputMp4, "video"));
        assertNotNull(codec);
        assertFalse(codec.isEmpty());
    }

    @Test
    void getBitrate_forVideo() {
        int bitrate = assertDoesNotThrow(() -> MediaUtils.getBitrate(
                DebugPaths.videoTestInputMp4, "video"));
        assertNotEquals(0, bitrate);
    }

    @Test
    void getSamplingRate_withVideo() {
        int sampling = assertDoesNotThrow(() ->
                MediaUtils.getSamplingRate(DebugPaths.videoTestInputMp4));
        assertTrue(sampling > 0);
    }

    @Test
    void calcVideoBitRate_withWebm() {
        int bitrate = assertDoesNotThrow(() ->
                MediaUtils.calcVideoBitRate(DebugPaths.videoTestInputWebm));
        assertTrue(bitrate > 0);
    }

    @Test
    void calcVideoBitRate_withMkv() {
        int bitrate = assertDoesNotThrow(() ->
                MediaUtils.calcVideoBitRate(DebugPaths.videoTestInputMkv));
        assertTrue(bitrate > 0);
    }

    @Test
    void calcVideoBitRate_withMp4() {
        int bitrate = assertDoesNotThrow(() ->
                MediaUtils.calcVideoBitRate(DebugPaths.videoTestInputMp4));
        assertTrue(bitrate > 0);
    }

    @Test
    void calcVideoBitRate_withMov() {
        int bitrate = assertDoesNotThrow(() ->
                MediaUtils.calcVideoBitRate(DebugPaths.videoTestInputMov));
        assertTrue(bitrate > 0);
    }

    @Test
    void calcVideoBitRate_withAudio() {
        int bitrate = assertDoesNotThrow(() ->
                MediaUtils.calcVideoBitRate(DebugPaths.audioTestInputMp3));
        assertTrue(bitrate > 0);
    }

    @Test
    void calcVideoBitRate_nonExistent() {
        assertThrows(NoSuchFileException.class,
                () -> MediaUtils.calcVideoBitRate(tempDir.resolve("none.test")));
    }

    @Test
    void getVideoDimensions_withWebm() {
        VideoSize size = assertDoesNotThrow(() ->
                VideoUtils.getVideoDimensions(DebugPaths.videoTestInputWebm));
        assertTrue(size.getHeight() > 0);
        assertTrue(size.getWidth() > 0);
    }

    @Test
    void getVideoDimensions_withMp4() {
        VideoSize size = assertDoesNotThrow(() ->
                VideoUtils.getVideoDimensions(DebugPaths.videoTestInputMp4));
        assertTrue(size.getHeight() > 0);
        assertTrue(size.getWidth() > 0);
    }

    @Test
    void getVideoDimensions_withMov() {
        VideoSize size = assertDoesNotThrow(() ->
                VideoUtils.getVideoDimensions(DebugPaths.videoTestInputMov));
        assertTrue(size.getHeight() > 0);
        assertTrue(size.getWidth() > 0);
    }

    @Test
    void getVideoDimensions_withMkv() {
        VideoSize size = assertDoesNotThrow(() ->
                VideoUtils.getVideoDimensions(DebugPaths.videoTestInputMkv));
        assertTrue(size.getHeight() > 0);
        assertTrue(size.getWidth() > 0);
    }

    @Test
    void getVideoDimensions_withAudio() {
        assertThrows(NullPointerException.class, () ->
                VideoUtils.getVideoDimensions(DebugPaths.audioTestInputMp3));
    }

    @Test
    void getVideoDimensions_nonExistent() {
        assertThrows(EncoderException.class, () ->
                VideoUtils.getVideoDimensions(tempDir.resolve("none.test")));
    }

    @Test
    void getVideoFrameRate_withWebm() {
        int frames = assertDoesNotThrow(() ->
                VideoUtils.getVideoFrameRate(DebugPaths.videoTestInputWebm));
        assertTrue(frames > 0);
    };

    @Test
    void getVideoFrameRate_withMp4() {
        int frames = assertDoesNotThrow(() ->
                VideoUtils.getVideoFrameRate(DebugPaths.videoTestInputMp4));
        assertTrue(frames > 0);
    };

    @Test
    void getVideoFrameRate_withMov() {
        int frames = assertDoesNotThrow(() ->
                VideoUtils.getVideoFrameRate(DebugPaths.videoTestInputMov));
        assertTrue(frames > 0);
    };

    @Test
    void getVideoFrameRate_withMkv() {
        int frames = assertDoesNotThrow(() ->
                VideoUtils.getVideoFrameRate(DebugPaths.videoTestInputMkv));
        assertTrue(frames > 0);
    };

    @Test
    void getVideoFrameRate_withAudio() {
        assertThrows(NullPointerException.class, () ->
                VideoUtils.getVideoFrameRate(DebugPaths.audioTestInputMp3));
    };

    @Test
    void getVideoFrameRate_nonExistent() {
        assertThrows(EncoderException.class, () ->
                VideoUtils.getVideoFrameRate(tempDir.resolve("none.test")));
    }
}

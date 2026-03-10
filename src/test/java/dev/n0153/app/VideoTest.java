package dev.n0153.app;

import dev.n0153.app.exceptions.CodecDetectionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import ws.schild.jave.EncoderException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class VideoTest {
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
            state.setVideoCodec(Utils.getCodec(osTargetPath, "video"));
            state.setAudioSamplingRate(Utils.getSamplingRate(osTargetPath));
            state.setVideoBitrate(Utils.getBitrate(osTargetPath, "video", config));
            state.setVideoFrameRate(Utils.getVideoFrameRate(osTargetPath));
            state.setVideoSize(Utils.getVideoDimensions(osTargetPath));
            state.setAudioChannels(Utils.getAudioChannels(osTargetPath));
            state.setAudioBitrate(Utils.getBitrate(osTargetPath, "audio", config));
            Utils.getTitle(state, false);
        } catch (EncoderException | IOException e){
            throw new RuntimeException(e);
        }
    }

    private void isSane(String title) {
        Path newFile = config.getGeneralOutputPath().resolve(title);
        assertTrue(Files.exists(newFile));
        try {
            if (!title.split("\\.")[1].equals("matroska")) {
                assertEquals("video", Utils.getFileType(newFile));
            }
            assertTrue(Files.size(newFile) > 0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @Tag("video")
    void reEncodeVideo_withMp4() {
        Video video = new Video(state, config);
        prepData(DebugPaths.videoTestInputMp4);
        assertDoesNotThrow(() ->
                video.reEncodeVideo(DebugPaths.videoTestInputMp4, "mp4"));
        isSane(state.getGeneralFileTitle());
    }

    @Test
    @Tag("video")
    void reEncodeVideo_withWebm() {
        Video video = new Video(state, config);
        prepData(DebugPaths.videoTestInputWebm);
        assertDoesNotThrow(() ->
                video.reEncodeVideo(DebugPaths.videoTestInputWebm, "webm"));
        isSane(state.getGeneralFileTitle());
    }

    @Test
    @Tag("video")
    void reEncodeVideo_withMov() {
        Video video = new Video(state, config);
        prepData(DebugPaths.videoTestInputMov);
        assertDoesNotThrow(() ->
                video.reEncodeVideo(DebugPaths.videoTestInputMov, "mov"));
        isSane(state.getGeneralFileTitle());
    }

    @Test
    @Tag("video")
    void reEncodeVideo_withMkv() {
        Video video = new Video(state, config);
        prepData(DebugPaths.videoTestInputMkv);
        assertDoesNotThrow(() ->
                video.reEncodeVideo(DebugPaths.videoTestInputMkv, "matroska"));
        isSane(state.getGeneralFileTitle());
    }

    @Test
    void reEncodeVideo_nonExistent() {
        Video video = new Video(state, config);
        assertThrows(NullPointerException.class, () ->
                prepData(tempDir.resolve("none.test")));
        assertThrows(IllegalStateException.class, () ->
                video.reEncodeVideo(tempDir.resolve("none.test"), "test"));
    }

    @Test
    void reEncodeVideo_withMp3() {
        Video video = new Video(state, config);
        assertThrows(CodecDetectionException.class, () ->
                prepData(tempDir.resolve(DebugPaths.audioTestInputMp3)));
        assertThrows(IllegalStateException.class, () ->
                video.reEncodeVideo(DebugPaths.audioTestInputMp3, "mp3"));
    }
}
package dev.n0153.app;

import static org.junit.jupiter.api.Assertions.*;

import dev.n0153.app.exceptions.*;
import nu.pattern.OpenCV;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import ws.schild.jave.EncoderException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Unit test for simple App.
 */
public class AppTest {

    /**,.
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue() {
        assertTrue(true);
    }

    @TempDir
    Path tempDir;
    DisarmConfig config;
    DisarmState state;

    @BeforeEach
    void setUp() {
        OpenCV.loadLocally();
        config = new BuilderConfig()
                .setGeneralOutputPath(tempDir)
                .build();
        state = new DisarmState(config);
    }

    private void prepDataMinimal(Path osTargetPath, boolean logo) {
        try {
            state.setMime(Utils.getFormatFromMime(Utils.getMimeType(osTargetPath)));
            state.setFileType(Utils.getFileType(osTargetPath));
            Utils.getTitle(state, logo);
        } catch (MimeTypeDetectionException | FileTypeDetectionException e) {
            throw new RuntimeException(e);
        }
    }

    private void prepData_forVideo(Path osTargetPath) {
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

    private void prepData_forAudio(Path osTargetPath) {
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

    @Test
    void disarmText_valid() {
        App app = new App(state, config);
        Path input = DebugPaths.textTestLocation;
        prepDataMinimal(input, false);
        assertDoesNotThrow(() -> app.disarmText(input));
        assertTrue(Files.exists(config.getGeneralOutputPath().resolve(state.getGeneralFileTitle()+".txt")));
    }

    @Test
    void disarmText_invalid() {
        App app = new App(state, config);
        Path input = DebugPaths.imageTestInputPng;
        prepDataMinimal(input, false);
        assertThrows(IllegalArgumentException.class, () ->
                app.disarmText(DebugPaths.imageTestInputPng));
    }

    @Test
    void disarmLogo_valid() {
        App app = new App(state, config);
        Path input = DebugPaths.logoLocation;
        prepDataMinimal(input, true);
        assertDoesNotThrow(() -> app.disarmLogo(input));
        if (config.getKeepLogo()) {
            assertTrue(Files.exists(config.getGeneralOutputPath().resolve(state.getLogoTitle())));
        }
    }

    @Test
    void disarmLogo_invalid() {
        App app = new App(state, config);
        Path input = DebugPaths.audioTestInputMp3;
        prepDataMinimal(input, true);
        assertThrows(IllegalArgumentException.class, ()
                -> app.disarmLogo(DebugPaths.audioTestInputMp3));
    }

    @Tag("image")
    @Test
    void disarmImage_noLogoValid() {
        App app = new App(state, config);
        Path input = DebugPaths.imageTestInputPng;
        prepDataMinimal(input, false);
        assertDoesNotThrow(() -> app.disarmImage(input, false));
        assertTrue(Files.exists(config.getGeneralOutputPath().resolve(state.getGeneralFileTitle())));
    }

    @Test
    void disarmImage_noLogoInvalid() {
        App app = new App(state, config);
        Path input = DebugPaths.audioTestInputMp3;
        prepDataMinimal(input, false);
        assertThrows(IllegalArgumentException.class, ()
                -> app.disarmImage(DebugPaths.audioTestInputMp3, false));
    }

    @Tag("video")
    @Test
    void disarmVideo_valid() {
        App app = new App(state, config);
        Path input = DebugPaths.videoTestInputMp4;
        prepData_forVideo(input);
        assertDoesNotThrow(() -> app.disarmVideo(input));
        assertTrue(Files.exists(config.getGeneralOutputPath().resolve(state.getGeneralFileTitle())));
    }

    @Test
    void disarmVideo_invalid() {
        App app = new App(state, config);
        Path input = DebugPaths.audioTestInputMp3;
        assertThrows(NullPointerException.class, () ->
                prepData_forVideo(input));
        assertThrows(ValidationException.class, ()
                -> app.disarmVideo(DebugPaths.audioTestInputMp3));
    }

    @Tag("audio")
    @Test
    void disarmAudio_valid() {
        App app = new App(state, config);
        Path input = DebugPaths.audioTestInputMp3;
        prepData_forAudio(input);
        assertDoesNotThrow(() -> app.disarmAudio(input));
        assertTrue(Files.exists(config.getGeneralOutputPath().resolve(state.getGeneralFileTitle())));
    }

    @Test
    void disarmAudio_invalid() {
        App app = new App(state, config);
        Path input = DebugPaths.videoTestInputMp4;
        prepData_forAudio(input);
        assertThrows(ValidationException.class, ()
                -> app.disarmAudio(DebugPaths.audioTestInputMp3));
    }

    @Tag("audio")
    @Test
    void disarmAudioNative_valid() {
        App app = new App(state, config);
        Path input = DebugPaths.audioTestInputWav;
        prepData_forAudio(input);
        assertDoesNotThrow(() -> app.disarmAudioNative(input, new Audio(state, config)));
        assertTrue(Files.exists(config.getGeneralOutputPath().resolve(state.getGeneralFileTitle())));
    }

    @Test
    void disarmAudioNative_invalid() {
        App app = new App(state, config);
        Path input = DebugPaths.audioTestInputMp3;
        prepData_forAudio(input);
        assertThrows(AudioProcessingException.class, ()
                -> app.disarmAudioNative(input, new Audio(state, config)));
    }

    @Test
    void scaleLogo_valid() {
        App app = new App(state, config);
        Path input = DebugPaths.logoLocation;
        prepDataMinimal(input, true);
        state.setLogo(Imgcodecs.imread(input.toString()));
        assertDoesNotThrow(app::scaleLogo);
        assertTrue(state.hasLogo());
        assertEquals(50, state.getLogo().height());
        assertEquals(50, state.getLogo().width());
    }

    @Test
    void scaleLogo_invalid() {
        App app = new App(state, config);
        Path input = DebugPaths.audioTestInputMp3;
        prepDataMinimal(input, true);
        assertThrows(IllegalArgumentException.class, ()->
                state.setLogo(Imgcodecs.imread(input.toString())));
        assertThrows(IllegalStateException.class, app::scaleLogo);
        assertFalse(state.hasLogo());
    }

    @Test
    void alterLogoTransparency_valid() {
        App app = new App(state, config);
        Path input = DebugPaths.logoLocation;
        Mat logoImage = Imgcodecs.imread(input.toString(), Imgcodecs.IMREAD_UNCHANGED);
        prepDataMinimal(input, true);
        state.setLogo(logoImage);
        assertDoesNotThrow(() -> app.alterLogoTransparency(logoImage, 0.6));
        Mat alpha = new Mat();
        Core.extractChannel(logoImage, alpha, 3);
        Scalar mean = Core.mean(alpha);
        assertTrue(mean.val[0] > 0); // weak assertion
    }

    @Test
    void alterLogoTransparency_invalid() {
        Path input = DebugPaths.audioTestInputMp3;
        prepDataMinimal(input, true);
        assertThrows(IllegalArgumentException.class, () ->
                state.setLogo(Imgcodecs.imread(input.toString(), Imgcodecs.IMREAD_UNCHANGED)));
    }

    @Tag("image")
    @Test
    void disarmSwitch_noLogo_image() {
        App app = new App(state, config);
        Path input = DebugPaths.imageTestInputPng;
        prepDataMinimal(input, false);
        assertDoesNotThrow(() ->
                app.disarmSwitch(input));
        assertTrue(Files.exists(config.getGeneralOutputPath().resolve(state.getGeneralFileTitle())));
    }

    @Tag("video")
    @Test
    void disarmSwitch_noLogo_video() {
        App app = new App(state, config);
        Path input = DebugPaths.videoTestInputMp4;
        prepData_forVideo(input);
        assertDoesNotThrow(() ->
                app.disarmSwitch(input));
        assertTrue(Files.exists(config.getGeneralOutputPath().resolve(state.getGeneralFileTitle())));
    }

    @Tag("audio")
    @Test
    void disarmSwitch_noLogo_audio() {
        App app = new App(state, config);
        Path input = DebugPaths.audioTestInputMp3;
        prepData_forAudio(input);
        assertDoesNotThrow(() ->
                app.disarmSwitch(input));
        assertTrue(Files.exists(config.getGeneralOutputPath().resolve(state.getGeneralFileTitle())));
    }

    @Test
    void disarmSwitch_noLogo_text() {
        App app = new App(state, config);
        Path input = DebugPaths.textTestLocation;
        prepDataMinimal(input, false);
        assertDoesNotThrow(() ->
                app.disarmSwitch(input));
        assertTrue(Files.exists(config.getGeneralOutputPath().resolve(state.getGeneralFileTitle()+".txt")));
    }

    @Test
    void disarmSwitch_noLogo_nonExistent() {
        App app = new App(state, config);
        Path input = tempDir.resolve("none.test");
        assertThrows(NullPointerException.class, () ->
                prepData_forVideo(input));
        assertThrows(IllegalStateException.class, () ->
                app.disarmSwitch(input));
    }

    @Tag("image")
    @Test
    void disarmSwitch_withLogo_image() {
        App app = new App(state, config);
        Path input = DebugPaths.imageTestInputPng;
        prepDataMinimal(input, false);
        assertDoesNotThrow(() ->
                app.disarmSwitch(input, DebugPaths.logoLocation));
        assertTrue(Files.exists(config.getGeneralOutputPath().resolve(state.getGeneralFileTitle())));
    }

    @Tag("video")
    @Test
    void disarmSwitch_withLogo_video() {
        App app = new App(state, config);
        Path input = DebugPaths.videoTestInputMp4;
        prepData_forVideo(input);
        assertDoesNotThrow(() ->
                app.disarmSwitch(input, DebugPaths.logoLocation));
        assertTrue(Files.exists(config.getGeneralOutputPath().resolve(state.getGeneralFileTitle())));
    }

    @Tag("audio")
    @Test
    void disarmSwitch_withLogo_audio() {
        App app = new App(state, config);
        Path input = DebugPaths.audioTestInputMp3;
        prepData_forAudio(input);
        assertDoesNotThrow(() ->
                app.disarmSwitch(input, DebugPaths.logoLocation));
        assertTrue(Files.exists(config.getGeneralOutputPath().resolve(state.getGeneralFileTitle())));
    }

    @Test
    void disarmSwitch_withLogo_text() {
        App app = new App(state, config);
        Path input = DebugPaths.textTestLocation;
        prepDataMinimal(input, false);
        assertDoesNotThrow(() ->
                app.disarmSwitch(input, DebugPaths.logoLocation));
        assertTrue(Files.exists(config.getGeneralOutputPath().resolve(state.getGeneralFileTitle()+".txt")));
    }

    @Test
    void disarmSwitch_withLogo_nonExistent() {
        App app = new App(state, config);
        Path input = tempDir.resolve("none.test");
        assertThrows(NullPointerException.class, () ->
                prepData_forVideo(input));
        assertThrows(IllegalStateException.class, () ->
                app.disarmSwitch(input, DebugPaths.logoLocation));
    }

    @Test
    void prepData_image() {
        App app = new App(state, config);
        assertDoesNotThrow(() -> app.prepData(DebugPaths.imageTestInputPng));
    }

    @Test
    void prepData_audio() {
        App app = new App(state, config);
        assertDoesNotThrow(() -> app.prepData(DebugPaths.audioTestInputMp3));
    }

    @Test
    void prepData_video() {
        App app = new App(state, config);
        assertDoesNotThrow(() -> app.prepData(DebugPaths.videoTestInputMp4));
    }

    @Test
    void prepData_text() {
        App app = new App(state, config);
        assertDoesNotThrow(() -> app.prepData(DebugPaths.textTestLocation));
    }

    @Test
    void prepData_nonExistent() {
        App app = new App(state, config);
        assertThrows(MimeTypeDetectionException.class, () -> app.prepData(tempDir.resolve("none.test")));
    }

    // single
    @Tag("image")
    @Test
    void fileDisarm_singleImage() {
        App app = new App(state, config);
        Path input = DebugPaths.imageTestInputPng;
        assertDoesNotThrow(() -> app.fileDisarm(input));
        assertTrue(Files.exists(config.getGeneralOutputPath().resolve(state.getGeneralFileTitle())));
    }

    @Tag("audio")
    @Test
    void fileDisarm_singleAudio() {
        App app = new App(state, config);
        Path input = DebugPaths.audioTestInputMp3;
        assertDoesNotThrow(() -> app.fileDisarm(input));
        assertTrue(Files.exists(config.getGeneralOutputPath().resolve(state.getGeneralFileTitle())));
    }

    @Tag("video")
    @Test
    void fileDisarm_singleVideo() {
        App app = new App(state, config);
        Path input = DebugPaths.videoTestInputMp4;
        assertDoesNotThrow(() -> app.fileDisarm(input));
        assertTrue(Files.exists(config.getGeneralOutputPath().resolve(state.getGeneralFileTitle())));
    }

    @Test
    void fileDisarm_singleText() {
        App app = new App(state, config);
        Path input = DebugPaths.textTestLocation;
        assertDoesNotThrow(() -> app.fileDisarm(input));
        assertTrue(Files.exists(config.getGeneralOutputPath().resolve(state.getGeneralFileTitle()+".txt")));
    }

    @Test
    void fileDisarm_singleNonExistent() {
        App app = new App(state, config);
        Path input = tempDir.resolve("none.test");
        assertThrows(RuntimeException.class, () ->
                prepDataMinimal(input, false));
        assertThrows(ValidationException.class, () ->
                app.fileDisarm(input));
    }

    // array
    @Tag("image")
    @Test
    void testFileDisarm_arrayImages() {
        App app = new App(state, config);
        Path[] input = {
                DebugPaths.imageTestInputPng,
                DebugPaths.imageTestInputJpeg,
                DebugPaths.imageTestInputWebp
        };
        assertDoesNotThrow(() -> app.fileDisarm(input));
        assertTrue(Files.exists(config.getGeneralOutputPath().resolve(state.getGeneralFileTitle())));
    }

    @Tag("audio")
    @Test
    void testFileDisarm_arrayAudio() {
        App app = new App(state, config);
        Path[] input = {
                DebugPaths.audioTestInputMp3,
                DebugPaths.audioTestInputAu,
                DebugPaths.audioTestInputWav
        };
        assertDoesNotThrow(() -> app.fileDisarm(input));
        assertTrue(Files.exists(config.getGeneralOutputPath().resolve(state.getGeneralFileTitle())));
    }

    @Tag("video")
    @Test
    void testFileDisarm_arrayVideo() {
        App app = new App(state, config);
        Path[] input = {
                DebugPaths.videoTestInputMp4,
                DebugPaths.videoTestInputMkv,
                DebugPaths.videoTestInputWebm
        };
        assertDoesNotThrow(() -> app.fileDisarm(input));
        assertTrue(Files.exists(config.getGeneralOutputPath().resolve(state.getGeneralFileTitle())));
    }

    @Test
    void testFileDisarm_arrayText() {
        App app = new App(state, config);
        Path[] input = {
                DebugPaths.textTestLocation,
                DebugPaths.textTestLocationHTMLPlain,
                DebugPaths.textTestLocationShellPlain
        };
        assertDoesNotThrow(() -> app.fileDisarm(input));
        assertTrue(Files.exists(config.getGeneralOutputPath().resolve(state.getGeneralFileTitle()+".txt")));
    }

    @Test
    void testFileDisarm_arrayNonExistent() {
        App app = new App(state, config);
        Path[] input = {
                tempDir.resolve("none-one.test"),
                tempDir.resolve("none-two.test"),
                tempDir.resolve("none-three.test"),
        };
        for (Path item: input) {
            assertThrows(RuntimeException.class, ()->
                    prepDataMinimal(item, false));
            assertThrows(ValidationException.class, () ->
                    app.fileDisarm(input));
        }
    }

    // single with logo
    @Tag("image")
    @Test
    void fileDisarm_singleWithLogoImage() {
        App app = new App(state, config);
        Path input = DebugPaths.imageTestInputPng;
        assertDoesNotThrow(() -> app.fileDisarm(input, DebugPaths.logoLocation));
        assertTrue(Files.exists(config.getGeneralOutputPath().resolve(state.getGeneralFileTitle())));
    }

    @Tag("audio")
    @Test
    void fileDisarm_singleWithLogoAudio() {
        App app = new App(state, config);
        Path input = DebugPaths.audioTestInputMp3;
        assertDoesNotThrow(() -> app.fileDisarm(input, DebugPaths.logoLocation));
        assertTrue(Files.exists(config.getGeneralOutputPath().resolve(state.getGeneralFileTitle())));
    }

    @Tag("video")
    @Test
    void fileDisarm_singleWithLogoVideo() {
        App app = new App(state, config);
        Path input = DebugPaths.videoTestInputMp4;
        assertDoesNotThrow(() -> app.fileDisarm(input, DebugPaths.logoLocation));
        assertTrue(Files.exists(config.getGeneralOutputPath().resolve(state.getGeneralFileTitle())));
    }

    @Test
    void fileDisarm_singleWithLogoText() {
        App app = new App(state, config);
        Path input = DebugPaths.textTestLocation;
        assertDoesNotThrow(() -> app.fileDisarm(input, DebugPaths.logoLocation));
        assertTrue(Files.exists(config.getGeneralOutputPath().resolve(state.getGeneralFileTitle()+".txt")));
    }

    @Test
    void fileDisarm_singleWithLogoNonExistent() {
        App app = new App(state, config);
        Path input = tempDir.resolve("none.test");
        assertThrows(RuntimeException.class, () ->
                prepDataMinimal(input, false));
        assertThrows(ValidationException.class, () ->
                app.fileDisarm(input, DebugPaths.logoLocation));
    }

    // array with logo
    @Tag("image")
    @Test
    void testFileDisarm_arrayWithLogoImages() {
        App app = new App(state, config);
        Path[] input = {
                DebugPaths.imageTestInputPng,
                DebugPaths.imageTestInputJpeg,
                DebugPaths.imageTestInputWebp
        };
        assertDoesNotThrow(() -> app.fileDisarm(input, DebugPaths.logoLocation));
        assertTrue(Files.exists(config.getGeneralOutputPath().resolve(state.getGeneralFileTitle())));
    }

    @Tag("audio")
    @Test
    void testFileDisarm_arrayWithLogoAudio() {
        App app = new App(state, config);
        Path[] input = {
                DebugPaths.audioTestInputMp3,
                DebugPaths.audioTestInputAu,
                DebugPaths.audioTestInputWav
        };
        assertDoesNotThrow(() -> app.fileDisarm(input, DebugPaths.logoLocation));
        assertTrue(Files.exists(config.getGeneralOutputPath().resolve(state.getGeneralFileTitle())));
    }

    @Tag("video")
    @Test
    void testFileDisarm_arrayWithLogoVideo() {
        App app = new App(state, config);
        Path[] input = {
                DebugPaths.videoTestInputMp4,
                DebugPaths.videoTestInputMkv,
                DebugPaths.videoTestInputWebm
        };
        assertDoesNotThrow(() -> app.fileDisarm(input, DebugPaths.logoLocation));
        assertTrue(Files.exists(config.getGeneralOutputPath().resolve(state.getGeneralFileTitle())));
    }

    @Test
    void testFileDisarm_arrayWithLogoText() {
        App app = new App(state, config);
        Path[] input = {
                DebugPaths.textTestLocation,
                DebugPaths.textTestLocationHTMLPlain,
                DebugPaths.textTestLocationShellPlain
        };
        assertDoesNotThrow(() -> app.fileDisarm(input, DebugPaths.logoLocation));
        assertTrue(Files.exists(config.getGeneralOutputPath().resolve(state.getGeneralFileTitle()+".txt")));
    }

    @Test
    void testFileDisarm_arrayWithLogoNonExistent() {
        App app = new App(state, config);
        Path[] input = {
                tempDir.resolve("none-one.test"),
                tempDir.resolve("none-two.test"),
                tempDir.resolve("none-three.test"),
        };
        for (Path item: input) {
            assertThrows(RuntimeException.class, ()->
                    prepDataMinimal(item, false));
            assertThrows(ValidationException.class, () ->
                    app.fileDisarm(input, DebugPaths.logoLocation));
        }
    }
}

package dev.n0153.app;

import nu.pattern.OpenCV;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class ImageTest {

    @TempDir
    Path tempDir;
    DisarmConfig config;
    DisarmState state;

    @BeforeEach
    void setUp() {
        OpenCV.loadLocally();
        config = new BuilderConfig()
                .setGeneralOutputPath(tempDir)
                .setKeepImage(true)
                .build();
        state = new DisarmState(config);
    }

    private void prepData(Path osTargetPath) {
        try {
            Validator.validatePath(config.getGeneralOutputPath());
            state.setMime(Utils.getFormatFromMime(Utils.getMimeType(osTargetPath)));
            state.setFileType(Utils.getFileType(osTargetPath));
            state.setOsTargetFile(osTargetPath);
            Utils.getTitle(state, false);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void isSane(String title) {
        Path newFile = config.getGeneralOutputPath().resolve(title);
        assertTrue(Files.exists(newFile));
        try {
            assertEquals("image", Utils.getFileType(newFile));
            assertTrue(Files.size(newFile) > 0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void scaleImageToFixedValueTest(Path osTargetPath) {
        int imgWidth = 512;
        int imgHeight = 512;
        Image image = new Image(state, config);
        state.setImage(Imgcodecs.imread(osTargetPath.toString()));
        prepData(osTargetPath);
        assertDoesNotThrow(() ->
                image.scaleImageToFixedValue(state.getImage(), imgWidth, imgHeight));
        isSane(state.getGeneralFileTitle());
        Mat outputImage = Imgcodecs.imread(tempDir.resolve(state.getGeneralFileTitle()).toString());
        assertEquals(imgHeight, outputImage.height());
        assertEquals(imgWidth, outputImage.width());
    }

    @Test
    @Tag("image")
    void scaleImageToFixedValue_withPng() {
        scaleImageToFixedValueTest(DebugPaths.imageTestInputPng);
    }

    @Test
    @Tag("image")
    void scaleImageToFixedValue_withJpeg() {
        scaleImageToFixedValueTest(DebugPaths.imageTestInputJpeg);
    }

    @Test
    @Tag("image")
    void scaleImageToFixedValue_withWebp() {
        scaleImageToFixedValueTest(DebugPaths.imageTestInputWebp);
    }

    @Test
    @Tag("image")
    void scaleImageToFixedValue_nonExistent() {
        int imgWidth = 512;
        int imgHeight = 512;
        Image image = new Image(state, config);
        assertThrows(IllegalArgumentException.class, () ->
                state.setImage(Imgcodecs.imread(tempDir.resolve("none.test").toString())));
        assertThrows(RuntimeException.class, () ->
                prepData(tempDir.resolve("none.test")));
        assertThrows(IllegalStateException.class, () ->
                image.scaleImageToFixedValue(state.getImage(), imgWidth, imgHeight));
    }

    @Test
    @Tag("image")
    void scaleImageToFixedValue_withAudio() {
        int imgWidth = 512;
        int imgHeight = 512;
        Image image = new Image(state, config);
        assertThrows(IllegalArgumentException.class, () ->
                state.setImage(Imgcodecs.imread(DebugPaths.audioTestInputMp3.toString())));
        assertThrows(RuntimeException.class, () ->
                prepData(tempDir.resolve(DebugPaths.audioTestInputMp3)));
        assertThrows(IllegalStateException.class, () ->
                image.scaleImageToFixedValue(state.getImage(), imgWidth, imgHeight));
    }

    private void scaleImageToScaleFactorTest(Path osTargetPath) {
        Image image = new Image(state, config);
        state.setImage(Imgcodecs.imread(osTargetPath.toString()));
        prepData(osTargetPath);
        assertDoesNotThrow(() ->
                image.scaleImageToScaleFactor(state.getImage()));
        isSane(state.getGeneralFileTitle());
        Mat outputImage = Imgcodecs.imread(tempDir.resolve(state.getGeneralFileTitle()).toString());
        assertTrue(outputImage.height() <= config.getImgMaxHeight());
        assertTrue(outputImage.width() <= config.getImgMaxWidth());
    }

    @Test
    @Tag("image")
    void scaleImageToScaleFactor_withPng() {
        scaleImageToScaleFactorTest(DebugPaths.imageTestInputPng);
    }

    @Test
    @Tag("image")
    void scaleImageToScaleFactor_withJpeg() {
        scaleImageToScaleFactorTest(DebugPaths.imageTestInputJpeg);
    }

    @Test
    @Tag("image")
    void scaleImageToScaleFactor_withWebp() {
        scaleImageToScaleFactorTest(DebugPaths.imageTestInputWebp);
    }

    @Test
    @Tag("image")
    void scaleImageToScaleFactor_nonExistent() {
        Image image = new Image(state, config);
        assertThrows(IllegalArgumentException.class, () ->
                state.setImage(Imgcodecs.imread(tempDir.resolve("none.test").toString())));
        assertThrows(RuntimeException.class, () ->
                prepData(tempDir.resolve("none.test")));
        assertThrows(IllegalStateException.class, () ->
                image.scaleImageToScaleFactor(state.getImage()));
    }

    @Test
    @Tag("image")
    void scaleImageToScaleFactor_withMp3() {
        Image image = new Image(state, config);
        assertThrows(IllegalArgumentException.class, () ->
                state.setImage(Imgcodecs.imread(DebugPaths.audioTestInputMp3.toString())));
        assertThrows(RuntimeException.class, () ->
                prepData(tempDir.resolve(DebugPaths.audioTestInputMp3)));
        assertThrows(IllegalStateException.class, () ->
                image.scaleImageToScaleFactor(state.getImage()));
    }

    private void applyWatermarkTest(Path osTargetPath) {
        Image image = new Image(state, config);
        state.setImgX(0);
        state.setImgY(0);
        state.setImage(Imgcodecs.imread(osTargetPath.toString()));
        state.setLogo(Imgcodecs.imread(DebugPaths.logoLocation.toString()));
        prepData(DebugPaths.logoLocation);
        prepData(osTargetPath);
        assertDoesNotThrow(image::applyWatermark);
        isSane(state.getGeneralFileTitle());
    }

    @Test
    @Tag("image")
    void applyWatermark_withPng() {
        applyWatermarkTest(DebugPaths.imageTestInputPng);
    }

    @Test
    @Tag("image")
    void applyWatermark_withJpeg() {
        applyWatermarkTest(DebugPaths.imageTestInputJpeg);
    }

    @Test
    @Tag("image")
    void applyWatermark_withWebp() {
        applyWatermarkTest(DebugPaths.imageTestInputWebp);
    }

    @Test
    @Tag("image")
    void applyWatermark_nonExistent() {
        Image image = new Image(state, config);
        state.setImgX(0);
        state.setImgX(0);
        assertThrows(IllegalArgumentException.class, () ->
                state.setImage(Imgcodecs.imread(tempDir.resolve("none.test").toString())));
        state.setLogo(Imgcodecs.imread(DebugPaths.logoLocation.toString()));
        prepData(DebugPaths.logoLocation);
        assertThrows(RuntimeException.class, () ->
                prepData(tempDir.resolve("none.test")));
        assertThrows(IllegalStateException.class, image::applyWatermark);
    }

    @Test
    @Tag("image")
    void applyWatermark_withMp3() {
        Image image = new Image(state, config);
        state.setImgX(0);
        state.setImgX(0);
        assertThrows(IllegalArgumentException.class, () ->
                state.setImage(Imgcodecs.imread(DebugPaths.audioTestInputMp3.toString())));
        state.setLogo(Imgcodecs.imread(DebugPaths.logoLocation.toString()));
        prepData(DebugPaths.logoLocation);
        prepData(DebugPaths.audioTestInputMp3);
        assertThrows(IllegalStateException.class, image::applyWatermark);
    }

    private void imageSetup(Path osTargetPath) {
        state.setImage(Imgcodecs.imread(osTargetPath.toString()));
        state.setLogo(Imgcodecs.imread(DebugPaths.logoLocation.toString()));
        prepData(DebugPaths.logoLocation);
        prepData(osTargetPath);
    }

    private void applyWatermarkTopLeftTest(Path osTargetPath) {
        Image image = new Image(state, config);
        imageSetup(osTargetPath);
        assertDoesNotThrow(image::applyWatermarkTopLeft);
        isSane(state.getGeneralFileTitle());
    }

    private void applyWatermarkTopRightTest(Path osTargetPath) {
        Image image = new Image(state, config);
        imageSetup(osTargetPath);
        assertDoesNotThrow(image::applyWatermarkTopRight);
        isSane(state.getGeneralFileTitle());
    }

    private void applyWatermarkBottomLeftTest(Path osTargetPath) {
        Image image = new Image(state, config);
        imageSetup(osTargetPath);
        assertDoesNotThrow(image::applyWatermarkBottomLeft);
        isSane(state.getGeneralFileTitle());
    }

    private void applyWatermarkBottomRightTest(Path osTargetPath) {
        Image image = new Image(state, config);
        imageSetup(osTargetPath);
        assertDoesNotThrow(image::applyWatermarkBottomRight);
        isSane(state.getGeneralFileTitle());
    }

    private void applyWatermarkAtRandomPositionTest(Path osTargetPath) {
        Image image = new Image(state, config);
        imageSetup(osTargetPath);
        assertDoesNotThrow(image::applyWatermarkAtRandomPosition);
        isSane(state.getGeneralFileTitle());
    }

    @Test
    @Tag("image")
    void applyWatermarkTopLeft_withPng() {
        applyWatermarkTopLeftTest(DebugPaths.imageTestInputPng);
    }

    @Test
    @Tag("image")
    void applyWatermarkTopLeft_withJpeg() {
        applyWatermarkTopLeftTest(DebugPaths.imageTestInputJpeg);
    }

    @Test
    @Tag("image")
    void applyWatermarkTopLeft_withWebp() {
        applyWatermarkTopLeftTest(DebugPaths.imageTestInputWebp);
    }

    private void applyWatermark_nonExistentTest() {
        assertThrows(IllegalArgumentException.class, () ->
                state.setImage(Imgcodecs.imread(tempDir.resolve("none.test").toString())));
        state.setLogo(Imgcodecs.imread(DebugPaths.logoLocation.toString()));
        prepData(DebugPaths.logoLocation);
        assertThrows(RuntimeException.class, () ->
                prepData(tempDir.resolve("none.test")));
    }

    private void applyWatermark_withMp3Test() {
        assertThrows(IllegalArgumentException.class, () ->
                state.setImage(Imgcodecs.imread(DebugPaths.audioTestInputMp3.toString())));
        state.setLogo(Imgcodecs.imread(DebugPaths.logoLocation.toString()));
        prepData(DebugPaths.logoLocation);
        prepData(DebugPaths.audioTestInputMp3);
    }

    @Test
    @Tag("image")
    void applyWatermarkTopLeft_nonExistent() {
        Image image = new Image(state, config);
        applyWatermark_nonExistentTest();
        assertThrows(IllegalStateException.class, image::applyWatermarkTopLeft);
    }

    @Test
    @Tag("image")
    void applyWatermarkTopLeft_withMp3() {
        Image image = new Image(state, config);
        state.setImgX(0);
        state.setImgX(0);
        applyWatermark_withMp3Test();
        assertThrows(IllegalStateException.class, image::applyWatermarkTopLeft);
    }

    @Test
    @Tag("image")
    void applyWatermarkTopRight_withPng() {
        applyWatermarkTopRightTest(DebugPaths.imageTestInputPng);
    }

    @Test
    @Tag("image")
    void applyWatermarkTopRight_withJpeg() {
        applyWatermarkTopRightTest(DebugPaths.imageTestInputJpeg);
    }

    @Test
    @Tag("image")
    void applyWatermarkTopRight_withWebp() {
        applyWatermarkTopRightTest(DebugPaths.imageTestInputWebp);
    }

    @Test
    @Tag("image")
    void applyWatermarkTopRight_withMp3() {
        Image image = new Image(state, config);
        applyWatermark_withMp3Test();
        assertThrows(IllegalStateException.class, image::applyWatermarkTopRight);
    }

    @Test
    @Tag("image")
    void applyWatermarkTopRight_nonExistent() {
        Image image = new Image(state, config);
        applyWatermark_nonExistentTest();
        assertThrows(IllegalStateException.class, image::applyWatermarkTopRight);
    }

    @Test
    @Tag("image")
    void applyWatermarkBottomLeft_withPng() {
        applyWatermarkBottomLeftTest(DebugPaths.imageTestInputPng);
    }

    @Test
    @Tag("image")
    void applyWatermarkBottomLeft_withJpeg() {
        applyWatermarkBottomLeftTest(DebugPaths.imageTestInputJpeg);
    }

    @Test
    @Tag("image")
    void applyWatermarkBottomLeft_withWebp() {
        applyWatermarkBottomLeftTest(DebugPaths.imageTestInputWebp);
    }

    @Test
    @Tag("image")
    void applyWatermarkBottomLeft_withMp3() {
        Image image = new Image(state, config);
        applyWatermark_withMp3Test();
        assertThrows(IllegalStateException.class, image::applyWatermarkBottomLeft);
    }

    @Test
    @Tag("image")
    void applyWatermarkBottomLeft_nonExistent() {
        Image image = new Image(state, config);
        applyWatermark_nonExistentTest();
        assertThrows(IllegalStateException.class, image::applyWatermarkBottomLeft);
    }

    @Test
    @Tag("image")
    void applyWatermarkBottomRight_withPng() {
        applyWatermarkBottomRightTest(DebugPaths.imageTestInputPng);
    }

    @Test
    @Tag("image")
    void applyWatermarkBottomRight_withJpeg() {
        applyWatermarkBottomRightTest(DebugPaths.imageTestInputJpeg);
    }

    @Test
    @Tag("image")
    void applyWatermarkBottomRight_withWebp() {
        applyWatermarkBottomRightTest(DebugPaths.imageTestInputWebp);
    }

    @Test
    @Tag("image")
    void applyWatermarkBottomRight_withMp3() {
        Image image = new Image(state, config);
        applyWatermark_withMp3Test();
        assertThrows(IllegalStateException.class, image::applyWatermarkBottomRight);
    }

    @Test
    @Tag("image")
    void applyWatermarkBottomRight_nonExistent() {
        Image image = new Image(state, config);
        applyWatermark_nonExistentTest();
        assertThrows(IllegalStateException.class, image::applyWatermarkBottomRight);
    }

    @Test
    @Tag("image")
    void applyWatermarkAtRandomPosition_withPng() {
        applyWatermarkAtRandomPositionTest(DebugPaths.imageTestInputPng);
    }

    @Test
    @Tag("image")
    void applyWatermarkAtRandomPosition_withJpeg() {
        applyWatermarkAtRandomPositionTest(DebugPaths.imageTestInputJpeg);
    }

    @Test
    @Tag("image")
    void applyWatermarkAtRandomPosition_withWebp() {
        applyWatermarkAtRandomPositionTest(DebugPaths.imageTestInputWebp);
    }

    @Test
    @Tag("image")
    void applyWatermarkAtRandomPosition_withMp3() {
        Image image = new Image(state, config);
        applyWatermark_withMp3Test();
        assertThrows(IllegalStateException.class, image::applyWatermarkAtRandomPosition);
    }

    @Test
    @Tag("image")
    void applyWatermarkAtRandomPosition_nonExistent() {
        Image image = new Image(state, config);
        applyWatermark_nonExistentTest();
        assertThrows(IllegalStateException.class, image::applyWatermarkAtRandomPosition);
    }
}
package dev.n0153.app;

import dev.n0153.app.exceptions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import ws.schild.jave.EncoderException;
import ws.schild.jave.InputFormatException;
import ws.schild.jave.info.VideoSize;

import javax.sound.sampled.AudioFileFormat;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class UtilsTest {

    DisarmConfig config = new BuilderConfig().build();
    DisarmState state = new DisarmState(config);
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

    @Test
    void getTitle() {
        state.setFileType("image");
        state.setMime("png");

        Utils.getTitle(state, false);

        String title = state.getGeneralFileTitle();
        assertNotNull(title);
        assertFalse(title.isEmpty());
        assertTrue(title.matches("image_\\d+_\\d+_\\d+_\\d+_\\d+_\\d+_\\d+_\\d+\\.png"),
                "Unexpected title format: " + title);
    }

    @Test
    void getTitle_withEmptyTypeAndMime() {
        assertThrows(IllegalStateException.class, () -> Utils.getTitle(state, false));
    }

    @Test
    void getTitle_withEmptyType() {
        state.setMime("png");
        assertThrows(IllegalStateException.class, () -> Utils.getTitle(state, false));
    }

    @Test
    void getTitle_withEmptyMime() {
        state.setFileType("image");
        assertThrows(IllegalStateException.class, () -> Utils.getTitle(state, false));
    }

    @Test
    void renameFileExisting() {
        Path dummyFile = createDummyFile("text");
        assertDoesNotThrow(() -> Utils.renameFile(dummyFile, "new_title.text"));
        assertDoesNotThrow(() -> Files.exists(tempDir.resolve("new_title.text")));
    }

    @Test
    void renameFileNonExisting() {
        assertThrows(FileRenameException.class, () ->
                Utils.renameFile(tempDir.resolve("none.test"), "new_title.text"));
    }

    private void getFileTypeTest(String fileType, String mime){
        try {
            Path dummyFile = createDummyFile(mime);
            state.setFileType(Utils.getFileType(dummyFile.toAbsolutePath()));

            assertNotNull(state.getFileType());
            assertFalse(state.getFileType().isEmpty());
            assertEquals(fileType, state.getFileType());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void getMimeTypeTest(String mime, String expectedMime) {
        try {
            Path dummyFile = createDummyFile(mime);
            state.setMime(Utils.getMimeType(dummyFile));
            assertNotNull(state.getMime());
            assertFalse(state.getMime().isEmpty());
            assertEquals(expectedMime, state.getMime());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void getMimeTypeTestUnsupported(String format, Class<? extends Throwable> exception) {
        Path dummyFile = createDummyFile(format);
        assertThrows(exception, () ->
                state.setMime(Utils.getMimeType(dummyFile)));
    }


    private void getFileTypeTestUnsupported(String mime) {
        Path dummyFile = createDummyFile(mime);
        assertThrows(IllegalArgumentException.class, () ->
                state.setFileType(Utils.getFileType(dummyFile.toAbsolutePath())));
    }

    private void getAudioTypeTest(String mime, AudioFileFormat.Type audioFormat) {
        Path dummyFile = createDummyFile(mime);
        AudioFileFormat.Type audioType = Utils.getAudioType(dummyFile);
        assertNotNull(audioType);
        assertEquals(audioFormat, audioType);
    }

    @Test
    void getFileType_withImage() {
        getFileTypeTest("image", "png");
    }

    @Test
    void getFileType_withVideo() {
        getFileTypeTest("video", "mp4");
    }

    @Test
    void getFileType_withAudio() {
        getFileTypeTest("audio", "mp3");
    }

    @Test
    void getFileType_withText() {
        getFileTypeTest("text", "text");
    }

    @Test
    void getFileType_withUnsupportedPdf() {
        getFileTypeTestUnsupported("pdf");
    }

    @Test
    void getFileType_withUnsupportedZip() {
        getFileTypeTestUnsupported("zip");
    }

    @Test
    void getFileType_withUnsupportedDocx() {
        getFileTypeTestUnsupported("docx");
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
            AudioFileFormat.Type audioType = Utils.getAudioType(tempFileSnd);
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
            AudioFileFormat.Type audioType = Utils.getAudioType(tempFileAifc);
            assertNotNull(audioType);
            assertEquals(AudioFileFormat.Type.AIFF, audioType);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void getMimeType_forPng() {
        getMimeTypeTest("png", "png");
    }

    @Test
    void getMimeType_forJpeg() {
        getMimeTypeTest("jpeg", "jpeg");
    }

    @Test
    void getMimeType_forWebp() {
        getMimeTypeTest("webp", "webp");
    }

    @Test
    void getMimeType_forMp4() {
        getMimeTypeTest("mp4", "mp4");
    }

    @Test
    void getMimeType_forMkv() {
        getMimeTypeTest("mkv", "x-matroska");
    }

    @Test
    void getMimeType_forWebm() {
        getMimeTypeTest("webm", "webm");
    }

    @Test
    void getMimeType_forMov() {
        getMimeTypeTest("mov", "quicktime");
    }

    @Test
    void getMimeType_forMp3() {
        getMimeTypeTest("mp3", "mpeg");
    }

    @Test
    void getMimeType_forOgg() {
        getMimeTypeTest("ogg", "ogg");
    }

    @Test
    void getMimeType_forFlac() {
        getMimeTypeTest("flac", "flac");
    }

    @Test
    void getMimeType_forWav() {
        getMimeTypeTest("wav", "x-wav");
    }

    @Test
    void getMimeType_forAu() {
        getMimeTypeTest("au", "basic");
    }

    @Test
    void getMimeType_forAiff() {
        getMimeTypeTest("aiff", "x-aiff");
    }

    @Test
        // Unsupported files should pass anyway as set/get mimeType doesn't validate against whitelist.
        // Whitelist validation occurs in Validator class and is called in the App class, not Utils.
    void getMimeType_unsupportedGif() {
        getMimeTypeTest("gif", "gif");
    }

    @Test
    void getMimeType_unsupportedHtml() {
        getMimeTypeTest("html", "html");
    }

    @Test
    void getMimeType_unsupportedPhp() {
        getMimeTypeTest("php", "plain");
    }

    @Test
    void getMimeType_unsupportedExe() {
        assertThrows(IllegalArgumentException.class, () ->getMimeTypeTest("exe", "exe"));
    }

    private void getFormatFromMimeTest(String mime, String expectedMime) {
        try {
            Path dummyFile = createDummyFile(mime);
            state.setMime(Utils.getFormatFromMime(Utils.getMimeType(dummyFile)));
            assertNotNull(state.getMime());
            assertFalse(state.getMime().isEmpty());
            assertEquals(expectedMime, state.getMime());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void getFormatFromMimeTest_forPng() {
        getFormatFromMimeTest("png", "png");
    }

    @Test
    void getFormatFromMimeTest_forJpeg() {
        getFormatFromMimeTest("jpeg", "jpeg");
    }

    @Test
    void getFormatFromMimeTest_forWebp() {
        getFormatFromMimeTest("webp", "webp");
    }

    @Test
    void getFormatFromMimeTest_forMp4() {
        getFormatFromMimeTest("mp4", "mp4");
    }

    @Test
    void getFormatFromMimeTest_forMkv() {
        getFormatFromMimeTest("mkv", "matroska");
    }

    @Test
    void getFormatFromMimeTest_forWebm() {
        getFormatFromMimeTest("webm", "webm");
    }

    @Test
    void getFormatFromMimeTest_forMov() {
        getFormatFromMimeTest("mov", "mov");
    }

    @Test
    void getFormatFromMimeTest_forMp3() {
        getFormatFromMimeTest("mp3", "mp3");
    }

    @Test
    void getFormatFromMimeTest_forOgg() {
        getFormatFromMimeTest("ogg", "ogg");
    }

    @Test
    void getFormatFromMimeTest_forFlac() {
        getFormatFromMimeTest("flac", "flac");
    }

    @Test
    void getFormatFromMimeTest_forWav() {
        getFormatFromMimeTest("wav", "wav");
    }

    @Test
    void getFormatFromMimeTest_forAu() {
        getFormatFromMimeTest("au", "au");
    }

    @Test
    void getFormatFromMimeTest_forAiff() {
        getFormatFromMimeTest("aiff", "aiff");
    }

    private void isImageTest(String mime) {
        try {
            Path dummyFile = createDummyFile(mime);
            assertNotNull(dummyFile);
            String fileType = Utils.getFileType(dummyFile);
            if (fileType.equals("image")) {
                assertTrue(Utils.isImage(dummyFile));
            } else {
                assertFalse(Utils.isImage(dummyFile));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void isImage_withImage() {
        isImageTest("png");
    }

    @Test
    void isImage_withVideo() {
        isImageTest("mp4");
    }

    @Test
    void isImage_withAudio() {
        isImageTest("mp3");
    }

    @Test
    void isImage_withText() {
        isImageTest("text");
    }

    private void isAudioTest(String mime) {
        try {
            Path dummyFile = createDummyFile(mime);
            assertNotNull(dummyFile);
            String fileType = Utils.getFileType(dummyFile);
            if (fileType.equals("audio")) {
                assertTrue(Utils.isAudio(dummyFile));
            } else {
                assertFalse(Utils.isAudio(dummyFile));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void isAudio_withImage() {
        isAudioTest("png");
    }

    @Test
    void isAudio_withAudio() {
        isAudioTest("mp3");
    }

    @Test
    void isAudio_withVideo() {
        isAudioTest("mp4");
    }

    @Test
    void isAudio_withText() {
        isAudioTest("text");
    }

    private void isVideoTest(String mime) {
        try {
            Path dummyFile = createDummyFile(mime);
            assertNotNull(dummyFile);
            String fileType = Utils.getFileType(dummyFile);
            if (fileType.equals("video")) {
                assertTrue(Utils.isVideo(dummyFile));
            } else {
                assertFalse(Utils.isVideo(dummyFile));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void isVideo_withImage() {
        isVideoTest("png");
    }

    @Test
    void isVideo_withAudio() {
        isVideoTest("mp3");
    }

    @Test
    void isVideo_withVideo() {
        isVideoTest("mp4");
    }

    @Test
    void isVideo_withText() {
        isVideoTest("text");
    }

    private void isTextTest(String mime) {
        try {
            Path dummyFile = createDummyFile(mime);
            assertNotNull(dummyFile);
            String fileType = Utils.getFileType(dummyFile);
            if (fileType.equals("text")) {
                assertTrue(Utils.isText(dummyFile));
            } else {
                assertFalse(Utils.isText(dummyFile));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void isText_withImage() {
        isTextTest("png");
    }

    @Test
    void isText_withAudio() {
        isTextTest("mp3");
    }

    @Test
    void isText_withVideo() {
        isTextTest("mp4");
    }

    @Test
    void isText_withText() {
        isTextTest("text");
    }

    @Test
    void fileDispose() {
        Path dummy = createDummyFile("mp4");
        assertDoesNotThrow(() -> Utils.fileDispose(dummy));
    }

    @Test
    void fileDispose_nonExistent() {
        assertThrows(FileDeletionException.class, () -> Utils.fileDispose(tempDir.resolve("none.txt")));
    }

    @Test
    void getSize() {
        Path dummyFile = createDummyFile("mp4");
        assertDoesNotThrow(() -> Utils.getSize(dummyFile));
        assertTrue(Utils.getSize(dummyFile) > -1);
    }

    @Test
    void getSize_nonExistent() {
        assertEquals(0, Utils.getSize(tempDir.resolve("none.test")));
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

    @Test
    void getCodec_forAudio() {
        String codec = assertDoesNotThrow(() -> Utils.getCodec(DebugPaths.audioTestInputMp3, "audio"));
        assertNotNull(codec);
        assertFalse(codec.isEmpty());
    }

    @Test
    void getCodec_forVideo() {
        String codec = assertDoesNotThrow(() -> Utils.getCodec(DebugPaths.videoTestInputMp4, "video"));
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
                DebugPaths.audioTestInputMp3, "audio", config));
        assertNotEquals(0, bitrate);
    }

    @Test
    void getBitrate_forVideo() {
        int bitrate = assertDoesNotThrow(() -> Utils.getBitrate(
                DebugPaths.videoTestInputMp4, "video", config));
        assertNotEquals(0, bitrate);
    }

    @Test
    void getBitrate_noneExistent() {
        assertThrows(EncoderException.class, () -> Utils.getBitrate(
                tempDir.resolve("none.mp3"), "audio", config));
    }

    @Test
    void getBitrate_withEmptyMedia() {
        try {
            assertEquals(-1, Utils.getBitrate(
                    DebugPaths.audioTestInputMp3, "", config));
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
    void getSamplingRate_withVideo() {
        int sampling = assertDoesNotThrow(() ->
                Utils.getSamplingRate(DebugPaths.videoTestInputMp4));
        assertTrue(sampling > 0);
    }

    @Test
    void getSamplingRate_withImage() {
        assertThrows(NullPointerException.class, () ->
                Utils.getSamplingRate(DebugPaths.imageTestInputPng));
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
         assertThrows(InputFormatException.class, () ->
                Utils.getAudioChannels(DebugPaths.videoTestOutputMp4));
    }

    @Test
    void getAudioChannels_nonExistent() {
         assertThrows(EncoderException.class, () ->
                Utils.getAudioChannels(tempDir.resolve("none.test")));
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
    };
}
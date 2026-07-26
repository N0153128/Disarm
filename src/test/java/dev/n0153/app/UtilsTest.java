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

    @Test
    void getTitle() {
        context.setFileType("image");
        context.setMimeType("png");

        String title = Utils.getTitle(createDummyFile(context.getMimeType()), context.getMimeType(), false);

        assertNotNull(title);
        assertFalse(title.isEmpty());
        assertTrue(title.matches("image_\\d+_\\d+_\\d+_\\d+_\\d+_\\d+_\\d+_\\d+\\.png"),
                "Unexpected title format: " + title);
    }

    @Test
    void getTitle_withEmptyTypeAndMime() {
        assertThrows(NullPointerException.class, () ->
                Utils.getTitle(createDummyFile(""),
                        "",
                        false));
    }

    @Test
    void getTitle_withEmptyType() {
        context.setMimeType("png");
        assertThrows(IllegalStateException.class, () ->
                Utils.getTitle(createDummyFile(context.getMimeType()),
                        "",
                        false));
    }

    @Test
    void getTitle_withEmptyMime() {
        context.setFileType("image");
        assertThrows(NullPointerException.class, () ->
                Utils.getTitle(createDummyFile(""),
                        context.getFileType(),
                        false));
    }

    @Test
    void renameFileExisting() {
        Path dummyFile = createDummyFile("text");
        assertDoesNotThrow(() -> Utils.renameFile(dummyFile, "new_title.text"));
        assertDoesNotThrow(() -> Files.exists(tempDir.resolve("new_title.text")));
    }

    @Test
    void renameFileNonExisting() {
        assertThrows(IOException.class, () ->
                Utils.renameFile(tempDir.resolve("none.test"), "new_title.text"));
    }

    private void getFileTypeTest(String fileType, String mime){
        try {
            Path dummyFile = createDummyFile(mime);
            context.setFileType(Utils.getFileType(dummyFile.toAbsolutePath()));

            assertNotNull(context.getFileType());
            assertFalse(context.getFileType().isEmpty());
            assertEquals(fileType, context.getFileType());
        } catch (DisarmException e) {
            throw new RuntimeException(e);
        }
    }

    private void getMimeTypeTest(String mime, String expectedMime) {
        try {
            Path dummyFile = createDummyFile(mime);
            context.setMimeType(Utils.getMimeType(dummyFile));
            assertNotNull(context.getMimeType());
            assertFalse(context.getMimeType().isEmpty());
            assertEquals(expectedMime, context.getMimeType());
        } catch (DisarmException e) {
            throw new RuntimeException(e);
        }
    }

    private void getMimeTypeTestUnsupported(String format, Class<? extends Throwable> exception) {
        Path dummyFile = createDummyFile(format);
        assertThrows(exception, () ->
                context.setMimeType(Utils.getMimeType(dummyFile)));
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
    void getFileType_withPdf() {
        getFileTypeTest("application","pdf");
    }

    @Test
    void getFileType_withZip() {
        getFileTypeTest("application", "zip");
    }

    @Test
    void getFileType_withDocx() {
        getFileTypeTest("application", "docx");
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

    @Test
    void fileDispose() {
        Path dummy = createDummyFile("mp4");
        assertDoesNotThrow(() -> Utils.fileDispose(dummy));
    }

    @Test
    void fileDispose_nonExistent() {
        assertThrows(IOException.class, () -> Utils.fileDispose(tempDir.resolve("none.txt")));
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
}
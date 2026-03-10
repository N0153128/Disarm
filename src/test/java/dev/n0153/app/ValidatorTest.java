package dev.n0153.app;

import dev.n0153.app.exceptions.*;
import nu.pattern.OpenCV;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class ValidatorTest {

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

    private Path createDummyFile() {
        try {
            Path tempFile = tempDir.resolve("test.txt");
            Files.write(tempFile, new byte[] {(byte) 0x68, 0x65, 0x6C, 0x6F});
            return tempFile;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void validatePath_validPath() {
        assertTrue(Validator.validatePath(DebugPaths.imageTestInputPng));
    }

    @Test
    void validatePath_SingleDot() {
        assertThrows(UnsafePathException.class, ()->
                Validator.validatePath(Paths.get(tempDir.toString()+"./")));
    }

    @Test
    void validatePath_DoubleDot() {
        assertThrows(UnsafePathException.class, ()->
                Validator.validatePath(Paths.get(tempDir.toString()+"../")));
    }

    @Test
    void validatePath_symlink() {
        try {
            Path realFile = createDummyFile();
            Path symlink = tempDir.resolve("symlink.txt");
            Files.createSymbolicLink(symlink, realFile);
            assertThrows(UnsafePathException.class, ()->
                    Validator.validatePath(symlink));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void validatePath_nonExistent() {
        assertThrows(UnsafePathException.class, ()->
                Validator.validatePath(tempDir.resolve("none.test")));
    }

    @Test
    void checkMimeWhiteList_ImagePng() {
        assertTrue(Validator.checkMimeWhiteList("image", "png"));
    }

    @Test
    void checkMimeWhiteList_ImageJpeg() {
        assertTrue(Validator.checkMimeWhiteList("image", "jpeg"));
    }

    @Test
    void checkMimeWhiteList_ImageJpg() {
        assertTrue(Validator.checkMimeWhiteList("image", "jpg"));
    }

    @Test
    void checkMimeWhiteList_ImageWebp() {
        assertTrue(Validator.checkMimeWhiteList("image", "webp"));
    }

    @Test
    void checkMimeWhiteList_TextText() {
        assertTrue(Validator.checkMimeWhiteList("text", "text"));
    }

    @Test
    void checkMimeWhiteList_TextLog() {
        assertTrue(Validator.checkMimeWhiteList("text", "log"));
    }

    @Test
    void checkMimeWhiteList_TextPlain() {
        assertTrue(Validator.checkMimeWhiteList("text", "plain"));
    }

    @Test
    void checkMimeWhiteList_VideoMp4() {
        assertTrue(Validator.checkMimeWhiteList("video", "mp4"));
    }

    @Test
    void checkMimeWhiteList_VideoMkv() {
        assertTrue(Validator.checkMimeWhiteList("video", "mkv"));
    }

    @Test
    void checkMimeWhiteList_VideoWebm() {
        assertTrue(Validator.checkMimeWhiteList("video", "webm"));
    }

    @Test
    void checkMimeWhiteList_VideoMov() {
        assertTrue(Validator.checkMimeWhiteList("video", "mov"));
    }

    @Test
    void checkMimeWhiteList_AudioMp3() {
        assertTrue(Validator.checkMimeWhiteList("audio", "mp3"));
    }

    @Test
    void checkMimeWhiteList_AudioOgg() {
        assertTrue(Validator.checkMimeWhiteList("audio", "ogg"));
    }

    @Test
    void checkMimeWhiteList_AudioFlac() {
        assertTrue(Validator.checkMimeWhiteList("audio", "flac"));
    }

    @Test
    void checkMimeWhiteList_AudioWav() {
        assertTrue(Validator.checkMimeWhiteList("audio", "wav"));
    }

    @Test
    void checkMimeWhiteList_AudioAu() {
        assertTrue(Validator.checkMimeWhiteList("audio", "au"));
    }

    @Test
    void checkMimeWhiteList_AudioAiff() {
        assertTrue(Validator.checkMimeWhiteList("audio", "aiff"));
    }

    @Test
    void checkMimeWhiteList_LogoPng() {
        assertTrue(Validator.checkMimeWhiteList("logo", "png"));
    }

    @Test
    void checkMimeWhiteList_DocDocx() {
        assertThrows(UnsupportedFileTypeException.class, ()->
                Validator.checkMimeWhiteList("application", "docx"));
    }

    @Test
    void checkMimeWhiteList_nonExistentNonExistent() {
        assertThrows(UnsupportedFileTypeException.class, ()->
                Validator.checkMimeWhiteList("none", "none"));
    }

    @Test
    void checkAudioBitrate_Mp3() {
        assertTrue(Validator.checkAudioBitrate("mp3", 320));
    }

    @Test
    void checkAudioBitrate_Flac() {
        assertTrue(Validator.checkAudioBitrate("flac", 1500000));
    }

    @Test
    void checkAudioBitrate_Ogg() {
        assertTrue(Validator.checkAudioBitrate("ogg", 500000));
    }

    @Test
    void checkAudioBitrate_Wav() {
        assertTrue(Validator.checkAudioBitrate("wav", 4608000));
    }

    @Test
    void checkAudioBitrate_Au() {
        assertTrue(Validator.checkAudioBitrate("au", 1536000));
    }

    @Test
    void checkAudioBitrate_Aiff() {
        assertTrue(Validator.checkAudioBitrate("aiff", 4608000));
    }

    @Test
    void checkAudioBitrate_Mp3TooHigh() {
        assertFalse(Validator.checkAudioBitrate("mp3", 320001));
    }

    @Test
    void checkAudioBitrate_Mp4() {
        assertFalse(Validator.checkAudioBitrate("mp4", 320001));

    }

    @Test
    void checkAudioBitrate_nonExistent() {
        assertFalse(Validator.checkAudioBitrate("none", 101));

    }

    @Test
    void checkVideoBitrate_Mp4() {
        assertTrue(Validator.checkVideoBitrate("mp4", 15000000));
    }

    @Test
    void checkVideoBitrate_Mov() {
        assertTrue(Validator.checkVideoBitrate("mov", 15000000));
    }

    @Test
    void checkVideoBitrate_Mkv() {
        assertTrue(Validator.checkVideoBitrate("mkv", 20000000));
    }

    @Test
    void checkVideoBitrate_Webm() {
        assertTrue(Validator.checkVideoBitrate("webm", 12000000));
    }

    @Test
    void checkVideoBitrate_Mp4TooHigh() {
        assertFalse(Validator.checkVideoBitrate("mp4", 15000001));
    }

    @Test
    void checkVideoBitrate_Png() {
        assertFalse(Validator.checkVideoBitrate("png", 320001));

    }

    @Test
    void checkVideoBitrate_nonExistent() {
        assertFalse(Validator.checkVideoBitrate("none", 101));

    }

    @Test
    void checkAudioBitrateForVideo_Mp4() {
        assertTrue(Validator.checkAudioBitrateForVideo("mp4", 512_000));
    }

    @Test
    void checkAudioBitrateForVideo_Mov() {
        assertTrue(Validator.checkAudioBitrateForVideo("mov", 1_536_000));
    }

    @Test
    void checkAudioBitrateForVideo_Mkv() {
        assertTrue(Validator.checkAudioBitrateForVideo("mkv", 1_536_000));
    }

    @Test
    void checkAudioBitrateForVideo_Webm() {
        assertTrue(Validator.checkAudioBitrateForVideo("webm", 512_000));
    }

    @Test
    void checkAudioBitrateForVideo_Mp4TooHigh() {
        assertFalse(Validator.checkAudioBitrateForVideo("mp4", 15000001));
    }

    @Test
    void checkAudioBitrateForVideo_Png() {
        assertFalse(Validator.checkAudioBitrateForVideo("png", 320001));

    }

    @Test
    void checkAudioBitrateForVideo_nonExistent() {
        assertFalse(Validator.checkAudioBitrateForVideo("none", 101));
    }

    @Test
    void checkAudioSampleRateForVideo_Mp4() {
        assertTrue(Validator.checkAudioSampleRateForVideo("mp4", 48000));
    }

    @Test
    void checkAudioSampleRateForVideo_Mov() {
        assertTrue(Validator.checkAudioSampleRateForVideo("mov", 48000));
    }

    @Test
    void checkAudioSampleRateForVideo_Mkv() {
        assertTrue(Validator.checkAudioSampleRateForVideo("mkv", 48000));
    }

    @Test
    void checkAudioSampleRateForVideo_Webm() {
        assertTrue(Validator.checkAudioSampleRateForVideo("webm", 48000));
    }

    @Test
    void checkAudioSampleRateForVideo_Mp4TooHigh() {
        assertFalse(Validator.checkAudioSampleRateForVideo("mp4", 15000001));
    }

    @Test
    void checkAudioSampleRateForVideo_Png() {
        assertFalse(Validator.checkAudioSampleRateForVideo("png", 320001));

    }

    @Test
    void checkAudioSampleRateForVideo_nonExistent() {
        assertFalse(Validator.checkAudioSampleRateForVideo("none", 101));
    }

    @Test
    void checkAudioSampleRate_Mp3() {
        assertTrue(Validator.checkAudioSampleRate("mp3", 48000));
    }

    @Test
    void checkAudioSampleRate_Flac() {
        assertTrue(Validator.checkAudioSampleRate("flac", 192000));
    }

    @Test
    void checkAudioSampleRate_Ogg() {
        assertTrue(Validator.checkAudioSampleRate("ogg", 48000));
    }

    @Test
    void checkAudioSampleRate_Wav() {
        assertTrue(Validator.checkAudioSampleRate("wav", 96000));
    }

    @Test
    void checkAudioSampleRate_Au() {
        assertTrue(Validator.checkAudioSampleRate("au", 48000));
    }

    @Test
    void checkAudioSampleRate_Aiff() {
        assertTrue(Validator.checkAudioSampleRate("aiff", 96000));
    }

    @Test
    void checkAudioSampleRate_Mp3TooHigh() {
        assertFalse(Validator.checkAudioSampleRate("mp3", 320001));
    }

    @Test
    void checkAudioSampleRate_nonExistent() {
        assertFalse(Validator.checkAudioSampleRate("none", 101));
    }

    @Test
    void checkVideoCodecWhiteList_Mp4HDot264() {
        assertTrue(Validator.checkVideoCodecWhiteList("mp4", "h.264"));
    }

    @Test
    void checkVideoCodecWhiteList_Mp4Hevc() {
        assertTrue(Validator.checkVideoCodecWhiteList("mp4", "hevc"));
    }

    @Test
    void checkVideoCodecWhiteList_Mp4Av1() {
        assertTrue(Validator.checkVideoCodecWhiteList("mp4", "av1"));
    }

    @Test
    void checkVideoCodecWhiteList_Mp4Mpeg4() {
        assertTrue(Validator.checkVideoCodecWhiteList("mp4", "mpeg-4"));
    }

    @Test
    void checkVideoCodecWhiteList_Mp4Vp9() {
        assertTrue(Validator.checkVideoCodecWhiteList("mp4", "vp9"));
    }

    @Test
    void checkVideoCodecWhiteList_Mp4H265() {
        assertTrue(Validator.checkVideoCodecWhiteList("mp4", "h.265"));
    }

    @Test
    void checkVideoCodecWhiteList_Mp4H264() {
        assertTrue(Validator.checkVideoCodecWhiteList("mp4", "h264"));
    }

    @Test
    void checkVideoCodecWhiteList_MkvHDot264() {
        assertTrue(Validator.checkVideoCodecWhiteList("mkv", "h.264"));
    }

    @Test
    void checkVideoCodecWhiteList_MkvHevc() {
        assertTrue(Validator.checkVideoCodecWhiteList("mkv", "hevc"));
    }

    @Test
    void checkVideoCodecWhiteList_MkvVp8() {
        assertTrue(Validator.checkVideoCodecWhiteList("mkv", "vp8"));
    }

    @Test
    void checkVideoCodecWhiteList_MkvVp9() {
        assertTrue(Validator.checkVideoCodecWhiteList("mkv", "vp9"));
    }

    @Test
    void checkVideoCodecWhiteList_MkvAv1() {
        assertTrue(Validator.checkVideoCodecWhiteList("mkv", "av1"));
    }

    @Test
    void checkVideoCodecWhiteList_MkvMpeg4() {
        assertTrue(Validator.checkVideoCodecWhiteList("mkv", "mpeg-4"));
    }

    @Test
    void checkVideoCodecWhiteList_MkvTheora() {
        assertTrue(Validator.checkVideoCodecWhiteList("mkv", "theora"));
    }

    @Test
    void checkVideoCodecWhiteList_MkvFfv1() {
        assertTrue(Validator.checkVideoCodecWhiteList("mkv", "ffv1"));
    }

    @Test
    void checkVideoCodecWhiteList_MkvMpeg2() {
        assertTrue(Validator.checkVideoCodecWhiteList("mkv", "mpeg-2"));
    }

    @Test
    void checkVideoCodecWhiteList_MkvVc1() {
        assertTrue(Validator.checkVideoCodecWhiteList("mkv", "vc-1"));
    }

    @Test
    void checkVideoCodecWhiteList_MkvHDot265() {
        assertTrue(Validator.checkVideoCodecWhiteList("mkv", "h.265"));
    }

    @Test
    void checkVideoCodecWhiteList_MkvH264() {
        assertTrue(Validator.checkVideoCodecWhiteList("mkv", "h264"));
    }

    @Test
    void checkVideoCodecWhiteList_WebmVp8() {
        assertTrue(Validator.checkVideoCodecWhiteList("webm", "vp8"));
    }

    @Test
    void checkVideoCodecWhiteList_WebmVp9() {
        assertTrue(Validator.checkVideoCodecWhiteList("webm", "vp9"));
    }

    @Test
    void checkVideoCodecWhiteList_WebmAv1() {
        assertTrue(Validator.checkVideoCodecWhiteList("webm", "av1"));
    }

    @Test
    void checkVideoCodecWhiteList_MovHDot264() {
        assertTrue(Validator.checkVideoCodecWhiteList("mov", "h.264"));
    }

    @Test
    void checkVideoCodecWhiteList_MovHevc() {
        assertTrue(Validator.checkVideoCodecWhiteList("mov", "hevc"));
    }

    @Test
    void checkVideoCodecWhiteList_MovProres() {
        assertTrue(Validator.checkVideoCodecWhiteList("mov", "prores"));
    }

    @Test
    void checkVideoCodecWhiteList_MovAv1() {
        assertTrue(Validator.checkVideoCodecWhiteList("mov", "av1"));
    }

    @Test
    void checkVideoCodecWhiteList_MovMjpeg() {
        assertTrue(Validator.checkVideoCodecWhiteList("mov", "mjpeg"));
    }

    @Test
    void checkVideoCodecWhiteList_MovMpeg4() {
        assertTrue(Validator.checkVideoCodecWhiteList("mov", "mpeg-4"));
    }

    @Test
    void checkVideoCodecWhiteList_MovDnxhd() {
        assertTrue(Validator.checkVideoCodecWhiteList("mov", "dnxhd"));
    }

    @Test
    void checkVideoCodecWhiteList_MovHDot265() {
        assertTrue(Validator.checkVideoCodecWhiteList("mov", "h.265"));
    }

    @Test
    void checkVideoCodecWhiteList_MovH264() {
        assertTrue(Validator.checkVideoCodecWhiteList("mov", "h264"));
    }

    @Test
    void checkVideoCodecWhiteList_PngHDot264() {
        assertThrows(UnsupportedFileTypeException.class, () ->
                Validator.checkVideoCodecWhiteList("png", "h.264"));
    }

    @Test
    void checkVideoCodecWhiteList_nonExistentH264() {
        assertThrows(UnsupportedFileTypeException.class, () ->
                Validator.checkVideoCodecWhiteList("none", "none"));    }

    @Test
    void checkAudioCodecWhiteListForVideo_Mp4Aac() {
        assertTrue(Validator.checkAudioCodecWhiteListForVideo("mp4", "aac"));
    }

    @Test
    void checkAudioCodecWhiteListForVideo_Mp4Mp3() {
        assertTrue(Validator.checkAudioCodecWhiteListForVideo("mp4", "mp3"));
    }

    @Test
    void checkAudioCodecWhiteListForVideo_Mp4Ac3() {
        assertTrue(Validator.checkAudioCodecWhiteListForVideo("mp4", "ac3"));
    }

    @Test
    void checkAudioCodecWhiteListForVideo_Mp4Eac3() {
        assertTrue(Validator.checkAudioCodecWhiteListForVideo("mp4", "e-ac3"));
    }

    @Test
    void checkAudioCodecWhiteListForVideo_Mp4Flac() {
        assertTrue(Validator.checkAudioCodecWhiteListForVideo("mp4", "flac"));
    }

    @Test
    void checkAudioCodecWhiteListForVideo_Mp4Opus() {
        assertTrue(Validator.checkAudioCodecWhiteListForVideo("mp4", "opus"));
    }

    @Test
    void checkAudioCodecWhiteListForVideo_Mp4Alac() {
        assertTrue(Validator.checkAudioCodecWhiteListForVideo("mp4", "alac"));
    }

    @Test
    void checkAudioCodecWhiteListForVideo_MkvAac() {
        assertTrue(Validator.checkAudioCodecWhiteListForVideo("mkv", "aac"));
    }

    @Test
    void checkAudioCodecWhiteListForVideo_MkvMp3() {
        assertTrue(Validator.checkAudioCodecWhiteListForVideo("mkv", "mp3"));
    }

    @Test
    void checkAudioCodecWhiteListForVideo_MkvFlac() {
        assertTrue(Validator.checkAudioCodecWhiteListForVideo("mkv", "flac"));
    }

    @Test
    void checkAudioCodecWhiteListForVideo_MkvOpus() {
        assertTrue(Validator.checkAudioCodecWhiteListForVideo("mkv", "opus"));
    }

    @Test
    void checkAudioCodecWhiteListForVideo_MkvVorbis() {
        assertTrue(Validator.checkAudioCodecWhiteListForVideo("mkv", "vorbis"));
    }

    @Test
    void checkAudioCodecWhiteListForVideo_MkvAc3() {
        assertTrue(Validator.checkAudioCodecWhiteListForVideo("mkv", "ac3"));
    }

    @Test
    void checkAudioCodecWhiteListForVideo_MkvDts() {
        assertTrue(Validator.checkAudioCodecWhiteListForVideo("mkv", "dts"));
    }

    @Test
    void checkAudioCodecWhiteListForVideo_MkvPcm() {
        assertTrue(Validator.checkAudioCodecWhiteListForVideo("mkv", "pcm"));
    }

    @Test
    void checkAudioCodecWhiteListForVideo_MkvTruehd() {
        assertTrue(Validator.checkAudioCodecWhiteListForVideo("mkv", "truehd"));
    }

    @Test
    void checkAudioCodecWhiteListForVideo_MkvEac3() {
        assertTrue(Validator.checkAudioCodecWhiteListForVideo("mkv", "eac3"));
    }

    @Test
    void checkAudioCodecWhiteListForVideo_WebmVorbis() {
        assertTrue(Validator.checkAudioCodecWhiteListForVideo("webm", "vorbis"));
    }

    @Test
    void checkAudioCodecWhiteListForVideo_WebmOpus() {
        assertTrue(Validator.checkAudioCodecWhiteListForVideo("webm", "opus"));
    }

    @Test
    void checkAudioCodecWhiteListForVideo_WebmLibvorbis() {
        assertTrue(Validator.checkAudioCodecWhiteListForVideo("webm", "libvorbis"));
    }

    @Test
    void checkAudioCodecWhiteListForVideo_MovAac() {
        assertTrue(Validator.checkAudioCodecWhiteListForVideo("mov", "aac"));
    }

    @Test
    void checkAudioCodecWhiteListForVideo_MovMp3() {
        assertTrue(Validator.checkAudioCodecWhiteListForVideo("mov", "mp3"));
    }

    @Test
    void checkAudioCodecWhiteListForVideo_MovPcm() {
        assertTrue(Validator.checkAudioCodecWhiteListForVideo("mov", "pcm"));
    }

    @Test
    void checkAudioCodecWhiteListForVideo_MovAlac() {
        assertTrue(Validator.checkAudioCodecWhiteListForVideo("mov", "alac"));
    }

    @Test
    void checkAudioCodecWhiteListForVideo_MovAc3() {
        assertTrue(Validator.checkAudioCodecWhiteListForVideo("mov", "ac3"));
    }

    @Test
    void checkAudioCodecWhiteListForVideo_PngMp3() {
        assertThrows(UnsupportedFileTypeException.class, () ->
                Validator.checkAudioCodecWhiteListForVideo("png", "mp3"));
    }

    @Test
    void checkAudioCodecWhiteListForVideo_nonExistent() {
        assertThrows(UnsupportedFileTypeException.class, () ->
                Validator.checkAudioCodecWhiteListForVideo("none", "none"));
    }

    @Test
    void checkAudioCodecWhiteList_Mp3Mp3() {
        assertTrue(Validator.checkAudioCodecWhiteList("mp3", "mp3"));
    }

    @Test
    void checkAudioCodecWhiteList_OggVorbis() {
        assertTrue(Validator.checkAudioCodecWhiteList("ogg", "vorbis"));
    }

    @Test
    void checkAudioCodecWhiteList_OggOpus() {
        assertTrue(Validator.checkAudioCodecWhiteList("ogg", "opus"));
    }

    @Test
    void checkAudioCodecWhiteList_OggFlac() {
        assertTrue(Validator.checkAudioCodecWhiteList("ogg", "flac"));
    }

    @Test
    void checkAudioCodecWhiteList_OggSpeex() {
        assertTrue(Validator.checkAudioCodecWhiteList("ogg", "speex"));
    }

    @Test
    void checkAudioCodecWhiteList_OggLibvorbis() {
        assertTrue(Validator.checkAudioCodecWhiteList("ogg", "libvorbis"));
    }

    @Test
    void checkAudioCodecWhiteList_FlacFlac() {
        assertTrue(Validator.checkAudioCodecWhiteList("flac", "flac"));
    }

    @Test
    void checkAudioCodecWhiteList_WavPcm() {
        assertTrue(Validator.checkAudioCodecWhiteList("wav", "pcm"));
    }

    @Test
    void checkAudioCodecWhiteList_WavAdpcm() {
        assertTrue(Validator.checkAudioCodecWhiteList("wav", "adpcm"));
    }

    @Test
    void checkAudioCodecWhiteList_WavMp3() {
        assertTrue(Validator.checkAudioCodecWhiteList("wav", "mp3"));
    }

    @Test
    void checkAudioCodecWhiteList_WavAlaw() {
        assertTrue(Validator.checkAudioCodecWhiteList("wav", "a-law"));
    }

    @Test
    void checkAudioCodecWhiteList_WavMulaw() {
        assertTrue(Validator.checkAudioCodecWhiteList("wav", "μ-law"));
    }

    @Test
    void checkAudioCodecWhiteList_WavGsm() {
        assertTrue(Validator.checkAudioCodecWhiteList("wav", "gsm"));
    }

    @Test
    void checkAudioCodecWhiteList_WavUlaw() {
        assertTrue(Validator.checkAudioCodecWhiteList("wav", "u-law"));
    }

    @Test
    void checkAudioCodecWhiteList_WavPcmu8() {
        assertTrue(Validator.checkAudioCodecWhiteList("wav", "pcm_u8"));
    }

    @Test
    void checkAudioCodecWhiteList_AuPcm() {
        assertTrue(Validator.checkAudioCodecWhiteList("au", "pcm"));
    }

    @Test
    void checkAudioCodecWhiteList_AuMulaw() {
        assertTrue(Validator.checkAudioCodecWhiteList("au", "μ-law"));
    }

    @Test
    void checkAudioCodecWhiteList_AuUlaw() {
        assertTrue(Validator.checkAudioCodecWhiteList("au", "u-law"));
    }

    @Test
    void checkAudioCodecWhiteList_AuPcms16be() {
        assertTrue(Validator.checkAudioCodecWhiteList("au", "pcm_s16be"));
    }

    @Test
    void checkAudioCodecWhiteList_AiffPcm() {
        assertTrue(Validator.checkAudioCodecWhiteList("aiff", "pcm"));
    }

    @Test
    void checkAudioCodecWhiteList_AiffMulaw() {
        assertTrue(Validator.checkAudioCodecWhiteList("aiff", "μ-law"));
    }

    @Test
    void checkAudioCodecWhiteList_AiffAlaw() {
        assertTrue(Validator.checkAudioCodecWhiteList("aiff", "a-law"));
    }

    @Test
    void checkAudioCodecWhiteList_AiffPcms16be() {
        assertTrue(Validator.checkAudioCodecWhiteList("aiff", "pcm_s16be"));
    }

    @Test
    void checkAudioCodecWhiteList_AiffImaadpcm() {
        assertTrue(Validator.checkAudioCodecWhiteList("aiff", "ima adpcm"));
    }

    @Test
    void checkAudioCodecWhiteList_AiffUlaw() {
        assertTrue(Validator.checkAudioCodecWhiteList("aiff", "u-law"));
    }

    @Test
    void checkAudioCodecWhiteList_PngAlaw() {
        assertThrows(UnsupportedFileTypeException.class, () ->
                Validator.checkAudioCodecWhiteList("png", "a-law"));
    }

    @Test
    void checkAudioCodecWhiteList_nonExistent() {
        assertThrows(UnsupportedFileTypeException.class, () ->
                Validator.checkAudioCodecWhiteList("none", "none"));
    }

    @Test
    void validateAudioDuration_valid() {
        assertTrue(Validator.validateAudioDuration(DebugPaths.audioTestInputMp3, config));
    }

    @Test
    void validateAudioDuration_invalid() {
        Path sixMinutes = Paths.get("resources/six-minutes.mp3");
        assertThrows(ValidationException.class, () ->
                Validator.validateAudioDuration(sixMinutes, config));
    }

    @Test
    void validateVideoDuration_valid() {
        assertTrue(Validator.validateVideoDuration(DebugPaths.videoTestInputMp4, config));
    }

    @Test
    void validateVideoDuration_invalid() {
        Path sixMinutes = Paths.get("resources/test-video/six-minutes.mp4");
        assertFalse(Validator.validateVideoDuration(sixMinutes, config));
    }

    //Text was sufficiently tested in TextTest
    @Test
    void validateEncoding_valid() {
        Path testBomUtf8 = Paths.get("resources/test-text/test-bom-utf8.txt");
        try{
            state.setTextFile(Files.readString(testBomUtf8));
            assertTrue(Validator.validateEncoding(new Text(state, config), state));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Disabled("validateEncoding cannot be reliably tested until DisarmState supports raw byte storage")
    @Test
    void validateEncoding_invalid() {
        Path testNoBom = Paths.get("resources/test-text/test-invalid-utf8.txt");
        try{
            state.setTextFile(Files.readString(testNoBom, StandardCharsets.ISO_8859_1));
            assertFalse(Validator.validateEncoding(new Text(state, config), state));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void isInputReadable_valid() {
        assertTrue(Validator.isInputReadable(DebugPaths.videoTestInputMp4));
    }

    @Test
    void isInputReadable_invalid() {
        try {
            Path tempUnreadable = Files.createTempDirectory("test");
            tempUnreadable.toFile().setReadable(false);
            assertFalse(Validator.isInputReadable(tempUnreadable));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void isOutputPathWritable_valid() {
        assertTrue(Validator.isOutputPathWritable(config.getGeneralOutputPath()));
    }

    @Test
    void isOutputPathWritable_invalid() {
        try {
            Path tempUnwritable = Files.createTempDirectory("test");
            tempUnwritable.toFile().setWritable(false);
            assertFalse(Validator.isOutputPathWritable(tempUnwritable));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }    }

    @Test
    void ensureSizeLimit_image() {
        try {
            state.setFileType("image");
            state.setMime(Utils.getMimeType(DebugPaths.imageTestInputPng));
            assertTrue(Validator.ensureSizeLimit(DebugPaths.imageTestInputPng, config, state));
        } catch (MimeTypeDetectionException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void ensureSizeLimit_audio() {
        try {
            state.setFileType("audio");
            state.setMime(Utils.getFormatFromMime(Utils.getMimeType(DebugPaths.audioTestInputMp3)));
            assertTrue(Validator.ensureSizeLimit(DebugPaths.audioTestInputMp3, config, state));
        } catch (MimeTypeDetectionException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void ensureSizeLimit_video() {
        try {
            state.setFileType("video");
            state.setMime(Utils.getMimeType(DebugPaths.videoTestInputMp4));
            assertTrue(Validator.ensureSizeLimit(DebugPaths.videoTestInputMp4, config, state));
        } catch (MimeTypeDetectionException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void ensureSizeLimit_text() {
        try {
            state.setFileType("text");
            state.setMime(Utils.getMimeType(DebugPaths.textTestLocation));
            assertTrue(Validator.ensureSizeLimit(DebugPaths.textTestLocation, config, state));
        } catch (MimeTypeDetectionException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void ensureSizeLimit_invalid() {
        try {
            state.setFileType("image");
            Path osTargetFilePath = Paths.get("resources/test-video/six-minutes.mp4");
            state.setMime(Utils.getMimeType(osTargetFilePath));
            assertFalse(Validator.ensureSizeLimit(osTargetFilePath, config, state));
        } catch (MimeTypeDetectionException e) {
            throw new RuntimeException(e);
        }
    }
}
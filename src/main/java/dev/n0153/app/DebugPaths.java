package dev.n0153.app;

import java.nio.file.Path;
import java.nio.file.Paths;

public class DebugPaths {
    public static final Path imageOutputPath = Paths.get("resources/test-img/output/");
    public static final Path logoOutputPath = Paths.get("resources/test-img/output/logo/");
    public static final Path audioOutputPath = Paths.get("resources/test-audio/output/");
    public static final Path videoOutputPath = Paths.get("resources/test-video/output/");
    public static final Path textOutputPath = Paths.get("resources/test-text/output/");

    //TEMP TESTING VARS - IMAGE
    public static final Path imageTestInputPng = Paths.get("resources/test-img/test-png.png");
    public static final Path imageTestInputGif = Paths.get("resources/test-img/test-gif.gif");
    public static final Path imageTestInputWebp = Paths.get("resources/test-img/test-webp.webp");
    public static final Path imageTestInputJpeg = Paths.get("resources/test-img/test-jpeg.jpeg");
    public static final Path imageTestInputTiff = Paths.get("resources/test-img/test-tiff.tiff");
    public static final Path imageTestInputSmallPng = Paths.get("resources/test-img/test-small-png.png");
    public static final Path logoLocation = Paths.get("resources/test-img/logo.png");

    //TEMP TESTING VARS - TEXT
    public static final Path textTestLocationHTMLPlain = Paths.get("resources/test-text/test-html.txt");
    public static final Path textTestLocationShellPlain = Paths.get("resources/test-text/test-shell.txt");
    public static final Path textTestLocation = Paths.get("resources/test-text/test.txt");
    public static final Path textTestLocationHTML = Paths.get("resources/test-text/test-html.html");
    public static final Path textTestLocationShell = Paths.get("resources/test-text/test-shell.sh");

    //TEMP TESTING VARS - AUDIO
    public static final Path audioTestInputWav = Paths.get("resources/test-audio/test-wav.wav");
    public static final Path audioTestInputAif = Paths.get("resources/test-audio/test-aif.aif");
    public static final Path audioTestInputAifc = Paths.get("resources/test-audio/test-aifc.aifc");
    public static final Path audioTestInputAiff = Paths.get("resources/test-audio/test-aiff.aiff");
    public static final Path audioTestInputAu = Paths.get("resources/test-audio/test-au.au");
    public static final Path audioTestInputFlac = Paths.get("resources/test-audio/test-flac.flac");
    public static final Path audioTestInputMp3 = Paths.get("resources/test-audio/test-mp3.mp3");
    public static final Path audioTestInputOgg = Paths.get("resources/test-audio/test-ogg.ogg");

    public static final Path videoTestInputMp4 = Paths.get("resources/test-video/test-mp4.mp4");
    public static final Path videoTestInputMov = Paths.get("resources/test-video/test-mov.mov");
    public static final Path videoTestInputMkv = Paths.get("resources/test-video/test-mkv.mkv");
    public static final Path videoTestInputWebm = Paths.get("resources/test-video/test-webm.webm");
    public static final Path videoTestOutputMp4 = Paths.get("resources/test-video/output/");

}

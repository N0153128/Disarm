package dev.n0153.app.debug.scripts;

import dev.n0153.app.*;
import dev.n0153.app.exceptions.DisarmException;
import dev.n0153.app.exceptions.FileTypeDetectionException;
import dev.n0153.app.exceptions.MimeTypeDetectionException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Path;

public class DebugGeneral {
    private final DisarmState state;
    private final DisarmConfig config;

    public DebugGeneral(DisarmState state, DisarmConfig config) {
        this.state = state;
        this.config = config;
    }
    private final Logger logger = LogManager.getLogger(DebugGeneral.class);


    public void pathingCheck(Path path) {
        boolean validation = Validator.validatePath(path);
        logger.debug("is the path valid? {}", validation);
    }

    public void isEverythingWorkingQuestionMark(String ignore) {
        try {
            DebugAudio audio = new DebugAudio(state, config);
            DebugImage img = new DebugImage(state, config);
            DebugText text = new DebugText(state, config);
            DebugVideo video = new DebugVideo(state, config);

            switch (ignore) {
                case "ignore audio": {
                    img.runImages();
                    text.runText();
                    video.runVideo();
                    break;
                }
                case "ignore images": {
                    audio.runAudio();
                    video.runVideo();
                    text.runText();
                    break;
                }
                case "ignore text": {
                    audio.runAudio();
                    video.runVideo();
                    img.runImages();
                    break;
                }
                case "ignore video": {
                    text.runText();
                    img.runImages();
                    audio.runAudio();
                }
                default:
                    text.runText();
                    img.runImages();
                    audio.runAudio();
                    video.runVideo();
                    break;
            }
        } catch (FileTypeDetectionException e) {
            logger.error("Failed to detect file type");
            throw new DisarmException("Failed to detect file type in Debug General", e);
        } catch (MimeTypeDetectionException e) {
            throw new RuntimeException(e);
        }

    }

    public void checkImages() {
        DebugImage images = new DebugImage(state, config);
        images.runImages();
    }

    public void checkAudio() {
        DebugAudio audio = new DebugAudio(state, config);
        try {
            audio.runAudio();
        } catch (FileTypeDetectionException e) {
            throw new DisarmException("Failed to detect file type in Debug General", e);
        } catch (MimeTypeDetectionException e) {
            throw new RuntimeException(e);
        }
    }

    public void checkText() {
        DebugText text = new DebugText(state, config);
        text.runText();
    }

    public void checkVideo() {
        DebugVideo video = new DebugVideo(state, config);
        video.runVideo();
    }

    public void benchmarkImages() {
        DebugImage image = new DebugImage(state, config);
        try {
            image.runHundredImage();
        } catch (IOException e) {
            throw new DisarmException("Failed to benchmark images in Debug General", e);
        }
    }

    public void benchmarkAudio() {
        DebugAudio audio = new DebugAudio(state, config);
        audio.runHundredAll();
    }

    public void benchmarkAudioOgg() {
        DebugAudio audio = new DebugAudio(state, config);
        audio.runHundredOgg();
    }

    public void benchmarkAudioFlac() {
        DebugAudio audio = new DebugAudio(state, config);
        audio.runHundredFlac();
    }

    public void benchmarkAudioAu() {
        DebugAudio audio = new DebugAudio(state, config);
        audio.runHundredAu();
    }

    public void benchmarkAudioAifc() {
        DebugAudio audio = new DebugAudio(state, config);
        audio.runHundredAifc();
    }

    public void benchmarkAudioWav() {
        DebugAudio audio = new DebugAudio(state, config);
        audio.runHundredWav();
    }

    public void benchmarkAudioAif() {
        DebugAudio audio = new DebugAudio(state, config);
        audio.runHundredAif();
    }

    public void benchmarkAudioMp3() {
        DebugAudio audio = new DebugAudio(state, config);
        audio.runHundredMp3();
    }

    public void benchmarkAudioNativeWav() {
        DebugAudio audio = new DebugAudio(state, config);
        audio.runHundredNative(DebugPaths.audioTestInputWav);
    }

    public void benchmarkAudioNativeAif() {
        DebugAudio audio = new DebugAudio(state, config);
        audio.runHundredNative(DebugPaths.audioTestInputAif);
    }

    public void benchmarkAudioNativeAu() {
        DebugAudio audio = new DebugAudio(state, config);
        audio.runHundredNative(DebugPaths.audioTestInputAu);
    }

    public void benchmarkText() {
        DebugText text = new DebugText(state, config);
        try {
            text.runHundredTextAll();
        } catch (IOException e) {
            throw new DisarmException("Failed to benchmark text in Debug General", e);
        }
    }

    public void benchmarkTextHTML() {
        DebugText text = new DebugText(state, config);
        try {
            text.runHundredTextHTML();
        } catch (IOException e) {
            throw new DisarmException("Failed to benchmark text in Debug General", e);
        }
    }

    public void benchmarkTextShell() {
        DebugText text = new DebugText(state, config);
        try {
            text.runHundredTextShell();
        } catch (IOException e) {
            throw new DisarmException("Failed to benchmark text in Debug General", e);
        }
    }

    public void benchmarkTextGeneral() {
        DebugText text = new DebugText(state, config);
        try {
            text.runHundredTextGeneral();
        } catch (IOException e) {
            throw new DisarmException("Failed to benchmark text in Debug General", e);
        }
    }

    public void benchmarkVideo() {
        DebugVideo video = new DebugVideo(state, config);
        video.runHundredAll();
    }

    public void benchmarkVideoMp4() {
        DebugVideo video = new DebugVideo(state, config);
        video.runHundredMp4();
    }

    public void benchmarkVideoMov() {
        DebugVideo video = new DebugVideo(state, config);
        video.runHundredMov();
    }

    public void benchmarkVideoMkv() {
        DebugVideo video = new DebugVideo(state, config);
        video.runHundredMkv();
    }

    public void benchmarkVideoWebm() {
        DebugVideo video = new DebugVideo(state, config);
        video.runHundredWebm();
    }
}

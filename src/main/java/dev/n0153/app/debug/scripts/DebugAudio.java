package dev.n0153.app.debug.scripts;

import dev.n0153.app.*;
import dev.n0153.app.exceptions.DisarmException;
import dev.n0153.app.exceptions.FileTypeDetectionException;
import dev.n0153.app.exceptions.MimeTypeDetectionException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;

public class DebugAudio {
    private final DisarmState state;
    private final DisarmConfig config;

    public DebugAudio(DisarmState state, DisarmConfig config) {
        this.state = state;
        this.config = config;
    }

    private final Logger logger = LogManager.getLogger(DebugAudio.class);

    public void runAudio() throws FileTypeDetectionException, MimeTypeDetectionException {
        Audio Audio = new Audio(state, config);
        App App = new App(state, config);
        logger.debug("running reEncodeWav");
        App.prepData(DebugPaths.audioTestInputWav);
        Audio.reEncodeWav(DebugPaths.audioTestInputWav);
        logger.debug("done");

        logger.debug("running reEncodeAu");
        App.prepData(DebugPaths.audioTestInputAu);
        Audio.reEncodeAu(DebugPaths.audioTestInputAu);
        logger.debug("done");

        logger.debug("running reEncodeAif");
        App.prepData(DebugPaths.audioTestInputAif);
        Audio.reEncodeAif(DebugPaths.audioTestInputAif);
        logger.debug("done");

        logger.debug("running reEncodeAif - aiff");
        App.prepData(DebugPaths.audioTestInputAiff);
        Audio.reEncodeAif(DebugPaths.audioTestInputAiff);
        logger.debug("done");

        logger.debug("running reEncodeAifc");
        App.prepData(DebugPaths.audioTestInputAifc);
        Audio.reEncodeAif(DebugPaths.audioTestInputAifc);
        logger.debug("done");

        logger.debug("running reEncodeMp3");
        App.prepData(DebugPaths.audioTestInputMp3);
        Audio.reEncodeMp3(DebugPaths.audioTestInputMp3);
        logger.debug("done");

        logger.debug("running reEncodeOgg");
        App.prepData(DebugPaths.audioTestInputOgg);
        Audio.reEncodeOgg(DebugPaths.audioTestInputOgg);
        logger.debug("done");

        logger.debug("running reEncodeFlac");
        App.prepData(DebugPaths.audioTestInputFlac);
        Audio.reEncodeFlac(DebugPaths.audioTestInputFlac);
        logger.debug("done");

        logger.debug("running reEncodeAudioNative with Wav");
        App.prepData(DebugPaths.audioTestInputWav);
        runAudioNative(DebugPaths.audioTestInputWav);
        logger.debug("done");

        logger.debug("running reEncodeAudioNative with Aif");
        App.prepData(DebugPaths.audioTestInputAif);
        runAudioNative(DebugPaths.audioTestInputAif);
        logger.debug("done");

        logger.debug("running reEncodeAudioNative with Au");
        App.prepData(DebugPaths.audioTestInputAu);
        runAudioNative(DebugPaths.audioTestInputAu);
        logger.debug("done");


        logger.debug("===");
        logger.debug("Running automated check");
        App.fileDisarm(DebugPaths.audioTestInputAiff);
        App.fileDisarm(DebugPaths.audioTestInputMp3);
        App.fileDisarm(DebugPaths.audioTestInputWav);
        App.fileDisarm(DebugPaths.audioTestInputOgg);
        App.fileDisarm(DebugPaths.audioTestInputFlac);
        App.fileDisarm(DebugPaths.audioTestInputAu);
        App.fileDisarm(DebugPaths.audioTestInputAifc);

        logger.debug("done");
    }

    public void runHundredAudio(Path osTargetPath, String format) {
        App App = new App(state, config);
        long[] mid = new long[100];
        for (int i = 0; i<100; i++){
            mid[i] = App.fileDisarm(osTargetPath);
        }
        long avg, sum = 0;
        for (long i : mid){
            sum+=i;
        }
        avg = sum/mid.length;
        logger.debug("average: {}:{}", avg, format);
    }

    public long runAudioNative(Path osTargetPath) throws FileTypeDetectionException {
        Audio Audio = new Audio(state, config);
        App app = new App(state, config);
        LocalDateTime start = LocalDateTime.now();
        logger.debug("running native implementation: {}", osTargetPath);
        logger.debug("NOTE! only WAV, AIFF, AU formats are supported");

        try {
            app.prepData(osTargetPath);
        } catch (MimeTypeDetectionException e) {
            throw new DisarmException("failed to prep data in Debug Audio");
        }
        Utils.getTitle(state, false);
        try {
            Audio.reEncodeAudioNative(osTargetPath);
        } catch (UnsupportedAudioFileException e) {
            throw new RuntimeException(e);
        }

        LocalDateTime finish = LocalDateTime.now();
        Duration duration = Duration.between(start, finish);
        logger.debug("Native implementation done in: " +duration.toMillis());
        return duration.toMillis();
    }

    public void runHundredAll() {
        runHundredMp3();
        runHundredAif();
        runHundredWav();
        runHundredAu();
        runHundredFlac();
        runHundredOgg();
        runHundredAifc();
    }

    public void runHundredMp3() {
        try {
            runHundredAudio(DebugPaths.audioTestInputMp3, "mp3");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void runHundredAif() {
        try {
            runHundredAudio(DebugPaths.audioTestInputAif, "aif");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void runHundredWav() {
        try {
            runHundredAudio(DebugPaths.audioTestInputWav, "wav");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void runHundredAu() {
        try {
            runHundredAudio(DebugPaths.audioTestInputAu, "au");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void runHundredFlac() {
        try {
            runHundredAudio(DebugPaths.audioTestInputFlac, "flac");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void runHundredOgg() {
        try {
            runHundredAudio(DebugPaths.audioTestInputOgg, "ogg");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void runHundredAifc() {
        try {
            runHundredAudio(DebugPaths.audioTestInputAifc, "aifc");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void runHundredNative(Path osTargetPath) {
        long[] mid = new long[100];
        for (int i = 0; i<100; i++){
            try {
                mid[i] = runAudioNative(osTargetPath);
            } catch (FileTypeDetectionException e) {
                throw new DisarmException("Failed to run native audio re-encoding in Debug Audio");
            }
        }
        long avg, sum = 0;
        for (long i : mid){
            sum+=i;
        }
        avg = sum/mid.length;
        logger.debug("average: {}ms", avg);
    }

    public void checkFormatting() {
        String length = Utils.getTrackLengthFormatted(Utils.getTrackLength(DebugPaths.audioTestInputMp3));
        logger.debug(length);
    }
}

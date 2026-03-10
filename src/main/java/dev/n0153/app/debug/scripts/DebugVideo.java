package dev.n0153.app.debug.scripts;

import dev.n0153.app.*;
import dev.n0153.app.exceptions.FileTypeDetectionException;
import dev.n0153.app.exceptions.MimeTypeDetectionException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;

public class DebugVideo {

    private final DisarmState state;
    private final DisarmConfig config;

    public DebugVideo(DisarmState state, DisarmConfig config) {
        this.state = state;
        this.config = config;
    }
    private static final Logger logger = LogManager.getLogger(DebugVideo.class);

    public void runVideo() {
        LocalDateTime start = LocalDateTime.now();

        Video Video = new Video(state, config);
        try {
            App app = new App(state, config);
            app.prepData(DebugPaths.videoTestInputMp4);
            Video.reEncodeMp4(DebugPaths.videoTestInputMp4);

            app.prepData(DebugPaths.videoTestInputMov);
            Video.reEncodeMov(DebugPaths.videoTestInputMov);

            app.prepData(DebugPaths.videoTestInputWebm);
            Video.reEncodeWebm(DebugPaths.videoTestInputWebm);

            app.prepData(DebugPaths.videoTestInputMkv);
            Video.reEncodeMkv(DebugPaths.videoTestInputMkv);

        } catch (MimeTypeDetectionException e) {
            throw new RuntimeException(e);
        } catch (FileTypeDetectionException e) {
            throw new RuntimeException(e);
        }

        LocalDateTime finish = LocalDateTime.now();
        Duration duration = Duration.between(start, finish);
        logger.info("operation took {}ms", duration.toMillis());
    }

    public void runHundredVideo(Path osTargetPath, String format) {
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

    public void runHundredMp4() {
        try {
            runHundredVideo(DebugPaths.videoTestInputMp4, "mp4");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void runHundredMov() {
        try {
            runHundredVideo(DebugPaths.videoTestInputMov, "mov");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void runHundredWebm() {
        try {
            runHundredVideo(DebugPaths.videoTestInputWebm, "webm");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void runHundredMkv() {
        try {
            runHundredVideo(DebugPaths.videoTestInputMkv, "mkv");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void runHundredAll() {
        runHundredMp4();
        runHundredMkv();
        runHundredMov();
        runHundredWebm();
    }
}

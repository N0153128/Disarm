package dev.n0153.app.debug.scripts;

import dev.n0153.app.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.IOException;

public class DebugImage {
    private final DisarmState state;
    private final DisarmConfig config;

    public DebugImage(DisarmState state, DisarmConfig config) {
        this.state = state;
        this.config = config;
    }

    private final Logger logger = LogManager.getLogger(DebugImage.class);
    // Image disarming test snippet
    public void runImages() {
        App App = new App(state, config);
        Mat image = Imgcodecs.imread(DebugPaths.imageTestInputPng.toString());
        Mat logo = Imgcodecs.imread(DebugPaths.logoLocation.toString(), Imgcodecs.IMREAD_UNCHANGED);

        state.setImage(image);
        logger.debug("Is image empty?{}", state.getImage().empty());
        state.setLogo(logo);
        logger.debug("Is logo empty?{}", state.getLogo().empty());

        App.fileDisarm(DebugPaths.imageTestInputPng, DebugPaths.logoLocation);

        state.getLogo().release();
        state.getImage().release();

    }

    public void runHundredImage() throws IOException {
        App app = new App(state, config);
        long[] mid = new long[100];
        for (int i = 0; i<100; i++){
            mid[i] = app.fileDisarm(DebugPaths.imageTestInputPng, DebugPaths.logoLocation);
        }
        long avg, sum = 0;
        for (long i : mid){
            sum+=i;
        }
        avg = sum/mid.length;
        logger.debug("average: {}", avg);
    }
}

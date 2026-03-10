package dev.n0153.app.debug.scripts;

import dev.n0153.app.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;


public class DebugText {
    private final DisarmState state;
    private final DisarmConfig config;

    public DebugText(DisarmState state, DisarmConfig config) {
        this.state = state;
        this.config = config;
    }
    private final Logger logger = LogManager.getLogger(DebugText.class);

    public void testText() throws IOException {
        Text Text = new Text(state, config);
        state.setTextFile(Files.readString(DebugPaths.textTestLocationHTML));
        String testString = "Hello world! today is a great day!";
        byte[] testStringBytes = testString.getBytes(StandardCharsets.UTF_8);
        boolean result = Validator.validateEncoding(Text, state);
        logger.debug(result);
    }

    public void runText() {
        Text Text = new Text(state, config);
        App App = new App(state, config);
        try {
            logger.debug("escaping HTML");
            state.setTextFile(Files.readString(DebugPaths.textTestLocationHTMLPlain));
            App.prepData(DebugPaths.textTestLocationHTMLPlain);
            Utils.getTitle(state, false);
            Text.escapeHTML();
            Text.saveTextData();
            logger.debug("done");

            logger.debug("stripping shell");
            state.setTextFile(Files.readString(DebugPaths.textTestLocationShellPlain));
            App.prepData(DebugPaths.textTestLocationShellPlain);
            Utils.getTitle(state, false);
            Text.stripPatterns();
            Text.saveTextData();
            logger.debug("done");

            logger.debug("validating encoding");
            state.setTextFile(Files.readString(DebugPaths.textTestLocation));
            App.prepData(DebugPaths.textTestLocation);
            Utils.getTitle(state, false);
            byte[] invalid = {(byte) 0xFF, (byte) 0xFF, (byte) 0xFF};
            logger.debug("validation: {}", Validator.validateEncoding(Text, state));
            Text.saveTextData();
            logger.debug("done");

            logger.debug("normalizing unicode");
            state.setTextFile(Files.readString(DebugPaths.textTestLocation));
            App.prepData(DebugPaths.textTestLocation);
            Utils.getTitle(state, false);
            Text.normalizeUnicode();
            Text.saveTextData();
            logger.debug("done");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void runHundredText(Path osTargetPath) throws IOException {
        App app = new App(state, config);
        long[] mid = new long[100];
        for (int i = 0; i<100; i++){
            mid[i] = app.fileDisarm(osTargetPath);
        }
        long avg, sum = 0;
        for (long i : mid){
            sum+=i;
        }
        avg = sum/mid.length;
        logger.debug("average: {}", avg);
    }

    public void runHundredTextHTML() throws IOException {
        runHundredText(DebugPaths.textTestLocationHTML);
    }

    public void runHundredTextShell() throws IOException {
        runHundredText(DebugPaths.textTestLocationShellPlain);
    }

    public void runHundredTextGeneral() throws IOException {
        runHundredText(DebugPaths.textTestLocation);
    }

    public void runHundredTextAll() throws IOException {
        runHundredTextHTML();
        runHundredTextShell();
        runHundredTextGeneral();
    }
}

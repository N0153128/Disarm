package dev.n0153.app;

import dev.n0153.app.debug.scripts.DebugGeneral;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.nio.file.Path;

@Command(name = "disarm", description = "Sanitize media files", mixinStandardHelpOptions = true, version = "disarm v0.1")

public class DisarmCLI implements Runnable{
    private static final Logger logger = LogManager.getLogger(DisarmCLI.class);

    @CommandLine.Parameters(arity = "1..*", description = "input file")
    private Path[] inputPath;

    @CommandLine.Option(names = {"-o", "--output"}, description = "output file path")
    private Path outputPath;

    @CommandLine.Option(names = {"-l", "--logo"}, description = "watermark specified image")
    private Path logo;

    @CommandLine.Option(names = {"-do", "--delete-original"}, negatable = true, description = "delete the original file after disarming")
    private boolean deleteOriginal;

    @CommandLine.Option(names = {"-da", "--debug-all"}, description = "test run. check all supported media types")
    private boolean debugAll;

    public void run() {
        logger.info("Disarm (pre-release)\nWorking file specified: {}", this.inputPath);
        BuilderConfig builder = DisarmConfig.builder();

        for (Path input : inputPath) {
            // getters and setters
            logger.info("Specified path: {}", input);
            if (outputPath != null) {
                builder.setGeneralOutputPath(this.outputPath);
            }
            if (deleteOriginal) {
                builder.setKeepOriginal(false);
            }

            // finalise parameters
            DisarmConfig config = builder.build();
            DisarmState state = new DisarmState(config);
            App app = new App(state, config);

            // runners
            if (debugAll) {
                DebugGeneral general = new DebugGeneral(state, config);
                general.isEverythingWorkingQuestionMark("");
            }
            if (logo != null && Utils.isImage(input) && Utils.isImage(logo)) {
                app.fileDisarm(input, logo);
            } else {
                logger.info("not an image");
                app.fileDisarm(input);
            }
        }
    }
}

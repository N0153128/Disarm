package dev.n0153.app;

import dev.n0153.app.debug.scripts.DebugGeneral;
import dev.n0153.app.plugins.DisarmPlugins;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Command(name = "disarm", description = "Sanitize media files", mixinStandardHelpOptions = true, version = "disarm v0.1")

public class DisarmCLI implements Runnable{
    private static final Logger logger = LogManager.getLogger(DisarmCLI.class);
    private final PluginRegistry registry;

    public DisarmCLI(PluginRegistry registry) {
        this.registry = registry;
    }

    private List<Object> discoverCommands() {
        return new ArrayList<>(this.registry.cliRegistryExperimental.values());
    }

    @CommandLine.Parameters(arity = "1..*", description = "input file")
    public Path[] inputPath;

    @CommandLine.Option(names = {"-o", "--output"}, description = "output file path")
    private Path outputPath;

    @CommandLine.Option(names = {"-l", "--logo"}, description = "watermark specified image")
    private Path logo;

    @CommandLine.Option(names = {"-do", "--delete-original"}, negatable = true, description = "delete the original file after disarming")
    private boolean deleteOriginal;

    @CommandLine.Option(names = {"-da", "--debug-all"}, description = "test run. check all supported media types")
    private boolean debugAll;

    @CommandLine.Spec
    CommandLine.Model.CommandSpec spec;

    private void init() {
        discoverCommands().forEach(cmd ->
                spec.addSubcommand(getCommandName(cmd), new CommandLine(cmd)));
        logger.info("Plugin commands registered");
    }

    private static String getCommandName(Object cmd) {
        return cmd.getClass().getAnnotation(Command.class).name();
    }

    public void run() {
        logger.info("Disarm (pre-release)\nWorking file specified: {}", this.inputPath);
        logger.warn("WARNING: using experimental app orchestrator");
        BuilderConfig builder = DisarmConfig.builder();
        init();
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
            MediaApp app = new MediaApp(registry);

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

package dev.n0153.app;

import dev.n0153.app.exceptions.FileTypeDetectionException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Command(name = "disarm", description = "Sanitize media files", mixinStandardHelpOptions = true, version = "disarm v0.1")

public class DisarmCLI implements Runnable{
    private static final Logger logger = LogManager.getLogger(DisarmCLI.class);
    private final PluginRegistry registry;
    private final GlobalConfig globalConfig;

    public DisarmCLI(PluginRegistry registry, GlobalConfig globalConfig) {
        this.globalConfig = globalConfig;
        this.registry = registry;
    }

    List<Object> discoverCommands() {
        return new ArrayList<>(this.registry.cliRegistry.values());
    }

    @CommandLine.Parameters(arity = "1..*", description = "input file")
    public Path[] inputPath;

    @CommandLine.Option(names = {"-o", "--output"}, description = "output file path")
    private Path outputPath;

    @CommandLine.Option(names = {"-do", "--delete-original"}, negatable = true, description = "delete the original file after disarming")
    private boolean deleteOriginal;

    @CommandLine.Option(names = {"-b", "--benchmark"}, description = "Enable benchmarking to see how much time file" +
            "processing took")
    private boolean benchmark;


    @CommandLine.Spec
    CommandLine.Model.CommandSpec spec;

    static String getCommandName(Object cmd) {
        return cmd.getClass().getAnnotation(Command.class).name();
    }

    public void run() {
        logger.warn("WARNING: using experimental app orchestrator");
        for (Path input : inputPath) {
            // related plugin auto-detection
            try {
                if (!Files.isRegularFile(input)) {
                    logger.info("Skipping non-file path: {}", input);
                    continue;
                }
                String fileType = Utils.getFileType(input);
                Runnable handler = registry.resolveCli(fileType);
                if (handler != null) {
                    handler.run();
                } else {
                    logger.info("no CLI handler found for {}", input.getFileName());
                }
            } catch (FileTypeDetectionException e) {
                throw new RuntimeException(e);
            }
            // getters and setters
            logger.info("Specified path: {}", input);
            if (outputPath != null) {
                globalConfig.setGeneralOutputPath(this.outputPath);
            }
            if (deleteOriginal) {
                globalConfig.setKeepOriginal(false);
            }
            if (benchmark) {
                globalConfig.setBenchmarking(true);
            }
            // finalise parameters
            MediaApp app = new MediaApp(registry, globalConfig);
            // runners
            app.fileDisarm(input);
        }
    }
}

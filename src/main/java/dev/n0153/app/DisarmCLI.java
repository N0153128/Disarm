package dev.n0153.app;

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
        return new ArrayList<>(this.registry.listCLI());
    }

    @CommandLine.Parameters(arity = "0..*", description =
            "Input file. Multiple files can be specified in a row, without a comma. Example: test.png test.jpeg")
    public Path[] inputPath;

    @CommandLine.Option(names = {"-o", "--output"}, description =
            "Output file path, where disarmed file will be saved. Example: path/to/folder")
    private Path outputPath;

    @CommandLine.Option(names = {"-do", "--delete-original"}, negatable = true, description =
            "Delete the original input file after disarming")
    private boolean deleteOriginal;

    @CommandLine.Option(names = {"-su", "--skip-unsupported"}, description =
            "If specified file is not supported by either of the installed plugins," +
                    " the file will be skipped with an appropriate warning message.")
    private boolean skipUnsupported;

    @CommandLine.Option(names = {"-sc", "--skip-crashed"}, description =
            "If specified file's processing failed, the file will be skipped with" +
                    " an appropriate warning message")
    private boolean skipCrashed;

    @CommandLine.Option(names = {"-ve", "--verbose-errors"}, description =
            "If execution fails, Disarm will throw a detailed report" +
                    " with all vital information, config snapshots and execution timing." +
                    " \nAccepted values: \n" +
                    "OFF - default value, feature disabled,\n" +
                    "CONSOLE - error report is printed in the console,\n" +
                    "FILE - error report is saved in the default error reports output path located at resources/output/reports,\n" +
                    "BOTH - error report is saved in the default error reports output path and is printed in the console.\n" +
                    "Example: -ve BOTH")
    private GlobalConfig.VerboseErrors verboseErrors;

    @CommandLine.Option(names = {"-tt", "--track-time"}, description =
            "Track when the program was booted, used for error reports, enabled by default, " +
                    "specifying this param will disable boot time tracking.")
    private boolean trackTime;

    @CommandLine.Option(names = {"-aifr", "--allow-input-from-root"}, description =
            "SECURITY SENSITIVE: Lifts a limitation, which prevents users from " +
                    "passing files from anywhere on the disk. Disabled by default " +
                    "due to security concerns, so that remote users cannot access files outside of " +
                    "the default input root")
    private boolean allowInputFromRoot;

    @CommandLine.Option(names = {"-dm", "--detect-mime"}, description =
            "DEBUG: Prints detected mime type for specified file. " +
                    "This mode does not perform any disarming and is used for debug purposes." +
                    " Disabled by default.")
    private boolean detectMime;

    @CommandLine.Option(names = {"-dr", "--delete-result"}, description =
            "Executes an entire file disarming cycle for benchmarking purposes." +
                    " Resulting file is deleted from the disk once execution finishes.")
    private boolean deleteResult;


    @CommandLine.Option(names = {"-ro", "--reports-output"}, description =
            "Error reports output path used to save verbose error (-ve) reports." +
                    " Defaults to resources/output/reports.")
    private Path reportsPath;

    @CommandLine.Option(names = {"-b", "--benchmark"}, description =
            "Enable benchmarking to see how much time file" +
            " processing took")
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
            if (skipUnsupported) {
                globalConfig.setSkipUnsupported(true);
            }
            if (skipCrashed) {
                globalConfig.setSkipCrashed(true);
            }
            if (verboseErrors != null) {
                globalConfig.setVerboseErrors(verboseErrors);
            }
            if(trackTime) {
                globalConfig.setTrackTime(false);
            }
            if (reportsPath != null) {
                globalConfig.setReportsOutputPath(reportsPath);
            }
            if (detectMime) {
                logger.info(Utils.getMimeFromSignature(input));
                continue;
            }
            if(allowInputFromRoot) {
                globalConfig.setRestrictInputFromRoot(true);
            }
            if (deleteResult) {
                globalConfig.setDeleteResult(true);
            }
            // finalise parameters
            MediaApp app = new MediaApp(registry, globalConfig);
            // runners
            app.fileDisarm(input);
        }
    }
}

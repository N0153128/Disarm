package dev.n0153.app;
import dev.n0153.app.plugins.DisarmPlugins;
import nu.pattern.OpenCV;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import picocli.CommandLine;

public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);
    private enum Status { RUNNING, FINISHED, FAILED }
    private static volatile Status status = Status.RUNNING;
    public static volatile boolean verbose = false;

    public static void main(String[] args) {
        verbose = System.getenv("DISARM_DEBUG") != null;
        installExitHandlers();
        int exitCode = run(args);
        status = (exitCode == 0) ? Status.FINISHED : Status.FAILED;
        System.exit(exitCode);
    }

    static void reportError(Throwable throwable) {
        logger.error("An error occurred during execution - {}.", throwable.getClass().getSimpleName());
        if (verbose) {
            throwable.printStackTrace();
        }
    }

    private static int run (String[] args) {
        try {
            logger.info("Working directory: {}",  System.getProperty("user.dir"));
            OpenCV.loadLocally();
            PluginRegistry registry = new PluginRegistry();
            GlobalConfig globalConfig = new GlobalConfig();
            Utils.init(globalConfig);
            new DisarmPlugins(globalConfig).registerAll(registry);
            DisarmCLI app = new DisarmCLI(registry, globalConfig);

            CommandLine cli = buildCli(app, registry);
            cli.setExecutionExceptionHandler((ex, cmd, parseResult) -> {
                reportError(ex);
                return 1;
            });
            return cli.execute(args);
        } catch (Exception e) {
            reportError(e);
            return 1;
        }
    }

    private static CommandLine buildCli(DisarmCLI app, PluginRegistry registry) {
        CommandLine cli = new CommandLine(app);

        registry.getCliRegistry().forEach((name, mixin) -> {
            cli.addMixin(name, mixin);
            logger.info("added mixin for: {}, {}", name, mixin);
        });

        app.discoverCommands().forEach(cmd ->
                cli.addSubcommand(DisarmCLI.getCommandName(cmd), new CommandLine(cmd)));
        return cli;
    }

    private static void installExitHandlers() {
        Thread.setDefaultUncaughtExceptionHandler((thread, ex) -> {
            status = Status.FAILED;
            reportError(ex);
            System.exit(1);
        });

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            switch (status) {
                case FINISHED -> System.out.println("Disarm finished working.");
                case FAILED -> System.out.println("Exiting...");
                case RUNNING -> System.out.println("\nInterrupted. Exiting...");
            }
        }, "exit-message"));
    }

    public static void setShowStackTrace(boolean showStackTrace) {
        verbose = showStackTrace;
    }
}
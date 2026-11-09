package dev.n0153.app.core;
import nu.pattern.OpenCV;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import picocli.CommandLine;

/**
 * Application entry point. Loads OpenCV, discovers and registers plugins,
 * assembles the PicoCLI command line (base command plus per-plugin mixins
 * and subcommands), and dispatches to it. Also installs a global
 * uncaught-exception handler and a shutdown hook that reports the run's
 * final status.
 * @since 0.2
 */
public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);

    /** Tracks the run's outcome for the shutdown-hook exit message. */
    private enum Status { RUNNING, FINISHED, FAILED }
    private static volatile Status status = Status.RUNNING;

    /** When true, uncaught/reported exceptions print their full stack trace; toggled via the {@code DISARM_DEBUG} env var or {@link #setShowStackTrace(boolean)}. */
    public static volatile boolean verbose = false;

    /**
     * Process entry point. Sets verbosity from the {@code DISARM_DEBUG}
     * environment variable, installs exit handlers, runs the CLI, and
     * exits the JVM with the resulting exit code.
     * @param args raw command-line arguments
     * @since 0.2
     */
    public static void main(String[] args) {
        verbose = System.getenv("DISARM_DEBUG") != null;
        installExitHandlers();
        int exitCode = run(args);
        status = (exitCode == 0) ? Status.FINISHED : Status.FAILED;
        System.exit(exitCode);
    }

    /**
     * Logs a reported error, printing its stack trace as well when {@link #verbose} is enabled.
     * @param throwable the error to report
     * @since 0.2
     */
    static void reportError(Throwable throwable) {
        logger.error("An error occurred during execution - {}.", throwable.getClass().getSimpleName());
        if (verbose) {
            throwable.printStackTrace();
        }
    }

    /**
     * Boots OpenCV, builds the plugin registry and global config, discovers
     * and registers all plugins, builds the CLI, and executes it against
     * the given arguments.
     * @param args raw command-line arguments
     * @return the process exit code (0 on success, 1 on failure)
     * @since 0.2
     */
    private static int run (String[] args) {
        try {
            logger.info("Working directory: {}",  System.getProperty("user.dir"));
            OpenCV.loadLocally();
            PluginRegistry registry = new PluginRegistry();
            GlobalConfig globalConfig = new GlobalConfig();
            Utils.init(globalConfig);
            PluginLoader.registerPlugins(PluginLoader.discoverPluginDirs(), registry, globalConfig);
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

    /**
     * Assembles the top-level {@link CommandLine}: wraps the base
     * {@link DisarmCLI} command, adds one mixin per registered plugin CLI,
     * and adds one subcommand per discovered command.
     * @param app the base CLI command instance
     * @param registry plugin registry supplying mixins and the config needed to discover commands
     * @return the fully assembled CommandLine ready to execute
     * @since 0.2
     */
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

    /**
     * Installs a default uncaught-exception handler (reports the error and
     * exits with code 1) and a shutdown hook that prints a final status
     * message based on how the run ended.
     * @since 0.2
     */
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

    /**
     * Enables or disables printing full stack traces for reported errors.
     * @param showStackTrace true to print stack traces, false to log only the exception type
     * @since 0.2
     */
    public static void setShowStackTrace(boolean showStackTrace) {
        verbose = showStackTrace;
    }
}
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

    public static void main(String[] args) {
        installExitHandlers();
        int exitCode = run(args);
        status = (exitCode == 0) ? Status.FINISHED : Status.FAILED;
        System.exit(exitCode);
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

            return buildCli(app, registry).execute(args);
        } catch (Exception e) {
            logger.error("Something went wrong", e);
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
            logger.error("Uncaught exception in thread {}", thread.getName(), ex);
            System.exit(1);
        });

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            switch (status) {
                case FINISHED -> System.out.println("Disarm finished working.");
                case FAILED -> System.out.println("Something went wrong. Exiting...");
                case RUNNING -> System.out.println("\nInterrupted. Exiting...");
            }
        }, "exit-message"));
    }
}
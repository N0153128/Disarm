package dev.n0153.app;
import dev.n0153.app.plugins.DisarmPlugins;
import nu.pattern.OpenCV;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import picocli.CommandLine;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;


public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);
    private static final AtomicBoolean exiting = new AtomicBoolean(false);
    private static volatile boolean crashed = false;

    public static void main(String[] args) {
    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
        if (!exiting.compareAndSet(false, true)) return;
        if (crashed) {
            System.out.println("Something went wrong. Exiting...");
        } else {
            System.out.println("Disarm finished working.");
        }
    }, "exit-message"));

    Thread.setDefaultUncaughtExceptionHandler((thread, ex) -> {
        crashed = true;
        System.err.println("Error: " + ex.getMessage());
        ex.printStackTrace();
        System.exit(1);
    });
        OpenCV.loadLocally();
        logger.info("Working directory: {}",  System.getProperty("user.dir"));
        PluginRegistry registry = new PluginRegistry();
        GlobalConfig globalConfig = new GlobalConfig();
        Utils.init(globalConfig);
        DisarmPlugins plugins = new DisarmPlugins(globalConfig);
        plugins.registerAll(registry);
        try {
            DisarmCLI app = new DisarmCLI(registry, globalConfig);
            CommandLine cli = new CommandLine(app);
            Map<String, Runnable> cliRegistry = registry.getCliRegistry();
            for (Map.Entry<String, Runnable> entry: cliRegistry.entrySet()) {
                cli.addMixin(entry.getKey(), entry.getValue());
                logger.info("added mixins for: {}, {}", entry.getKey(), entry.getValue());
            }
            app.discoverCommands().forEach(cmd ->
                    cli.addSubcommand(DisarmCLI.getCommandName(cmd), new CommandLine(cmd)));

            int exitCode = cli.execute(args);
            System.exit(exitCode);

        } catch (Exception e) {
            logger.error("Something went wrong: %s".formatted(e));
        }
    }
}
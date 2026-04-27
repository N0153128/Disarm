package dev.n0153.app;
import dev.n0153.app.exceptions.DisarmException;
import dev.n0153.app.exceptions.FileTypeDetectionException;
import dev.n0153.app.plugins.DisarmPlugins;
import nu.pattern.OpenCV;
import picocli.CommandLine;

import java.nio.file.Files;
import java.nio.file.Path;


public class Main {
    public static void main(String[] args) {
        OpenCV.loadLocally();
        System.out.println("Working directory: " + System.getProperty("user.dir"));

        try {
            DisarmPlugins plugins = new DisarmPlugins();
            PluginRegistry registry = new PluginRegistry();
            plugins.registerAll(registry);

            DisarmCLI app = new DisarmCLI(registry);
            CommandLine cli = new CommandLine(app);

            for (String arg : args) {
                if (arg.startsWith("-")) continue; // skip options

                try {
                    Path path = Path.of(arg);
                    if (!Files.exists(path)) continue; // skip non-existent paths

                    String fileType = Utils.getFileType(path);
                    Runnable handler = registry.resolveCli(fileType);
                    if (handler != null) {
                        cli.addMixin(fileType, handler);
                    }
                } catch (Exception e) {
                    // not a valid path, skip
                }
            }

            app.discoverCommands().forEach(cmd ->
                    cli.addSubcommand(DisarmCLI.getCommandName(cmd), new CommandLine(cmd)));

            int exitCode = cli.execute(args);
            System.exit(exitCode);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
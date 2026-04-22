package dev.n0153.app;
import dev.n0153.app.plugins.DisarmPlugins;
import nu.pattern.OpenCV;
import picocli.CommandLine;


public class Main {

    public static void main(String[] args) {
        OpenCV.loadLocally();
        System.out.println("Working directory: " + System.getProperty("user.dir"));
        try {
            DisarmPlugins plugins = new DisarmPlugins();
            PluginRegistry registry = new PluginRegistry();
            plugins.registerAll(registry);
            int exitCode = new CommandLine(new DisarmCLI(registry)).execute(args);
            System.exit(exitCode);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
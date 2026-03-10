package dev.n0153.app;
import nu.pattern.OpenCV;
import picocli.CommandLine;


public class Main {

    public static void main(String[] args) {
        OpenCV.loadLocally();
        System.out.println("Working directory: " + System.getProperty("user.dir"));
        try {
            int exitCode = new CommandLine(new DisarmCLI()).execute(args);
            System.exit(exitCode);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
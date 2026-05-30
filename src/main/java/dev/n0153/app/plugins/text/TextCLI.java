package dev.n0153.app.plugins.text;

import dev.n0153.app.DisarmCLI;
import dev.n0153.app.PluginRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import picocli.CommandLine;
import picocli.CommandLine.Command;


@Command(name = "text", description = "Text processing plugin ")
public class TextCLI  implements Runnable {
    private static final Logger logger = LogManager.getLogger(TextCLI.class);

    private final TextConfig config = new TextConfig();
    private final PluginRegistry registry;

    public TextCLI(PluginRegistry registry) {
        this.registry = registry;
        registry.updateConfig("text", config);
    }

    @CommandLine.ParentCommand
    DisarmCLI inputPath;

    @CommandLine.Option(names = {"-mts", "--max-text-size"})
    private int textSize;

    @Override
    public void run() {
        if (textSize > 0) {
            this.config.setMaxTextSize(textSize);
        }

        registry.updateConfig("text", config);
    }
}

package dev.n0153.app.plugins.text;

import dev.n0153.app.DisarmCLI;
import dev.n0153.app.PluginRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.Objects;


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

    @CommandLine.Option(names = {"-oe", "--output-encoding"})
    private String outputEncoding;

    @CommandLine.Option(names = {"-nf", "--normalize-form"})
    private String normalizeForm;

    @Override
    public void run() {
        if (textSize > 0) {
            this.config.setMaxTextSize(textSize);
        }
        if (Objects.equals(outputEncoding, "UTF-8")) {
            this.config.setOutputEncoding(StandardCharsets.UTF_16);
        }
        if (Objects.equals(outputEncoding, "US_ASCII")) {
            this.config.setOutputEncoding(StandardCharsets.US_ASCII);
        }
        if (Objects.equals(outputEncoding, "UTF-16BE")) {
            this.config.setOutputEncoding(StandardCharsets.UTF_16BE);
        }
        if (Objects.equals(outputEncoding, "UTF-16LE")) {
            this.config.setOutputEncoding(StandardCharsets.UTF_16LE);
        }
        if (Objects.equals(outputEncoding, "ISO_8859_1")) {
            this.config.setOutputEncoding(StandardCharsets.ISO_8859_1);
        }
        if (Objects.equals(normalizeForm, "NFKC")) {
            this.config.setNormalizeForm(Normalizer.Form.NFKC);
        }
        if (Objects.equals(normalizeForm, "NFC")) {
            this.config.setNormalizeForm(Normalizer.Form.NFC);
        }
        if (Objects.equals(normalizeForm, "NFD")) {
            this.config.setNormalizeForm(Normalizer.Form.NFD);
        }
        if (Objects.equals(normalizeForm, "NFKD")) {
            this.config.setNormalizeForm(Normalizer.Form.NFKD);
        }

        registry.updateConfig("text", config);
    }
}

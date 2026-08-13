package dev.n0153.app.plugins.text;

import dev.n0153.app.DisarmCLI;
import dev.n0153.app.PluginRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;


@Command(name = "text", description = "Text Processing Plugin.", mixinStandardHelpOptions = true)
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

    @CommandLine.Option(names = {"-mts", "--max-text-size"}, description =
            "Changes text file size limit. " +
                    "Accepts bytes as integers. Example for 10MB: -mts 10000000")
    private int textSize;

    @CommandLine.Option(names = {"-oe", "--output-encoding"}, description =
            "Changes output encoding, defaults to UTF-8. " +
                    "Accepts one of the supported values: UTF-16, US_ASCII, UTF-16BE, UTF-16LE and ISO_8859_1." +
                    " Example: -oe UTF-16BE")
    private String outputEncoding;

    @CommandLine.Option(names = {"-nf", "--normalize-form"}, description =
            "Changes text normalization form, defaults to NFKC. " +
                    "Accepts one of the supported values: NFKC, NFC, NFD, NFKD. " +
                    "Example: -nf NFD")
    private String normalizeForm;

    @CommandLine.Option(names = {"-dst", "--dont-save-text"}, description =
            "Executes an entire text disarming cycle for benchmarking purposes." +
                    " Resulting file will not be saved to disk. " +
                    "Similar to -dr, but text-specific and skips the write entirely. Example: -dst")
    private boolean dontSaveText;

    @CommandLine.Option(names = {"-tof", "--text-output-format"}, description =
            "Changes output file's format, accepts one of supported formats: json, log, txt. " +
                    "WARNING: certain format combinations will fail to save. " +
                    "Example: -tof json")
    private String textOutputFormat;

    @CommandLine.Option(names = {"-sssf", "--skip-script-strip-for"}, description =
            "Disable script tag stripping from one of the supported text formats." +
                    " WARNING! using this flag may pose a security risk. It should be used only for legitimate " +
                    "source code files that contain script tags. " +
                    "Script stripping is enabled by default and advised to lease as is." +
                    "Example: -sssf json")
    private String skipScriptStripFor;

    @CommandLine.Option(names = {"-ccr", "--control-characters-range"}, description =
            "Add an additional range of characters to trim whiles disarming text. " +
                    "Default ranges are: 0x0000 to 0x001F and 0x007F to 0x009F. " +
                    "Accepts the following format: rangeStart:rangeEnd, where rangeStart and rangeEnd are " +
                    "hexadecimal integer literals." +
                    "Example: -ccr 0x0000:0x001F")
    private String controlCharactersRanges;

    @CommandLine.Option(names = {"-us", "--url-schemes"}, description =
            "Add an additional keyword to trim whiles disarming text file. " +
                    "Default schemes to trim: \"javascript:\", \"vbscript:\", \"data:\"" +
                    "Accepts the following format: scheme,scheme,scheme." +
                    "Example: -us vbscript:,livescript:,mocha:")
    private String urlSchemes;

    @Override
    public void run() {
        if (textSize > 0) {
            this.config.setMaxTextSize(textSize);
        }
        if (Objects.equals(outputEncoding, "UTF-16")) {
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
        if (dontSaveText) {
            this.config.setDontSaveText(true);
        }
        if (textOutputFormat != null) {
            this.config.setTextDefaultOutputTo(textOutputFormat);
        }
        if (skipScriptStripFor != null) {
            this.config.setSkipScriptStripFor(skipScriptStripFor);
        }
        if (controlCharactersRanges != null) {
            if (controlCharactersRanges.contains(":")) {
                int rangeStart = Integer.decode(controlCharactersRanges.split(":")[0]);
                int rangeEnd= Integer.decode(controlCharactersRanges.split(":")[1]);
                List<TextConfig.ControlCharactersRange> ranges =
                        new ArrayList<>(this.config.getControlCharactersRanges());
                ranges.add(new TextConfig.ControlCharactersRange(rangeStart, rangeEnd));
                this.config.setControlCharactersRanges(ranges);
            } else {
                throw new IllegalArgumentException(
                        "An incorrectly formatted string was provided for control characters range"
                );
            }
        }
        if (urlSchemes != null) {
            if (urlSchemes.contains(",")) {
                List<String> schemes = new ArrayList<>(Arrays.asList(this.config.getUrlSchemes()));
                schemes.addAll(Arrays.asList(urlSchemes.split(",")));
                this.config.setUrlSchemes(schemes.toArray(new String[0]));
            } else {
                throw new IllegalArgumentException(
                        "An incorrectly formatted string was provided for url schemes"
                );
            }
        }
        registry.updateConfig("text", config);
    }
}

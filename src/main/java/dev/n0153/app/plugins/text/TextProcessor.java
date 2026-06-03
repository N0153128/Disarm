package dev.n0153.app.plugins.text;

import dev.n0153.app.GlobalConfig;
import dev.n0153.app.MediaContext;
import dev.n0153.app.MediaProcessor;
import dev.n0153.app.Utils;
import dev.n0153.app.exceptions.DisarmException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.Normalizer;
import java.util.regex.Pattern;

public class TextProcessor implements MediaProcessor<TextConfig> {
    private final TextConfig config;
    private final TextContext context;
    private final GlobalConfig globalConfig;
    private static final Logger logger = LogManager.getLogger(TextProcessor.class);

    public TextProcessor(TextConfig config, TextContext context, GlobalConfig globalConfig) {
        this.config = config;
        this.context = context;
        this.globalConfig = globalConfig;
    }

    /**
     * Normalises to NFKC form to prevent lookalike character attacks.
     * @since 0.1
     */
    public void normalizeUnicode() {
        String text = Normalizer.normalize(context.getTextContent(), config.getNormalizeForm());
        for (char c : config.getZeroLengthChars()) {
            text = text.replace(String.valueOf(c), "");
        }
        context.setTextContent(text);
    }

    /**
     * Escapes HTML special characters, replacing them with safe character entity references.
     * @since 0.1
     */
    public void escapeHTML() {
        String text = context.getTextContent()
                .replaceAll("&", "&amp;")
                .replaceAll("<", "&lt;")
                .replaceAll(">", "&gt;")
                .replaceAll("\"", "&quot;")
                .replaceAll("'", "&#39;");
        context.setTextContent(text);
    }

    /**
     * Removes control characters, strips dangerous patterns: Script tags, control characters and dangerous URL schemes.
     * @since 0.1
     */
    public void stripPatterns() {
        String text = context.getTextContent();
        for(String scheme : config.getUrlSchemes()) {
            text = text.replaceAll(Pattern.quote(scheme), "");
        }

        text = text.replaceAll("<script.*?>.*?</script>", "")
                .replaceAll("[\\u0000-\\u001F\\u007F-\\u009F]", "");
        context.setTextContent(text);
    }

    /**
     * Saves the sanitised text from runtime state object as a text file.
     * @throws IOException If fails to write file.
     * @since 0.1
     */
    public void saveTextData() throws IOException {
        Path toFile = globalConfig.getGeneralOutputPath().resolve(context.getTextTitle()+".txt");
        Files.writeString(toFile, context.getTextContent(), StandardCharsets.UTF_8);
    }

    @Override
    public void process(Path osTargetPath) throws DisarmException {
        try {
            logger.info("Processing text file: {}", osTargetPath.toString());
            byte[] text = Files.readAllBytes(osTargetPath);
            context.setTextContent(new String(text, StandardCharsets.UTF_8));
            context.setTextTitle(Utils.getTitle(osTargetPath, false));

            normalizeUnicode();
            stripPatterns();
            escapeHTML();
            if (globalConfig.getKeepResult()) {
                saveTextData();
            }
        } catch (IOException e) {
            throw new DisarmException("Failed to save text file");
        }
    }

    @Override
    public MediaContext getContext() {
        return context;
    }
}

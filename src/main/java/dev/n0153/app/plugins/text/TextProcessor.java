package dev.n0153.app.plugins.text;

import dev.n0153.app.GlobalConfig;
import dev.n0153.app.MediaContext;
import dev.n0153.app.MediaProcessor;
import dev.n0153.app.exceptions.DisarmException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.Normalizer;

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
        String text = Normalizer.normalize(context.getTextContent(), Normalizer.Form.NFKC)
                .replaceAll("[\\u200B-\\u200D\\uFEFF]", "");
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

    }

    @Override
    public MediaContext getContext() {
        return null;
    }
}

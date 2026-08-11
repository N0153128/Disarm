package dev.n0153.app.plugins.text;

import dev.n0153.app.GlobalConfig;
import dev.n0153.app.MediaContext;
import dev.n0153.app.MediaProcessor;
import dev.n0153.app.Utils;
import dev.n0153.app.exceptions.TextProcessingException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.Normalizer;
import java.util.Objects;
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

        text = text.replaceAll("<script.*?>.*?</script>", "");
        StringBuilder strippedText = new StringBuilder(text.length());
        for (int i = 0; i <= text.length(); i++) {
            char currentCharacter = text.charAt(i);
            if (!config.shouldStripControlCharacter(currentCharacter)) {
                strippedText.append(currentCharacter);
            }
        }
        text = strippedText.toString();
        context.setTextContent(text);
    }

    /**
     * Saves the sanitised text from runtime state object as a text file.
     * @throws IOException If fails to write file.
     * @since 0.1
     */
    public void saveTextData() throws IOException {
        Path toFile = globalConfig.getGeneralOutputPath().resolve(context.getTextTitle()+".txt");
        if (Objects.equals(config.getTextDefaultOutputTo(), "default")) {
            Files.writeString(toFile, context.getTextContent(), config.getOutputEncoding());
        } else {
            String baseTitle = context.getTextTitle().split("\\.")[0];
            toFile = globalConfig.getGeneralOutputPath().resolve(baseTitle+"."+config.getTextDefaultOutputTo());
            Files.writeString(toFile, context.getTextContent(), config.getOutputEncoding());
        }
    }

    @Override
    public void process(Path osTargetPath) throws TextProcessingException {
        try {
            logger.info("Processing text file: {}", osTargetPath.toString());
            byte[] text = context.getRawBytes();
            context.setTextContent(new String(text, config.getOutputEncoding()));
            context.setTextTitle(Utils.getTitle(osTargetPath, Utils.getMimeType(osTargetPath), false));

            normalizeUnicode();
            stripPatterns();
            escapeHTML();
            if (!config.getDontSaveText()) {
                saveTextData();
            }
        } catch (IOException e) {
            throw new TextProcessingException("Failed to save text file", osTargetPath);
        }
    }

    @Override
    public MediaContext getContext() {
        return context;
    }
}

package dev.n0153.app;

import java.util.List;

public class ValidationResult {
    private final boolean valid;
    private final List<String> warnings;
    private final String error;

    public ValidationResult(boolean valid, List<String> warnings, String error) {
        this.valid = valid;
        this.warnings = warnings;
        this.error = error;
    }

    public boolean isValid() { return valid; };
    public List<String> getWarnings() { return warnings; }
    public String getError() { return error; }
}

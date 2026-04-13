package dev.n0153.app;

public interface MediaValidator<Config extends MediaConfig> {
    ValidationResult validate(ProcessingContext context, Config config);
}

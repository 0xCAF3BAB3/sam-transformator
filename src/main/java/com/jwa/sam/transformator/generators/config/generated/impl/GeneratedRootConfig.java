package com.jwa.sam.transformator.generators.config.generated.impl;

import com.jwa.sam.transformator.generators.config.generated.GeneratedConfig;

import java.nio.file.Path;

public final class GeneratedRootConfig implements GeneratedConfig {
    private final Path outputDirectory;

    public GeneratedRootConfig(final Path outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    public final Path getOutputDirectory() {
        return outputDirectory;
    }
}

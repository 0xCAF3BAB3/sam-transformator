package com.jwa.amlmodel.code.generator.generators.config.generated.impl;

import com.jwa.amlmodel.code.generator.generators.config.generated.GeneratedConfig;

import java.nio.file.Path;

public final class GeneratedComponentConfig implements GeneratedConfig {
    private final GeneratedServiceConfig serviceConfig;
    private final String componentGroupId;
    private final String artifactId;
    private final Path componentDirectory;
    private final Path componentMainFile;

    public GeneratedComponentConfig(final GeneratedServiceConfig serviceConfig, final String componentGroupId, final String artifactId, final Path componentDirectory, final Path componentMainFile) {
        this.serviceConfig = serviceConfig;
        this.componentGroupId = componentGroupId;
        this.artifactId = artifactId;
        this.componentDirectory = componentDirectory;
        this.componentMainFile = componentMainFile;
    }

    public final GeneratedServiceConfig getServiceConfig() {
        return serviceConfig;
    }

    public final String getComponentGroupId() {
        return componentGroupId;
    }

    public final String getArtifactId() {
        return artifactId;
    }

    public final Path getComponentDirectory() {
        return componentDirectory;
    }

    public final Path getComponentMainFile() {
        return componentMainFile;
    }
}

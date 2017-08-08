package com.jwa.amlmodel.code.generator.generators.config.generated.impl;

import com.jwa.amlmodel.code.generator.generators.config.generated.GeneratedConfig;

import java.nio.file.Path;

public final class GeneratedComponentConfig implements GeneratedConfig {
    private final GeneratedServiceConfig serviceConfig;
    private final String componentGroupId;
    private final String componentArtifactId;
    private final Path componentDirectory;
    private final Path componentMainFile;
    private final Path componentPomFile;

    public GeneratedComponentConfig(final GeneratedServiceConfig serviceConfig, final String componentGroupId, final String componentArtifactId, final Path componentDirectory, final Path componentMainFile, final Path componentPomFile) {
        this.serviceConfig = serviceConfig;
        this.componentGroupId = componentGroupId;
        this.componentArtifactId = componentArtifactId;
        this.componentDirectory = componentDirectory;
        this.componentMainFile = componentMainFile;
        this.componentPomFile = componentPomFile;
    }

    public final GeneratedServiceConfig getServiceConfig() {
        return serviceConfig;
    }

    public final String getComponentGroupId() {
        return componentGroupId;
    }

    public final String getComponentArtifactId() {
        return componentArtifactId;
    }

    public final Path getComponentDirectory() {
        return componentDirectory;
    }

    public final Path getComponentMainFile() {
        return componentMainFile;
    }

    public final Path getComponentPomFile() {
        return componentPomFile;
    }
}

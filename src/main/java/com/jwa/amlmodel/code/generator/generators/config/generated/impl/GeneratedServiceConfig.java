package com.jwa.amlmodel.code.generator.generators.config.generated.impl;

import com.jwa.amlmodel.code.generator.generators.config.generated.GeneratedConfig;

import java.nio.file.Path;

public final class GeneratedServiceConfig implements GeneratedConfig {
    private final GeneratedRootConfig rootConfig;
    private final Path serviceDirectory;
    private final String serviceGroupId;
    private final String serviceArtifactId;
    private final Path servicePomFile;

    public GeneratedServiceConfig(final GeneratedRootConfig rootConfig, final Path serviceDirectory, final String serviceGroupId, final String serviceArtifactId, final Path servicePomFile) {
        this.rootConfig = rootConfig;
        this.serviceDirectory = serviceDirectory;
        this.serviceGroupId = serviceGroupId;
        this.serviceArtifactId = serviceArtifactId;
        this.servicePomFile = servicePomFile;
    }

    public final GeneratedRootConfig getRootConfig() {
        return rootConfig;
    }

    public final Path getServiceDirectory() {
        return serviceDirectory;
    }

    public final String getServiceGroupId() {
        return serviceGroupId;
    }

    public final String getServiceArtifactId() {
        return serviceArtifactId;
    }

    public final Path getServicePomFile() {
        return servicePomFile;
    }
}

package com.jwa.amlmodel.code.generator.generators.config.generated.impl;

import com.jwa.amlmodel.code.generator.generators.config.generated.GeneratedConfig;

import java.nio.file.Path;

public final class GeneratedPortsConfig implements GeneratedConfig {
    private final GeneratedComponentConfig componentConfig;
    private final Path componentCommunicationserviceFile;
    private final String communicationPackageName;

    public GeneratedPortsConfig(final GeneratedComponentConfig componentConfig, final Path componentCommunicationserviceFile, final String communicationPackageName) {
        this.componentConfig = componentConfig;
        this.componentCommunicationserviceFile = componentCommunicationserviceFile;
        this.communicationPackageName = communicationPackageName;
    }

    public final GeneratedComponentConfig getComponentConfig() {
        return componentConfig;
    }

    public final Path getComponentCommunicationserviceFile() {
        return componentCommunicationserviceFile;
    }

    public final String getCommunicationPackageName() {
        return communicationPackageName;
    }
}

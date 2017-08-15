package com.jwa.amlmodel.code.generator.generators.config.generated.impl;

import com.jwa.amlmodel.code.generator.generators.config.generated.GeneratedConfig;

import java.nio.file.Path;

public final class GeneratedPortsConfig implements GeneratedConfig {
    private final Path componentCommunicationserviceFile;
    private final String communicationPackageName;
    private final String communicationModuleName;
    private final GeneratedComponentConfig componentConfig;

    public GeneratedPortsConfig(final Path componentCommunicationserviceFile, final String communicationPackageName, final String communicationModuleName, final GeneratedComponentConfig componentConfig) {
        this.componentCommunicationserviceFile = componentCommunicationserviceFile;
        this.communicationPackageName = communicationPackageName;
        this.communicationModuleName = communicationModuleName;
        this.componentConfig = componentConfig;
    }

    public final Path getComponentCommunicationserviceFile() {
        return componentCommunicationserviceFile;
    }

    public final String getCommunicationPackageName() {
        return communicationPackageName;
    }

    public final String getCommunicationModuleName() {
        return communicationModuleName;
    }

    public final GeneratedComponentConfig getComponentConfig() {
        return componentConfig;
    }
}

package com.jwa.sam.transformator.generators.config.generated.impl;

import com.jwa.sam.transformator.generators.config.generated.GeneratedConfig;

import java.nio.file.Path;

public final class GeneratedPortsConfig implements GeneratedConfig {
    private final Path componentCommunicationserviceClassFile;
    private final GeneratedComponentConfig componentConfig;

    public GeneratedPortsConfig(final Path componentCommunicationserviceClassFile, final GeneratedComponentConfig componentConfig) {
        this.componentCommunicationserviceClassFile = componentCommunicationserviceClassFile;
        this.componentConfig = componentConfig;
    }

    public final Path getComponentCommunicationserviceClassFile() {
        return componentCommunicationserviceClassFile;
    }

    public final GeneratedComponentConfig getComponentConfig() {
        return componentConfig;
    }
}

package com.jwa.amlmodel.code.generator.generators.config.generated.impl;

import com.jwa.amlmodel.code.generator.generators.config.generated.GeneratedConfig;
import com.jwa.amlmodel.code.generator.generators.utils.MavenModuleInfo;

import java.nio.file.Path;

public final class GeneratedComponentConfig implements GeneratedConfig {
    private final MavenModuleInfo componentMavenModuleInfo;
    private final Path componentMainClassFile;
    private boolean isMessagemodelDependencySet = false;
    private final GeneratedServiceConfig serviceConfig;

    public GeneratedComponentConfig(final MavenModuleInfo componentMavenModuleInfo, final Path componentMainClassFile, final GeneratedServiceConfig serviceConfig) {
        this.componentMavenModuleInfo = componentMavenModuleInfo;
        this.componentMainClassFile = componentMainClassFile;
        this.serviceConfig = serviceConfig;
    }

    public final MavenModuleInfo getComponentMavenModuleInfo() {
        return componentMavenModuleInfo;
    }

    public final Path getComponentMainClassFile() {
        return componentMainClassFile;
    }

    public final boolean isMessagemodelDependencySet() {
        return isMessagemodelDependencySet;
    }

    public void setMessagemodelDependencySet() {
        isMessagemodelDependencySet = true;
    }

    public final GeneratedServiceConfig getServiceConfig() {
        return serviceConfig;
    }
}

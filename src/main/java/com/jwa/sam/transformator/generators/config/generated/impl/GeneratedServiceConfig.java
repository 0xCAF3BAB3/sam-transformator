package com.jwa.sam.transformator.generators.config.generated.impl;

import com.jwa.sam.transformator.generators.config.generated.GeneratedConfig;
import com.jwa.sam.transformator.generators.utils.MavenModuleInfo;
import com.jwa.sam.transformator.generators.utils.MavenProjectInfo;

public final class GeneratedServiceConfig implements GeneratedConfig {
    private final MavenProjectInfo serviceMavenProjectInfo;
    private MavenModuleInfo communicationMavenModuleInfo;
    private MavenModuleInfo messagemodelMavenModuleInfo;
    private final GeneratedRootConfig rootConfig;

    public GeneratedServiceConfig(final MavenProjectInfo serviceMavenProjectInfo, final GeneratedRootConfig rootConfig) {
        this.serviceMavenProjectInfo = serviceMavenProjectInfo;
        this.rootConfig = rootConfig;
    }

    public final MavenProjectInfo getServiceMavenProjectInfo() {
        return serviceMavenProjectInfo;
    }

    public final MavenModuleInfo getCommunicationMavenModuleInfo() {
        return communicationMavenModuleInfo;
    }

    public final void setCommunicationMavenModuleInfo(MavenModuleInfo communicationMavenModuleInfo) {
        this.communicationMavenModuleInfo = communicationMavenModuleInfo;
    }

    public final MavenModuleInfo getMessagemodelMavenModuleInfo() {
        return messagemodelMavenModuleInfo;
    }

    public final void setMessagemodelMavenModuleInfo(MavenModuleInfo messagemodelMavenModuleInfo) {
        this.messagemodelMavenModuleInfo = messagemodelMavenModuleInfo;
    }

    public final GeneratedRootConfig getRootConfig() {
        return rootConfig;
    }
}

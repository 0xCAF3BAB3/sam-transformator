package com.jwa.amlmodel.code.generator.generators.config.generated.impl;

import com.jwa.amlmodel.code.generator.generators.config.generated.GeneratedConfig;
import com.jwa.amlmodel.code.generator.generators.utils.MavenProjectInfo;

public final class GeneratedServiceConfig implements GeneratedConfig {
    private final MavenProjectInfo serviceMavenProjectInfo;
    private final GeneratedRootConfig rootConfig;

    public GeneratedServiceConfig(final MavenProjectInfo serviceMavenProjectInfo, final GeneratedRootConfig rootConfig) {
        this.serviceMavenProjectInfo = serviceMavenProjectInfo;
        this.rootConfig = rootConfig;
    }

    public final MavenProjectInfo getServiceMavenProjectInfo() {
        return serviceMavenProjectInfo;
    }

    public final GeneratedRootConfig getRootConfig() {
        return rootConfig;
    }
}

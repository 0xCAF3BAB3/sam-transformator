package com.jwa.amlmodel.code.generator.generators.config.generated.impl;

import com.jwa.amlmodel.code.generator.generators.config.generated.GeneratedConfig;

public final class GeneratedPortConfig implements GeneratedConfig {
    private final GeneratedPortsConfig portsConfig;

    public GeneratedPortConfig(final GeneratedPortsConfig portsConfig) {
        this.portsConfig = portsConfig;
    }

    public final GeneratedPortsConfig getPortsConfig() {
        return portsConfig;
    }
}

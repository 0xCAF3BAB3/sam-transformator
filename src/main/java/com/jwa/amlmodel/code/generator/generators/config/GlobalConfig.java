package com.jwa.amlmodel.code.generator.generators.config;

import freemarker.template.Configuration;

public final class GlobalConfig {
    private final Configuration freemarkerConfig;

    public GlobalConfig(final Configuration freemarkerConfig) {
        this.freemarkerConfig = freemarkerConfig;
    }

    public final Configuration getFreemarkerConfig() {
        return freemarkerConfig;
    }
}

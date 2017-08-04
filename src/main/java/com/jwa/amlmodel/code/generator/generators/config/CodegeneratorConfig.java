package com.jwa.amlmodel.code.generator.generators.config;

import freemarker.template.Configuration;

public final class CodegeneratorConfig {
    private final Configuration freemarkerConfig;

    public CodegeneratorConfig(final Configuration freemarkerConfig) {
        this.freemarkerConfig = freemarkerConfig;
    }

    public final Configuration getFreemarkerConfig() {
        return freemarkerConfig;
    }
}

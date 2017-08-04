package com.jwa.amlmodel.code.generator.generators.config;

import freemarker.template.Configuration;

public final class CodeGeneratorConfig {
    private final Configuration freemarkerConfig;

    public CodeGeneratorConfig(final Configuration freemarkerConfig) {
        this.freemarkerConfig = freemarkerConfig;
    }

    public final Configuration getFreemarkerConfig() {
        return freemarkerConfig;
    }
}

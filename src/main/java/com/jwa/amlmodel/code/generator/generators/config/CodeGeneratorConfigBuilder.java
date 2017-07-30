package com.jwa.amlmodel.code.generator.generators.config;

import freemarker.template.Configuration;

import org.cdlflex.models.CAEX.InternalElement;
import org.eclipse.emf.common.util.EList;

import java.util.HashMap;
import java.util.Map;

public final class CodeGeneratorConfigBuilder {
    private EList<InternalElement> data;
    private Configuration freemarkerConfig;
    private final Map<String, String> parameters;

    public CodeGeneratorConfigBuilder() {
        this.data = null;
        this.freemarkerConfig = null;
        this.parameters = new HashMap<>();
    }

    public final CodeGeneratorConfigBuilder setData(final EList<InternalElement> data) {
        this.data = data;
        return this;
    }

    public final CodeGeneratorConfigBuilder setFreemarkerConfig(final Configuration freemarkerConfig) {
        this.freemarkerConfig = freemarkerConfig;
        return this;
    }

    public final CodeGeneratorConfigBuilder setParameter(final String key, final String value) {
        parameters.put(key, value);
        return this;
    }

    public final CodeGeneratorConfig build() throws IllegalArgumentException {
        final CodeGeneratorConfig config = new CodeGeneratorConfig(data, freemarkerConfig, parameters);
        if (config.getData() == null) {
            throw new IllegalArgumentException("Passed data is invalid");
        }
        if (config.getFreemarkerConfig() == null) {
            throw new IllegalArgumentException("Passed freemarker-config is invalid");
        }
        return config;
    }
}

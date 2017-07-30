package com.jwa.amlmodel.code.generator.generators.config;

import freemarker.template.Configuration;

import org.cdlflex.models.CAEX.InternalElement;
import org.eclipse.emf.common.util.EList;

import java.util.Map;

public final class CodeGeneratorConfig {
    private final EList<InternalElement> data;
    private final Configuration freemarkerConfig;
    private final Map<String, String> parameters;

    CodeGeneratorConfig(final EList<InternalElement> data, final Configuration freemarkerConfig, final Map<String, String> parameters) {
        this.data = data;
        this.freemarkerConfig = freemarkerConfig;
        this.parameters = parameters;
    }

    public final EList<InternalElement> getData() {
        return data;
    }

    public final Configuration getFreemarkerConfig() {
        return freemarkerConfig;
    }

    public final String getParameter(final String parameterKey) throws IllegalArgumentException {
        if (!parameters.containsKey(parameterKey)) {
            throw new IllegalArgumentException("Parameter '" + parameterKey + "' is missing");
        }
        final String value = parameters.get(parameterKey);
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException("Parameter '" + parameterKey + "' has invalid value");
        }
        return value;
    }
}

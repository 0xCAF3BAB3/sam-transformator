package com.jwa.amlmodel.code.generator.generators.config;

import freemarker.template.Configuration;

import java.nio.charset.Charset;
import java.nio.file.Path;

public final class GlobalConfig {
    private final Configuration freemarkerConfig;
    private final Path templateFilesDirectory;
    private final Charset charset;

    public GlobalConfig(final Configuration freemarkerConfig, final Path templateFilesDirectory, final Charset charset) {
        this.freemarkerConfig = freemarkerConfig;
        this.templateFilesDirectory = templateFilesDirectory;
        this.charset = charset;
    }

    public final Configuration getFreemarkerConfig() {
        return freemarkerConfig;
    }

    public final Path getTemplateFilesDirectory() {
        return templateFilesDirectory;
    }

    public final Charset getCharset() {
        return charset;
    }
}

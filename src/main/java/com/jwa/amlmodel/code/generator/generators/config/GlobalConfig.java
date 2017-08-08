package com.jwa.amlmodel.code.generator.generators.config;

import com.jwa.amlmodel.code.generator.generators.Codegenerator;
import com.jwa.amlmodel.code.generator.generators.constants.FreemarkerTemplatesConstants;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class GlobalConfig {
    public static final Charset CHARSET = StandardCharsets.UTF_8;
    private static final Path DIRECTORY_TEMPLATES = Paths.get("src/main/java/" + Codegenerator.class.getPackage().getName().replace(".", "/") + "/templates/"); // TODO: this line is quite hacky
    public static final Path DIRECTORY_FILES_TEMPLATES = DIRECTORY_TEMPLATES.resolve("files/");
    private static final Path DIRECTORY_FREEMARKER_TEMPLATES = DIRECTORY_TEMPLATES.resolve("freemarker/");
    private static final Configuration CONFIG_FREEMARKER;

    static {
        final Configuration freemarkerConfig = new Configuration(Configuration.VERSION_2_3_26);
        try {
            freemarkerConfig.setDirectoryForTemplateLoading(DIRECTORY_FREEMARKER_TEMPLATES.toFile());
        } catch (IOException e) {
            throw new RuntimeException("Freemarker-template directory invalid: " + e.getMessage(), e);
        }
        freemarkerConfig.setDefaultEncoding(CHARSET.displayName());
        freemarkerConfig.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        freemarkerConfig.setLogTemplateExceptions(false);
        CONFIG_FREEMARKER = freemarkerConfig;
    }

    public static Template getTemplate(final FreemarkerTemplatesConstants templateType) {
        try {
            return CONFIG_FREEMARKER.getTemplate(templateType.getFilepath());
        } catch (IOException e) {
            throw new RuntimeException("Loading Freemarker-template '" + templateType.getFilepath() + "' failed: " + e.getMessage(), e);
        }
    }
}

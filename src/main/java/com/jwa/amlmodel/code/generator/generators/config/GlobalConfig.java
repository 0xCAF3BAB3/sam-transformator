package com.jwa.amlmodel.code.generator.generators.config;

import com.jwa.amlmodel.code.generator.generators.utils.IOUtils;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class GlobalConfig {
    private static final Charset CHARSET = StandardCharsets.UTF_8;
    private static final Path DIRECTORY_CODEFILES = Paths.get("codefiles/");
    private static final Path DIRECTORY_FILES = DIRECTORY_CODEFILES.resolve("files/");
    private static final Path DIRECTORY_TEMPLATES = DIRECTORY_CODEFILES.resolve("templates/");
    private static final Path DIRECTORY_FILE_TEMPLATES = DIRECTORY_TEMPLATES.resolve("file/");
    private static final Path DIRECTORY_FREEMARKER_TEMPLATES = DIRECTORY_TEMPLATES.resolve("freemarker/");
    private static final Configuration CONFIG_FREEMARKER;

    static {
        final Configuration freemarkerConfig = new Configuration(Configuration.VERSION_2_3_26);
        try {
            freemarkerConfig.setDirectoryForTemplateLoading(DIRECTORY_FREEMARKER_TEMPLATES.toFile());
        } catch (IOException e) {
            throw new RuntimeException("Setting Freemarker directory for template-loading failed: " + e.getMessage(), e);
        }
        freemarkerConfig.setDefaultEncoding(CHARSET.name());
        freemarkerConfig.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        freemarkerConfig.setLogTemplateExceptions(false);
        CONFIG_FREEMARKER = freemarkerConfig;
    }

    private GlobalConfig() {}

    public static Charset getCharset() {
        return CHARSET;
    }

    public static Path getFiles(final Files files) {
        if (files == null) {
            throw new IllegalArgumentException("Passed files is null");
        }
        final Path path = DIRECTORY_FILES.resolve(files.getDirectoryPath());
        if (!IOUtils.isValidDirectory(path)) {
            throw new RuntimeException("Loading files '" + path + "' failed: " + "Directory doesn't exists or is invalid");
        }
        return path;
    }

    public static Template getTemplate(final FreemarkerTemplate template) {
        if (template == null) {
            throw new IllegalArgumentException("Passed template is null");
        }
        try {
            return CONFIG_FREEMARKER.getTemplate(template.getFilepath());
        } catch (IOException e) {
            throw new RuntimeException("Loading Freemarker-template '" + template.getFilepath() + "' failed: " + e.getMessage(), e);
        }
    }

    public static Path getTemplate(final FileTemplate template) {
        if (template == null) {
            throw new IllegalArgumentException("Passed template is null");
        }
        final Path path = DIRECTORY_FILE_TEMPLATES.resolve(template.getFilepath());
        if (!IOUtils.isValidFile(path)) {
            throw new RuntimeException("Loading file-template '" + path + "' failed: " + "File doesn't exists or is invalid");
        }
        return path;
    }
}

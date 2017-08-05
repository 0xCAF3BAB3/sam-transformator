package com.jwa.amlmodel.code.generator.generators.impl;

import com.jwa.amlmodel.code.generator.generators.Codegenerator;
import com.jwa.amlmodel.code.generator.generators.CodegeneratorException;
import com.jwa.amlmodel.code.generator.generators.CodegeneratorValidationException;
import com.jwa.amlmodel.code.generator.generators.amlmodel.AmlmodelConstants;
import com.jwa.amlmodel.code.generator.generators.amlmodel.AmlmodelUtils;
import com.jwa.amlmodel.code.generator.generators.config.CodegeneratorConfig;

import freemarker.template.Template;
import freemarker.template.TemplateException;

import org.cdlflex.models.CAEX.InternalElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public final class MessagemodelCodegenerator implements Codegenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessagemodelCodegenerator.class);
    private final Path messagemodelOutput;
    private final String packageName;

    public MessagemodelCodegenerator(final Path messagemodelOutput, final String packageName) {
        if (!Files.exists(messagemodelOutput)) {
            throw new IllegalArgumentException("Passed messagemodel-output doesn't exists");
        }
        this.messagemodelOutput = messagemodelOutput;
        this.packageName = packageName;
    }

    @Override
    public final void generate(final InternalElement node, final CodegeneratorConfig codeGeneratorConfig) throws CodegeneratorException {
        String messagemodelName = AmlmodelUtils.getRoleStartingWith(node, AmlmodelConstants.NAME_ROLE_MESSAGEMODEL + "/");

        LOGGER.trace("Generating message-model '" + messagemodelName + "' ...");

        if (messagemodelName.equals("MessageModel")) {
            throw new CodegeneratorValidationException("Reserved message-model name");
        }

        final File messagemodelFile = new File(messagemodelOutput.toFile().getPath()+ "/" + messagemodelName + ".java");

        final boolean exists = messagemodelFile.isFile();
        if (exists) {
            LOGGER.trace(messagemodelName + " is already generated");
        } else {
            final Map<String, String> dataModel = new HashMap<>();
            dataModel.put("packageName", packageName);
            dataModel.put("messagemodelName", messagemodelName);
            try {
                final Template template = codeGeneratorConfig.getFreemarkerConfig().getTemplate("Messagemodel_Instance.ftlh");
                try (final Writer writer = new FileWriter(messagemodelFile)) {
                    template.process(dataModel, writer);
                }
            } catch (IOException | TemplateException e) {
                throw new CodegeneratorException(e.getMessage(), e);
            }
            LOGGER.trace(messagemodelName + " was created");
        }

        LOGGER.trace("Generating message-model '" + messagemodelName + "' finished");
    }
}

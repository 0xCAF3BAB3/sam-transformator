package com.jwa.amlmodel.code.generator.generators.impl;

import com.jwa.amlmodel.code.generator.generators.Codegenerator;
import com.jwa.amlmodel.code.generator.generators.CodegeneratorException;
import com.jwa.amlmodel.code.generator.generators.config.FreemarkerTemplate;
import com.jwa.amlmodel.code.generator.generators.config.GlobalConfig;
import com.jwa.amlmodel.code.generator.generators.config.generated.impl.GeneratedMessagemodelConfig;
import com.jwa.amlmodel.code.generator.generators.config.generated.impl.GeneratedPortConfig;
import com.jwa.amlmodel.code.generator.generators.constants.AmlmodelConstants;
import com.jwa.amlmodel.code.generator.generators.utils.CodefileUtils;
import com.jwa.amlmodel.code.generator.generators.utils.IOUtils;

import freemarker.template.Template;
import freemarker.template.TemplateException;

import org.cdlflex.models.CAEX.InternalElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public final class MessagemodelCodegenerator implements Codegenerator<GeneratedPortConfig, GeneratedMessagemodelConfig> {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessagemodelCodegenerator.class);

    @Override
    public final GeneratedMessagemodelConfig generate(final InternalElement node, final GeneratedPortConfig parentConfig) throws CodegeneratorException {
        if (!AmlmodelConstants.hasMessagemodelRole(node)) {
            throw new IllegalArgumentException("Passed node has no messagemodel-role");
        }

        final String portName = node.getName();

        LOGGER.trace("Generating message-model for port-node '" + portName + "' ...");

        final String messagemodelModuleName = "messagemodel";
        final Path serviceDirectory = parentConfig.getPortsConfig().getComponentConfig().getServiceConfig().getServiceDirectory();
        if (!CodefileUtils.mavenModuleExists(messagemodelModuleName, serviceDirectory)) {
            try {
                // TODO: fix parameters ... to real parameters
                CodefileUtils.createMavenModule(parentConfig.getPortsConfig().getComponentConfig().getComponentGroupId(), messagemodelModuleName, serviceDirectory, "...", "...", StandardCharsets.UTF_8);
                // TODO: copy MessageModel.java into mavenModuleStructure.getCodeDirectory()
            } catch (IOException e) {
                throw new CodegeneratorException("Failed to generate Maven module '" + messagemodelModuleName + "': " + e.getMessage(), e);
            }
        }

        // add dependency to messagemodel-module to component (if not already added)
        final Path componentPomFile = parentConfig.getPortsConfig().getComponentConfig().getComponentPomFile();
        try {
            if (!CodefileUtils.hasMavenDependancy(parentConfig.getPortsConfig().getComponentConfig().getComponentGroupId(), messagemodelModuleName, componentPomFile, GlobalConfig.CHARSET)) {
                CodefileUtils.addMavenDependancy(parentConfig.getPortsConfig().getComponentConfig().getComponentGroupId(), messagemodelModuleName, componentPomFile, GlobalConfig.CHARSET);
            }
        } catch (IOException e) {
            throw new CodegeneratorException("Failed to check/add Maven module '" + messagemodelModuleName + "' to '" + componentPomFile + "': " + e.getMessage(), e);
        }

        // if specified message-model is not already added to maven-module 'messagemodel': create it
        final String messagemodelName = AmlmodelConstants.getMessagemodelName(node);
        final Path messagemodelMavenCodeDirectory = Paths.get("code-output/Service1/messagemodel/src/main/java/com/jwa/service1/messagemodel"); // TODO: remove this hack
        final Path messagemodelFile = messagemodelMavenCodeDirectory.resolve(messagemodelName + ".java");
        if (!IOUtils.isValidFile(messagemodelFile)) {
            final Map<String, String> messagemodelDatamodel = new HashMap<>();
            messagemodelDatamodel.put("messagemodelName", messagemodelName);
            try {
                final Template template = GlobalConfig.getTemplate(FreemarkerTemplate.MESSAGE);
                try (final Writer writer = Files.newBufferedWriter(messagemodelFile, GlobalConfig.CHARSET)) {
                    template.process(messagemodelDatamodel, writer);
                }
            } catch (IOException | TemplateException e) {
                throw new CodegeneratorException("Failed to generate file '" + messagemodelFile + "': " + e.getMessage(), e);
            }
        }

        LOGGER.trace("Generating message-model for port-node '" + portName + "' finished");

        return new GeneratedMessagemodelConfig();
    }
}

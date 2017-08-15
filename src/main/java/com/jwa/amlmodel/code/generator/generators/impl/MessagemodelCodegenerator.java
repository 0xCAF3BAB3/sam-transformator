package com.jwa.amlmodel.code.generator.generators.impl;

import com.jwa.amlmodel.code.generator.generators.Codegenerator;
import com.jwa.amlmodel.code.generator.generators.CodegeneratorException;
import com.jwa.amlmodel.code.generator.generators.config.FileTemplate;
import com.jwa.amlmodel.code.generator.generators.config.FreemarkerTemplate;
import com.jwa.amlmodel.code.generator.generators.config.GlobalConfig;
import com.jwa.amlmodel.code.generator.generators.config.generated.impl.GeneratedMessagemodelConfig;
import com.jwa.amlmodel.code.generator.generators.config.generated.impl.GeneratedPortConfig;
import com.jwa.amlmodel.code.generator.generators.config.generated.impl.GeneratedServiceConfig;
import com.jwa.amlmodel.code.generator.generators.constants.AmlmodelConstants;
import com.jwa.amlmodel.code.generator.generators.utils.CodefileUtils;
import com.jwa.amlmodel.code.generator.generators.utils.IOUtils;

import freemarker.template.Template;

import org.cdlflex.models.CAEX.InternalElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public final class MessagemodelCodegenerator implements Codegenerator<GeneratedPortConfig, GeneratedMessagemodelConfig> {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessagemodelCodegenerator.class);

    @Override
    public final GeneratedMessagemodelConfig generate(final InternalElement node, final GeneratedPortConfig portConfig) throws CodegeneratorException {
        if (!AmlmodelConstants.hasMessagemodelRole(node)) {
            throw new IllegalArgumentException("Passed node has no messagemodel-role");
        }

        final String portName = node.getName();

        LOGGER.trace("Generating message-model for port-node '" + portName + "' ...");

        final String messagemodelModuleName = "messagemodel";
        final Path serviceDirectory = portConfig.getPortsConfig().getComponentConfig().getServiceConfig().getServiceDirectory();
        final String packageName = portConfig.getPortsConfig().getComponentConfig().getComponentGroupId() + "." + messagemodelModuleName;
        if (!CodefileUtils.mavenModuleExists(messagemodelModuleName, serviceDirectory)) {
            try {
                final String pomFileContent;
                final Path pomFileTemplate = GlobalConfig.getTemplate(FileTemplate.POM_MESSAGEMODEL);
                final Map<String, String> pomFileContentDatamodel = new HashMap<>();
                final GeneratedServiceConfig serviceConfig = portConfig.getPortsConfig().getComponentConfig().getServiceConfig();
                pomFileContentDatamodel.put("parentGroupId", serviceConfig.getServiceGroupId());
                pomFileContentDatamodel.put("parentArtifactId", serviceConfig.getServiceArtifactId());
                pomFileContentDatamodel.put("groupId", portConfig.getPortsConfig().getComponentConfig().getComponentGroupId());
                pomFileContentDatamodel.put("artifactId", messagemodelModuleName);
                pomFileContentDatamodel.put("dependencyCommunicationGroupId", portConfig.getPortsConfig().getComponentConfig().getComponentGroupId());
                pomFileContentDatamodel.put("dependencyCommunicationArtifactId", portConfig.getPortsConfig().getCommunicationModuleName());
                try {
                    pomFileContent = CodefileUtils.processFileTemplate(pomFileTemplate, pomFileContentDatamodel, GlobalConfig.getCharset());
                } catch (IOException e) {
                    throw new CodegeneratorException("Failed to generate snippet '" + "pomFileContent" + "': " + e.getMessage(), e);
                }
                final String logconfigFileContent;
                final Map<String, String> logconfigDatamodel = new HashMap<>();
                logconfigDatamodel.put("name", messagemodelModuleName);
                logconfigDatamodel.put("groupId", portConfig.getPortsConfig().getComponentConfig().getComponentGroupId());
                final Template logconfigTemplate = GlobalConfig.getTemplate(FreemarkerTemplate.LOG4J2);
                try (final Writer writer = new StringWriter()) {
                    logconfigTemplate.process(logconfigDatamodel, writer);
                    logconfigFileContent = writer.toString();
                } catch (IOException | TemplateException e) {
                    throw new CodegeneratorException("Failed to generate snippet '" + "logconfigFileContent" + "': " + e.getMessage(), e);
                }
                final CodefileUtils.MavenModuleStructure mavenModuleStructure = CodefileUtils.createMavenModule(portConfig.getPortsConfig().getComponentConfig().getComponentGroupId(), messagemodelModuleName, serviceDirectory, pomFileContent, logconfigFileContent, GlobalConfig.getCharset());
                final Path messagemodelFile = mavenModuleStructure.getCodeDirectory().resolve("MessageModel.java");
                final Map<String, String> messagemodelDatamodel = new HashMap<>();
                messagemodelDatamodel.put("packageName", packageName);
                messagemodelDatamodel.put("communicationPackageName", portConfig.getPortsConfig().getCommunicationPackageName());
                final Template messagemodelTemplate = GlobalConfig.getTemplate(FreemarkerTemplate.MESSAGEMODEL);
                try (final Writer writer = Files.newBufferedWriter(messagemodelFile, GlobalConfig.getCharset())) {
                    messagemodelTemplate.process(messagemodelDatamodel, writer);
                } catch (IOException | TemplateException e) {
                    throw new CodegeneratorException("Failed to generate file '" + messagemodelFile + "': " + e.getMessage(), e);
                }
            } catch (IOException e) {
                throw new CodegeneratorException("Failed to generate Maven module '" + messagemodelModuleName + "': " + e.getMessage(), e);
            }
        }

        // add dependency to messagemodel-module to component (if not already added)
        final Path componentPomFile = portConfig.getPortsConfig().getComponentConfig().getComponentPomFile();
        try {
            if (!CodefileUtils.hasMavenDependancy(portConfig.getPortsConfig().getComponentConfig().getComponentGroupId(), messagemodelModuleName, componentPomFile, GlobalConfig.getCharset())) {
                CodefileUtils.addMavenDependancy(portConfig.getPortsConfig().getComponentConfig().getComponentGroupId(), messagemodelModuleName, componentPomFile, GlobalConfig.getCharset());
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
            messagemodelDatamodel.put("packageName", packageName);
            messagemodelDatamodel.put("messagemodelName", messagemodelName);
            try {
                final Template template = GlobalConfig.getTemplate(FreemarkerTemplate.MESSAGE);
                try (final Writer writer = Files.newBufferedWriter(messagemodelFile, GlobalConfig.getCharset())) {
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

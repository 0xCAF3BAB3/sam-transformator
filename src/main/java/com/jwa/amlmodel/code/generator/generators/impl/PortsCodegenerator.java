package com.jwa.amlmodel.code.generator.generators.impl;

import com.jwa.amlmodel.code.generator.generators.Codegenerator;
import com.jwa.amlmodel.code.generator.generators.CodegeneratorException;
import com.jwa.amlmodel.code.generator.generators.config.FileTemplate;
import com.jwa.amlmodel.code.generator.generators.config.FreemarkerTemplate;
import com.jwa.amlmodel.code.generator.generators.config.GlobalConfig;
import com.jwa.amlmodel.code.generator.generators.config.generated.impl.GeneratedComponentConfig;
import com.jwa.amlmodel.code.generator.generators.config.generated.impl.GeneratedPortsConfig;
import com.jwa.amlmodel.code.generator.generators.config.generated.impl.GeneratedServiceConfig;
import com.jwa.amlmodel.code.generator.generators.constants.AmlmodelConstants;
import com.jwa.amlmodel.code.generator.generators.utils.CodefileUtils;

import freemarker.template.Template;
import freemarker.template.TemplateException;

import org.cdlflex.models.CAEX.InternalElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public final class PortsCodegenerator implements Codegenerator<GeneratedComponentConfig, GeneratedPortsConfig> {
    private static final Logger LOGGER = LoggerFactory.getLogger(PortsCodegenerator.class);

    @Override
    public final GeneratedPortsConfig generate(final InternalElement node, final GeneratedComponentConfig parentConfig) throws CodegeneratorException {
        if (!AmlmodelConstants.hasPortsRole(node)) {
            throw new IllegalArgumentException("Passed node has no ports-role");
        }

        final String portsName = node.getName();

        LOGGER.trace("Generating ports for ports-node '" + portsName + "' ...");

        final String communicationModuleName = "communication";
        if (!CodefileUtils.mavenModuleExists(communicationModuleName, parentConfig.getServiceConfig().getServiceDirectory())) {
            try {
                final String pomFileContent;
                final Path pomFileTemplate = GlobalConfig.getTemplate(FileTemplate.POM_COMMUNICATION);
                final Map<String, String> pomFileContentDatamodel = new HashMap<>();
                final GeneratedServiceConfig serviceConfig = parentConfig.getServiceConfig();
                pomFileContentDatamodel.put("parentGroupId", serviceConfig.getServiceGroupId());
                pomFileContentDatamodel.put("parentArtifactId", serviceConfig.getServiceArtifactId());
                pomFileContentDatamodel.put("groupId", parentConfig.getComponentGroupId());
                pomFileContentDatamodel.put("artifactId", communicationModuleName);
                try {
                    pomFileContent = CodefileUtils.processFileTemplate(pomFileTemplate, pomFileContentDatamodel, GlobalConfig.CHARSET);
                } catch (IOException e) {
                    throw new CodegeneratorException("Failed to generate snippet '" + "pomFileContent" + "': " + e.getMessage(), e);
                }
                final String logconfigFileContent;
                final Map<String, String> logconfigDatamodel = new HashMap<>();
                logconfigDatamodel.put("name", communicationModuleName);
                logconfigDatamodel.put("groupId", parentConfig.getComponentGroupId());
                try {
                    final Template template = GlobalConfig.getTemplate(FreemarkerTemplate.LOG4J2);
                    try (final Writer writer = new StringWriter()) {
                        template.process(logconfigDatamodel, writer);
                        logconfigFileContent = writer.toString();
                    }
                } catch (IOException | TemplateException e) {
                    throw new CodegeneratorException("Failed to generate snippet '" + "logconfigFileContent" + "': " + e.getMessage(), e);
                }
                CodefileUtils.MavenModuleStructure mavenModuleStructure = CodefileUtils.createMavenModule(parentConfig.getComponentGroupId(), communicationModuleName, parentConfig.getServiceConfig().getServiceDirectory(), pomFileContent, logconfigFileContent, GlobalConfig.CHARSET);
                // TODO: copy components into mavenModuleStructure.getCodeDirectory()
            } catch (IOException e) {
                throw new CodegeneratorException("Failed to generate Maven module '" + communicationModuleName + "': " + e.getMessage(), e);
            }
        }

        final String communicationPackageName = parentConfig.getComponentGroupId() + "." + communicationModuleName;

        // adapt Maven module component:
        // CommunicationService.java anlegen  hat initial noch keine Ports zugewiesen
        Path componentCommunicationserviceFile = parentConfig.getComponentMainFile().getParent().resolve("CommunicationService.java");
        final Map<String, String> componentCommunicationserviceDatamodel = new HashMap<>();
        componentCommunicationserviceDatamodel.put("packageName", parentConfig.getComponentGroupId() + "." + parentConfig.getComponentArtifactId());
        componentCommunicationserviceDatamodel.put("communicationPackageName", communicationPackageName);
        try {
            final Template template = GlobalConfig.getTemplate(FreemarkerTemplate.COMMSERVICE_INITIAL);
            try (final Writer writer = Files.newBufferedWriter(componentCommunicationserviceFile, GlobalConfig.CHARSET)) {
                template.process(componentCommunicationserviceDatamodel, writer);
            }
        } catch (IOException | TemplateException e) {
            throw new CodegeneratorException("Failed to generate file '" + componentCommunicationserviceFile + "': " + e.getMessage(), e);
        }
        // Main.java anpassen  Example-Usage des CommunicationService der Methode ‚main’ hinzufügen
        final String snippet;
        try {
            final Template template = GlobalConfig.getTemplate(FreemarkerTemplate.MAIN_COMMSERVICEUSAGE_SNIPPET);
            try (final Writer writer = new StringWriter()) {
                template.process(new HashMap<>(), writer);
                snippet = writer.toString();
            }
        } catch (IOException | TemplateException e) {
            throw new CodegeneratorException("Failed to generate snippet '" + "ComponentMainCommunicationserviceUsage" + "': " + e.getMessage(), e);
        }
        try {
            CodefileUtils.addToMethod(snippet, "main", parentConfig.getComponentMainFile(), GlobalConfig.CHARSET);
        } catch (IOException e) {
            throw new CodegeneratorException("Failed to adapt file '" + parentConfig.getComponentMainFile() + "': " + e.getMessage(), e);
        }
        // pom.xml anpassen  module ‚communication’ als ‚dependency’ hinzufügen
        final Path componentPomFile = parentConfig.getComponentDirectory().resolve("pom.xml");
        try {
            CodefileUtils.addMavenDependancy(parentConfig.getComponentGroupId(), communicationModuleName, componentPomFile, GlobalConfig.CHARSET);
        } catch (IOException e) {
            throw new CodegeneratorException("Failed to add Maven module '" + communicationModuleName + "' to '" + componentPomFile + "': " + e.getMessage(), e);
        }

        LOGGER.trace("Generating ports for ports-node '" + portsName + "' finished");

        return new GeneratedPortsConfig(parentConfig, componentCommunicationserviceFile, communicationPackageName, communicationModuleName);
    }
}

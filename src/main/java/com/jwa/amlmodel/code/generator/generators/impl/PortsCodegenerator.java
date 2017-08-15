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

import org.apache.commons.io.FileUtils;
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
    public final GeneratedPortsConfig generate(final InternalElement node, final GeneratedComponentConfig componentConfig) throws CodegeneratorException {
        if (!AmlmodelConstants.hasPortsRole(node)) {
            throw new IllegalArgumentException("Passed node has no ports-role");
        }

        final String portsName = node.getName();

        LOGGER.trace("Generating ports for ports-node '" + portsName + "' ...");

        final String communicationModuleName = "communication";
        final String communicationPackageName = componentConfig.getComponentGroupId() + "." + communicationModuleName;
        if (!CodefileUtils.mavenModuleExists(communicationModuleName, componentConfig.getServiceConfig().getServiceDirectory())) {
            try {
                final String pomFileContent;
                final Path pomFileTemplate = GlobalConfig.getTemplate(FileTemplate.POM_COMMUNICATION);
                final Map<String, String> pomFileContentDatamodel = new HashMap<>();
                final GeneratedServiceConfig serviceConfig = componentConfig.getServiceConfig();
                pomFileContentDatamodel.put("parentGroupId", serviceConfig.getServiceGroupId());
                pomFileContentDatamodel.put("parentArtifactId", serviceConfig.getServiceArtifactId());
                pomFileContentDatamodel.put("groupId", componentConfig.getComponentGroupId());
                pomFileContentDatamodel.put("artifactId", communicationModuleName);
                try {
                    pomFileContent = CodefileUtils.processFileTemplate(pomFileTemplate, pomFileContentDatamodel, GlobalConfig.getCharset());
                } catch (IOException e) {
                    throw new CodegeneratorException("Failed to generate snippet '" + "pomFileContent" + "': " + e.getMessage(), e);
                }
                final String logconfigFileContent;
                final Map<String, String> logconfigDatamodel = new HashMap<>();
                logconfigDatamodel.put("name", communicationModuleName);
                logconfigDatamodel.put("groupId", componentConfig.getComponentGroupId());
                final Template logconfigTemplate = GlobalConfig.getTemplate(FreemarkerTemplate.LOG4J2);
                try (final Writer writer = new StringWriter()) {
                    logconfigTemplate.process(logconfigDatamodel, writer);
                    logconfigFileContent = writer.toString();
                } catch (IOException | TemplateException e) {
                    throw new CodegeneratorException("Failed to generate snippet '" + "logconfigFileContent" + "': " + e.getMessage(), e);
                }
                CodefileUtils.MavenModuleStructure mavenModuleStructure = CodefileUtils.createMavenModule(componentConfig.getComponentGroupId(), communicationModuleName, componentConfig.getServiceConfig().getServiceDirectory(), pomFileContent, logconfigFileContent, GlobalConfig.getCharset());
                // copy components into mavenModuleStructure.getCodeDirectory()
                final Path communicationFilesDirectory = GlobalConfig.getFiles(com.jwa.amlmodel.code.generator.generators.config.Files.COMMUNICATION);
                FileUtils.copyDirectory(communicationFilesDirectory.toFile(), mavenModuleStructure.getCodeDirectory().toFile());
                // adapt components: package-name and imports
                CodefileUtils.adaptPackageAndImportNames(mavenModuleStructure.getCodeDirectory(), "{{communicationPackageName}}", communicationPackageName, GlobalConfig.getCharset());
            } catch (IOException e) {
                throw new CodegeneratorException("Failed to generate Maven module '" + communicationModuleName + "': " + e.getMessage(), e);
            }
        }

        // adapt Maven module component:
        // CommunicationService.java anlegen  hat initial noch keine Ports zugewiesen
        Path componentCommunicationserviceFile = componentConfig.getComponentMainFile().getParent().resolve("CommunicationService.java");
        final Map<String, String> componentCommunicationserviceDatamodel = new HashMap<>();
        componentCommunicationserviceDatamodel.put("packageName", componentConfig.getComponentGroupId() + "." + componentConfig.getComponentArtifactId());
        componentCommunicationserviceDatamodel.put("communicationPackageName", communicationPackageName);
        final Template componentCommunicationserviceTemplate = GlobalConfig.getTemplate(FreemarkerTemplate.COMMSERVICE_INITIAL);
        try (final Writer writer = Files.newBufferedWriter(componentCommunicationserviceFile, GlobalConfig.getCharset())) {
            componentCommunicationserviceTemplate.process(componentCommunicationserviceDatamodel, writer);
        } catch (IOException | TemplateException e) {
            throw new CodegeneratorException("Failed to generate file '" + componentCommunicationserviceFile + "': " + e.getMessage(), e);
        }
        // Main.java anpassen  Example-Usage des CommunicationService der Methode ‚main’ hinzufügen
        final String snippet;
        final Template template = GlobalConfig.getTemplate(FreemarkerTemplate.MAIN_COMMSERVICEUSAGE_SNIPPET);
        try (final Writer writer = new StringWriter()) {
            template.process(new HashMap<>(), writer);
            snippet = writer.toString();
        } catch (IOException | TemplateException e) {
            throw new CodegeneratorException("Failed to generate snippet '" + "ComponentMainCommunicationserviceUsage" + "': " + e.getMessage(), e);
        }
        try {
            CodefileUtils.addToMethod(snippet, "main", componentConfig.getComponentMainFile(), GlobalConfig.getCharset());
        } catch (IOException e) {
            throw new CodegeneratorException("Failed to adapt file '" + componentConfig.getComponentMainFile() + "': " + e.getMessage(), e);
        }
        // adapt Main.java: add imports
        final String[] importStatements = {
                communicationPackageName + ".ports.PortsService",
                communicationPackageName + ".ports.PortsServiceException"
        };
        for(String importStatement : importStatements) {
            try {
                CodefileUtils.addImport(importStatement, componentConfig.getComponentMainFile(), GlobalConfig.getCharset());
            } catch (IOException e) {
                throw new CodegeneratorException("Failed to adapt file '" + componentConfig.getComponentMainFile() + "': " + e.getMessage(), e);
            }
        }
        // pom.xml anpassen  module ‚communication’ als ‚dependency’ hinzufügen
        final Path componentPomFile = componentConfig.getComponentDirectory().resolve("pom.xml");
        try {
            CodefileUtils.addMavenDependancy(componentConfig.getComponentGroupId(), communicationModuleName, componentPomFile, GlobalConfig.getCharset());
        } catch (IOException e) {
            throw new CodegeneratorException("Failed to add Maven module '" + communicationModuleName + "' to '" + componentPomFile + "': " + e.getMessage(), e);
        }

        LOGGER.trace("Generating ports for ports-node '" + portsName + "' finished");

        return new GeneratedPortsConfig(componentConfig, componentCommunicationserviceFile, communicationPackageName, communicationModuleName);
    }
}

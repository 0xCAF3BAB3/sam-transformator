package com.jwa.amlmodel.code.generator.generators.impl;

import com.jwa.amlmodel.code.generator.generators.Codegenerator;
import com.jwa.amlmodel.code.generator.generators.CodegeneratorException;
import com.jwa.amlmodel.code.generator.generators.config.GlobalConfig;
import com.jwa.amlmodel.code.generator.generators.config.generated.impl.GeneratedComponentConfig;
import com.jwa.amlmodel.code.generator.generators.config.generated.impl.GeneratedPortsConfig;
import com.jwa.amlmodel.code.generator.generators.constants.FreemarkerTemplatesConstants;
import com.jwa.amlmodel.code.generator.generators.utils.CodefileUtils;

import freemarker.template.Template;
import freemarker.template.TemplateException;

import org.cdlflex.models.CAEX.InternalElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public final class PortsCodegenerator implements Codegenerator<GeneratedComponentConfig, GeneratedPortsConfig> {
    private static final Logger LOGGER = LoggerFactory.getLogger(PortsCodegenerator.class);

    @Override
    public final GeneratedPortsConfig generate(final InternalElement node, final GeneratedComponentConfig parentConfig) throws CodegeneratorException {
        final String portsName = node.getName();

        LOGGER.trace("Generating ports for ports-node '" + portsName + "' ...");

        final String communicationModuleName = "communication";
        if (!CodefileUtils.mavenModuleExists(communicationModuleName, parentConfig.getServiceConfig().getServiceDirectory())) {
            try {
                // TODO: fix parameters ... to real parameters
                CodefileUtils.MavenModuleStructure mavenModuleStructure = CodefileUtils.createMavenModule(parentConfig.getComponentGroupId(), communicationModuleName, parentConfig.getServiceConfig().getServiceDirectory(), "...", "...", StandardCharsets.UTF_8);
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
        componentCommunicationserviceDatamodel.put("packageName", parentConfig.getComponentGroupId() + "." + parentConfig.getArtifactId());
        componentCommunicationserviceDatamodel.put("communicationPackageName", communicationPackageName);
        try {
            final Template template = GlobalConfig.getTemplate(FreemarkerTemplatesConstants.COMMSERVICE);
            try (final Writer writer = Files.newBufferedWriter(componentCommunicationserviceFile, GlobalConfig.CHARSET)) {
                template.process(componentCommunicationserviceDatamodel, writer);
            }
        } catch (IOException | TemplateException e) {
            throw new CodegeneratorException("Failed to generate file '" + componentCommunicationserviceFile + "': " + e.getMessage(), e);
        }
        // Main.java anpassen  Example-Usage des CommunicationService der Methode ‚main’ hinzufügen
        final String snippet;
        try {
            final Template template = GlobalConfig.getTemplate(FreemarkerTemplatesConstants.MAIN_COMMSERVICEUSAGE_SNIPPET);
            try (final Writer writer = new StringWriter()) {
                template.process(new HashMap<>(), writer);
                snippet = writer.toString();
            }
        } catch (IOException | TemplateException e) {
            throw new CodegeneratorException("Failed to generate snippet '" + "ComponentMainCommunicationserviceUsage" + "': " + e.getMessage(), e);
        }
        try {
            CodefileUtils.addToMethod(snippet, "public static void main(final String[] args) {", parentConfig.getComponentMainFile(), GlobalConfig.CHARSET);
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

        return new GeneratedPortsConfig(parentConfig, componentCommunicationserviceFile, communicationPackageName);
    }
}

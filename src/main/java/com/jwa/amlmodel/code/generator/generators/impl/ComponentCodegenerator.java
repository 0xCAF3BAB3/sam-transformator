package com.jwa.amlmodel.code.generator.generators.impl;

import com.jwa.amlmodel.code.generator.generators.Codegenerator;
import com.jwa.amlmodel.code.generator.generators.CodegeneratorException;
import com.jwa.amlmodel.code.generator.generators.config.GlobalConfig;
import com.jwa.amlmodel.code.generator.generators.config.generated.impl.GeneratedComponentConfig;
import com.jwa.amlmodel.code.generator.generators.config.generated.impl.GeneratedServiceConfig;
import com.jwa.amlmodel.code.generator.generators.constants.AmlmodelConstants;
import com.jwa.amlmodel.code.generator.generators.constants.FreemarkerTemplatesConstants;
import com.jwa.amlmodel.code.generator.generators.utils.CodefileUtils;

import freemarker.template.Template;
import freemarker.template.TemplateException;

import org.cdlflex.models.CAEX.InternalElement;
import org.cdlflex.models.CAEX.util.AmlUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public final class ComponentCodegenerator implements Codegenerator<GeneratedServiceConfig, GeneratedComponentConfig> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ComponentCodegenerator.class);

    @Override
    public final GeneratedComponentConfig generate(final InternalElement node, final GeneratedServiceConfig parentConfig) throws CodegeneratorException {
        if (!AmlmodelConstants.hasComponentRole(node)) {
            throw new IllegalArgumentException("Passed node has no role '" + AmlmodelConstants.NAME_ROLE_COMPONENT + "'");
        }

        final String componentName = node.getName();

        LOGGER.trace("Generating component for component-node '" + componentName + "' ...");

        final String componentArtifactId = AmlUtil.getAttributeValue(node, AmlmodelConstants.NAME_ATTRIBUTE_COMPONENT_ARTIFACTID).get();
        final Path componentDirectory = parentConfig.getServiceDirectory().resolve(componentArtifactId);
        try {
            Files.createDirectories(componentDirectory);
        } catch (IOException e) {
            throw new CodegeneratorException("Failed to create directory '" + componentDirectory + "': " + e.getMessage(), e);
        }

        final String componentGroupId = AmlUtil.getAttributeValue(node, AmlmodelConstants.NAME_ATTRIBUTE_COMPONENT_GROUPID).get();

        final CodefileUtils.MavenDirectoryStructure componentMavenDirectoryStructure;
        try {
            componentMavenDirectoryStructure = CodefileUtils.generateMavenJavaDirectoryStructure(componentDirectory, componentGroupId, componentArtifactId);
        } catch (IOException e) {
            throw new CodegeneratorException("Failed to generate Maven directory-structure in directory '" + componentDirectory + "': " + e.getMessage(), e);
        }

        final Path componentMainFile = componentMavenDirectoryStructure.getCodeDirectory().resolve("Main.java");
        final Map<String, String> componentMainDatamodel = new HashMap<>();
        final String packageName = componentGroupId + "." + componentArtifactId;
        componentMainDatamodel.put("packageName", packageName);
        try {
            final Template template = GlobalConfig.getTemplate(FreemarkerTemplatesConstants.MAIN_INITIAL);
            try (final Writer writer = Files.newBufferedWriter(componentMainFile, GlobalConfig.CHARSET)) {
                template.process(componentMainDatamodel, writer);
            }
        } catch (IOException | TemplateException e) {
            throw new CodegeneratorException("Failed to generate file '" + componentMainFile + "': " + e.getMessage(), e);
        }

        final Path componentLogconfigFile = componentMavenDirectoryStructure.getResourcesDirectory().resolve("log4j2.xml");
        final Map<String, String> componentLogconfigDatamodel = new HashMap<>();
        componentLogconfigDatamodel.put("name", componentArtifactId);
        componentLogconfigDatamodel.put("groupId", componentGroupId);
        try {
            final Template template = GlobalConfig.getTemplate(FreemarkerTemplatesConstants.LOG4J2);
            try (final Writer writer = Files.newBufferedWriter(componentLogconfigFile, GlobalConfig.CHARSET)) {
                template.process(componentLogconfigDatamodel, writer);
            }
        } catch (IOException | TemplateException e) {
            throw new CodegeneratorException("Failed to generate file '" + componentLogconfigFile + "': " + e.getMessage(), e);
        }

        final Path componentPomTemplateFile = GlobalConfig.DIRECTORY_FILES_TEMPLATES
                .resolve("component")
                .resolve("pom.xml");
        final Path componentPomFile = componentDirectory.resolve("pom.xml");
        final Map<String, String> componentPomDatamodel = new HashMap<>();
        componentPomDatamodel.put("parentGroupId", parentConfig.getServiceGroupId());
        componentPomDatamodel.put("parentArtifactId", parentConfig.getServiceArtifactId());
        componentPomDatamodel.put("groupId", componentGroupId);
        componentPomDatamodel.put("artifactId", componentArtifactId);
        componentPomDatamodel.put("profileId", componentArtifactId);
        final String pathToMainClass = componentGroupId + "." + componentArtifactId + ".Main";
        componentPomDatamodel.put("pathToMainClass", pathToMainClass);
        try {
            CodefileUtils.processFileTemplate(componentPomTemplateFile, componentPomFile, componentPomDatamodel, GlobalConfig.CHARSET);
        } catch (IOException e) {
            throw new CodegeneratorException("Failed to generate file '" + componentPomFile + "': " + e.getMessage(), e);
        }

        try {
            CodefileUtils.addMavenModule(componentArtifactId, parentConfig.getServicePomFile(), GlobalConfig.CHARSET);
        } catch (IOException e) {
            throw new CodegeneratorException(e.getMessage(), e);
        }

        LOGGER.trace("Generating component for component-node '" + componentName + "' finished");

        return new GeneratedComponentConfig(parentConfig, componentGroupId, componentArtifactId, componentDirectory, componentMainFile);
    }
}

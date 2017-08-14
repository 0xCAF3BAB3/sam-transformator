package com.jwa.amlmodel.code.generator.generators.impl;

import com.jwa.amlmodel.code.generator.generators.Codegenerator;
import com.jwa.amlmodel.code.generator.generators.CodegeneratorException;
import com.jwa.amlmodel.code.generator.generators.config.FileTemplate;
import com.jwa.amlmodel.code.generator.generators.config.FreemarkerTemplate;
import com.jwa.amlmodel.code.generator.generators.config.GlobalConfig;
import com.jwa.amlmodel.code.generator.generators.config.generated.impl.GeneratedRootConfig;
import com.jwa.amlmodel.code.generator.generators.config.generated.impl.GeneratedServiceConfig;
import com.jwa.amlmodel.code.generator.generators.constants.AmlmodelConstants;
import com.jwa.amlmodel.code.generator.generators.utils.CodefileUtils;

import freemarker.template.Template;
import freemarker.template.TemplateException;

import org.cdlflex.models.CAEX.InternalElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public final class ServiceCodegenerator implements Codegenerator<GeneratedRootConfig, GeneratedServiceConfig> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceCodegenerator.class);

    @Override
    public final GeneratedServiceConfig generate(final InternalElement node, final GeneratedRootConfig parentConfig) throws CodegeneratorException {
        if (!AmlmodelConstants.hasServiceRole(node)) {
            throw new IllegalArgumentException("Passed node has no service-role");
        }

        final String serviceName = node.getName();

        LOGGER.trace("Generating service for service-node '" + serviceName + "' ...");

        final Path serviceDirectory = parentConfig.getOutputDirectory().resolve(serviceName);
        try {
            Files.createDirectories(serviceDirectory);
        } catch (IOException e) {
            throw new CodegeneratorException("Failed to create directory '" + serviceDirectory + "': " + e.getMessage(), e);
        }

        final Path readmeFile = serviceDirectory.resolve("README.md");
        final Map<String, String> readmeDatamodel = new HashMap<>();
        readmeDatamodel.put("serviceName", serviceName);
        final Template template = GlobalConfig.getTemplate(FreemarkerTemplate.README_SERVICE);
        try (final Writer writer = Files.newBufferedWriter(readmeFile, GlobalConfig.getCharset())) {
            template.process(readmeDatamodel, writer);
        } catch (IOException | TemplateException e) {
            throw new CodegeneratorException("Failed to generate file '" + readmeFile + "': " + e.getMessage(), e);
        }

        final String serviceGroupId = AmlmodelConstants.getServiceGroupId(node);
        final String serviceArtifactId = AmlmodelConstants.getServiceArtifactId(node);
        final Path servicePomTemplateFile = GlobalConfig.getTemplate(FileTemplate.POM_SERVICE);
        final Path servicePomFile = serviceDirectory.resolve("pom.xml");
        final Map<String, String> serviePomDatamodel = new HashMap<>();
        serviePomDatamodel.put("groupId", serviceGroupId);
        serviePomDatamodel.put("artifactId", serviceArtifactId);
        try {
            CodefileUtils.processFileTemplate(servicePomTemplateFile, servicePomFile, serviePomDatamodel, GlobalConfig.getCharset());
        } catch (IOException e) {
            throw new CodegeneratorException("Failed to generate file '" + servicePomFile + "': " + e.getMessage(), e);
        }

        LOGGER.trace("Generating service for service-node '" + serviceName + "' finished");

        return new GeneratedServiceConfig(parentConfig, serviceDirectory, serviceGroupId, serviceArtifactId, servicePomFile);
    }
}

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
import com.jwa.amlmodel.code.generator.generators.utils.MavenProjectInfo;

import freemarker.template.Template;

import org.cdlflex.models.CAEX.InternalElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public final class ServiceCodegenerator implements Codegenerator<GeneratedRootConfig, GeneratedServiceConfig> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceCodegenerator.class);

    @Override
    public final GeneratedServiceConfig generate(final InternalElement node, final GeneratedRootConfig rootConfig) throws CodegeneratorException {
        if (!AmlmodelConstants.hasServiceRole(node)) {
            throw new IllegalArgumentException("Passed node has no service-role");
        }

        final String serviceName = node.getName();

        LOGGER.trace("Generating service for service-node '" + serviceName + "' ...");

        final String serviceGroupId = AmlmodelConstants.getServiceGroupId(node);
        final String serviceArtifactId = AmlmodelConstants.getServiceArtifactId(node);
        final MavenProjectInfo serviceMavenProjectInfo = createServiceMavenProject(serviceName, serviceGroupId, serviceArtifactId, rootConfig);

        createReadme(serviceName, serviceMavenProjectInfo);

        LOGGER.trace("Generating service for service-node '" + serviceName + "' finished");

        return new GeneratedServiceConfig(serviceMavenProjectInfo, rootConfig);
    }

    private static MavenProjectInfo createServiceMavenProject(final String serviceName, final String serviceGroupId, final String serviceArtifactId, final GeneratedRootConfig rootConfig) throws CodegeneratorException {
        final String pomFileContent;
        final Path pomTemplate = GlobalConfig.getTemplate(FileTemplate.POM_SERVICE);
        final Map<String, String> pomDatamodel = new HashMap<>();
        pomDatamodel.put("groupId", serviceGroupId);
        pomDatamodel.put("artifactId", serviceArtifactId);
        try {
            pomFileContent = CodefileUtils.processFileTemplate(pomTemplate, pomDatamodel, GlobalConfig.getCharset());
        } catch (IOException e) {
            throw new CodegeneratorException("Failed to generate pom-file-content: " + e.getMessage(), e);
        }

        try {
            return CodefileUtils.createMavenProject(
                    serviceGroupId,
                    serviceArtifactId,
                    rootConfig.getOutputDirectory().resolve(serviceName),
                    pomFileContent,
                    GlobalConfig.getCharset()
            );
        } catch (IOException e) {
            // TODO: throw better exception
            throw new CodegeneratorException(e.getMessage(), e);
        }
    }

    private static void createReadme(final String serviceName, final MavenProjectInfo serviceMavenProjectInfo) throws CodegeneratorException {
        final Template readmeTemplate = GlobalConfig.getTemplate(FreemarkerTemplate.README_SERVICE);
        final Map<String, String> readmeDatamodel = new HashMap<>();
        readmeDatamodel.put("serviceName", serviceName);
        final Path readmeFile = serviceMavenProjectInfo.getDirectory().resolve("README.md");
        try {
            CodefileUtils.processTemplate(readmeTemplate, readmeDatamodel, readmeFile, GlobalConfig.getCharset());
        } catch (IOException e) {
            throw new CodegeneratorException("Failed to generate file '" + readmeFile + "': " + e.getMessage(), e);
        }
    }
}
